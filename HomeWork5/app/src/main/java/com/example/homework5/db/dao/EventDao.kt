package com.example.homework5.db.dao

import androidx.room.*
import com.example.homework5.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long

    // Получить все события пользователя без сортировки
    @Query("SELECT * FROM events WHERE userId = :userId")
    suspend fun getEventsByUserRaw(userId: Long): List<Event>

    // Сортировка по дате новые сначала
    @Query("SELECT * FROM events WHERE userId = :userId ORDER BY date DESC")
    fun getEventsByUserSortedByDateDesc(userId: Long): Flow<List<Event>>

    // Сортировка по дате старые сначала
    @Query("SELECT * FROM events WHERE userId = :userId ORDER BY date ASC")
    fun getEventsByUserSortedByDateAsc(userId: Long): Flow<List<Event>>

    // Сортировка по названию A-Z
    @Query("SELECT * FROM events WHERE userId = :userId ORDER BY title ASC")
    fun getEventsByUserSortedByTitleAsc(userId: Long): Flow<List<Event>>

    // Сортировка по названию Z-A
    @Query("SELECT * FROM events WHERE userId = :userId ORDER BY title DESC")
    fun getEventsByUserSortedByTitleDesc(userId: Long): Flow<List<Event>>

    // Сортировка по категории
    @Query("SELECT * FROM events WHERE userId = :userId ORDER BY " +
            "CASE category " +
            "WHEN 'PERSONAL' THEN 1 " +
            "WHEN 'WORK' THEN 2 " +
            "WHEN 'ENTERTAINMENT' THEN 3 " +
            "WHEN 'HEALTH' THEN 4 " +
            "WHEN 'LEARNING' THEN 5 " +
            "WHEN 'OTHER' THEN 6 " +
            "END")
    fun getEventsByUserSortedByCategory(userId: Long): Flow<List<Event>>

    @Delete
    suspend fun delete(event: Event)

    @Query("DELETE FROM events WHERE userId = :userId")
    suspend fun deleteAllUserEvents(userId: Long)

    @Query("SELECT COUNT(*) FROM events WHERE userId = :userId")
    suspend fun getEventCount(userId: Long): Int
}