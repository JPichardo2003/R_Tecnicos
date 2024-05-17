package com.ucne.registro_tecnicos.presentation.tecnico

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ucne.registro_tecnicos.R
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

@Composable
fun TecnicoScreen(
    viewModel: TecnicoViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TecnicoBody(
        uiState = uiState,
        onNombreChanged = viewModel::onNombreChanged,
        onSueldoHoraChanged = viewModel::onSueldoHoraChanged,
        onSaveTecnico = {
            viewModel.saveTecnico()
        },
        onDeleteTecnico = {
            viewModel.deleteTecnico()
        },
        onNombreExist = {nombre, id: Int? ->
            viewModel.nombreExists(nombre, id)
        }
    )
}
var nombreVacio by mutableStateOf(false)
var nombreExtenso by mutableStateOf(false)
var nombreRepetido by mutableStateOf(false)
var sueldoHoraNoValido by mutableStateOf(false)
@Composable
fun TecnicoBody(
    uiState: TecnicoUIState,
    onNombreChanged: (String)->Unit,
    onSueldoHoraChanged: (String)->Unit,
    onNombreExist: (String, Int?) -> Boolean,
    onSaveTecnico: () -> Unit,
    onDeleteTecnico: () -> Unit
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
                        label = { Text(text = "Nombre") },
                        value = uiState.nombre,
                        onValueChange =  onNombreChanged,
                        isError = nombreExtenso || nombreVacio || nombreRepetido,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Campo Nombre"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    nombreRepetido = onNombreExist(uiState.nombre, uiState.tecnicoId)
                    if(nombreRepetido){
                        Text(
                            text = "Nombre ya existe.",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }
                    if(nombreVacio){
                        Text(
                            text = "Campo Obligatorio.",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }
                    if(nombreExtenso){
                        Text(
                            text = "Nombre debe ser menor a 30 caracteres.",
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
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        isError = sueldoHoraNoValido,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.icons8dollarblack),
                                contentDescription = "Sueldo por Hora"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if(sueldoHoraNoValido){
                        Text(
                            text = "Sueldo por Hora debe ser > 0.0",
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
                                onNombreChanged("")
                                onSueldoHoraChanged("")
                                nombreVacio = false
                                nombreExtenso = false
                                sueldoHoraNoValido = false
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
                                if (validar(uiState.nombre,uiState.sueldoHora) && !onNombreExist(uiState.nombre, uiState.tecnicoId)) {
                                    onSaveTecnico()
                                    guardo = true
                                    nombreVacio = false
                                    nombreExtenso = false
                                    sueldoHoraNoValido = false
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
                                showDialog = true
                                nombreVacio = false
                                nombreExtenso = false
                                sueldoHoraNoValido = false
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
                            title = { Text("Eliminar Tecnico") },
                            text = { Text("¿Está seguro de que desea eliminar este tecnico?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteTecnico()
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
                }
            }
        }
    }
}
@Composable
fun Notification(message: String) {
    Toast.makeText(LocalContext.current, message, Toast.LENGTH_LONG).show()
}
private fun validar(nombre: String, sueldoHora: Double?) : Boolean {
    nombreVacio = nombre.isEmpty()
    nombreExtenso = nombre.length > 30
    sueldoHoraNoValido = (sueldoHora ?: 0.0) <= 0.0
    return !nombreExtenso && !nombreVacio && !sueldoHoraNoValido
}
@Preview
@Composable
private fun TecnicoPreview() {
    Registro_TecnicosTheme {
        TecnicoBody(
            uiState = TecnicoUIState(),
            onSaveTecnico = {},
            onDeleteTecnico = {},
            onNombreChanged =  {},
            onSueldoHoraChanged = {},
            onNombreExist = { _, _ -> false }
        )
    }
}