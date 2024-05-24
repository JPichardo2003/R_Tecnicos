package com.ucne.registro_tecnicos.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDownInput(
    items: List<T>,
    label: String,
    itemToString: (T) -> String,
    onItemSelected: (T) -> Unit,
    itemToId: (T) -> Int?,
    selectedItemId: Int?,
    isError: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = items.find { itemToId(it) == selectedItemId }
    val selectedText = selectedItem?.let { itemToString(it) } ?: ""

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ){
        Column{
            OutlinedTextField(
                value = selectedText,
                readOnly = true,
                onValueChange = {},
                modifier = Modifier
                    .clickable { expanded = !expanded }
                    .menuAnchor().fillMaxWidth(),
                label = { Text(label) },
                trailingIcon = {
                    Icon(icon, contentDescription = "inputSelect",
                        Modifier.clickable { expanded = !expanded })
                },
                isError = isError
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        text = { Text(itemToString(item)) }
                    )
                }
            }
        }
    }

}