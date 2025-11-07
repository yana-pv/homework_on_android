package com.example.homework3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.homework3.R
import com.example.homework3.model.NotificationData
import com.example.homework3.model.NotificationPriority
import com.example.homework3.service.NotificationService
import com.example.homework3.utils.Dimens

@Composable
fun NotificationSettingsScreen(notificationService: NotificationService) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isExpandable by remember { mutableStateOf(false) }
    var selectedPriority by remember { mutableStateOf(NotificationPriority.MEDIUM) }
    var shouldOpenApp by remember { mutableStateOf(false) }
    var hasReplyAction by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.basePadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.defaultSpacing)
    ) {
        Text(
            text = stringResource(R.string.notification_settings),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                titleError = false
            },
            label = { Text(stringResource(R.string.title_required)) },
            isError = titleError,
            supportingText = {
                if (titleError) {
                    Text(
                        stringResource(R.string.error_empty_title),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = message,
            onValueChange = {
                message = it
                if (message.isEmpty()) isExpandable = false
            },
            label = { Text(stringResource(R.string.message)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = Dimens.textFieldMaxLines
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.expandable_notification))
            Switch(
                checked = isExpandable,
                onCheckedChange = { isExpandable = it },
                enabled = message.isNotEmpty()
            )
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.notification_priority),
                style = MaterialTheme.typography.bodyMedium
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = stringResource(selectedPriority.displayNameRes),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.select_priority)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { dropdownExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, null)
                        }
                    }
                )

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    NotificationPriority.values().forEach { priority ->
                        DropdownMenuItem(
                            text = { Text(stringResource(priority.displayNameRes)) },
                            onClick = {
                                selectedPriority = priority
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.open_app_on_click))
            Switch(
                checked = shouldOpenApp,
                onCheckedChange = { shouldOpenApp = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.add_reply_action))
            Switch(
                checked = hasReplyAction,
                onCheckedChange = { hasReplyAction = it }
            )
        }

        Spacer(modifier = Modifier.height(Dimens.mediumPadding))

        Button(
            onClick = {
                if (title.isBlank()) {
                    titleError = true
                    return@Button
                }

                val notificationData = NotificationData(
                    title = title,
                    message = message,
                    isExpandable = isExpandable,
                    priority = selectedPriority,
                    shouldOpenApp = shouldOpenApp,
                    hasReplyAction = hasReplyAction
                )

                notificationService.showNotification(notificationData)

                title = ""
                message = ""
                isExpandable = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonHeight),
            enabled = title.isNotEmpty()
        ) {
            Text(stringResource(R.string.create_notification))
        }
    }
}