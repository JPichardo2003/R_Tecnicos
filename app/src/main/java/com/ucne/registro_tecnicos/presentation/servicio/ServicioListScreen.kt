package com.ucne.registro_tecnicos.presentation.servicio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucne.registro_tecnicos.data.local.entities.ServicioEntity
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity
import com.ucne.registro_tecnicos.presentation.components.FloatingButton
import com.ucne.registro_tecnicos.presentation.components.TopAppBar
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

@Composable
fun ServicioListScreen(
    viewModel: ServicioViewModel,
    onVerServicio: (ServicioEntity) -> Unit,
    onAddServicio: () -> Unit,
    openDrawer: () -> Unit
) {
    val servicios by viewModel.servicios.collectAsStateWithLifecycle()
    val tecnicos by viewModel.tecnicos.collectAsStateWithLifecycle()

    ServicioListBody(
        servicios = servicios,
        tecnicos = tecnicos,
        onVerServicio = onVerServicio,
        onVerTecnicoNombre = viewModel::getTecnicoNombre,
        onAddServicio = onAddServicio,
        openDrawer = openDrawer,
    )
}


@Composable
fun ServicioListBody(
    servicios: List<ServicioEntity>,
    tecnicos: List<TecnicoEntity>,
    onVerServicio: (ServicioEntity) -> Unit,
    onVerTecnicoNombre: (Int?) -> String,
    onAddServicio: () -> Unit,
    openDrawer: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(
            title = "Servicios",
            onMenuClick = openDrawer
        )},
        floatingActionButton = { FloatingButton(onAddServicio) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .padding(it)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ID", modifier = Modifier.weight(0.10f))
                Text(text = "Servicio", modifier = Modifier.weight(0.350f))
                Text(text = "Cliente", modifier = Modifier.weight(0.30f))
                Text(text = "Técnico", modifier = Modifier.weight(0.25f))
            }

            Divider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(servicios) { servicio ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onVerServicio(servicio) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = servicio.servicioId.toString(), modifier = Modifier.weight(0.12f))
                        Text(text = servicio.descripcion.toString(), modifier = Modifier.weight(0.450f))
                        Text(text = servicio.cliente .toString(), modifier = Modifier.weight(0.40f))
                        Text(text = onVerTecnicoNombre(servicio.tecnicoId), modifier = Modifier.weight(0.30f))
                    }
                }
            }
        }

    }
}
@Preview
@Composable
private fun ServicioListPreview() {
    val servicios = listOf(
        ServicioEntity(
            servicioId = 1,
            descripcion = "Cambio de RAM",
            total = 54.0,
            tecnicoId = 1,
            cliente = "Microsoft",
            fecha = "05/25/2024"
        )
    )
    Registro_TecnicosTheme {
        ServicioListBody(servicios = servicios,
            onVerServicio = {},
            onAddServicio = {},
            openDrawer = {},
            onVerTecnicoNombre = {"Pichardo"},
            tecnicos = listOf(
                TecnicoEntity(
                    tecnicoId = 1,
                    nombre = "William",
                    sueldoHora = 1000.0,
                    tipoId = 1
                )
            )
        )
    }
}
