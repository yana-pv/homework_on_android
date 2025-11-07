package com.example.homework3.service

import com.example.homework3.model.UserMessage

object MessageRepository {
    private var _messages: MutableList<UserMessage> = mutableListOf()
    val messages: List<UserMessage>
        get() = _messages.sortedBy { it.timestamp }

    fun addMessage(message: UserMessage) {
        _messages.add(message)
    }

    fun clearMessages() {
        _messages.clear()
    }

    fun getMessagesState(): List<UserMessage> {
        return _messages.sortedBy { it.timestamp }
    }
}