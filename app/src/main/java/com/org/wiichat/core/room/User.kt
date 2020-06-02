package com.org.wiichat.core.room

import android.net.wifi.p2p.WifiP2pDevice
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    var userId : Int,
    @ColumnInfo
    var wifiDevice  : WifiP2pDevice,
    @ColumnInfo
    var timestamp : String
)