package com.org.wiichat.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import com.org.wiichat.activities.MainActivity

class WifiDirectBroadcastReceiver(
    m: WifiP2pManager,
    c: WifiP2pManager.Channel,
    ctx: Context
) : BroadcastReceiver() {
    var manager = m
    var channel = c
    var context = ctx
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent!!.action) {
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                manager.requestPeers(
                    channel,
                    (context as MainActivity).supportFragmentManager
                        .findFragmentByTag("devices") as WifiP2pManager.PeerListListener
                )
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val info =
                    intent.getParcelableExtra<NetworkInfo>(WifiManager.EXTRA_NETWORK_INFO) as NetworkInfo
                if (info.isConnected) {
                    manager.requestConnectionInfo(
                        channel,
                        (context as MainActivity).supportFragmentManager
                            .findFragmentByTag("devices") as WifiP2pManager.ConnectionInfoListener
                    )
                }
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {

            }
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {

            }
        }
    }
}