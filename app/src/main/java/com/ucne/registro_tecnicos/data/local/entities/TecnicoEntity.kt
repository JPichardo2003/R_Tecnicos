package com.ucne.registro_tecnicos.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "Tecnicos")
data class TecnicoEntity(
    @PrimaryKey
    val tecnicoId: Int? = null,
    var nombre: String? = "",
    var sueldoHora: Double? = 0.0,
    var tipoId: Int? = null
)