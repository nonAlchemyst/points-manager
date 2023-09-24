package com.example.points_manager.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.points_manager.data.db.daoses.PointDao
import com.example.points_manager.data.db.models.PointEntity

@Database(entities = [PointEntity::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getPointDao(): PointDao
}