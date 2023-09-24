package com.example.points_manager.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.points_manager.domain.models.Point
import com.example.points_manager.domain.usecases.GetPointsUseCase
import com.example.points_manager.presentation.viewmodels.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class PointsListViewModel (
    private val useCase: GetPointsUseCase
): BaseViewModel() {

    private val _points = MutableLiveData<List<Point>>()
    val points = _points as LiveData<List<Point>>

    fun observePoints() {
        viewModelScope.launch {
            useCase.invoke().collect { points ->
                _points.value = points
            }
        }
    }

    class Factory @Inject constructor(
        private val useCase: GetPointsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == PointsListViewModel::class.java)
            return PointsListViewModel(useCase) as T
        }
    }
}