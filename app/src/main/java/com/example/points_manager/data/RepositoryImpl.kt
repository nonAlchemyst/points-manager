package com.example.points_manager.data

import android.util.Log
import com.example.points_manager.data.db.LocalRepositoryImpl
import com.example.points_manager.domain.Repository
import com.example.points_manager.domain.models.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(private val localRepositoryImpl: LocalRepositoryImpl): Repository {
    override fun getPoints() = localRepositoryImpl.getPoints().map {
        Log.d("TAG", "points were mapped")
        Mapper.pointEntitiesToPoints(it)
    }


    override suspend fun addPoint(point: Point) = withContext(Dispatchers.IO){
        localRepositoryImpl.addPoint(Mapper.pointToPointEntity(point))
    }

}
