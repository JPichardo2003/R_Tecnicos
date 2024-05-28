package com.ucne.registro_tecnicos.presentation.tecnico

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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucne.registro_tecnicos.R
import com.ucne.registro_tecnicos.data.local.entities.TipoTecnicoEntity
import com.ucne.registro_tecnicos.presentation.components.DropDownInput
import com.ucne.registro_tecnicos.presentation.components.Notification
import com.ucne.registro_tecnicos.presentation.components.TopAppBar
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

@Composable
fun TecnicoScreen(
    viewModel: TecnicoViewModel,
    goBack: () -> Unit,
    openDrawer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tipos by viewModel.tipos.collectAsStateWithLifecycle(emptyList())
    viewModel.tecnicos.collectAsStateWithLifecycle()

    TecnicoBody(
        uiState = uiState,
        tipos = tipos,
        onNombreChanged = viewModel::onNombreChanged,
        onSueldoHoraChanged = viewModel::onSueldoHoraChanged,
        onTipoTecnicoChanged = viewModel::onTipoTecnicoChanged,
        onValidation = viewModel::validation,
        goBack = goBack,
        openDrawer = openDrawer,
        onSaveTecnico = {
            viewModel.saveTecnico()
        },
        onDeleteTecnico = {
            viewModel.deleteTecnico()
        },
        onNewTecnico = {
            viewModel.newTecnico()
        }
    )
}

@Composable
fun TecnicoBody(
    uiState: TecnicoUIState,
    tipos: List<TipoTecnicoEntity>,
    goBack: () -> Unit,
    openDrawer: () -> Unit,
    onNombreChanged: (String) -> Unit,
    onSueldoHoraChanged: (String) -> Unit,
    onTipoTecnicoChanged: (Int) -> Unit,
    onSaveTecnico: () -> Unit,
    onDeleteTecnico: () -> Unit,
    onNewTecnico: () -> Unit,
    onValidation: () -> Boolean
) {
    var guardo by remember { mutableStateOf(false) }
    var errorGuardar by remember { mutableStateOf(false) }
    var elimino by remember { mutableStateOf(false) }
    var errorEliminar by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTipo by remember { mutableStateOf<TipoTecnicoEntity?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = "Registro Técnicos",
                onMenuClick = openDrawer
            )
        }
    ) {
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
                    OutlinedTextField(
                        label = { Text(text = "Nombre") },
                        value = uiState.nombre,
                        onValueChange = onNombreChanged,
                        isError = (uiState.nombreError != null),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Campo Nombre"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if(uiState.nombreError != null){
                        Text(
                            text = uiState.nombreError ?: "",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedTextField(
                        label = { Text(text = "Sueldo Hora") },
                        value = uiState.sueldoHora.toString().replace("null", ""),
                        placeholder = { Text(text = "0.0") },
                        prefix = { Text(text = "$") },
                        onValueChange = onSueldoHoraChanged,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        isError = uiState.sueldoHoraError != null,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.icons8dollarblack),
                                contentDescription = "Sueldo por Hora"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.sueldoHoraError != null) {
                        Text(
                            text = uiState.sueldoHoraError ?: "",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }

                    DropDownInput(
                        items = tipos,
                        label = "Tipos Técnico",
                        itemToString = { it.descripcion ?: "" },
                        itemToId = {it.tipoId},
                        onItemSelected = {
                            selectedTipo = it
                            onTipoTecnicoChanged(it.tipoId ?: 0)
                        },
                        selectedItemId = uiState.tipoId,
                        isError = uiState.tipoError != null,
                    )
                    if (uiState.tipoError != null) {
                        Text(
                            text = uiState.tipoError ?: "",
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
                                onNewTecnico()
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
                                    onSaveTecnico()
                                    guardo = true
                                    goBack()
                                } else {
                                    errorGuardar = true
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
                                if (uiState.tecnicoId != null) {
                                    showDialog = true
                                } else {
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
                        if (elimino) {
                            Notification("Eliminado Correctamente")
                            elimino = false
                        }
                        if (errorEliminar) {
                            Notification("Error al Eliminar")
                            errorEliminar = false
                        }
                        if (guardo) {
                            Notification("Guardado Correctamente")
                            guardo = false
                        }
                        if (errorGuardar) {
                            Notification("Error al Guardar")
                            errorGuardar = false
                        }
                    }
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = { Text("Eliminar Técnico") },
                            text = { Text("¿Está seguro de que desea eliminar este técnico?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteTecnico()
                                        showDialog = false
                                        elimino = true
                                        goBack()
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
private fun TecnicoPreview() {
    Registro_TecnicosTheme {
        TecnicoBody(
            uiState = TecnicoUIState(),
            onNombreChanged = {},
            onSueldoHoraChanged = {},
            onTipoTecnicoChanged = {},
            onValidation = { false },
            onSaveTecnico = {},
            onDeleteTecnico = {},
            onNewTecnico = {},
            tipos = emptyList(),
            goBack = {},
            openDrawer = {}
        )
    }
}