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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ucne.registro_tecnicos.R
import com.ucne.registro_tecnicos.Screen
import com.ucne.registro_tecnicos.data.local.entities.TipoTecnicoEntity
import com.ucne.registro_tecnicos.presentation.components.DropDownInput
import com.ucne.registro_tecnicos.presentation.components.NavigationDrawer
import com.ucne.registro_tecnicos.presentation.components.Notification
import com.ucne.registro_tecnicos.presentation.components.TopAppBar
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

@Composable
fun TecnicoScreen(
    viewModel: TecnicoViewModel,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tipos by viewModel.tipos.collectAsStateWithLifecycle(emptyList())
    val tecnicos by viewModel.tecnicos.collectAsStateWithLifecycle()

    NavigationDrawer(navController = navController){
        TecnicoBody(
            uiState = uiState,
            tipos = tipos,
            navController = navController,
            onNombreChanged = viewModel::onNombreChanged,
            onSueldoHoraChanged = viewModel::onSueldoHoraChanged,
            onTipoSelected = viewModel::onTipoSelected,
            onSaveTecnico = {
                viewModel.saveTecnico()
            },
            onDeleteTecnico = {
                viewModel.deleteTecnico()
            },
            onNombreExist = {nombre: String, id: Int? ->
                viewModel.nombreExists(nombre, id)
            }
        )
    }
}
var nombreVacio by mutableStateOf(false)
var nombreExtenso by mutableStateOf(false)
var nombreRepetido by mutableStateOf(false)
var sueldoHoraNoValido by mutableStateOf(false)
var sinTipo by mutableStateOf(false)
@Composable
fun TecnicoBody(
    uiState: TecnicoUIState,
    tipos: List<TipoTecnicoEntity>,
    onNombreChanged: (String) -> Unit,
    onSueldoHoraChanged: (String) -> Unit,
    onTipoSelected: (String) -> Unit,
    onNombreExist: (String, Int?) -> Boolean,
    onSaveTecnico: () -> Unit,
    onDeleteTecnico: () -> Unit,
    navController: NavHostController
) {
    var selectedTipo by remember { mutableStateOf<TipoTecnicoEntity?>(null) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar("Registro Técnicos")}
    ) {
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
                            text = "Nombre de técnico ya existe.",
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

                    DropDownInput(
                        items = tipos,
                        label = "Tipos Técnico",
                        itemToString = { it.descripcion ?: "" },
                        onItemSelected = {
                            selectedTipo = it
                            onTipoSelected(it.descripcion ?: "")
                        },
                        selectedItem = uiState.tipo ?: "",
                    )
                    if(sinTipo){
                        Text(
                            text = "Campo Obligatorio.",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic
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
                                onTipoSelected("")
                                nombreVacio = false
                                nombreExtenso = false
                                sueldoHoraNoValido = false
                                sinTipo = false
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
                                if (validar(uiState.nombre,uiState.sueldoHora,uiState.tipo) && !onNombreExist(uiState.nombre, uiState.tecnicoId)) {
                                    onSaveTecnico()
                                    onNombreChanged("")
                                    onSueldoHoraChanged("")
                                    onTipoSelected("")
                                    guardo = true
                                    nombreVacio = false
                                    nombreExtenso = false
                                    sueldoHoraNoValido = false
                                    sinTipo = false
                                    navController.navigate(Screen.TecnicoList)
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
                                if(uiState.tecnicoId != null){
                                    showDialog = true
                                    nombreVacio = false
                                    nombreExtenso = false
                                    sueldoHoraNoValido = false
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
                            title = { Text("Eliminar Técnico") },
                            text = { Text("¿Está seguro de que desea eliminar este técnico?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteTecnico()
                                        showDialog = false
                                        elimino = true
                                        nombreVacio = false
                                        nombreExtenso = false
                                        sueldoHoraNoValido = false
                                        sinTipo = false
                                        navController.navigate(Screen.TecnicoList)
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
private fun validar(nombre: String, sueldoHora: Double?, tipo: String) : Boolean {
    nombreVacio = nombre.isEmpty()
    nombreExtenso = nombre.length > 30
    sueldoHoraNoValido = (sueldoHora ?: 0.0) <= 0.0
    sinTipo = tipo.isEmpty()
    return !nombreExtenso && !nombreVacio && !sueldoHoraNoValido && !sinTipo
}
@Preview
@Composable
private fun TecnicoPreview() {
    Registro_TecnicosTheme {
        TecnicoBody(
            uiState = TecnicoUIState(),
            onNombreChanged =  {},
            onSueldoHoraChanged = {},
            onTipoSelected = {},
            onNombreExist = { _, _ -> false },
            onSaveTecnico = {},
            onDeleteTecnico = {},
            tipos = emptyList(),
            navController = NavHostController(LocalContext.current)
        )
    }
}