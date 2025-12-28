package com.example.homework5.screens.events

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.homework5.R
import com.example.homework5.constants.SortType
import com.example.homework5.di.ServiceLocator
import com.example.homework5.model.Event
import com.example.homework5.navigation.NavigationDestination
import com.example.homework5.screens.LoadingShimmer
import com.example.homework5.utils.formatDate
import com.example.homework5.utils.getColor
import com.example.homework5.utils.getDisplayName
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    userId: Long,
    onNavigate: (NavigationDestination) -> Unit,
    onEventDeleted: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val eventRepository = ServiceLocator.getEventRepository()

    var isLoading by remember { mutableStateOf(true) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var sortType by remember { mutableStateOf(SortType.DATE_DESC) }
    var showSortBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(userId, sortType) {
        isLoading = true
        eventRepository.getEvents(userId, sortType).collect { eventList ->
            events = eventList
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.events_list),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onNavigate(NavigationDestination.Profile(userId, "User"))
                        }
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }

                    IconButton(onClick = { showSortBottomSheet = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigate(NavigationDestination.AddEvent(userId)) },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LoadingShimmer()
            }

            else if (events.isEmpty()) {
                EmptyEventsState()
            }

            else {
                EventsList(
                    events = events,
                    onEventClick = { },
                    onEventDelete = { event ->
                        scope.launch {
                            eventRepository.deleteEvent(event)
                            onEventDeleted()
                        }
                    }
                )
            }
        }
    }

    if (showSortBottomSheet) {
        SortBottomSheet(
            currentSortType = sortType,
            onSortTypeSelected = {
                sortType = it
                showSortBottomSheet = false
            },
            onDismiss = { showSortBottomSheet = false }
        )
    }
}

@Composable
fun EventsList(
    events: List<Event>,
    onEventClick: (Event) -> Unit,
    onEventDelete: (Event) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events, key = { it.id }) { event ->
            EventCard(
                event = event,
                onClick = { onEventClick(event) },
                onDelete = { onEventDelete(event) }
            )
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                CategoryChip(category = event.category)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (event.description.isNotBlank()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.date.formatDate(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.delete_event))
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: com.example.homework5.constants.EventCategory,
    context: Context = LocalContext.current
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(category.getColor().copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = category.getDisplayName(context),
            color = category.getColor(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmptyEventsState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.no_events),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    currentSortType: SortType,
    onSortTypeSelected: (SortType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            SortOptionItem(
                text = stringResource(R.string.sort_date_asc),
                isSelected = currentSortType == SortType.DATE_ASC,
                onClick = { onSortTypeSelected(SortType.DATE_ASC) }
            )

            SortOptionItem(
                text = stringResource(R.string.sort_date_desc),
                isSelected = currentSortType == SortType.DATE_DESC,
                onClick = { onSortTypeSelected(SortType.DATE_DESC) }
            )

            SortOptionItem(
                text = stringResource(R.string.sort_title_asc),
                isSelected = currentSortType == SortType.TITLE_ASC,
                onClick = { onSortTypeSelected(SortType.TITLE_ASC) }
            )

            SortOptionItem(
                text = stringResource(R.string.sort_title_desc),
                isSelected = currentSortType == SortType.TITLE_DESC,
                onClick = { onSortTypeSelected(SortType.TITLE_DESC) }
            )

            SortOptionItem(
                text = stringResource(R.string.sort_category),
                isSelected = currentSortType == SortType.CATEGORY,
                onClick = { onSortTypeSelected(SortType.CATEGORY) }
            )
        }
    }
}

@Composable
fun SortOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}