package com.example.homework5.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.homework5.constants.EventCategory
import com.example.homework5.db.converter.DateConverter
import java.util.Date


@Entity(tableName = "events")
@TypeConverters(DateConverter::class)
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val description: String,
    val date: Date,
    val category: EventCategory,
    val createdAt: Date = Date()
)