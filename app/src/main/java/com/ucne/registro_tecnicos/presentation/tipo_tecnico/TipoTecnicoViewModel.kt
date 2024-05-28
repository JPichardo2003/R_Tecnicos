package com.ucne.registro_tecnicos.presentation.tipo_tecnico

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

    val tipoTecnicos = repository.getTiposTecnicos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

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
        val descripcionError = when {
            descripcion.isEmpty() -> "Campo Obligatorio"
            descripcionExists(descripcion, uiState.value.tipoId) -> "Tipo técnico ya existe"
            else -> null
        }
        if (descripcion.matches(regex) && !descripcion.startsWith(" ")) {
            uiState.update {
                it.copy(
                    descripcion = descripcion,
                    descripcionError = descripcionError
                )
            }
        }
    }

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
        val descripcionEmpty = uiState.value.descripcion.isEmpty()
        val descripcionRepeated = descripcionExists(uiState.value.descripcion, uiState.value.tipoId)
        if (descripcionEmpty) {
            uiState.update { it.copy(descripcionError = "Campo Obligatorio.") }
        }
        if (descripcionRepeated) {
            uiState.update { it.copy(descripcionError = "Tipo técnico ya existe") }
        }
        return !descripcionEmpty && !descripcionRepeated
    }
    private fun descripcionExists(descripcion: String, id: Int?): Boolean {
        return tipoTecnicos.value.any { it.descripcion?.replace(" ", "")?.uppercase() == descripcion.replace(" ", "").uppercase() && it.tipoId != id }
    }
}

data class TipoTecnicoUIState(
    val tipoId: Int? = null,
    var descripcion: String = "",
    var descripcionError: String? = null,
    var saveSuccess: Boolean = false
)

fun TipoTecnicoUIState.toEntity(): TipoTecnicoEntity {
    return TipoTecnicoEntity(
        tipoId = tipoId,
        descripcion = descripcion
    )
}