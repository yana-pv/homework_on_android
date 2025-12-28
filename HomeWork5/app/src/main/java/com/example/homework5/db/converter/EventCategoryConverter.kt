package com.example.homework5.db.converter

import androidx.room.TypeConverter
import com.example.homework5.constants.EventCategory

class EventCategoryConverter {
    @TypeConverter
    fun fromString(value: String): EventCategory {
        return EventCategory.valueOf(value)
    }

    @TypeConverter
    fun categoryToString(category: EventCategory): String {
        return category.name
    }
}