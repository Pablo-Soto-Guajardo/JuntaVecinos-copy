package cl.duoc.juntazaror.viewmodel

import cl.duoc.juntazaror.data.remote.dto.DependenciaDto
import cl.duoc.juntazaror.data.repository.DependenciaRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

/**
 * Guía 15: prueba unitaria del ViewModel con Kotest (StringSpec).
 * Se crea una versión de prueba del ViewModel que NO llama a la API:
 * sobrescribe fetchDependencias() y carga una lista falsa.
 */
class DependenciaViewModelTest : StringSpec({

    "dependencias debe contener los elementos esperados después de fetchDependencias()" {
        val fakeDependencias = listOf(
            DependenciaDto("SALON_PRINCIPAL", "Salón principal", 80, 35000),
            DependenciaDto("MULTICANCHA", "Multicancha", 40, 18000)
        )

        val viewModel = object : DependenciaViewModel(mockk<DependenciaRepository>(relaxed = true)) {
            override fun fetchDependencias() {
                _dependencias.value = fakeDependencias
            }
        }

        runTest {
            viewModel.fetchDependencias()
            viewModel.dependencias.value shouldContainExactly fakeDependencias
        }
    }

    "al iniciar no hay error ni carga en curso" {
        val viewModel = object : DependenciaViewModel(mockk<DependenciaRepository>(relaxed = true)) {
            override fun fetchDependencias() {}
        }

        viewModel.error.value shouldBe null
        viewModel.cargando.value shouldBe false
    }
})
