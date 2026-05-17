package com.example.recipeapp.core.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
    return try {
        Result.Success(withContext(Dispatchers.IO) { call() })
    } catch (e: UnknownHostException) {
        Result.NetworkError(e)
    } catch (e: SocketTimeoutException) {
        Result.NetworkError(e)
    } catch (e: HttpException) {
        Result.ServerError(e.code(), e.message ?: "Server error")
    } catch (e: com.google.gson.JsonSyntaxException) {
        Result.ParsingError(e)
    } catch (e: Exception) {
        when (e.message) {
            "Recipe not found" -> Result.NoDataError(e.message ?: "No data found")
            else -> Result.UnknownError(e)
        }
    }
}