package com.example.homework3.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.homework3.R
import com.example.homework3.service.MessageRepository
import com.example.homework3.utils.Dimens
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UserMessagesScreen() {
    var newMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    val messagesState = remember {
        mutableStateOf(MessageRepository.getMessagesState())
    }
    val messages by messagesState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.basePadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.user_messages),
                style = MaterialTheme.typography.headlineSmall
            )

            if (messages.isNotEmpty()) {
                IconButton(onClick = {
                    MessageRepository.clearMessages()
                    messagesState.value = emptyList()
                }) {
                    Icon(Icons.Default.Delete, stringResource(R.string.clear_all))
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.mediumPadding))

        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_messages))
            }
        }

        else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = Dimens.defaultSpacing),
                verticalArrangement = Arrangement.spacedBy(Dimens.smallSpacing),
                reverseLayout = true
            ) {
                items(messages.reversed()) { message ->
                    MessageItem(message = message)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.smallSpacing)
        ) {
            OutlinedTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                placeholder = { Text(stringResource(R.string.type_your_message)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            Button(
                onClick = {
                    if (newMessage.isNotEmpty()) {
                        MessageRepository.addMessage(
                            com.example.homework3.model.UserMessage(newMessage)
                        )
                        messagesState.value = MessageRepository.getMessagesState()
                        newMessage = ""
                    }
                },
                enabled = newMessage.isNotEmpty()
            ) {
                Text(stringResource(R.string.send))
            }
        }
    }
}

@Composable
fun MessageItem(message: com.example.homework3.model.UserMessage) {
    val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.smallPadding)
    ) {
        Column(modifier = Modifier.padding(Dimens.mediumPadding)) {
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(Dimens.smallPadding))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormat.format(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (message.isFromReply) {
                    Text(
                        text = stringResource(R.string.from_reply),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}