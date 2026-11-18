package cl.duoc.juntazaror.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import cl.duoc.juntazaror.data.remote.dto.DependenciaDto
import cl.duoc.juntazaror.ui.screens.reservas.DependenciasScreen
import cl.duoc.juntazaror.viewmodel.DependenciaViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

/**
 * Guía 15: prueba de interfaz con Compose + JUnit.
 * Se usa un ViewModel falso para no depender del servidor.
 * Se ejecuta en el emulador o en el celular (carpeta androidTest).
 */
class DependenciasScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_nombre_de_las_dependencias_debe_aparecer_en_pantalla() {
        val fakeDependencias = listOf(
            DependenciaDto("SALON_PRINCIPAL", "Salón principal", 80, 35000),
            DependenciaDto("MULTICANCHA", "Multicancha", 40, 18000)
        )

        val fakeViewModel = object : DependenciaViewModel() {
            override val dependencias = MutableStateFlow(fakeDependencias)
            override fun fetchDependencias() {} // No llama a la API
        }

        composeRule.setContent {
            DependenciasScreen(viewModel = fakeViewModel)
        }

        composeRule.onNodeWithText("Nombre: Salón principal").assertIsDisplayed()
        composeRule.onNodeWithText("Nombre: Multicancha").assertIsDisplayed()
        composeRule.onNodeWithText("Capacidad: 40 personas").assertIsDisplayed()
    }

    @Test
    fun si_no_hay_conexion_se_muestra_el_mensaje_de_error() {
        val fakeViewModel = object : DependenciaViewModel() {
            override val error = MutableStateFlow<String?>("No se pudo conectar con el servidor.")
            override fun fetchDependencias() {}
        }

        composeRule.setContent {
            DependenciasScreen(viewModel = fakeViewModel)
        }

        composeRule.onNodeWithText("No se pudo conectar con el servidor.").assertIsDisplayed()
    }
}
