package com.ucne.registro_tecnicos.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "TiposTecnicos")
data class TipoTecnicoEntity(
    @PrimaryKey
    val tipoId: Int? = null,
    var descripcion: String? = ""
)