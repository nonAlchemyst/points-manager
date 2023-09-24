package com.example.points_manager.data.db.daoses

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.points_manager.data.db.models.PointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PointDao {

    @Query("SELECT * FROM PointEntity")
    fun loadPoints(): Flow<List<PointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pointEntity: PointEntity)

}