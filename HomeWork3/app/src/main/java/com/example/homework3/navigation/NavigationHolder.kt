package com.example.homework3.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object NavigationHolder {
    private val navigationBackstack = mutableListOf(NavigationIds.NOTIFICATION_SETTINGS)
    private val _currentDestination = mutableStateOf(NavigationIds.NOTIFICATION_SETTINGS)
    val currentDestination: State<NavigationIds> = _currentDestination

    fun navigate(destination: NavigationIds) {
        _currentDestination.value = destination
        navigationBackstack.add(destination)
    }

    fun isUseCustomBackPressed(): Boolean {
        return navigationBackstack.size > 1
    }

    fun popBackstack() {
        if (navigationBackstack.size >= 2) {
            val previousDestination = navigationBackstack[navigationBackstack.size - 2]
            _currentDestination.value = previousDestination
            navigationBackstack.removeAt(navigationBackstack.size - 1)
        }
    }
}