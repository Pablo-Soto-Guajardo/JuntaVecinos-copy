package cl.duoc.juntazaror.ui.screens.reservas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import cl.duoc.juntazaror.data.remote.dto.DependenciaDto
import cl.duoc.juntazaror.ui.components.MensajeError
import cl.duoc.juntazaror.ui.components.PantallaConBarra
import cl.duoc.juntazaror.utils.Formatos
import cl.duoc.juntazaror.viewmodel.DependenciaViewModel

/**
 * Guía 14: pantalla que muestra los datos obtenidos de la API REST
 * (GET /reservas/dependencias) en una LazyColumn de Cards.
 */
@Composable
fun DependenciasScreen(viewModel: DependenciaViewModel, onVolver: () -> Unit = {}) {
    val dependencias by viewModel.dependencias.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    PantallaConBarra(
        titulo = "Dependencias",
        subtitulo = "Capacidad y tarifas (desde el servidor)",
        onVolver = onVolver,
        acciones = {
            IconButton(onClick = { viewModel.fetchDependencias() }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            MensajeError(error, modifier = Modifier.padding(16.dp))

            if (cargando) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.testTag("cargando"))
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(dependencias) { dependencia ->
                    TarjetaDependencia(dependencia)
                }
            }
        }
    }
}

@Composable
private fun TarjetaDependencia(dependencia: DependenciaDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Nombre: ${dependencia.nombre}", style = MaterialTheme.typography.titleMedium)
            Text("Capacidad: ${dependencia.capacidad} personas", style = MaterialTheme.typography.bodyMedium)
            Text(
                "Tarifa: ${Formatos.pesos(dependencia.tarifaPorBloque)} por bloque",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
