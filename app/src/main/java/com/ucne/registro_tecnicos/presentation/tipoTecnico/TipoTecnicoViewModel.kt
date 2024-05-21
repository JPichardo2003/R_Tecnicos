package com.ucne.registro_tecnicos.presentation.tipoTecnico

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucne.registro_tecnicos.data.local.entities.TipoTecnicoEntity
import com.ucne.registro_tecnicos.data.repository.TipoTecnicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TipoTecnicoViewModel(private val repository: TipoTecnicoRepository, private val tipoId: Int)
    : ViewModel() {
    var uiState = MutableStateFlow(TipoTecnicoUIState())
        private set

    init{
        viewModelScope.launch {
            val tipoTecnico = repository.getTipoTecnico(tipoId)

            tipoTecnico?.let {
                uiState.update {
                    it.copy(
                        tipoId = tipoTecnico.tipoId,
                        descripcion = tipoTecnico.descripcion ?: ""
                    )
                }
            }
        }
    }

    fun onDescripcionChanged(descripcion: String){
        val regex = Regex("[\\p{L} ]*")
        if (descripcion.matches(regex) && !descripcion.startsWith(" ")) {
            uiState.update {
                it.copy(descripcion = descripcion)
            }
        }
    }

    val tipoTecnicos = repository.getTiposTecnicos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveTipoTecnico() {
        viewModelScope.launch {
            repository.saveTipoTecnico(uiState.value.toEntity())
            newTipoTecnico()
        }
    }
    fun deleteTipoTecnico() {
        viewModelScope.launch {
            repository.deleteTipoTecnico(uiState.value.toEntity())
        }
    }
    fun newTipoTecnico(){
        viewModelScope.launch {
            uiState.value = TipoTecnicoUIState()
        }
    }
    fun validation(): Boolean {
        uiState.value.descripcionEmpty = (uiState.value.descripcion.isEmpty())
        uiState.value.descripcionRepetida = descripcionExists(uiState.value.descripcion, uiState.value.tipoId)
        uiState.update {
            it.copy( saveSuccess =  !uiState.value.descripcionEmpty && !uiState.value.descripcionRepetida)
        }
        return uiState.value.saveSuccess
    }
    private fun descripcionExists(descripcion: String, id: Int?): Boolean {
        return tipoTecnicos.value.any { it.descripcion?.replace(" ", "")?.uppercase() == descripcion.replace(" ", "").uppercase() && it.tipoId != id }
    }
}

data class TipoTecnicoUIState(
    val tipoId: Int? = null,
    var descripcion: String = "",
    var descripcionEmpty: Boolean = false,
    var descripcionRepetida: Boolean = false,
    var saveSuccess: Boolean = false
)

fun TipoTecnicoUIState.toEntity(): TipoTecnicoEntity {
    return TipoTecnicoEntity(
        tipoId = tipoId,
        descripcion = descripcion
    )
}