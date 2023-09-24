package com.example.points_manager.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.points_manager.presentation.utils.Event

class MainActivityViewModel: ViewModel() {

    private val _contextMenuAction = MutableLiveData<Event<ContextMenuItem>>()
    val contextMenuItem = _contextMenuAction as LiveData<Event<ContextMenuItem>>

    fun onContextMenuItemSelected(contextMenuItem: ContextMenuItem) {
        _contextMenuAction.value = Event(contextMenuItem)
    }

    enum class ContextMenuItem {
        SAVE_POINT, POINTS_LIST
    }
}