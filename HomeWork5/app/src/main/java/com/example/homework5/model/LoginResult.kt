package com.example.homework5.model

sealed class LoginResult {
    data class Success(val userId: Long) : LoginResult()
    data class AccountDeleted(val user: User) : LoginResult()
}