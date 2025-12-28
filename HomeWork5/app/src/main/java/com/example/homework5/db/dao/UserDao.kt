package com.example.homework5.db.dao

import androidx.room.*
import com.example.homework5.model.User
import java.util.Date

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username AND isDeleted = 0")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsernameWithDeleted(username: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    @Update
    suspend fun update(user: User)

    @Query("UPDATE users SET isDeleted = 1, deletedAt = :deletedAt WHERE id = :userId")
    suspend fun markAsDeleted(userId: Long, deletedAt: Date)

    @Query("UPDATE users SET isDeleted = 0, deletedAt = NULL WHERE id = :userId")
    suspend fun restoreUser(userId: Long)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deletePermanently(userId: Long)

    @Query("SELECT * FROM users WHERE isDeleted = 1 AND deletedAt <= :thresholdDate")
    suspend fun getUsersToPurge(thresholdDate: Date): List<User>
}