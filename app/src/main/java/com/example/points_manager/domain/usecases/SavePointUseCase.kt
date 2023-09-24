package com.example.points_manager.domain.usecases

import com.example.points_manager.domain.Repository
import com.example.points_manager.domain.models.Point
import javax.inject.Inject

interface SavePointUseCase {
    suspend operator fun invoke(point: Point)
}

class SavePointUseCaseImpl @Inject constructor(private val repository: Repository): SavePointUseCase {
    override suspend fun invoke(point: Point) = repository.addPoint(point)
}