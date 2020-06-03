package com.org.wiichat.pojo

import android.net.wifi.p2p.WifiP2pDevice
import java.io.Serializable

class DeviceObject(
    var wifiP2pDevice: WifiP2pDevice,
    var timestamp: Long = System.currentTimeMillis()
) : Serializable