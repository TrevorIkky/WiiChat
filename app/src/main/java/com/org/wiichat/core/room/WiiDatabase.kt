package com.org.wiichat.core.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Chat::class], version = 1, exportSchema = false)
abstract class WiiDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    var wiiDatabaseInstance: WiiDatabase? = null
    fun getInstance(context: Context): WiiDatabase {
        return if (wiiDatabaseInstance != null) {
            wiiDatabaseInstance!!
        } else {
            Room.databaseBuilder(
                context.applicationContext,
                WiiDatabase::class.java,
                "wii-database"
            ).build()
        }
    }
}