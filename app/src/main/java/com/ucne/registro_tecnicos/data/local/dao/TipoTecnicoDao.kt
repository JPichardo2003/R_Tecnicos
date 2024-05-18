package com.ucne.registro_tecnicos.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.ucne.registro_tecnicos.data.local.entities.TipoTecnicoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TipoTecnicoDao {
    @Upsert()
    suspend fun save(tipoTecnico: TipoTecnicoEntity)

    @Query(
        """
        SELECT * 
        FROM TiposTecnicos 
        WHERE tipoId=:id  
        LIMIT 1
        """
    )
    suspend fun find(id: Int): TipoTecnicoEntity?

    @Delete
    suspend fun delete(tipoTecnico: TipoTecnicoEntity)

    @Query("SELECT * FROM TiposTecnicos")
    fun getAll(): Flow<List<TipoTecnicoEntity>>
}