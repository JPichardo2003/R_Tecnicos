package com.ucne.registro_tecnicos.presentation.tecnico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity
import com.ucne.registro_tecnicos.data.repository.TecnicoRepository
import com.ucne.registro_tecnicos.data.repository.TipoTecnicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TecnicoViewModel(
    private val repository: TecnicoRepository,
    tipoRepository: TipoTecnicoRepository,
    private val tecnicoId: Int
)
    : ViewModel() {
    var uiState = MutableStateFlow(TecnicoUIState())
        private set

    val tipos = tipoRepository.getTiposTecnicos()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    val tecnicos = repository.getTecnicos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init{
        viewModelScope.launch {
            val tecnico = repository.getTecnico(tecnicoId)

            tecnico?.let {
                uiState.update {
                    it.copy(
                        tecnicoId = tecnico.tecnicoId,
                        nombre = tecnico.nombre ?: "",
                        sueldoHora = tecnico.sueldoHora,
                        tipoId = tecnico.tipoId
                    )
                }
            }
        }
    }

    fun onNombreChanged(nombre: String){
        val regex = Regex("[\\p{L} ]*")
        val nombreError: String? = when {
            nombre.isEmpty() -> "Campo Obligatorio."
            nombreExists(nombre, uiState.value.tecnicoId) -> "El nombre está repetido."
            else -> null
        }
        if (nombre.matches(regex) && !nombre.startsWith(" ")) {
            uiState.update {
                it.copy(
                    nombre = nombre,
                    nombreError = nombreError
                )
            }
        }
    }
    fun onSueldoHoraChanged(sueldoHoraStr: String) {
        val regex = Regex("[0-9]{0,7}\\.?[0-9]{0,2}")
        if (sueldoHoraStr.matches(regex)) {
            val sueldoHora = sueldoHoraStr.toDoubleOrNull() ?: 0.0
            uiState.update {
                it.copy(
                    sueldoHora = sueldoHora,
                    sueldoHoraError = null
                )
            }
        }
    }
    fun onTipoTecnicoChanged(tipoId: Int?) {
        uiState.update {
            it.copy(
                tipoId = tipoId,
                tipoError = null
            )
        }
    }

    fun saveTecnico() {
        viewModelScope.launch {
            repository.saveTecnico(uiState.value.toEntity())
            newTecnico()
        }
    }
    fun deleteTecnico() {
        viewModelScope.launch {
            repository.deleteTecnico(uiState.value.toEntity())
        }
    }
    fun newTecnico() {
        viewModelScope.launch {
            uiState.value = TecnicoUIState()
        }
    }
    private fun nombreExists(nombre: String, id: Int?): Boolean {
        return tecnicos.value.any { it.nombre?.replace(" ", "")?.uppercase() == nombre.replace(" ", "").uppercase() && it.tecnicoId != id }
    }

    fun getTipoDescripcion(tipoId: Int?): String {
        return tipos.value.find { it.tipoId == tipoId }?.descripcion ?: ""
    }

    fun validation(): Boolean {
        val nombreEmpty = uiState.value.nombre.isEmpty()
        val nombreRepeated = nombreExists(uiState.value.nombre, uiState.value.tecnicoId)
        val sueldoHoraEmpty = ((uiState.value.sueldoHora ?: 0.0) <= 0.0)
        val tipoEmpty = (uiState.value.tipoId == null) || (uiState.value.tipoId == 0)
        if (nombreEmpty) {
            uiState.update { it.copy(nombreError = "Campo Obligatorio.") }
        }
        if (nombreRepeated) {
            uiState.update { it.copy(nombreError = "El nombre está repetido.") }
        }
        if (sueldoHoraEmpty) {
            uiState.update { it.copy(sueldoHoraError = "Sueldo debe ser mayor que 0.0") }
        }
        if (tipoEmpty) {
            uiState.update { it.copy(tipoError = "Debe seleccionar un tipo técnico") }
        }
        return !nombreEmpty && !nombreRepeated && !sueldoHoraEmpty && !tipoEmpty
    }
}

data class TecnicoUIState(
    val tecnicoId: Int? = null,
    var nombre: String = "",
    var nombreError: String? = null,
    var sueldoHora: Double? = null,
    var sueldoHoraError: String? = null,
    var tipoId: Int? = null,
    var tipoError: String? = null,
)

fun TecnicoUIState.toEntity(): TecnicoEntity {
    return TecnicoEntity(
        tecnicoId = tecnicoId,
        nombre = nombre,
        sueldoHora = sueldoHora,
        tipoId = tipoId
    )
}