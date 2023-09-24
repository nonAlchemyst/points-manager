package com.example.points_manager.data

import com.example.points_manager.domain.models.Point
import com.example.points_manager.data.db.models.PointEntity

object Mapper {
    fun pointEntitiesToPoints(pointEntities: List<PointEntity>): List<Point> {
        return pointEntities.map { pointEntityToPoint(it) }
    }

    fun pointEntityToPoint(pointEntity: PointEntity): Point {
        return Point(pointEntity.id, pointEntity.latitude, pointEntity.longitude)
    }

    fun pointToPointEntity(point: Point): PointEntity {
        return PointEntity(point.latitude, point.longitude)
    }
}