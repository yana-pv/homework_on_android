package com.example.homework5.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String,
    val createdAt: Date = Date(),
    val isDeleted: Boolean = false,
    val deletedAt: Date? = null
)