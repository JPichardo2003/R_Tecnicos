package com.ucne.registro_tecnicos.presentation.tecnico

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity
import com.ucne.registro_tecnicos.data.repository.TecnicoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TecnicoViewModel(private val repository: TecnicoRepository) : ViewModel() {

    val tecnicos = repository.getTecnicos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveTecnico(tecnico: TecnicoEntity) {
        viewModelScope.launch {
            repository.saveTecnico(tecnico)
        }
    }
    fun deleteTecnico(tecnico: TecnicoEntity) {
        viewModelScope.launch {
            repository.deleteTecnico(tecnico)
        }
    }
    fun nombreExists(nombre: String, id: Int?): Boolean {
        return tecnicos.value.any { it.nombre?.replace(" ", "")?.uppercase() == nombre.replace(" ", "").uppercase() && it.tecnicoId != id }
    }

    companion object {
        fun provideFactory(
            repository: TecnicoRepository
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return TecnicoViewModel(repository) as T
                }
            }
    }
}