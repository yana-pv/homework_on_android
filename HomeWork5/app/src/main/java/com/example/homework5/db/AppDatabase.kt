package com.example.homework5.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.homework5.db.dao.EventDao
import com.example.homework5.db.dao.UserDao
import com.example.homework5.db.converter.DateConverter
import com.example.homework5.db.converter.EventCategoryConverter
import com.example.homework5.model.Event
import com.example.homework5.model.User

@Database(
    entities = [User::class, Event::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(DateConverter::class, EventCategoryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "event_calendar.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}