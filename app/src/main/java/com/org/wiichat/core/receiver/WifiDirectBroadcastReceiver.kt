package com.org.wiichat.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import com.org.wiichat.activities.MainActivity

class WifiDirectBroadcastReceiver(
    m: WifiP2pManager,
    c: WifiP2pManager.Channel,
    a: MainActivity
) : BroadcastReceiver() {
    var manager = m
    var channel = c
    var activity = a
    val TAG = "WifiBroadcastReceiver"
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent!!.action) {
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(
                    channel,
                    activity as WifiP2pManager.PeerListListener
                )
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val info =
                    intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO) as NetworkInfo
                if (info.isConnected) {
                    manager.requestConnectionInfo(
                        channel,
                        activity as WifiP2pManager.ConnectionInfoListener
                    )
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device =
                    intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice
                Log.d(TAG, device.deviceName)
            }
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val wifiState = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (wifiState == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Log.d(TAG, "WifiStateEnabled.")
                } else {
                    Log.d(TAG, "WifiStateDisabled.")
                }
            }
        }
    }
}