package com.example.homework5.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.homework5.R
import com.example.homework5.di.ServiceLocator
import com.example.homework5.navigation.NavigationDestination
import com.example.homework5.screens.LoadingDialog
import com.example.homework5.utils.Constants
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AccountRecoveryScreen(
    userId: Long,
    onNavigate: (NavigationDestination) -> Unit,
    onRecoveryComplete: () -> Unit,
    onPermanentDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val userRepository = ServiceLocator.getUserRepository()

    var isLoading by remember { mutableStateOf(false) }
    var userInfo by remember { mutableStateOf<String?>(null) }
    var userDeletedDate by remember { mutableStateOf<Date?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(userId) {
        if (userId > 0) {
            val user = userRepository.getUserById(userId)
            user?.let {
                userDeletedDate = it.deletedAt
                val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale("ru"))
                userInfo = formatter.format(it.deletedAt ?: Date())

                val sevenDaysAgo = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -Constants.DAYS_TO_RESTORE_ACCOUNT)
                }.time

                if (it.deletedAt != null && it.deletedAt.before(sevenDaysAgo)) {
                    userRepository.deleteAccountPermanently(userId)
                    onNavigate(NavigationDestination.Login)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.account_deleted),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(
                    R.string.account_deleted_message,
                    userInfo ?: "неизвестное время"
                ),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            userDeletedDate?.let { deletedDate ->
                val calendar = Calendar.getInstance()
                calendar.time = deletedDate
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                val restoreDeadline = calendar.time
                val daysLeft = ((restoreDeadline.time - Date().time) / (1000 * 60 * 60 * 24)).toInt()

                if (daysLeft > 0) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.elevatedCardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )

                                Text(
                                    text = stringResource(R.string.until_permanent_deletion),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 32.dp, vertical = 12.dp)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = daysLeft.toString(),
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = getDayWord(daysLeft),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .height(4.dp)
                                    .fillMaxWidth(0.6f)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        color = when {
                                            daysLeft <= 1 -> MaterialTheme.colorScheme.error
                                            daysLeft <= 3 -> Color(0xFFFF9800)
                                            else -> MaterialTheme.colorScheme.primary
                                        }.copy(alpha = 0.8f)
                                    )
                            )

                            val hintText = when {
                                daysLeft <= 1 -> stringResource(R.string.restore_last_day)
                                daysLeft <= 3 -> stringResource(R.string.restore_time_running_out)
                                else -> stringResource(R.string.restore_account_hint)
                            }

                            Text(
                                text = hintText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            userRepository.restoreAccount(userId)
                            onRecoveryComplete()
                        }

                        catch (e: Exception) {
                            null
                        }

                        finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Restore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(stringResource(R.string.restore_account))
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            userRepository.deleteAccountPermanently(userId)
                            onPermanentDelete()
                        }

                        catch (e: Exception) {
                            null
                        }

                        finally {
                            isLoading = false
                        }
                    }
                },

                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.5.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(stringResource(R.string.delete_permanently))
            }
        }
    }

    if (isLoading) {
        LoadingDialog()
    }
}

private fun getDayWord(days: Int): String {
    return when {
        days % 10 == 1 && days % 100 != 11 -> "день"
        days % 10 in 2..4 && (days % 100 < 10 || days % 100 >= 20) -> "дня"
        else -> "дней"
    }
}