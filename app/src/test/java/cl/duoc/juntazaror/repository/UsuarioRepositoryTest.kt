package cl.duoc.juntazaror.repository

import cl.duoc.juntazaror.data.local.dao.UsuarioDao
import cl.duoc.juntazaror.data.model.Rol
import cl.duoc.juntazaror.data.model.Usuario
import cl.duoc.juntazaror.data.remote.ApiService
import cl.duoc.juntazaror.data.remote.dto.LoginRequest
import cl.duoc.juntazaror.data.remote.dto.LoginResponse
import cl.duoc.juntazaror.data.remote.dto.UsuarioDto
import cl.duoc.juntazaror.data.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

/** Pruebas del login contra la API (mock) y de la copia del usuario en Room (mock). */
class UsuarioRepositoryTest : StringSpec({

    val usuarioDto = UsuarioDto(1, "Camila Rojas", "11.111.111-1", "vecino@zaror.cl", "911111111", "Los Aromos 120", "VECINO")

    "login correcto guarda el usuario en Room y entrega el token" {
        val dao = mockk<UsuarioDao>(relaxed = true)
        val api = mockk<ApiService>()
        // El repositorio debe enviar el correo sin espacios y en minúsculas
        coEvery { api.login(LoginRequest("vecino@zaror.cl", "junta123")) } returns
            LoginResponse("ok", "token123", "Sesión iniciada", usuarioDto)
        coEvery { dao.obtenerPorId(1) } returns null
        val repo = UsuarioRepository(dao, api)

        runTest {
            val sesion = repo.iniciarSesion("  Vecino@Zaror.cl ", "junta123")

            sesion.shouldNotBeNull()
            sesion.token shouldBe "token123"
            sesion.usuario.rol shouldBe Rol.VECINO
            coVerify { dao.insertar(sesion.usuario) }
        }
    }

    "login con credenciales incorrectas devuelve null y no guarda nada" {
        val dao = mockk<UsuarioDao>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.login(any()) } returns LoginResponse("error", "", "Correo o contraseña incorrectos", null)
        val repo = UsuarioRepository(dao, api)

        runTest {
            repo.iniciarSesion("vecino@zaror.cl", "mala").shouldBeNull()
            coVerify(exactly = 0) { dao.insertar(any()) }
        }
    }

    "al volver a iniciar sesión se conserva la foto de perfil guardada en el teléfono" {
        val dao = mockk<UsuarioDao>(relaxed = true)
        val api = mockk<ApiService>()
        coEvery { api.login(any()) } returns LoginResponse("ok", "token123", null, usuarioDto)
        coEvery { dao.obtenerPorId(1) } returns Usuario(
            id = 1, nombre = "Camila Rojas", rut = "11.111.111-1", correo = "vecino@zaror.cl",
            telefono = "911111111", direccion = "Los Aromos 120", rol = Rol.VECINO, fotoUri = "content://foto.jpg"
        )
        val repo = UsuarioRepository(dao, api)

        runTest {
            val sesion = repo.iniciarSesion("vecino@zaror.cl", "junta123")
            sesion?.usuario?.fotoUri shouldBe "content://foto.jpg"
        }
    }
})
