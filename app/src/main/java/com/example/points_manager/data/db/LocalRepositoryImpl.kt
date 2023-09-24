package com.example.points_manager.data.db

import com.example.points_manager.data.db.daoses.PointDao
import com.example.points_manager.data.db.models.PointEntity
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(private val pointDao: PointDao) {
    fun getPoints() = pointDao.loadPoints()
    suspend fun addPoint(pointEntity: PointEntity) = pointDao.insert(pointEntity)
}