package com.example.homework4.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.homework4.R
import com.example.homework4.data.Constants
import com.example.homework4.managers.CoroutineStateManager
import com.example.homework4.ui.theme.CancelButtonColor
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var coroutineCount by remember { mutableIntStateOf(Constants.SLIDER_MIN) }
    var selectedDispatcherName by remember { mutableStateOf(Constants.DISPATCHER_DEFAULT) }
    var isSequential by remember { mutableStateOf(true) }
    var isDeferred by remember { mutableStateOf(false) }
    var isInBackground by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val coroutineStateManager = remember {
        CoroutineStateManager(
            context = context,
            scope = scope,
            snackbarHostState = snackbarHostState,
            onResetSettings = {
                coroutineCount = Constants.SLIDER_MIN
                selectedDispatcherName = Constants.DISPATCHER_DEFAULT
                isSequential = true
                isDeferred = false
            }
        )
    }

    val dispatchersMap = mapOf(
        Constants.DISPATCHER_DEFAULT to Dispatchers.Default,
        Constants.DISPATCHER_IO to Dispatchers.IO,
        Constants.DISPATCHER_MAIN to Dispatchers.Main,
        Constants.DISPATCHER_UNCONFINED to Dispatchers.Unconfined
    )

    val selectedDispatcher = dispatchersMap[selectedDispatcherName] ?: Dispatchers.Default

    val onSequentialChanged = { checked: Boolean ->
        isSequential = checked
    }
    val onParallelChanged = { checked: Boolean ->
        isSequential = !checked
    }

    var isDispatcherMenuExpanded by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, isInBackground) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (!isInBackground && coroutineStateManager.isRunning) {
                        coroutineStateManager.pauseCoroutines()
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (!isInBackground && coroutineStateManager.pausedCoroutineCount > 0 && !coroutineStateManager.isRunning) {
                        coroutineStateManager.resumeCoroutines(selectedDispatcher, isSequential, isDeferred)
                    }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Constants.Dimens.SCREEN_PADDING),
            verticalArrangement = Arrangement.spacedBy(Constants.Dimens.MEDIUM_SPACER),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${stringResource(R.string.coroutines_count)}$coroutineCount")
            Slider(
                value = coroutineCount.toFloat(),
                onValueChange = { newValue -> coroutineCount = newValue.toInt() },
                valueRange = Constants.SLIDER_MIN.toFloat()..Constants.SLIDER_MAX.toFloat(),
                steps = ((Constants.SLIDER_MAX - Constants.SLIDER_MIN) / Constants.SLIDER_STEP) - 1,
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = isDispatcherMenuExpanded,
                onExpandedChange = { isDispatcherMenuExpanded = !isDispatcherMenuExpanded }
            ) {
                OutlinedTextField(
                    value = selectedDispatcherName,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(stringResource(R.string.select_dispatcher)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDispatcherMenuExpanded) },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isDispatcherMenuExpanded,
                    onDismissRequest = { isDispatcherMenuExpanded = false }
                ) {
                    dispatchersMap.keys.forEach { dispatcherName ->
                        DropdownMenuItem(
                            text = { Text(dispatcherName) },
                            onClick = {
                                selectedDispatcherName = dispatcherName
                                isDispatcherMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.sequential_execution))
                Spacer(modifier = Modifier.width(Constants.Dimens.SMALL_SPACER))
                Switch(
                    checked = isSequential,
                    onCheckedChange = onSequentialChanged
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.parallel_execution))
                Spacer(modifier = Modifier.width(Constants.Dimens.SMALL_SPACER))
                Switch(
                    checked = !isSequential,
                    onCheckedChange = onParallelChanged
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.deferred_execution))
                Spacer(modifier = Modifier.width(Constants.Dimens.SMALL_SPACER))
                Switch(
                    checked = isDeferred,
                    onCheckedChange = { isDeferred = it }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.background_work))
                Spacer(modifier = Modifier.width(Constants.Dimens.SMALL_SPACER))
                Switch(
                    checked = isInBackground,
                    onCheckedChange = { isInBackground = it }
                )
            }

            if (coroutineStateManager.isRunning) {
                Button(
                    onClick = {
                        coroutineStateManager.cancelCoroutines()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CancelButtonColor
                    )
                ) {
                    Text(stringResource(R.string.cancel_coroutines))
                }

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            else {
                Button(
                    onClick = {
                        coroutineStateManager.startCoroutines(
                            coroutineCount,
                            selectedDispatcher,
                            isSequential,
                            isDeferred
                        )
                    }
                ) {
                    Text(stringResource(R.string.start_coroutines))
                }
            }
        }
    }
}