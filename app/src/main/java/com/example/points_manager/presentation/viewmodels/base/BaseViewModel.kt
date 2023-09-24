package com.example.points_manager.presentation.viewmodels.base

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.points_manager.presentation.utils.Event
import com.example.points_manager.presentation.navigation.NavigationCommand

open class BaseViewModel: ViewModel() {

    private val _navigation = MutableLiveData<Event<NavigationCommand>>()
    val navigation = _navigation as LiveData<Event<NavigationCommand>>

    fun navigate(@IdRes direction: Int, arguments: Bundle? = null) {
        _navigation.value = Event(NavigationCommand.ToDirection(direction, arguments))
    }

    fun navigateBack() {
        _navigation.value = Event(NavigationCommand.Back())
    }

    fun navigateBack(arguments: Bundle) {
        _navigation.value = Event(NavigationCommand.Back(arguments))
    }
}