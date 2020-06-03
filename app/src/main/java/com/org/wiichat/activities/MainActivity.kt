package com.org.wiichat.activities

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.org.wiichat.R
import com.org.wiichat.adapters.TabsAdapter
import com.org.wiichat.core.PersistentStorage
import com.org.wiichat.core.receiver.WifiDirectBroadcastReceiver
import com.org.wiichat.databinding.ActivityMainBinding
import com.org.wiichat.fragments.DevicesFragment
import com.org.wiichat.fragments.RecentFragment
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity(), WifiP2pManager.ActionListener,
    WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    lateinit var activityMainBinding: ActivityMainBinding
    lateinit var pagerAdapter: TabsAdapter
    val manager by lazy {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }
    lateinit var broadcastReceiver: WifiDirectBroadcastReceiver
    lateinit var channel: WifiP2pManager.Channel

    val REQUIRED_PERMISSIONS = arrayListOf<String>(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    companion object {
        var wifiP2PInfo: WifiP2pInfo? = null
    }

    val PERMISSIONS_REQUEST_CODE = 106
    val TAG = "MainActivity"

    lateinit var deviceFragment: DevicesFragment
    lateinit var persistentStorage: PersistentStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding =
            DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main);
        setSupportActionBar(activityMainBinding.toolbar)
        init()
    }

    private fun init() {
        if (hasPermissions())
            initializeP2P()
        else
            requestPermissions()

        supportActionBar?.apply {
            title = "WiiChat"
            titleColor = resources.getColor(R.color.colorPrimary)
        }
        activityMainBinding.toolbar.setTitleTextColor(resources.getColor(R.color.colorPrimary))

    }

    override fun onResume() {
        broadcastReceiver.also {
            registerReceiver(broadcastReceiver, IntentFilter(
            ).apply {
                addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
                addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            })
        }
        super.onResume()
    }

    private fun initializeP2P() {
        channel = manager.initialize(this@MainActivity, mainLooper, null)
        channel.also { channel: WifiP2pManager.Channel ->
            broadcastReceiver =
                WifiDirectBroadcastReceiver(
                    manager,
                    channel,
                    this@MainActivity
                )
            deviceFragment = DevicesFragment()
            pagerAdapter = TabsAdapter(supportFragmentManager)

            persistentStorage = PersistentStorage(this)
            if (persistentStorage.getUserId() == 0.toLong())
                persistentStorage.createUserId()

            pagerAdapter.addFragment(RecentFragment(), "Recent")
            pagerAdapter.addFragment(deviceFragment, "Around")
            pagerAdapter.addFragment(RecentFragment(), "Hidden")
            pagerAdapter.addFragment(RecentFragment(), "Other")

            activityMainBinding.tabsViewPager.adapter = pagerAdapter
            activityMainBinding.tabIndicator.setViewPager(activityMainBinding.tabsViewPager)
        }
        manager.discoverPeers(channel, this)


    }

    private fun hasPermissions() = REQUIRED_PERMISSIONS.all { permission ->
        ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            REQUIRED_PERMISSIONS.toTypedArray(),
            PERMISSIONS_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.isNotEmpty()) {
            initializeP2P()
        }
    }


    override fun onStop() {
        unregisterReceiver(broadcastReceiver)
        super.onStop()
    }

    override fun onSuccess() {
        //Broadcast intent sent
        Log.d(TAG, "Discovered peers!")
    }

    override fun onFailure(reason: Int) {
        Toasty.error(
            this@MainActivity, "Unable to discover peers ${reason}",
            Toasty.LENGTH_LONG
        ).show()
    }

    override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
        if (peers!!.deviceList.isNotEmpty()) {
            deviceFragment.peerList(ArrayList(peers.deviceList))
        }
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        wifiP2PInfo = info
        if (MainActivity.wifiP2PInfo != null) {
            val c = Intent(this, ChatActivity::class.java)
            c.putExtra("isGroupOwner", info!!.isGroupOwner)
            c.putExtra("isGroupFormed", info.groupFormed)
            c.putExtra("hostAddress", info.groupOwnerAddress.hostAddress)
            startActivity(c)
        } else {
            Toasty.error(
                this,
                "An unexpected error occurred. Please check " +
                        "that the device you are connecting to has the WiiChat app",
                Toasty.LENGTH_LONG
            ).show()
        }
    }
}
