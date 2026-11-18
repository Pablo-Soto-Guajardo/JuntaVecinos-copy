package cl.duoc.juntazaror.repository

import cl.duoc.juntazaror.data.local.dao.ReservaDao
import cl.duoc.juntazaror.data.model.BloqueHorario
import cl.duoc.juntazaror.data.model.Dependencia
import cl.duoc.juntazaror.data.model.EstadoReserva
import cl.duoc.juntazaror.data.model.Reserva
import cl.duoc.juntazaror.data.remote.ApiService
import cl.duoc.juntazaror.data.remote.dto.EstadoRequest
import cl.duoc.juntazaror.data.remote.dto.ReservaDto
import cl.duoc.juntazaror.data.repository.ReservaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate

/** Pruebas del repositorio de reservas: API (mock) + copia local en Room (mock). */
class ReservaRepositoryTest : StringSpec({

    val token = "Bearer token-de-prueba"
    val fecha = LocalDate.now().plusDays(7)

    val dtoServidor = ReservaDto(
        id = 1, usuarioId = 1, nombreSolicitante = "Camila Rojas", dependencia = "SALON_PRINCIPAL",
        fecha = fecha.toString(), bloque = "TARDE", motivo = "Cumpleaños familiar", asistentes = 40,
        monto = 35000, estado = "CONFIRMADA"
    )

    /** Reserva tal como se guarda en Room (id 0 = aún no existe en el servidor). */
    fun reservaLocal(id: Long = 0, estado: EstadoReserva = EstadoReserva.PENDIENTE) = Reserva(
        id = id, usuarioId = 1, nombreSolicitante = "Camila Rojas", dependencia = Dependencia.SALON_PRINCIPAL,
        fechaEpochDay = fecha.toEpochDay(), bloque = BloqueHorario.TARDE, motivo = "Cumpleaños familiar",
        asistentes = 40, monto = 35000, estado = estado
    )

    "sincronizar() reemplaza la copia local con las reservas del servidor" {
        val dao = mockk<ReservaDao>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.getReservas(token) } returns listOf(dtoServidor)
        val repo = ReservaRepository(dao, api)

        runTest {
            val resultado = repo.sincronizar(token)

            resultado shouldHaveSize 1
            resultado[0].dependencia shouldBe Dependencia.SALON_PRINCIPAL
            resultado[0].estado shouldBe EstadoReserva.CONFIRMADA
            resultado[0].fechaEpochDay shouldBe fecha.toEpochDay()
            coVerifyOrder {
                dao.borrarTodas()
                dao.insertarTodas(resultado)
            }
        }
    }

    "sin conexión, sincronizar() lanza la excepción y NO borra la copia local" {
        val dao = mockk<ReservaDao>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.getReservas(any()) } throws IOException("Sin conexión")
        val repo = ReservaRepository(dao, api)

        runTest {
            shouldThrow<IOException> { repo.sincronizar(token) }
            coVerify(exactly = 0) { dao.borrarTodas() }
        }
    }

    "crear() envía la reserva sin id y guarda la respuesta del servidor en Room" {
        val dao = mockk<ReservaDao>(relaxed = true)
        val api = mockk<ApiService>()
        val enviado = slot<ReservaDto>()
        coEvery { api.crearReserva(token, capture(enviado)) } returns
            dtoServidor.copy(id = 7, estado = "PENDIENTE")
        val repo = ReservaRepository(dao, api)

        runTest {
            val creada = repo.crear(reservaLocal(), token)

            enviado.captured.id shouldBe null          // el id lo asigna el servidor
            enviado.captured.fecha shouldBe fecha.toString()
            creada.id shouldBe 7L
            creada.estado shouldBe EstadoReserva.PENDIENTE
            coVerify { dao.insertar(creada) }
        }
    }

    "crear() en un bloque ocupado propaga el error 409 del servidor" {
        val dao = mockk<ReservaDao>(relaxed = true)
        val api = mockk<ApiService>()
        val cuerpo = "{\"status\":409,\"message\":\"Este bloque ya está reservado, elige otro\"}"
            .toResponseBody("application/json".toMediaTypeOrNull())
        coEvery { api.crearReserva(any(), any()) } throws HttpException(Response.error<ReservaDto>(409, cuerpo))
        val repo = ReservaRepository(dao, api)

        runTest {
            val error = shouldThrow<HttpException> { repo.crear(reservaLocal(), token) }
            error.code() shouldBe 409
            coVerify(exactly = 0) { dao.insertar(any()) }
        }
    }

    "cambiarEstado() envía el nuevo estado y actualiza Room" {
        val dao = mockk<ReservaDao>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.cambiarEstadoReserva(token, 1, EstadoRequest("CONFIRMADA")) } returns dtoServidor
        val repo = ReservaRepository(dao, api)
        runTest {
            val resultado = repo.cambiarEstado(reservaLocal(id = 1), EstadoReserva.CONFIRMADA, token)

            resultado.estado shouldBe EstadoReserva.CONFIRMADA
            coVerify { dao.actualizar(resultado) }
        }
    }

    "eliminar() lanza error si el servidor rechaza y no borra de Room" {
        val dao = mockk<ReservaDao>(relaxed = true)
        val api = mockk<ApiService>()
        val cuerpo = "{\"message\":\"Solo se pueden cancelar solicitudes pendientes\"}"
            .toResponseBody("application/json".toMediaTypeOrNull())
        coEvery { api.eliminarReserva(token, 1) } returns Response.error(400, cuerpo)
        val repo = ReservaRepository(dao, api)
        runTest {
            shouldThrow<HttpException> { repo.eliminar(reservaLocal(id = 1, estado = EstadoReserva.CONFIRMADA), token) }
            coVerify(exactly = 0) { dao.eliminar(any()) }
        }
    }
})
