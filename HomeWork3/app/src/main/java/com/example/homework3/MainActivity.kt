package com.example.homework3

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.homework3.model.UserMessage
import com.example.homework3.navigation.NavigationHolder
import com.example.homework3.navigation.NavigationIds
import com.example.homework3.service.MessageRepository
import com.example.homework3.service.NotificationService
import com.example.homework3.ui.screens.NotificationEditScreen
import com.example.homework3.ui.screens.NotificationSettingsScreen
import com.example.homework3.ui.screens.UserMessagesScreen
import com.example.homework3.ui.theme.HomeWork3Theme
import com.example.homework3.utils.Keys

class MainActivity : ComponentActivity() {
    private lateinit var notificationService: NotificationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        notificationService = NotificationService(this)
        notificationService.createNotificationChannels()

        handleNotificationIntent(intent)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (NavigationHolder.isUseCustomBackPressed()) {
                    NavigationHolder.popBackstack()
                }

                else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        setContent {
            HomeWork3Theme {
                NotificationApp(notificationService)
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent) {
        val title = intent.getStringExtra(Keys.Intent.NOTIFICATION_TITLE)
        val message = intent.getStringExtra(Keys.Intent.NOTIFICATION_MESSAGE)

        if (!title.isNullOrEmpty() || !message.isNullOrEmpty()) {
            if (!message.isNullOrEmpty()) {
                MessageRepository.addMessage(UserMessage(
                    getString(R.string.notification_prefix, message)
                ))
            }
            NavigationHolder.navigate(NavigationIds.USER_MESSAGES)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }
}

@Composable
fun NotificationApp(notificationService: NotificationService) {
    val currentDestination by NavigationHolder.currentDestination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, stringResource(R.string.settings)) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = currentDestination == NavigationIds.NOTIFICATION_SETTINGS,
                    onClick = { NavigationHolder.navigate(NavigationIds.NOTIFICATION_SETTINGS) }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Edit, stringResource(R.string.edit)) },
                    label = { Text(stringResource(R.string.edit)) },
                    selected = currentDestination == NavigationIds.NOTIFICATION_EDIT,
                    onClick = { NavigationHolder.navigate(NavigationIds.NOTIFICATION_EDIT) }
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Email, stringResource(R.string.messages)) },
                    label = { Text(stringResource(R.string.messages)) },
                    selected = currentDestination == NavigationIds.USER_MESSAGES,
                    onClick = { NavigationHolder.navigate(NavigationIds.USER_MESSAGES) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentDestination) {
                NavigationIds.NOTIFICATION_SETTINGS -> NotificationSettingsScreen(notificationService)
                NavigationIds.NOTIFICATION_EDIT -> NotificationEditScreen(notificationService)
                NavigationIds.USER_MESSAGES -> UserMessagesScreen()
            }
        }
    }
}