package com.example.homework4.data.repositories

import kotlinx.coroutines.*
import com.example.homework4.data.Constants
import com.example.homework4.data.models.CoroutineException
import kotlin.random.Random

object CoroutineExecutionRepository {
    private val exceptionMap = mapOf(
        Constants.EXCEPTION_SLOW_OPERATION to CoroutineException.SlowOperationException,
        Constants.EXCEPTION_CRITICAL_ERROR to CoroutineException.CriticalErrorException,
        Constants.EXCEPTION_UNEXPECTED_STATE to CoroutineException.UnexpectedStateException
    )

    private fun getExceptionForNotificationType(type: String): Exception {
        return exceptionMap[type] ?: RuntimeException("Неизвестный тип исключения: $type")
    }

    suspend fun performHeavyOperation(): String {
        val delayTime = Random.nextLong(Constants.MIN_DELAY_MS, Constants.MAX_DELAY_MS + 1)
        delay(delayTime)

        if (delayTime >= Constants.SLOW_THRESHOLD_MS && Random.nextFloat() < Constants.EXCEPTION_PROBABILITY) {
            val exceptionTypes = exceptionMap.keys.toList()
            val randomType = exceptionTypes.random()
            throw getExceptionForNotificationType(randomType)
        }
        return "Выполнено за ${delayTime}мс"
    }
}