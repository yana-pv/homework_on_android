package com.example.homework5.di

import android.content.Context
import com.example.homework5.data.repository.EventRepository
import com.example.homework5.data.repository.UserRepository
import kotlinx.coroutines.MainScope

object ServiceLocator {
    private var userRepository: UserRepository? = null
    private var eventRepository: EventRepository? = null

    fun init(context: Context) {
        userRepository = UserRepository(context)
        eventRepository = EventRepository(context)

        // Фоновая задача для очистки удаленных аккаунтов
        userRepository?.startAutoPurgeJob(MainScope())
    }

    fun getUserRepository(): UserRepository {
        return userRepository ?: throw IllegalStateException("ServiceLocator не инициализирован")
    }

    fun getEventRepository(): EventRepository {
        return eventRepository ?: throw IllegalStateException("ServiceLocator не инициализирован")
    }
}