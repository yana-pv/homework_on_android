package com.example.homework5.data.repository

import android.content.Context
import com.example.homework5.constants.EventCategory
import com.example.homework5.constants.SortType
import com.example.homework5.db.AppDatabase
import com.example.homework5.db.dao.EventDao
import com.example.homework5.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.util.Date

class EventRepository(private val context: Context) {

    private val eventDao: EventDao = AppDatabase.getDatabase(context).eventDao()

    suspend fun createEvent(
        userId: Long,
        title: String,
        description: String,
        date: Date,
        category: EventCategory
    ): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                val eventId = eventDao.insert(
                    Event(
                        userId = userId,
                        title = title,
                        description = description,
                        date = date,
                        category = category
                    )
                )
                Result.success(eventId)
            }

            catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun getEvents(userId: Long, sortType: SortType): Flow<List<Event>> {
        return when (sortType) {
            SortType.DATE_DESC -> eventDao.getEventsByUserSortedByDateDesc(userId)
            SortType.DATE_ASC -> eventDao.getEventsByUserSortedByDateAsc(userId)
            SortType.TITLE_ASC -> eventDao.getEventsByUserSortedByTitleAsc(userId)
            SortType.TITLE_DESC -> eventDao.getEventsByUserSortedByTitleDesc(userId)
            SortType.CATEGORY -> eventDao.getEventsByUserSortedByCategory(userId)
        }
    }

    suspend fun deleteEvent(event: Event) {
        withContext(Dispatchers.IO) {
            eventDao.delete(event)
        }
    }

    suspend fun getEventCount(userId: Long): Int {
        return withContext(Dispatchers.IO) {
            eventDao.getEventCount(userId)
        }
    }
}