package com.example.homework5.screens.events

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.homework5.R
import com.example.homework5.di.ServiceLocator
import com.example.homework5.screens.LoadingDialog
import com.example.homework5.utils.getColor
import com.example.homework5.utils.getDisplayName
import com.example.homework5.constants.EventCategory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    userId: Long,
    onBack: () -> Unit,
    onEventAdded: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()
    val eventRepository = ServiceLocator.getEventRepository()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateInput by remember { mutableStateOf("") }
    var timeInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(EventCategory.PERSONAL) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_event),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            errorMessage = null
                        },
                        label = { Text(stringResource(R.string.event_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text,
                            autoCorrect = true
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = errorMessage != null
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = {
                            description = it
                            errorMessage = null
                        },
                        label = { Text(stringResource(R.string.event_description)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text,
                            autoCorrect = true
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        maxLines = 4
                    )

                    DateAndTimeInput(
                        dateInput = dateInput,
                        timeInput = timeInput,
                        onDateChange = { dateInput = it },
                        onTimeChange = { timeInput = it },
                        onDateFocus = { focusManager.moveFocus(FocusDirection.Down) },
                        onTimeFocus = { keyboardController?.hide() }
                    )

                    Column {
                        Text(
                            text = stringResource(R.string.event_category),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CategorySelector(
                            selectedCategory = selectedCategory,
                            onCategorySelected = { selectedCategory = it },
                            context = context
                        )
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            errorMessage = context.getString(R.string.error_title_empty)
                            return@Button
                        }

                        if (dateInput.isBlank()) {
                            errorMessage = context.getString(R.string.error_date_empty)
                            return@Button
                        }

                        if (timeInput.isBlank()) {
                            errorMessage = context.getString(R.string.error_time_empty)
                            return@Button
                        }

                        val dateResult = parseDate(dateInput, timeInput)
                        if (dateResult == null) {
                            errorMessage = context.getString(R.string.error_date_invalid)
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            val result = eventRepository.createEvent(
                                userId = userId,
                                title = title,
                                description = description,
                                date = dateResult,
                                category = selectedCategory
                            )
                            isLoading = false

                            result.fold(
                                onSuccess = {
                                    onEventAdded()
                                    onBack()
                                },
                                onFailure = {
                                    errorMessage = it.message ?: context.getString(R.string.event_creation_failed)
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text(stringResource(R.string.save_event))
                }
            }
        }
    }

    if (isLoading) {
        LoadingDialog()
    }
}

@Composable
fun DateAndTimeInput(
    dateInput: String,
    timeInput: String,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onDateFocus: () -> Unit,
    onTimeFocus: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = dateInput,
            onValueChange = onDateChange,
            label = { Text(stringResource(R.string.date_format_hint)) },
            placeholder = { Text(stringResource(R.string.date_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onNext = { onDateFocus() }
            )
        )

        OutlinedTextField(
            value = timeInput,
            onValueChange = onTimeChange,
            label = { Text(stringResource(R.string.time_format_hint)) },
            placeholder = { Text(stringResource(R.string.time_placeholder)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions(
                onDone = { onTimeFocus() }
            )
        )
    }
}

@Composable
fun CategorySelector(
    selectedCategory: EventCategory,
    onCategorySelected: (EventCategory) -> Unit,
    context: Context = LocalContext.current
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EventCategory.entries.forEach { category ->
            CategoryChipRow(
                category = category,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                context = context
            )
        }
    }
}

@Composable
fun CategoryChipRow(
    category: EventCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    context: Context = LocalContext.current
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (isSelected) category.getColor().copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) category.getColor() else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(category.getColor())
            )

            Text(
                text = category.getDisplayName(context),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = category.getColor()
                )
            }
        }
    }
}

private fun parseDate(dateStr: String, timeStr: String): Date? {
    return try {
        val cleanDate = dateStr.trim()
        val cleanTime = timeStr.trim()

        if (cleanDate.isEmpty() || cleanTime.isEmpty()) {
            return null
        }

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        dateFormat.isLenient = false

        val dateTimeStr = "$cleanDate $cleanTime"
        dateFormat.parse(dateTimeStr)
    }

    catch (e: Exception) {
        null
    }
}

