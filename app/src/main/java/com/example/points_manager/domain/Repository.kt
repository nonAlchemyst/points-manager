package com.example.points_manager.domain

import com.example.points_manager.domain.models.Point
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getPoints(): Flow<List<Point>>
    suspend fun addPoint(point: Point)


}