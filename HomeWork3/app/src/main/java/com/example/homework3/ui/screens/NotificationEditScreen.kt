package com.example.homework3.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.homework3.R
import com.example.homework3.service.NotificationService
import com.example.homework3.utils.Dimens

@Composable
fun NotificationEditScreen(notificationService: NotificationService) {
    var notificationId by remember { mutableStateOf("") }
    var newMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.basePadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.defaultSpacing)
    ) {
        Text(
            text = stringResource(R.string.edit_notification),
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = notificationId,
            onValueChange = { notificationId = it },
            label = { Text(stringResource(R.string.notification_id)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.enter_notification_id)) },
            singleLine = true
        )

        OutlinedTextField(
            value = newMessage,
            onValueChange = { newMessage = it },
            label = { Text(stringResource(R.string.new_message)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = Dimens.textFieldMaxLines
        )

        Button(
            onClick = {
                val id = notificationId.toIntOrNull()
                if (id == null) {
                    Toast.makeText(context, context.getString(R.string.error_enter_valid_id), Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val success = notificationService.updateNotification(id, newMessage)
                if (success) {
                    Toast.makeText(context, context.getString(R.string.notification_updated), Toast.LENGTH_SHORT).show()
                    newMessage = ""
                }
                else {
                    Toast.makeText(context, context.getString(R.string.error_notification_not_found), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonHeight),
            enabled = notificationId.isNotEmpty() && newMessage.isNotEmpty()
        ) {
            Text(stringResource(R.string.update_notification))
        }

        Divider(modifier = Modifier.padding(vertical = Dimens.mediumPadding))

        Button(
            onClick = {
                if (notificationService.hasActiveNotifications()) {
                    notificationService.dismissAllNotifications()
                    Toast.makeText(context, context.getString(R.string.all_notifications_dismissed), Toast.LENGTH_SHORT).show()
                }

                else {
                    Toast.makeText(context, context.getString(R.string.error_no_notifications), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text(stringResource(R.string.dismiss_all_notifications))
        }
    }
}