package com.org.wiichat.pojo

import android.net.wifi.p2p.WifiP2pDevice

data class ChatObject(
    var wifiP2pDevice: WifiP2pDevice,
    var timestamp: Long = System.currentTimeMillis()
)