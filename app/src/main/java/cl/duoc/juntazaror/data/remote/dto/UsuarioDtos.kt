package cl.duoc.juntazaror.data.remote.dto

/** JSON que se envía a POST /auth/login */
data class LoginRequest(
    val correo: String,
    val clave: String
)

/** JSON que se envía a POST /auth/register */
data class RegistroRequest(
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val clave: String
)

/** JSON que se envía a PUT /usuarios/{id} */
data class ActualizarUsuarioRequest(
    val nombre: String,
    val telefono: String,
    val direccion: String
)

/** Usuario tal como lo entrega ms-admin (sin contraseña). */
data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val direccion: String,
    val rol: String
)

/** Respuesta de login y registro: status "ok" (con token y usuario) o "error" (con mensaje). */
data class LoginResponse(
    val status: String,
    val token: String? = null,
    val mensaje: String? = null,
    val usuario: UsuarioDto? = null
)
