package com.example.homework5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.homework5.di.ServiceLocator
import com.example.homework5.navigation.NavigationDestination
import com.example.homework5.screens.auth.*
import com.example.homework5.screens.events.AddEventScreen
import com.example.homework5.screens.events.EventsScreen
import com.example.homework5.screens.profile.ProfileScreen
import com.example.homework5.ui.theme.Homework5Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ServiceLocator.init(applicationContext)

        setContent {
            Homework5Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EventCalendarApp()
                }
            }
        }
    }
}

@Composable
fun EventCalendarApp() {
    var currentDestination by remember { mutableStateOf<NavigationDestination>(NavigationDestination.Login) }
    var userId by remember { mutableStateOf<Long?>(null) }
    var username by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val userRepository = ServiceLocator.getUserRepository()
    val eventRepository = ServiceLocator.getEventRepository()

    LaunchedEffect(Unit) {
        val isLoggedIn = userRepository.isLoggedIn()
        if (isLoggedIn) {
            userId = userRepository.getCurrentUserId()
            username = userRepository.getCurrentUsername()
            userId?.let {
                currentDestination = NavigationDestination.Events(it)
            }
        }
    }

    when (val destination = currentDestination) {
        is NavigationDestination.Login -> {
            LoginScreen(
                onNavigate = { newDestination ->
                    when (newDestination) {
                        is NavigationDestination.Register -> {
                            currentDestination = newDestination
                        }
                        is NavigationDestination.AccountRecovery -> {
                            currentDestination = newDestination
                        }
                        else -> {}
                    }
                },
                onLoginSuccess = {
                    coroutineScope.launch {
                        userId = userRepository.getCurrentUserId()
                        username = userRepository.getCurrentUsername()
                        userId?.let {
                            currentDestination = NavigationDestination.Events(it)
                        }
                    }
                }
            )
        }

        is NavigationDestination.Register -> {
            RegisterScreen(
                onNavigate = { newDestination ->
                    when (newDestination) {
                        is NavigationDestination.Login -> {
                            currentDestination = newDestination
                        }
                        else -> {}
                    }
                },
                onRegisterSuccess = {
                    coroutineScope.launch {
                        userId = userRepository.getCurrentUserId()
                        username = userRepository.getCurrentUsername()
                        userId?.let {
                            currentDestination = NavigationDestination.Events(it)
                        }
                    }
                }
            )
        }

        is NavigationDestination.Events -> {
            EventsScreen(
                userId = destination.userId,
                onNavigate = { newDestination ->
                    when (newDestination) {
                        is NavigationDestination.AddEvent -> {
                            currentDestination = newDestination
                        }
                        is NavigationDestination.Profile -> {
                            username?.let { name ->
                                currentDestination = NavigationDestination.Profile(destination.userId, name)
                            }
                        }
                        else -> {
                        }
                    }
                },
                onEventDeleted = {}
            )
        }

        is NavigationDestination.AddEvent -> {
            AddEventScreen(
                userId = destination.userId,
                onBack = {
                    currentDestination = NavigationDestination.Events(destination.userId)
                },
                onEventAdded = {}
            )
        }

        is NavigationDestination.Profile -> {
            ProfileScreen(
                userId = destination.userId,
                username = destination.username,
                onBack = {
                    currentDestination = NavigationDestination.Events(destination.userId)
                },
                onLogout = {
                    coroutineScope.launch {
                        userRepository.logout()
                        currentDestination = NavigationDestination.Login
                    }
                },
                onAccountDeleted = {
                    currentDestination = NavigationDestination.Login
                }
            )
        }

        is NavigationDestination.AccountRecovery -> {
            AccountRecoveryScreen(
                userId = destination.userId,
                onNavigate = { newDestination ->
                    when (newDestination) {
                        is NavigationDestination.Login -> {
                            currentDestination = newDestination
                        }
                        else -> {}
                    }
                },
                onRecoveryComplete = {
                    coroutineScope.launch {
                        userId = userRepository.getCurrentUserId()
                        username = userRepository.getCurrentUsername()
                        userId?.let {
                            currentDestination = NavigationDestination.Events(it)
                        }
                    }
                },
                onPermanentDelete = {
                    currentDestination = NavigationDestination.Login
                }
            )
        }
    }
}