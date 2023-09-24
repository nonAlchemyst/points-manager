package com.example.points_manager.data.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PointEntity")
data class PointEntity(
    val latitude: Double,
    val longitude: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
