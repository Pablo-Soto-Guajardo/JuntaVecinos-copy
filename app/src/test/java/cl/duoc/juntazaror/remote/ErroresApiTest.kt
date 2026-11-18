package cl.duoc.juntazaror.remote

import cl.duoc.juntazaror.data.remote.ErroresApi
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException

/** Verifica que cada error de la API se traduzca a un mensaje claro para el usuario. */
class ErroresApiTest : StringSpec({

    fun errorHttp(codigo: Int, json: String) =
        HttpException(Response.error<Any>(codigo, json.toResponseBody("application/json".toMediaTypeOrNull())))

    "un error del servidor muestra el mensaje que envía el microservicio" {
        val error = errorHttp(409, "{\"status\":409,\"message\":\"Este bloque ya está reservado, elige otro\"}")
        ErroresApi.mensaje(error) shouldBe "Este bloque ya está reservado, elige otro"
    }

    "un 401 indica que la sesión expiró" {
        ErroresApi.mensaje(errorHttp(401, "")) shouldContain "sesión expiró"
    }

    "si el cuerpo no es JSON se muestra el código de error" {
        ErroresApi.mensaje(errorHttp(500, "texto plano")) shouldContain "500"
    }

    "sin conexión con el servidor se muestra un mensaje de conexión" {
        ErroresApi.mensaje(ConnectException("Failed to connect")) shouldContain "No se pudo conectar"
    }

    "cualquier otro error muestra un mensaje genérico" {
        ErroresApi.mensaje(IllegalStateException("x")) shouldBe "Ocurrió un error inesperado"
    }
})
