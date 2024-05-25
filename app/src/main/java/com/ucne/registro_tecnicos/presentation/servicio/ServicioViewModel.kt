package com.ucne.registro_tecnicos.presentation.servicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucne.registro_tecnicos.data.local.entities.ServicioEntity
import com.ucne.registro_tecnicos.data.repository.ServicioRepository
import com.ucne.registro_tecnicos.data.repository.TecnicoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ServicioViewModel(
    private val repository: ServicioRepository,
    tecnicoRepository: TecnicoRepository,
    private val servicioId: Int
)
    : ViewModel() {
    var uiState = MutableStateFlow(ServicioUIState())
        private set

    val tecnicos = tecnicoRepository.getTecnicos()
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    val servicios = repository.getServicios()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init{
        viewModelScope.launch {
            val servicio = repository.getServicio(servicioId)

            servicio?.let {
                uiState.update {
                    it.copy(
                        servicioId = servicio.servicioId,
                        fecha = LocalDate.parse(servicio.fecha, DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        total = servicio.total,
                        tecnicoId = servicio.tecnicoId,
                        descripcion = servicio.descripcion ?: "",
                        cliente = servicio.cliente ?: ""
                    )
                }
            }
        }
    }

    fun onDescripcionChanged(descripcion: String){
        if (!descripcion.startsWith(" ")) {
            uiState.update {
                it.copy(descripcion = descripcion)
            }
        }
    }
    fun onClienteChanged(cliente: String){
        val regex = Regex("[\\p{L} ]*")
        if (cliente.matches(regex) && !cliente.startsWith(" ")) {
            uiState.update {
                it.copy(cliente = cliente)
            }
        }
    }

    fun onFechaChanged(fecha: LocalDate) {
        uiState.update {
            it.copy(fecha = fecha)
        }
    }
    fun onTotalChanged(totalStr: String) {
        val regex = Regex("[0-9]{0,7}\\.?[0-9]{0,2}")
        if (totalStr.matches(regex)) {
            val total = totalStr.toDoubleOrNull() ?: 0.0
            uiState.update {
                it.copy(
                    total = total
                )
            }
        }
    }
    fun onTecnicoChanged(tecnicoId: Int?) {
        uiState.update {
            it.copy(tecnicoId = tecnicoId)
        }
    }

    fun saveServicio() {
        viewModelScope.launch {
            repository.saveServicio(uiState.value.toEntity())
            newServicio()
        }
    }
    fun deleteServicio() {
        viewModelScope.launch {
            repository.deleteServicio(uiState.value.toEntity())
        }
    }
    fun newServicio() {
        viewModelScope.launch {
            uiState.value = ServicioUIState()
        }
    }
    fun getTecnicoNombre(tecnicoId: Int?): String {
        return tecnicos.value.find { it.tecnicoId == tecnicoId }?.nombre ?: ""
    }

    fun validation(): Boolean {
        uiState.value.descripcionEmpty = (uiState.value.descripcion.isEmpty())
        uiState.value.clienteEmpty = (uiState.value.cliente.isEmpty())
        uiState.value.totalEmpty = ((uiState.value.total ?: 0.0) <= 0.0)
        uiState.value.tecnicoEmpty = (uiState.value.tecnicoId == null)
        uiState.update {
            it.copy( saveSuccess =  !uiState.value.descripcionEmpty &&
                    !uiState.value.totalEmpty &&
                    !uiState.value.tecnicoEmpty &&
                    !uiState.value.clienteEmpty
            )
        }
        return uiState.value.saveSuccess
    }
}

data class ServicioUIState(
    val servicioId: Int? = null,
    var fecha: LocalDate = LocalDate.now(),
    var descripcion: String = "",
    var descripcionEmpty: Boolean = false,
    var cliente: String = "",
    var clienteEmpty: Boolean = false,
    var total: Double? = null,
    var totalEmpty: Boolean = false,
    var tecnicoId: Int? = null,
    var tecnicoEmpty: Boolean = false,
    var saveSuccess: Boolean = false,
)

fun ServicioUIState.toEntity(): ServicioEntity {
    return ServicioEntity(
        servicioId = servicioId,
        fecha = fecha.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
        descripcion = descripcion,
        cliente = cliente,
        total = total,
        tecnicoId = tecnicoId
    )
}