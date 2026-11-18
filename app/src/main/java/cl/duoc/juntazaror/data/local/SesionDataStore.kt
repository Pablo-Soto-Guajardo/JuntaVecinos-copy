package cl.duoc.juntazaror.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferencias_junta")

/**
 * Preferencias guardadas con DataStore:
 * - id del usuario conectado
 * - token JWT entregado por el backend (se envía en cada petición protegida)
 * - si las notificaciones están activas
 */
class SesionDataStore(private val context: Context) {

    private val USUARIO_ID = longPreferencesKey("usuario_id")
    private val TOKEN = stringPreferencesKey("token_jwt")
    private val NOTIFICACIONES = booleanPreferencesKey("notificaciones_activas")

    val usuarioId: Flow<Long?> = context.dataStore.data.map { preferencias ->
        preferencias[USUARIO_ID]
    }

    val token: Flow<String?> = context.dataStore.data.map { preferencias ->
        preferencias[TOKEN]
    }

    val notificacionesActivas: Flow<Boolean> = context.dataStore.data.map { preferencias ->
        preferencias[NOTIFICACIONES] ?: true
    }

    suspend fun guardarSesion(usuarioId: Long, token: String) {
        context.dataStore.edit { preferencias ->
            preferencias[USUARIO_ID] = usuarioId
            preferencias[TOKEN] = token
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { preferencias ->
            preferencias.remove(USUARIO_ID)
            preferencias.remove(TOKEN)
        }
    }

    suspend fun guardarNotificaciones(activas: Boolean) {
        context.dataStore.edit { preferencias -> preferencias[NOTIFICACIONES] = activas }
    }
}
