package com.org.wiichat.core.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM user WHERE userId = :userId")
    fun getUserFromId(userId: Int): List<User>

    @Insert
    suspend fun addUser(user: User): Long

    @Delete
    suspend fun deleteUser(user: User)

}