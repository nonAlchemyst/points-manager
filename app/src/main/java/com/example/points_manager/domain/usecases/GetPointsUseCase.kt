package com.example.points_manager.domain.usecases

import com.example.points_manager.domain.Repository
import com.example.points_manager.domain.models.Point
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetPointsUseCase {
    operator fun invoke(): Flow<List<Point>>
}

class GetPointsUseCaseImpl @Inject constructor(
    private val repository: Repository
): GetPointsUseCase {
    override operator fun invoke() =
        repository.getPoints()
}