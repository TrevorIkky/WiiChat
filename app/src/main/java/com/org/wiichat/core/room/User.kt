package com.org.wiichat.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["deviceName", "dateAdded"])])
data class User(
    @PrimaryKey
    var userId: Int,
    @ColumnInfo
    var address: String? = null,
    @ColumnInfo
    var deviceName: String? = null,
    @ColumnInfo
    var dateAdded: Long = System.currentTimeMillis()
) {
}