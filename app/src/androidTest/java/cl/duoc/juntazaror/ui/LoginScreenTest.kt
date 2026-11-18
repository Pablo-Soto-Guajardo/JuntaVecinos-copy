package cl.duoc.juntazaror.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cl.duoc.juntazaror.data.model.LoginErrores
import cl.duoc.juntazaror.data.model.LoginUiState
import cl.duoc.juntazaror.ui.screens.FormularioLogin
import cl.duoc.juntazaror.ui.theme.JuntaZarorTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Guía 15: pruebas de interfaz del formulario de inicio de sesión. */
class LoginScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun se_muestran_los_errores_de_validacion() {
        val estado = LoginUiState(
            errores = LoginErrores(
                correo = "El correo es obligatorio",
                clave = "La contraseña es obligatoria"
            )
        )

        composeRule.setContent {
            JuntaZarorTheme {
                FormularioLogin(estado, onCorreoChange = {}, onClaveChange = {}, onIngresar = {}, onRegistrarse = {})
            }
        }

        composeRule.onNodeWithText("El correo es obligatorio").assertIsDisplayed()
        composeRule.onNodeWithText("La contraseña es obligatoria").assertIsDisplayed()
    }

    @Test
    fun se_muestra_el_error_cuando_el_servidor_no_responde() {
        val estado = LoginUiState(errorGeneral = "No se pudo conectar con el servidor.")

        composeRule.setContent {
            JuntaZarorTheme {
                FormularioLogin(estado, onCorreoChange = {}, onClaveChange = {}, onIngresar = {}, onRegistrarse = {})
            }
        }

        composeRule.onNodeWithText("No se pudo conectar con el servidor.").assertIsDisplayed()
    }

    @Test
    fun al_presionar_ingresar_se_llama_a_la_accion() {
        var presionado = false

        composeRule.setContent {
            JuntaZarorTheme {
                FormularioLogin(LoginUiState(), onCorreoChange = {}, onClaveChange = {},
                    onIngresar = { presionado = true }, onRegistrarse = {})
            }
        }

        composeRule.onNodeWithText("Ingresar").performClick()
        assertTrue(presionado)
    }
}
