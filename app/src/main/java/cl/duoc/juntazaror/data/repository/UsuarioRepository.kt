package cl.duoc.juntazaror.data.repository

import cl.duoc.juntazaror.data.local.dao.UsuarioDao
import cl.duoc.juntazaror.data.model.Usuario
import cl.duoc.juntazaror.data.remote.ApiService
import cl.duoc.juntazaror.data.remote.RetrofitInstance
import cl.duoc.juntazaror.data.remote.dto.ActualizarUsuarioRequest
import cl.duoc.juntazaror.data.remote.dto.LoginRequest
import cl.duoc.juntazaror.data.remote.dto.LoginResponse
import cl.duoc.juntazaror.data.remote.dto.RegistroRequest
import cl.duoc.juntazaror.data.remote.dto.aUsuario
import cl.duoc.juntazaror.utils.Formatos

/** Resultado de un login o registro correcto. */
data class SesionIniciada(val usuario: Usuario, val token: String)

/**
 * Repositorio de usuarios.
 * - Login, registro y edición de datos se hacen en el backend (ms-admin) con Retrofit.
 * - Room guarda una copia del usuario conectado para abrir la app sin conexión.
 * El ApiService se recibe por parámetro para poder reemplazarlo por un mock en las pruebas (Guía 15).
 */
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val api: ApiService = RetrofitInstance.api
) {

    suspend fun obtenerPorId(id: Long): Usuario? = usuarioDao.obtenerPorId(id)

    /**
     * Devuelve la sesión si el servidor acepta las credenciales, o null si son incorrectas.
     * Si no hay conexión lanza una excepción (la maneja el ViewModel).
     */
    suspend fun iniciarSesion(correo: String, clave: String): SesionIniciada? {
        val respuesta = api.login(LoginRequest(correo.trim().lowercase(), clave))
        return guardarSesion(respuesta)
    }

    /** Crea la cuenta (siempre como VECINO) y deja la sesión iniciada. */
    suspend fun registrarVecino(
        nombre: String,
        rut: String,
        correo: String,
        telefono: String,
        direccion: String,
        clave: String
    ): SesionIniciada? {
        val respuesta = api.registrar(
            RegistroRequest(
                nombre = nombre.trim(),
                rut = Formatos.rut(rut),
                correo = correo.trim().lowercase(),
                telefono = telefono.replace(" ", "").removePrefix("+56"),
                direccion = direccion.trim(),
                clave = clave
            )
        )
        return guardarSesion(respuesta)
    }

    /** Si la respuesta es correcta guarda el usuario en Room (conservando su foto local). */
    private suspend fun guardarSesion(respuesta: LoginResponse): SesionIniciada? {
        val dto = respuesta.usuario
        val token = respuesta.token
        if (respuesta.status != "ok" || dto == null || token.isNullOrBlank()) return null

        val fotoAnterior = usuarioDao.obtenerPorId(dto.id)?.fotoUri
        val usuario = dto.aUsuario(fotoAnterior)
        usuarioDao.insertar(usuario)
        return SesionIniciada(usuario, token)
    }

    /** PUT al servidor y luego actualiza la copia local. */
    suspend fun actualizarDatos(usuario: Usuario, token: String): Usuario {
        val dto = api.actualizarUsuario(
            token,
            usuario.id,
            ActualizarUsuarioRequest(usuario.nombre, usuario.telefono, usuario.direccion)
        )
        val actualizado = dto.aUsuario(usuario.fotoUri)
        usuarioDao.actualizar(actualizado)
        return actualizado
    }

    /** La foto de perfil solo se guarda en el teléfono. */
    suspend fun actualizarFoto(usuario: Usuario) = usuarioDao.actualizar(usuario)
}
