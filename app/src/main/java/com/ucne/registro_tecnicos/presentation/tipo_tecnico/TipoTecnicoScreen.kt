package com.ucne.registro_tecnicos.presentation.tipo_tecnico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucne.registro_tecnicos.presentation.components.Notification
import com.ucne.registro_tecnicos.presentation.components.TopAppBar
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

@Composable
fun TipoTecnicoScreen(
    viewModel: TipoTecnicoViewModel,
    goBackTipoTecnicoList: () -> Unit,
    openDrawer: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    viewModel.tipoTecnicos.collectAsStateWithLifecycle()

    TipoTecnicoBody(
        uiState = uiState,
        onDescripcionChanged = viewModel::onDescripcionChanged,
        onValidation = viewModel::validation,
        openDrawer = openDrawer,
        goBackTipoTecnicoList = goBackTipoTecnicoList,
        onSaveTipoTecnico = {
            viewModel.saveTipoTecnico()
        },
        onDeleteTipoTecnico = {
            viewModel.deleteTipoTecnico()
        },
        onNewTipoTecnico = {
            viewModel.newTipoTecnico()
        }
    )
}
@Composable
fun TipoTecnicoBody(
    uiState: TipoTecnicoUIState,
    onDescripcionChanged: (String) -> Unit,
    onSaveTipoTecnico: () -> Unit,
    onDeleteTipoTecnico: () -> Unit,
    onNewTipoTecnico: () -> Unit,
    openDrawer: () -> Unit,
    goBackTipoTecnicoList: () -> Unit,
    onValidation: () -> Boolean,
) {
    var descripcionVacio by remember{mutableStateOf(false)}
    var descripcionRepetido by remember{mutableStateOf(false)}

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title="Registro TipoTécnicos",
                onMenuClick = openDrawer
            )
        }
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {

                    var guardo by remember { mutableStateOf(false) }
                    var errorGuardar by remember { mutableStateOf(false) }
                    var elimino by remember { mutableStateOf(false) }
                    var errorEliminar by remember { mutableStateOf(false) }
                    var showDialog by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        label = { Text(text = "Descripción") },
                        value = uiState.descripcion,
                        onValueChange =  onDescripcionChanged,
                        isError = descripcionRepetido || descripcionVacio,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Campo Descripción"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if(descripcionRepetido){
                        Text(
                            text = "Tipo Técnico ya existe.",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }
                    if(descripcionVacio){
                        Text(
                            text = "Campo Obligatorio.",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.padding(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = {
                                onNewTipoTecnico()
                                descripcionVacio = false
                                descripcionRepetido = false

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "new button"
                            )
                            Text(
                                text = "New"
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                if (onValidation()) {
                                    onSaveTipoTecnico()
                                    guardo = true
                                    descripcionVacio = false
                                    descripcionRepetido = false
                                    goBackTipoTecnicoList()
                                }
                                else{
                                    errorGuardar = true
                                    descripcionVacio = uiState.descripcionEmpty
                                    descripcionRepetido = uiState.descripcionRepetida
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "save button"
                            )
                            Text(text = "Save")
                        }

                        OutlinedButton(
                            onClick = {
                                if(uiState.tipoId != null){
                                    showDialog = true
                                    descripcionVacio = false
                                    descripcionRepetido = false
                                }else{
                                    errorEliminar = true
                                }

                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete button"
                            )
                            Text(text = "Delete")
                        }
                        if(elimino){
                            Notification("Eliminado Correctamente")
                            elimino = false
                        }
                        if(errorEliminar) {
                            Notification("Error al Eliminar")
                            errorEliminar = false
                        }

                        if(guardo){
                            Notification("Guardado Correctamente")
                            guardo = false
                        }
                        if(errorGuardar) {
                            Notification("Error al Guardar")
                            errorGuardar = false
                        }
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Eliminar Tipo Técnico") },
                            text = { Text("¿Está seguro de que desea eliminar este tipo técnico?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteTipoTecnico()
                                        showDialog = false
                                        elimino = true
                                        goBackTipoTecnicoList()
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
                }
            }
        }
    }
}


@Preview
@Composable
private fun TipoTecnicoPreview() {
    Registro_TecnicosTheme {
        TipoTecnicoBody(
            uiState = TipoTecnicoUIState(),
            onDescripcionChanged =  {},
            onSaveTipoTecnico = {},
            onDeleteTipoTecnico = {},
            onNewTipoTecnico = {},
            onValidation = { false },
            openDrawer = {},
            goBackTipoTecnicoList = {}
        )
    }
}