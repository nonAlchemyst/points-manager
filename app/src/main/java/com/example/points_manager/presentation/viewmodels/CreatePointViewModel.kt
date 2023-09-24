package com.example.points_manager.presentation.viewmodels

import com.example.points_manager.domain.models.Point
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.points_manager.domain.usecases.SavePointUseCase
import com.example.points_manager.presentation.utils.CoordsValidation
import com.example.points_manager.presentation.utils.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreatePointViewModel(
    initLatitude: String?,
    initLongitude: String?,
    private val savePointUseCase: SavePointUseCase
): ViewModel() {

    private var _latitude = initLatitude ?: ""
    private var _longitude = initLongitude ?: ""

    private val _message = MutableLiveData(Event(""))
    val message = _message as LiveData<Event<String>>

    private val _needDismiss = MutableLiveData(Event(false))
    val needDismiss = _needDismiss as LiveData<Event<Boolean>>

    fun latitudeOnChange(value: String) {
        _latitude = value
    }

    fun longitudeOnChange(value: String) {
        _longitude = value
    }

    fun saveOnClick() {
        validateLatitudeOrError(_latitude) { latitude ->
            validateLongitudeOrError(_longitude) { longitude ->
                viewModelScope.launch {
                    savePointUseCase.invoke(Point(id = null, latitude, longitude))
                    _needDismiss.value = Event(true)
                }
            }
        }
    }

    private inline fun validateLatitudeOrError(latitude: String, block: (latitude: Double) -> Unit): Boolean {
        return latitude.toDoubleOrNull().let {
            if(CoordsValidation.checkLatitude(it)) {
                block.invoke(it!!)
                true
            } else {
                _message.value = Event("Широта введена неверно")
                false
            }
        }
    }

    private inline fun validateLongitudeOrError(longitude: String, block: (longitude: Double) -> Unit): Boolean {
        return longitude.toDoubleOrNull().let {
            if(CoordsValidation.checkLongitude(it)) {
                block.invoke(it!!)
                true
            } else {
                _message.value = Event("Широта введена неверно")
                false
            }
        }
    }

    class Factory (
        private val initLatitude: String?,
        private val initLongitude: String?,
        private val savePointUseCase: SavePointUseCase
    ): ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == CreatePointViewModel::class.java)
            return CreatePointViewModel(initLatitude, initLongitude, savePointUseCase) as T
        }

        class Factory1 @Inject constructor(
            private val savePointUseCase: SavePointUseCase
        ) {
            fun create(initLatitude: String?, initLongitude: String?): Factory {
                return Factory(initLatitude, initLongitude, savePointUseCase)
            }
        }
    }

}