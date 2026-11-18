package cl.duoc.juntazaror.data.repository

import cl.duoc.juntazaror.data.local.dao.ReservaDao
import cl.duoc.juntazaror.data.model.EstadoReserva
import cl.duoc.juntazaror.data.model.Reserva
import cl.duoc.juntazaror.data.remote.ApiService
import cl.duoc.juntazaror.data.remote.RetrofitInstance
import cl.duoc.juntazaror.data.remote.dto.EstadoRequest
import cl.duoc.juntazaror.data.remote.dto.aDto
import cl.duoc.juntazaror.data.remote.dto.aReserva
import retrofit2.HttpException

/**
 * Repositorio de reservas.
 * - El SERVIDOR (ms-reservas) es la fuente oficial: valida que el bloque esté libre.
 * - Room es la copia local: permite ver las reservas aunque no haya conexión.
 */
class ReservaRepository(
    private val reservaDao: ReservaDao,
    private val api: ApiService = RetrofitInstance.api
) {

    /** Datos guardados en el teléfono (se muestran de inmediato o cuando no hay conexión). */
    suspend fun obtenerLocales(): List<Reserva> = reservaDao.obtenerTodas()

    /** GET al servidor y reemplaza la copia local. Lanza excepción si no hay conexión. */
    suspend fun sincronizar(token: String): List<Reserva> {
        val remotas = api.getReservas(token).map { it.aReserva() }
        reservaDao.borrarTodas()
        reservaDao.insertarTodas(remotas)
        return remotas
    }

    /** POST: el servidor responde 409 si el bloque ya está ocupado. */
    suspend fun crear(reserva: Reserva, token: String): Reserva {
        val creada = api.crearReserva(token, reserva.aDto()).aReserva()
        reservaDao.insertar(creada)
        return creada
    }

    /** PUT: confirmar o rechazar (directiva). */
    suspend fun cambiarEstado(reserva: Reserva, nuevoEstado: EstadoReserva, token: String): Reserva {
        val actualizada = api.cambiarEstadoReserva(token, reserva.id, EstadoRequest(nuevoEstado.name)).aReserva()
        reservaDao.actualizar(actualizada)
        return actualizada
    }

    /** DELETE: cancelar una solicitud pendiente. */
    suspend fun eliminar(reserva: Reserva, token: String) {
        val respuesta = api.eliminarReserva(token, reserva.id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
        reservaDao.eliminar(reserva)
    }
}
