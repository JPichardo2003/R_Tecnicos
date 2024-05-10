package com.ucne.registro_tecnicos.data.repository

import com.ucne.registro_tecnicos.data.local.dao.TecnicoDao
import com.ucne.registro_tecnicos.data.local.entities.TecnicoEntity

class TecnicoRepository(private val tecnicoDao: TecnicoDao) {
    suspend fun saveTecnico(tecnico: TecnicoEntity) = tecnicoDao.save(tecnico)
    suspend fun deleteTecnico(tecnico: TecnicoEntity) = tecnicoDao.delete(tecnico)
    fun getTecnicos() = tecnicoDao.getAll()
}