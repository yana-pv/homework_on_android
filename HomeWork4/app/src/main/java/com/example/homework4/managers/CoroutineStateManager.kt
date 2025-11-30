package com.example.homework4.managers

import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import com.example.homework4.R
import com.example.homework4.data.models.CoroutineException
import com.example.homework4.data.repositories.CoroutineExecutionRepository

class CoroutineStateManager(
    private val context: Context,
    private val scope: CoroutineScope,
    private val snackbarHostState: SnackbarHostState,
    private val onResetSettings: () -> Unit = {}
) {
    var isRunning by mutableStateOf(false)
    var isCancelled by mutableStateOf(false)
    var actualCoroutineCount by mutableIntStateOf(0)
    var completedCoroutineCount by mutableIntStateOf(0)
    var pausedCoroutineCount by mutableIntStateOf(0)

    private var job: Job? = null

    fun startCoroutines(
        coroutineCount: Int,
        selectedDispatcher: CoroutineDispatcher,
        isSequential: Boolean,
        isDeferred: Boolean
    ) {
        if (isRunning) return

        isRunning = true
        isCancelled = false
        completedCoroutineCount = 0
        pausedCoroutineCount = 0
        actualCoroutineCount = coroutineCount

        job = scope.launch {
            executeCoroutines(selectedDispatcher, isSequential, isDeferred)
        }.apply {
            invokeOnCompletion { cause ->
                isRunning = false
                if (cause !is CancellationException) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.all_coroutines_completed, actualCoroutineCount)
                        )
                    }
                    pausedCoroutineCount = 0
                }
            }
        }
    }

    fun cancelCoroutines() {
        job?.cancel()
        isRunning = false
        isCancelled = true
        val cancelledCount = actualCoroutineCount - completedCoroutineCount
        Toast.makeText(
            context,
            context.getString(R.string.cancelled_coroutines, cancelledCount),
            Toast.LENGTH_LONG
        ).show()
        pausedCoroutineCount = 0
    }

    private suspend fun executeCoroutines(
        selectedDispatcher: CoroutineDispatcher,
        isSequential: Boolean,
        isDeferred: Boolean
    ) {
        val jobs = (1..actualCoroutineCount).map { index ->
            createJob(selectedDispatcher, isSequential, isDeferred)
        }

        if (isSequential) {
            jobs.forEach { job ->
                if (job is Deferred<*>) {
                    job.await()
                }

                else {
                    (job as? Job)?.join()
                }
            }
        }

        else {
            jobs.forEach { job ->
                if (job is Deferred<*>) {
                    job.await()
                }

                else {
                    (job as? Job)?.join()
                }
            }
        }
    }

    private fun createJob(
        selectedDispatcher: CoroutineDispatcher,
        isSequential: Boolean,
        isDeferred: Boolean
    ): Any {
        val startBlock: suspend CoroutineScope.() -> Unit = {
            try {
                CoroutineExecutionRepository.performHeavyOperation()
                withContext(Dispatchers.Main) {
                    completedCoroutineCount++
                }
            }

            catch (e: CoroutineException.SlowOperationException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.slow_operation_failed, e.message),
                        Toast.LENGTH_LONG
                    ).show()
                }
                withContext(Dispatchers.Main) {
                    completedCoroutineCount++
                }
            }

            catch (e: CoroutineException.CriticalErrorException) {
                snackbarHostState.showSnackbar(context.getString(R.string.critical_error, e.message))
                withContext(Dispatchers.Main) {
                    completedCoroutineCount++
                }
            }

            catch (e: CoroutineException.UnexpectedStateException) {
                withContext(Dispatchers.Main) {
                    onResetSettings()
                    completedCoroutineCount++
                }
            }

            catch (e: CancellationException) {
                throw e
            }

            catch (e: Exception) {
                snackbarHostState.showSnackbar(context.getString(R.string.unexpected_error, e.message))
                withContext(Dispatchers.Main) {
                    completedCoroutineCount++
                }
            }
        }

        return if (isDeferred) {
            if (isSequential) {
                scope.launch(selectedDispatcher, start = CoroutineStart.LAZY) {
                    startBlock()
                }
            }

            else {
                scope.async(selectedDispatcher, start = CoroutineStart.LAZY) {
                    startBlock()
                }
            }
        }

        else {
            if (isSequential) {
                scope.launch(selectedDispatcher) {
                    startBlock()
                }
            }

            else {
                scope.async(selectedDispatcher) {
                    startBlock()
                }
            }
        }
    }

    fun pauseCoroutines() {
        if (isRunning) {
            job?.cancel()
            pausedCoroutineCount = actualCoroutineCount - completedCoroutineCount
            isRunning = false
            isCancelled = true
        }
    }

    fun resumeCoroutines(
        selectedDispatcher: CoroutineDispatcher,
        isSequential: Boolean,
        isDeferred: Boolean
    ) {
        if (pausedCoroutineCount > 0 && !isRunning) {
            startCoroutines(pausedCoroutineCount, selectedDispatcher, isSequential, isDeferred)
        }
    }
}