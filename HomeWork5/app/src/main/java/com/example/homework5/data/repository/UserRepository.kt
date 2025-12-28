package com.example.homework5.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.homework5.R
import com.example.homework5.db.AppDatabase
import com.example.homework5.db.dao.UserDao
import com.example.homework5.model.LoginResult
import com.example.homework5.model.User
import com.example.homework5.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserRepository(private val context: Context) {

    private val userDao: UserDao = AppDatabase.getDatabase(context).userDao()
    private val dataStore = context.dataStore

    companion object {
        val USER_ID_KEY = longPreferencesKey("user_id")
        val USERNAME_KEY = stringPreferencesKey("username")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    suspend fun register(username: String, password: String): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                val existingUser = userDao.getUserByUsernameWithDeleted(username)
                if (existingUser != null && !existingUser.isDeleted) {
                    return@withContext Result.failure(Exception(context.getString(R.string.user_already_exists)))
                }

                // Если пользователь был удален, но хочет зарегистрироваться с тем же логином
                if (existingUser != null && existingUser.isDeleted) {
                    val sevenDaysAgo = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -7)
                    }.time

                    return@withContext if (existingUser.deletedAt != null &&
                        existingUser.deletedAt.after(sevenDaysAgo)) {
                        Result.failure(Exception(context.getString(R.string.account_recovery_restricted)))
                    }

                    else {
                        // Удаляем старый аккаунт и создаем новый
                        userDao.deletePermanently(existingUser.id)
                        val userId = userDao.insert(
                            User(
                                username = username,
                                password = password
                            )
                        )
                        Result.success(userId)
                    }
                }

                val userId = userDao.insert(
                    User(
                        username = username,
                        password = password
                    )
                )

                Result.success(userId)
            }

            catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun login(username: String, password: String): Result<LoginResult> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserByUsernameWithDeleted(username)

                if (user == null) {
                    return@withContext Result.failure(Exception(context.getString(R.string.account_not_found)))
                }

                if (user.password != password) {
                    return@withContext Result.failure(Exception(context.getString(R.string.wrong_password)))
                }

                if (user.isDeleted) {
                    val sevenDaysAgo = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, -Constants.DAYS_TO_RESTORE_ACCOUNT)
                    }.time

                    return@withContext if (user.deletedAt != null &&
                        user.deletedAt.after(sevenDaysAgo)) {
                        // Показываем экран восстановления
                        Result.success(LoginResult.AccountDeleted(user))
                    }

                    else {
                        userDao.deletePermanently(user.id)
                        Result.failure(Exception(context.getString(R.string.account_not_found)))
                    }
                }

                // Аккаунт активен - сохраняем сессию
                saveSession(user.id, user.username)

                Result.success(LoginResult.Success(user.id))
            }

            catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserById(userId: Long): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }

    suspend fun restoreAccount(userId: Long) {
        withContext(Dispatchers.IO) {
            val user = userDao.getUserById(userId)

            if (user != null && user.isDeleted) {
                userDao.restoreUser(userId)
                saveSession(userId, user.username)
            }
        }
    }

    suspend fun deleteAccountPermanently(userId: Long) {
        withContext(Dispatchers.IO) {
            try {
                userDao.deletePermanently(userId)

                val eventDao = AppDatabase.getDatabase(context).eventDao()
                eventDao.deleteAllUserEvents(userId)

                logout()
            }

            catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun getCurrentUserId(): Long? {
        return dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.first()
    }

    suspend fun getCurrentUsername(): String? {
        return dataStore.data.map { preferences ->
            preferences[USERNAME_KEY]
        }.first()
    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }.first()
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(USERNAME_KEY)
            preferences[IS_LOGGED_IN_KEY] = false
        }
    }

    suspend fun saveSession(userId: Long, username: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USERNAME_KEY] = username
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    suspend fun deleteAccount(userId: Long) {
        withContext(Dispatchers.IO) {
            userDao.markAsDeleted(userId, Date())
            logout()
        }
    }

    suspend fun purgeOldDeletedAccounts() {
        withContext(Dispatchers.IO) {
            val sevenDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -7)
            }.time

            val usersToPurge = userDao.getUsersToPurge(sevenDaysAgo)
            usersToPurge.forEach { user ->
                try {
                    userDao.deletePermanently(user.id)

                    val eventDao = AppDatabase.getDatabase(context).eventDao()
                    eventDao.deleteAllUserEvents(user.id)
                }

                catch (e: Exception) {
                    null
                }
            }
        }
    }

    fun startAutoPurgeJob(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            purgeOldDeletedAccounts()

            // Каждые 24 часа
            while (true) {
                kotlinx.coroutines.delay(Constants.AUTO_PURGE_INTERVAL_HOURS * 60 * 60 * 1000L)
                purgeOldDeletedAccounts()
            }
        }
    }
}