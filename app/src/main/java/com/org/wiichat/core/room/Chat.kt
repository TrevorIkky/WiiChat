package com.org.wiichat.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.org.wiichat.pojo.MessageObject

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true)
    var chatId: Int,
    @ColumnInfo
    var userIdRef : Int,
    @ColumnInfo
    var messageObject: MessageObject
)