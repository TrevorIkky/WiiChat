package com.org.wiichat.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    var userId: Int,
    @ColumnInfo
    var address: String,
    @ColumnInfo
    var deviceName: String,
    @ColumnInfo
    var timestamp: String
) {
}