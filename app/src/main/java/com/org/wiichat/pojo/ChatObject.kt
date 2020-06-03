package com.org.wiichat.pojo

import android.net.wifi.p2p.WifiP2pDevice
import java.io.Serializable

class ChatObject(
    var wifiP2pDevice: WifiP2pDevice,
    var timestamp: Long = System.currentTimeMillis()
) : Serializable