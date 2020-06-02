package com.org.wiichat.core.room

import androidx.room.Embedded
import androidx.room.Relation


data class UserAndChat(
    @Embedded
    var user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userIdRef"
    )
    val chat: Chat
)