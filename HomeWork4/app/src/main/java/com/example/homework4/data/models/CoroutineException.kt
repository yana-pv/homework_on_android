package com.example.homework4.data.models

sealed class CoroutineException(message: String) : Exception(message) {
    object SlowOperationException : CoroutineException("Операция заняла слишком много времени")
    object CriticalErrorException : CoroutineException("Произошла критическая ошибка")
    object UnexpectedStateException : CoroutineException("Обнаружено неожиданное состояние")
}