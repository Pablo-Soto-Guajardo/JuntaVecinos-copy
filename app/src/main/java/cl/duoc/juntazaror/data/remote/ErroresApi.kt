package cl.duoc.juntazaror.data.remote

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException

/** Forma del JSON de error que devuelven los microservicios (GlobalExceptionHandler). */
data class ErrorApi(
    val status: Int? = null,
    val message: String? = null
)

/** Traduce una excepción de Retrofit a un mensaje entendible para el usuario. */
object ErroresApi {

    fun mensaje(error: Throwable): String = when (error) {
        is HttpException -> when (error.code()) {
            401 -> "Tu sesión expiró. Cierra sesión y vuelve a ingresar."
            else -> leerMensajeServidor(error) ?: "El servidor respondió con un error (${error.code()})"
        }
        is IOException -> "No se pudo conectar con el servidor. Revisa tu conexión a internet."
        else -> "Ocurrió un error inesperado"
    }

    /** Lee el campo "message" del cuerpo del error, si existe. */
    private fun leerMensajeServidor(error: HttpException): String? = try {
        val cuerpo = error.response()?.errorBody()?.string()
        Gson().fromJson(cuerpo, ErrorApi::class.java)?.message
    } catch (e: Exception) {
        null
    }
}
