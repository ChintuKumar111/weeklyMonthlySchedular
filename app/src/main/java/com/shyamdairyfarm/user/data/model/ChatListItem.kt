package com.shyamdairyfarm.user.data.model

data class ChatListItem(
    var chatId: String = "",
    val lastMessage: String = "",
    val timestamp: Long = 0L,
    val otherUserName: String = "",
    val otherUserId: String = ""
)
