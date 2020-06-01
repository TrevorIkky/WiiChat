package com.org.wiichat.fragments

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.org.wiichat.R
import com.org.wiichat.adapters.ChatsAdapter
import com.org.wiichat.databinding.FragmentDevicesBinding
import com.org.wiichat.pojo.ChatObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DevicesFragment(
    m: WifiP2pManager,
    c: WifiP2pManager.Channel
) : Fragment(), WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    lateinit var devicesFragmentBinding: FragmentDevicesBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ChatsAdapter

    private var manager = m
    private var channel = c
    private var info: WpsInfo? = null

    var deviceList = arrayListOf<WifiP2pDevice>()
    var chatList = arrayListOf<ChatObject>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        devicesFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_devices, container!!, false)
        recyclerView = devicesFragmentBinding.devicesRecyclerView
        devicesFragmentBinding.devicesRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity())
        init()
        return devicesFragmentBinding.root
    }

    private fun init() {
        for ((_, item) in deviceList.withIndex()) {
            chatList.add(
                ChatObject(
                    item, System.currentTimeMillis()
                )
            )
        }
        chatList.sortWith(Comparator { o1, o2 -> if (o1.timestamp > o2.timestamp) 1 else 0 })
        adapter = ChatsAdapter(requireActivity(), chatList, manager, channel)
        recyclerView.adapter = adapter

    }

    override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
        if (peers!!.deviceList.isNotEmpty()) {
            deviceList.addAll(peers.deviceList)
        }
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {

    }
}
