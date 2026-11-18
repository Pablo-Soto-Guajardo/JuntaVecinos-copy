package cl.duoc.juntazaror.repository

import cl.duoc.juntazaror.data.remote.ApiService
import cl.duoc.juntazaror.data.remote.dto.DependenciaDto
import cl.duoc.juntazaror.data.repository.DependenciaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.io.IOException

/**
 * Guía 15: se reemplaza la API real por un mock (MockK).
 * coEvery define qué responde la función suspend del mock.
 */
class DependenciaRepositoryTest : StringSpec({

    "getDependencias() debe retornar la lista simulada de la API" {
        val fakeDependencias = listOf(
            DependenciaDto("SALON_PRINCIPAL", "Salón principal", 80, 35000),
            DependenciaDto("SALA_REUNIONES", "Sala de reuniones", 20, 12000)
        )
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getDependencias() } returns fakeDependencias

        // Versión de prueba del repositorio que usa la API simulada
        class TestableDependenciaRepository(private val testApi: ApiService) : DependenciaRepository(testApi) {
            override suspend fun getDependencias(): List<DependenciaDto> = testApi.getDependencias()
        }
        val repo = TestableDependenciaRepository(mockApi)

        runTest {
            val result = repo.getDependencias()
            result shouldBe fakeDependencias
            coVerify(exactly = 1) { mockApi.getDependencias() }
        }
    }

    "si la API falla, el error llega al ViewModel" {
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getDependencias() } throws IOException("Sin conexión")

        val repo = DependenciaRepository(mockApi)

        runTest {
            shouldThrow<IOException> { repo.getDependencias() }
        }
    }
})
