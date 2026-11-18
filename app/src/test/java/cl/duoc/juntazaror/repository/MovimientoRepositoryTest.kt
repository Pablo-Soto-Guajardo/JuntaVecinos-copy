package cl.duoc.juntazaror.repository

import cl.duoc.juntazaror.data.local.dao.MovimientoDao
import cl.duoc.juntazaror.data.model.CategoriaMovimiento
import cl.duoc.juntazaror.data.model.Movimiento
import cl.duoc.juntazaror.data.model.TipoMovimiento
import cl.duoc.juntazaror.data.remote.ApiService
import cl.duoc.juntazaror.data.remote.dto.MovimientoDto
import cl.duoc.juntazaror.data.repository.MovimientoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import java.time.LocalDate

class MovimientoRepositoryTest : StringSpec({

    val hoy = LocalDate.now()

    fun movimiento(tipo: TipoMovimiento, categoria: CategoriaMovimiento, monto: Long) = Movimiento(
        tipo = tipo, categoria = categoria, monto = monto, fechaEpochDay = hoy.toEpochDay(),
        descripcion = "Movimiento de prueba", registradoPor = "Patricia Soto"
    )

    "calcularResumen() suma ingresos, gastos y obtiene el saldo" {
        val repo = MovimientoRepository(mockk(relaxed = true), mockk(relaxed = true))
        val lista = listOf(
            movimiento(TipoMovimiento.INGRESO, CategoriaMovimiento.CUOTA_SOCIAL, 120000),
            movimiento(TipoMovimiento.INGRESO, CategoriaMovimiento.ARRIENDO, 35000),
            movimiento(TipoMovimiento.GASTO, CategoriaMovimiento.LUZ, 42500)
        )

        val resumen = repo.calcularResumen(lista)

        resumen.totalIngresos shouldBe 155000L
        resumen.totalGastos shouldBe 42500L
        resumen.saldo shouldBe 112500L
    }

    "calcularResumen() de una lista vacía es cero" {
        val repo = MovimientoRepository(mockk(relaxed = true), mockk(relaxed = true))
        repo.calcularResumen(emptyList()).saldo shouldBe 0L
    }

    "registrar() envía el movimiento al servidor y guarda en Room lo que este responde" {
        val dao = mockk<MovimientoDao>(relaxed = true)
        val api = mockk<ApiService>()
        val enviado = slot<MovimientoDto>()
        coEvery { api.registrarMovimiento("Bearer t", capture(enviado)) } returns MovimientoDto(
            id = 9, tipo = "GASTO", categoria = "AGUA", monto = 18900, fecha = hoy.toString(),
            descripcion = "Boleta de agua", comprobanteUri = "content://boleta.jpg", registradoPor = "Patricia Soto"
        )
        val repo = MovimientoRepository(dao, api)

        runTest {
            val guardado = repo.registrar(
                movimiento(TipoMovimiento.GASTO, CategoriaMovimiento.AGUA, 18900)
                    .copy(descripcion = "Boleta de agua", comprobanteUri = "content://boleta.jpg"),
                "Bearer t"
            )

            enviado.captured.categoria shouldBe "AGUA"
            enviado.captured.fecha shouldBe hoy.toString()
            guardado.id shouldBe 9L
            coVerify { dao.insertar(guardado) }
        }
    }
})
