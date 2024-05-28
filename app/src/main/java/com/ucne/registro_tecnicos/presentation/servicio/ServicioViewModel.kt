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
                it.copy(
                    descripcion = descripcion,
                    descripcionError = null
                )
            }
        }
    }
    fun onClienteChanged(cliente: String){
        val regex = Regex("[\\p{L} ]*")
        if (cliente.matches(regex) && !cliente.startsWith(" ")) {
            uiState.update {
                it.copy(
                    cliente = cliente,
                    clienteError = null
                )
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
                    total = total,
                    totalError = null
                )
            }
        }
    }
    fun onTecnicoChanged(tecnicoId: Int?) {
        uiState.update {
            it.copy(tecnicoId = tecnicoId, tecnicoIdError = null)
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
        val descripcionEmpty = uiState.value.descripcion.isEmpty()
        val clienteEmpty = uiState.value.cliente.isEmpty()
        val totalEmpty = (uiState.value.total ?: 0.0) <= 0.0
        val tecnicoIdEmpty = uiState.value.tecnicoId == null
        if (descripcionEmpty) {
            uiState.update { it.copy(descripcionError = "Campo Obligatorio") }
        }
        if (clienteEmpty) {
            uiState.update { it.copy(clienteError = "Campo Obligatorio") }
        }
        if (totalEmpty) {
            uiState.update { it.copy(totalError = "Debe ingresar un total") }
        }
        if (tecnicoIdEmpty) {
            uiState.update { it.copy(tecnicoIdError = "Debe seleccionar un tecnico")}
        }
        return !descripcionEmpty && !clienteEmpty && !totalEmpty && !tecnicoIdEmpty
    }
}

data class ServicioUIState(
    val servicioId: Int? = null,
    var fecha: LocalDate = LocalDate.now(),
    var descripcion: String = "",
    var descripcionError: String? = null,
    var cliente: String = "",
    var clienteError: String? = null,
    var total: Double? = null,
    var totalError: String? = null,
    var tecnicoId: Int? = null,
    var tecnicoIdError: String? = null,
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