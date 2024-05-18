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

    init{
        viewModelScope.launch {
            val tecnico = repository.getTecnico(tecnicoId)

            tecnico?.let {
                uiState.update {
                    it.copy(
                        tecnicoId = tecnico.tecnicoId,
                        nombre = tecnico.nombre ?: "",
                        sueldoHora = tecnico.sueldoHora,
                        tipo = tecnico.tipo ?: ""
                    )
                }
            }
        }
    }

    fun onNombreChanged(nombre: String){
        val regex = Regex("[a-zA-Z ]*")
        if (nombre.matches(regex) && !nombre.startsWith(" ")) {
            uiState.update {
                it.copy(nombre = nombre)
            }
        }
    }
    fun onSueldoHoraChanged(sueldoHoraStr: String) {
        val regex = Regex("[0-9]*\\.?[0-9]{0,2}")
        if (sueldoHoraStr.matches(regex)) {
            val sueldoHora = if(sueldoHoraStr == "") null else sueldoHoraStr.toDoubleOrNull() ?: 0.0
            uiState.update {
                it.copy(
                    sueldoHora = sueldoHora,
                )
            }
        }
    }
    fun onTipoSelected(tipo: String) {
        uiState.value = uiState.value.copy(tipo = tipo)
    }

    val tecnicos = repository.getTecnicos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveTecnico() {
        viewModelScope.launch {
            repository.saveTecnico(uiState.value.toEntity())
        }
    }
    fun deleteTecnico() {
        viewModelScope.launch {
            repository.deleteTecnico(uiState.value.toEntity())
        }
    }
    fun nombreExists(nombre: String, id: Int?): Boolean {
        return tecnicos.value.any { it.nombre?.replace(" ", "")?.uppercase() == nombre.replace(" ", "").uppercase() && it.tecnicoId != id }
    }
}

data class TecnicoUIState(
    val tecnicoId: Int? = null,
    var nombre: String = "",
    var nombreError: String? = null,
    var sueldoHora: Double? = null,
    var sueldoHoraError: String? = null,
    var tipo: String = ""
)

fun TecnicoUIState.toEntity(): TecnicoEntity {
    return TecnicoEntity(
        tecnicoId = tecnicoId,
        nombre = nombre,
        sueldoHora = sueldoHora,
        tipo = tipo
    )
}