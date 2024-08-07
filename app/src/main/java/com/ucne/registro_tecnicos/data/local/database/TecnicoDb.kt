package com.ucne.registro_tecnicos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ucne.registro_tecnicos.data.local.dao.ServicioDao
import com.ucne.registro_tecnicos.data.local.dao.TecnicoDao
import com.ucne.registro_tecnicos.data.local.dao.TipoTecnicoDao
import com.ucne.registro_tecnicos.data.local.entities.ServicioEntity
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity
import com.ucne.registro_tecnicos.data.local.entities.TipoTecnicoEntity

@Database(
    entities = [
        TecnicoEntity::class,
        TipoTecnicoEntity::class,
        ServicioEntity::class
    ],
    version = 5,
    exportSchema = false
)

abstract class TecnicoDb : RoomDatabase() {
    abstract fun tecnicoDao(): TecnicoDao
    abstract fun tipoTecnicoDao(): TipoTecnicoDao
    abstract fun servicioDao(): ServicioDao


}