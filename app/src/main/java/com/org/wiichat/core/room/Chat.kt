package com.org.wiichat.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey(autoGenerate = true)
    var chatId: Int,
    @ColumnInfo
    var userIdRef: Int,
    @ColumnInfo
    var message: String,
    @ColumnInfo
    var baseImage: String,
    @ColumnInfo
    var timestamp: Long = System.currentTimeMillis()
)