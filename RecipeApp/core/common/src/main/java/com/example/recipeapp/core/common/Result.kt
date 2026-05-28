package com.example.recipeapp.core.common

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class NetworkError(val cause: Throwable) : Result<Nothing>()
    data class ServerError(val code: Int, val message: String) : Result<Nothing>()
    data class ParsingError(val cause: Throwable) : Result<Nothing>()
    data class NoDataError(val message: String = "No data found") : Result<Nothing>()
    data class UnknownError(val cause: Throwable) : Result<Nothing>()
}