package com.org.wiichat.interfaces

import android.net.wifi.p2p.WifiP2pDevice
import java.util.ArrayList

interface InitPeerList {
    fun peerList(list: ArrayList<WifiP2pDevice>)
}