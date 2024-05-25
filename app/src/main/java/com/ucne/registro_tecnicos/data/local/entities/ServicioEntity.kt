package com.ucne.registro_tecnicos.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "ServiciosTecnicos")
data class ServicioEntity(
    @PrimaryKey
    val servicioId: Int? = null,
    var fecha: String? = "",
    var tecnicoId: Int? = null,
    var cliente: String? = "",
    var descripcion: String? = "",
    var total: Double? = 0.0
)