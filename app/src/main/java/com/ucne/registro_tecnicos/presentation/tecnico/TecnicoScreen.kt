package com.ucne.registro_tecnicos.presentation.tecnico

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.ucne.registro_tecnicos.ui.theme.Registro_TecnicosTheme

@Composable
fun TecnicoScreen(
    viewModel: TecnicoViewModel
) {
    val tecnicos by viewModel.tecnicos.collectAsStateWithLifecycle()
    TecnicoBody(
        onSaveTecnico = { tecnico ->
            viewModel.saveTecnico(tecnico)
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
fun TecnicoBody(onSaveTecnico: (TecnicoEntity) -> Unit, onNombreExist: (String, Int?) -> Boolean) {
    var tecnicoId by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var sueldoHora by remember { mutableStateOf<Double?>(null) }

    var guardo by remember { mutableStateOf(false) }
    var errorGuardar by remember { mutableStateOf(false) }

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
                value = nombre,
                onValueChange = { name ->
                    val regex = Regex("[a-zA-Z ]*")
                    if (name.matches(regex) && !name.startsWith(" ")) {
                        nombre = name
                        nombreVacio = nombre.isEmpty()
                    }
                },
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
            nombreRepetido = onNombreExist(nombre, tecnicoId.toIntOrNull())
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
                value = sueldoHora.toString().replace("null", ""),
                placeholder = { Text(text = "0.0") },
                prefix = { Text(text = "$") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                onValueChange = {
                    val regex = Regex("[0-9]*\\.?[0-9]{0,2}")
                    if (it.matches(regex)) {
                        sueldoHora = it.toDoubleOrNull() ?: 0.0
                        sueldoHoraNoValido = (sueldoHora ?: 0.0) <= 0.0
                    }
                },
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
                        tecnicoId = ""
                        nombre = ""
                        sueldoHora = null
                        nombreVacio = false
                        nombreExtenso = false
                        sueldoHoraNoValido = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "new button"
                    )
                    Text(text = "Nuevo")
                }
                OutlinedButton(
                    onClick = {
                        if (validar(nombre,sueldoHora) && !onNombreExist(nombre, tecnicoId.toIntOrNull())) {
                            onSaveTecnico(
                                TecnicoEntity(
                                    tecnicoId = tecnicoId.toIntOrNull(),
                                    nombre = nombre,
                                    sueldoHora = sueldoHora
                                )
                            )
                            guardo = true
                            tecnicoId = ""
                            nombre = ""
                            sueldoHora = null
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
                    Text(text = "Guardar")
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
            onSaveTecnico = {
            },
            onNombreExist = { _,_ -> false }
        )
    }
}