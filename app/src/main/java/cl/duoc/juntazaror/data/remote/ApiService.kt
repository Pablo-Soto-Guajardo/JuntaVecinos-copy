package cl.duoc.juntazaror.data.remote

import cl.duoc.juntazaror.data.remote.dto.ActualizarUsuarioRequest
import cl.duoc.juntazaror.data.remote.dto.DependenciaDto
import cl.duoc.juntazaror.data.remote.dto.EstadoRequest
import cl.duoc.juntazaror.data.remote.dto.LoginRequest
import cl.duoc.juntazaror.data.remote.dto.LoginResponse
import cl.duoc.juntazaror.data.remote.dto.MovimientoDto
import cl.duoc.juntazaror.data.remote.dto.RegistroRequest
import cl.duoc.juntazaror.data.remote.dto.ReservaDto
import cl.duoc.juntazaror.data.remote.dto.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Guía 14: interfaz que define los endpoints del backend (api-gateway, puerto 9090).
 * Retrofit genera el código que hace las peticiones HTTP.
 * Las rutas protegidas reciben el encabezado "Authorization: Bearer <token>".
 */
interface ApiService {

    // ======================= ms-admin =======================

    @POST("auth/login")
    suspend fun login(@Body datos: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun registrar(@Body datos: RegistroRequest): LoginResponse

    @PUT("usuarios/{id}")
    suspend fun actualizarUsuario(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body datos: ActualizarUsuarioRequest
    ): UsuarioDto

    // ======================= ms-reservas =======================

    // Público: no necesita token
    @GET("reservas/dependencias")
    suspend fun getDependencias(): List<DependenciaDto>

    @GET("reservas")
    suspend fun getReservas(@Header("Authorization") token: String): List<ReservaDto>

    @POST("reservas")
    suspend fun crearReserva(
        @Header("Authorization") token: String,
        @Body reserva: ReservaDto
    ): ReservaDto

    @PUT("reservas/{id}/estado")
    suspend fun cambiarEstadoReserva(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body estado: EstadoRequest
    ): ReservaDto

    // DELETE no devuelve contenido (204): por eso se usa Response<Unit>
    @DELETE("reservas/{id}")
    suspend fun eliminarReserva(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Unit>

    // ======================= ms-finanzas =======================

    @GET("movimientos")
    suspend fun getMovimientos(@Header("Authorization") token: String): List<MovimientoDto>

    @POST("movimientos")
    suspend fun registrarMovimiento(
        @Header("Authorization") token: String,
        @Body movimiento: MovimientoDto
    ): MovimientoDto
}
