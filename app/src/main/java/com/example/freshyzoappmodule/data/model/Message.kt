package com.example.freshyzoappmodule.data.model

data class Message(
    val senderId: String = "",
    val message: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L,
    val read: Boolean = false
)
