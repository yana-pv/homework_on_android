package com.example.homework5.navigation

sealed class NavigationDestination {
    object Login : NavigationDestination()
    object Register : NavigationDestination()
    data class Events(val userId: Long) : NavigationDestination()
    data class AddEvent(val userId: Long) : NavigationDestination()
    data class Profile(val userId: Long, val username: String) : NavigationDestination()
    data class AccountRecovery(val userId: Long) : NavigationDestination()
}