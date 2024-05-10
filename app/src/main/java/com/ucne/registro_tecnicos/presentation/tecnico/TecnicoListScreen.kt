package com.ucne.registro_tecnicos.presentation.tecnico

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
        onVerTecnico = onVerTecnico,
        onDeleteTecnico = {  tecnico ->
            viewModel.deleteTecnico(tecnico)}
    )
}
@Composable
fun TecnicoListBody(
    tecnicos: List<TecnicoEntity>,
    onVerTecnico: (TecnicoEntity) -> Unit,
    onDeleteTecnico: (TecnicoEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var tecnicoToDelete by remember { mutableStateOf<TecnicoEntity?>(null) }
    var elimino by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "ID", modifier = Modifier.weight(0.06f))
            Text(text = "Nombre", modifier = Modifier.weight(0.200f))
            Text(text = "SueldoHora", modifier = Modifier.weight(0.25f))
            Spacer(modifier = Modifier.weight(0.05f)) // Espacio adicional para el icono de basura
        }

        Divider()

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
                    Text(text = tecnico.nombre.toString(), modifier = Modifier.weight(0.370f))
                    Text(text = tecnico.sueldoHora.toString(), modifier = Modifier.weight(0.40f))

                    IconButton(
                        onClick = { tecnicoToDelete = tecnico
                            showDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Tecnico"
                        )
                    }
                }
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Eliminar Tecnico") },
            text = { Text("¿Está seguro de que desea eliminar este tecnico?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTecnico(tecnicoToDelete!!)
                        showDialog = false
                        elimino = true
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    if(elimino){
        Notification("Tecnico Eliminado")
        elimino = false
    }
}
@Preview
@Composable
private fun TecnicoListPreview() {
    val tecnicos = listOf(
        TecnicoEntity(
            tecnicoId = 1,
            nombre = "Julio Pichardo",
            sueldoHora = 54.0
        )
    )
    Registro_TecnicosTheme {
        TecnicoListBody(tecnicos = tecnicos,
            onVerTecnico = {},
            ) {
        }
    }
}
