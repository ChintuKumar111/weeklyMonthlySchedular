package com.example.freshyzoappmodule.data.model

data class chatListItem(
    var chatId: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val otherUserName: String = "",
    val otherUserId: String = ""
)
