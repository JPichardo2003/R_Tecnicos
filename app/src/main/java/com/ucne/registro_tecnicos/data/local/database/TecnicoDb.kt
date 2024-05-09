package com.ucne.registro_tecnicos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ucne.registro_tecnicos.data.local.dao.TecnicoDao
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity

@Database(
    entities = [
        TecnicoEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class TecnicoDb : RoomDatabase() {
    abstract fun tecnicoDao(): TecnicoDao
}