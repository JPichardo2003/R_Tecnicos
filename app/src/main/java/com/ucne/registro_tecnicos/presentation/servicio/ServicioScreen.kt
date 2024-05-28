package com.ucne.registro_tecnicos.presentation.servicio

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity
import com.ucne.registro_tecnicos.presentation.components.DropDownInput
import com.ucne.registro_tecnicos.presentation.components.Notification
import com.ucne.registro_tecnicos.presentation.components.TopAppBar
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ServicioScreen(
    viewModel: ServicioViewModel,
    goBackServicioList: () -> Unit,
    openDrawer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tecnicos by viewModel.tecnicos.collectAsStateWithLifecycle(emptyList())
    viewModel.servicios.collectAsStateWithLifecycle()

    ServicioBody(
        uiState = uiState,
        tecnicos = tecnicos,
        onDescripcionChanged = viewModel::onDescripcionChanged,
        onClienteChanged = viewModel::onClienteChanged,
        onFechaChanged = viewModel::onFechaChanged,
        onTotalChanged = viewModel::onTotalChanged,
        onTecnicoChanged = viewModel::onTecnicoChanged,
        onValidation = viewModel::validation,
        goBackServicioList = goBackServicioList,
        openDrawer = openDrawer,
        onSaveServicio = {
            viewModel.saveServicio()
        },
        onDeleteServicio = {
            viewModel.deleteServicio()
        },
        onNewServicio = {
            viewModel.newServicio()
        }
    )
}

@Composable
fun ServicioBody(
    uiState: ServicioUIState,
    tecnicos: List<TecnicoEntity>,
    goBackServicioList: () -> Unit,
    openDrawer: () -> Unit,
    onDescripcionChanged: (String) -> Unit,
    onClienteChanged: (String) -> Unit,
    onFechaChanged: (LocalDate) -> Unit,
    onTotalChanged: (String) -> Unit,
    onTecnicoChanged: (Int) -> Unit,
    onSaveServicio: () -> Unit,
    onDeleteServicio: () -> Unit,
    onNewServicio: () -> Unit,
    onValidation: () -> Boolean
) {
    var guardo by remember { mutableStateOf(false) }
    var errorGuardar by remember { mutableStateOf(false) }
    var elimino by remember { mutableStateOf(false) }
    var errorEliminar by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false)}
    var selectedTecnico by remember { mutableStateOf<TecnicoEntity?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = "Registro Servicios",
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
                        label = { Text(text = "Fecha") },
                        value = uiState.fecha.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        readOnly = true,
                        onValueChange = { },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    showDatePicker = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date Picker"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { showDatePicker = true })
                    )

                    DropDownInput(
                        items = tecnicos,
                        label = "Técnico",
                        itemToString = { it.nombre ?: "" },
                        itemToId = {it.tecnicoId},
                        onItemSelected = {
                            selectedTecnico = it
                            onTecnicoChanged(it.tecnicoId ?: 0)
                        },
                        selectedItemId = uiState.tecnicoId,
                        isError = uiState.tecnicoIdError != null
                    )
                    if (uiState.tecnicoIdError != null) {
                        Text(
                            text = uiState.tecnicoIdError ?: "",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedTextField(
                        label = { Text(text = "Cliente") },
                        value = uiState.cliente,
                        onValueChange = onClienteChanged,
                        isError = uiState.clienteError != null,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Campo Cliente"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.clienteError != null) {
                        Text(
                            text = uiState.clienteError ?: "",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedTextField(
                        label = { Text(text = "Descripción") },
                        value = uiState.descripcion,
                        onValueChange = onDescripcionChanged,
                        isError = uiState.descripcionError != null,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Campo Descripción"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.descripcionError != null) {
                        Text(
                            text = uiState.descripcionError ?: "",
                            color = Color.Red,
                            fontStyle = FontStyle.Italic,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedTextField(
                        label = { Text(text = "Total") },
                        value = uiState.total.toString().replace("null", ""),
                        placeholder = { Text(text = "0.0") },
                        prefix = { Text(text = "$") },
                        onValueChange = onTotalChanged,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        isError = uiState.totalError != null,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.icons8dollarblack),
                                contentDescription = "Sueldo por Hora"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.totalError != null) {
                        Text(
                            text = uiState.totalError ?: "",
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
                                onNewServicio()
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
                                    onSaveServicio()
                                    guardo = true
                                    goBackServicioList()
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
                                if (uiState.servicioId != null) {
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
                            title = { Text("Eliminar Servicio") },
                            text = { Text("¿Está seguro de que desea eliminar este servicio?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        onDeleteServicio()
                                        showDialog = false
                                        elimino = true
                                        goBackServicioList()
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
                    if(showDatePicker){
                        val day: Int = uiState.fecha.dayOfMonth
                        val month: Int = uiState.fecha.monthValue - 1
                        val year: Int = uiState.fecha.year

                        val datePickerDialog = DatePickerDialog(
                            LocalContext.current, { _: DatePicker, year: Int, month: Int, day: Int ->
                                val selectedDate = LocalDate.of(year, month + 1, day)
                                onFechaChanged(selectedDate)
                            }, year, month, day
                        )
                        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                        datePickerDialog.show()
                        showDatePicker = false
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ServicioPreview() {
    Registro_TecnicosTheme {
        ServicioBody(
            uiState = ServicioUIState(),
            onDescripcionChanged = {},
            onTotalChanged = {},
            onTecnicoChanged = {},
            onValidation = { false },
            onSaveServicio = {},
            onDeleteServicio = {},
            onNewServicio = {},
            tecnicos = emptyList(),
            goBackServicioList = {},
            openDrawer = {},
            onClienteChanged = {},
            onFechaChanged = {}
        )
    }
}