package com.ucne.registro_tecnicos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.ucne.registro_tecnicos.data.local.entities.ServicioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {
    @Upsert()
    suspend fun save(servicio: ServicioEntity)

    @Query(
        """
        SELECT * 
        FROM ServiciosTecnicos 
        WHERE servicioId=:id  
        LIMIT 1
        """
    )
    suspend fun find(id: Int): ServicioEntity?

    @Delete
    suspend fun delete(servicio: ServicioEntity)

    @Query("SELECT * FROM ServiciosTecnicos")
    fun getAll(): Flow<List<ServicioEntity>>
}