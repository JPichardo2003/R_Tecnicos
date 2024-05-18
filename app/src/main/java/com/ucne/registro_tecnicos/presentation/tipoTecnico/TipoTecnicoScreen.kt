package com.ucne.registro_tecnicos.presentation.tipoTecnico

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ucne.registro_tecnicos.Screen
import com.ucne.registro_tecnicos.presentation.components.NavigationDrawer
import com.ucne.registro_tecnicos.presentation.components.Notification
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

@Composable
fun TipoTecnicoScreen(
    viewModel: TipoTecnicoViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NavigationDrawer(navController = navController){
        TipoTecnicoBody(
            uiState = uiState,
            navController = navController,
            onDescripcionChanged = viewModel::onDescripcionChanged,
            onSaveTipoTecnico = {
                viewModel.saveTipoTecnico()
            },
            onDeleteTipoTecnico = {
                viewModel.deleteTipoTecnico()
            },
            onDescripcionExist = { descripcion, id: Int? ->
                viewModel.descripcionExists(descripcion, id)
            }
        )
    }
}
var descripcionVacio by mutableStateOf(false)
var descripcionRepetido by mutableStateOf(false)
@Composable
fun TipoTecnicoBody(
    uiState: TipoTecnicoUIState,
    onDescripcionChanged: (String) -> Unit,
    onDescripcionExist: (String, Int?) -> Boolean,
    onSaveTipoTecnico: () -> Unit,
    onDeleteTipoTecnico: () -> Unit,
    navController: NavHostController
) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(8.dp)
        ){
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
                    descripcionRepetido = onDescripcionExist(uiState.descripcion, uiState.tipoId)
                    if(descripcionRepetido){
                        Text(
                            text = "Tipo Tecnico ya existe.",
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
                                onDescripcionChanged("")
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
                                if (validar(uiState.descripcion) && !onDescripcionExist(uiState.descripcion, uiState.tipoId)) {
                                    onSaveTipoTecnico()
                                    uiState.descripcion = ""
                                    guardo = true
                                    descripcionVacio = false
                                    descripcionRepetido = false
                                    navController.navigate(Screen.TipoTecnicoList)
                                }
                                else{ errorGuardar = true}
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
                            title = { Text("Eliminar Tipo Tecnico") },
                            text = { Text("¿Está seguro de que desea eliminar este tipo tecnico?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteTipoTecnico()
                                        showDialog = false
                                        elimino = true
                                        navController.navigate(Screen.TipoTecnicoList)
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
private fun validar(descripcion: String) : Boolean {
    descripcionVacio = descripcion.isEmpty()
    return !descripcionVacio
}
@Preview
@Composable
private fun TipoTecnicoPreview() {
    Registro_TecnicosTheme {
        TipoTecnicoBody(
            uiState = TipoTecnicoUIState(),
            onDescripcionChanged =  {},
            onDescripcionExist = { _, _ -> false },
            onSaveTipoTecnico = {},
            onDeleteTipoTecnico = {},
            navController = NavHostController(LocalContext.current)
        )
    }
}