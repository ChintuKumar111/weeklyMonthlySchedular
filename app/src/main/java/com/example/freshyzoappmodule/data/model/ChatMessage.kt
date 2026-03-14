package com.example.freshyzoappmodule.data.model

data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val read: Boolean = false
)
