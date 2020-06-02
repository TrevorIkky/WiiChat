package com.org.wiichat.activities

import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.org.wiichat.R
import com.org.wiichat.adapters.TabsAdapter
import com.org.wiichat.core.receiver.WifiDirectBroadcastReceiver
import com.org.wiichat.databinding.ActivityMainBinding
import com.org.wiichat.fragments.DevicesFragment
import com.org.wiichat.fragments.RecentFragment
import es.dmoral.toasty.Toasty

class MainActivity : AppCompatActivity(), WifiP2pManager.ActionListener {

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
    val PERMISSIONS_REQUEST_CODE = 106


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
        pagerAdapter = TabsAdapter(supportFragmentManager)
        pagerAdapter.addFragment(RecentFragment(), "Recent")
        pagerAdapter.addFragment(DevicesFragment(manager, channel), "Around")
        pagerAdapter.addFragment(DevicesFragment(manager, channel), "Hidden")
        activityMainBinding.tabsViewPager.adapter = pagerAdapter
        activityMainBinding.tabIndicator.setViewPager(activityMainBinding.tabsViewPager)
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
    }

    override fun onFailure(reason: Int) {
        Toasty.error(
            this@MainActivity, "Unable to discover peers ${reason}",
            Toasty.LENGTH_LONG
        ).show()
    }
}
