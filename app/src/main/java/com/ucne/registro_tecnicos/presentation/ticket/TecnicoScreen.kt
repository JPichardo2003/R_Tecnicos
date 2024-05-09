package com.ucne.registro_tecnicos.presentation.ticket

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        }
    )
}

@Composable
fun TecnicoBody(onSaveTecnico: (TecnicoEntity) -> Unit) {
    var tecnicoId by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var sueldoHora by remember { mutableStateOf<Double?>(null) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {


            OutlinedTextField(
                label = { Text(text = "Persona") },
                value = nombre,
                onValueChange = { nombre = it },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Campo Nombre"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

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
                    }
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.icons8dollarblack),
                        contentDescription = "Sueldo por Hora"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

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
                        onSaveTecnico(
                            TecnicoEntity(
                                tecnicoId = tecnicoId.toIntOrNull(),
                                nombre = nombre,
                                sueldoHora = sueldoHora
                            )
                        )
                        tecnicoId = ""
                        nombre = ""
                        sueldoHora = null
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "save button"
                    )
                    Text(text = "Guardar")
                }
            }
        }

    }

}


@Preview
@Composable
private fun TecnicoPreview() {
    Registro_TecnicosTheme {
        TecnicoBody() {
        }
    }
}