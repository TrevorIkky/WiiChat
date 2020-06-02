package com.org.wiichat.core.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat")
    fun getAllMessages(): LiveData<List<Chat>>

    @Query("SELECT * FROM chat WHERE userIdRef = :id")
    fun getUserMessages(id: Int): LiveData<List<Chat>>

    @Insert
    suspend fun addChatMessage(chat: Chat): Long

    @Delete
    suspend fun deleteChatMessage(chat: Chat)
}