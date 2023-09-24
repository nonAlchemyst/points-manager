package com.example.points_manager.presentation.navigation

import android.os.Bundle
import androidx.annotation.IdRes

sealed class NavigationCommand {
    data class ToDirection(@IdRes val directions: Int, val arguments: Bundle? = null): NavigationCommand()
    data class Back(val arguments: Bundle? = null): NavigationCommand()
}