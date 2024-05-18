package com.ucne.registro_tecnicos.data.repository

import com.ucne.registro_tecnicos.data.local.dao.TipoTecnicoDao
import com.ucne.registro_tecnicos.data.local.entities.TipoTecnicoEntity

class TipoTecnicoRepository(private val tipoTecnicoDao: TipoTecnicoDao) {
    suspend fun saveTipoTecnico(tipoTecnico: TipoTecnicoEntity) = tipoTecnicoDao.save(tipoTecnico)
    suspend fun deleteTipoTecnico(tipoTecnico: TipoTecnicoEntity) = tipoTecnicoDao.delete(tipoTecnico)
    fun getTiposTecnicos() = tipoTecnicoDao.getAll()
    suspend fun getTipoTecnico(id: Int) = tipoTecnicoDao.find(id)
}