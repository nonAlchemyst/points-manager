package com.example.points_manager.presentation.viewmodels

import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.points_manager.R
import com.example.points_manager.domain.models.Point
import com.example.points_manager.domain.usecases.GetPointsUseCase
import com.example.points_manager.presentation.dialogs.CreatePointDialogFragment
import com.example.points_manager.presentation.viewmodels.base.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MapViewModel(
    private val useCase: GetPointsUseCase
): BaseViewModel() {

    private var pointIdForAnimate: Int? = null
    private val _points = MutableLiveData<List<Point>>()
    val points = _points as LiveData<List<Point>>

    fun observePoints() {
        viewModelScope.launch {
            useCase.invoke().shareIn(viewModelScope, SharingStarted.Lazily, 1).collect { points ->
                _points.value = points
            }
        }
    }

    fun setPointId(pointId: Int) {
        pointIdForAnimate = pointId
    }

    fun getPointId(): Int? {
        val pointId = pointIdForAnimate
        pointIdForAnimate = null
        return pointId
    }

    fun navigateToPointsList() {
        navigate(R.id.action_mapFragment_to_pointsFragment)
    }

    fun navigateToDialog(point: Point) {
        navigate(R.id.action_mapFragment_to_createPointDialogFragment, bundleOf(
            CreatePointDialogFragment.ARG_LATITUDE to point.latitude.toString().trySubstring(0, 11),
            CreatePointDialogFragment.ARG_LONGITUDE to point.longitude.toString().trySubstring(0, 11)
        ))
    }

    class Factory @Inject constructor(
        private val useCase: GetPointsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == MapViewModel::class.java)
            return MapViewModel(useCase) as T
        }
    }

    fun String.trySubstring(startIndex: Int, endIndex: Int): String {
        val end = if(this.lastIndex > endIndex) {
            endIndex
        } else {
            this.lastIndex
        }
        return this.substring(startIndex, end)
    }
}