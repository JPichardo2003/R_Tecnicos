package com.ucne.registro_tecnicos.presentation.ticket

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

@Composable
fun TecnicoListScreen(
    viewModel: TecnicoViewModel,
    onVerTecnico: (TecnicoEntity) -> Unit
) {
    val tecnicos by viewModel.tecnicos.collectAsStateWithLifecycle()
    TecnicoListBody(
        tecnicos = tecnicos,
        onVerTecnico = onVerTecnico
    )
}
@Composable
fun TecnicoListBody(
    tecnicos: List<TecnicoEntity>,
    onVerTecnico: (TecnicoEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(tecnicos) { tecnico ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onVerTecnico(tecnico) }
                        .padding(16.dp)
                ) {
                    Text(text = tecnico.tecnicoId.toString(), modifier = Modifier.weight(0.10f))
                    Text(text = tecnico.nombre.toString(), modifier = Modifier.weight(0.400f))
                    Text(text = tecnico.sueldoHora.toString(), modifier = Modifier.weight(0.40f))
                }
            }
        }
    }
}

@Preview
@Composable
private fun TecnicoListPreview() {
    val tecnicos = listOf(
        TecnicoEntity(
            nombre = "Julio Pichardo",
            sueldoHora = 54.0
        )
    )
    Registro_TecnicosTheme {
        TecnicoListBody(tecnicos = tecnicos) {
        }
    }
}
