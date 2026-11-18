package cl.duoc.juntazaror.data.repository

import cl.duoc.juntazaror.data.local.SesionDataStore
import kotlinx.coroutines.flow.first

class SesionRepository(private val sesionDataStore: SesionDataStore) {

    suspend fun obtenerUsuarioId(): Long? = sesionDataStore.usuarioId.first()

    suspend fun obtenerToken(): String? = sesionDataStore.token.first()

    /** Valor del encabezado "Authorization" que exige el api-gateway: "Bearer <token>". */
    suspend fun obtenerAuthHeader(): String = "Bearer ${obtenerToken() ?: ""}"

    suspend fun notificacionesActivas(): Boolean = sesionDataStore.notificacionesActivas.first()

    suspend fun iniciarSesion(usuarioId: Long, token: String) = sesionDataStore.guardarSesion(usuarioId, token)

    suspend fun cerrarSesion() = sesionDataStore.cerrarSesion()

    suspend fun guardarNotificaciones(activas: Boolean) =
        sesionDataStore.guardarNotificaciones(activas)
}
