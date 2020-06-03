package com.org.wiichat.fragments

import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.org.wiichat.R
import com.org.wiichat.activities.MainActivity
import com.org.wiichat.adapters.ChatsAdapter
import com.org.wiichat.databinding.FragmentDevicesBinding
import com.org.wiichat.interfaces.InitPeerList
import com.org.wiichat.pojo.ChatObject
import es.dmoral.toasty.Toasty
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class DevicesFragment(
) : Fragment(), InitPeerList {

    lateinit var devicesFragmentBinding: FragmentDevicesBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ChatsAdapter

    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel
    private var peers = MutableLiveData<ArrayList<WifiP2pDevice>>().default(
        arrayListOf()
    )

    val config by lazy {
        WifiP2pConfig()
    }
    private val TAG = "DevicesFragment"


    var chatList = arrayListOf<ChatObject>()

    private fun <T : Any?> MutableLiveData<T>.default(initialValue: T) =
        apply { setValue(initialValue) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        devicesFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_devices, container!!, false)
        manager = (activity as MainActivity).manager
        channel = (activity as MainActivity).channel
        recyclerView = devicesFragmentBinding.devicesRecyclerView

        devicesFragmentBinding.devicesRecyclerView.layoutManager =
            LinearLayoutManager(requireActivity())
        init()
        return devicesFragmentBinding.root
    }

    private fun init() {
        chatList.clear()
        peers.observe(viewLifecycleOwner, androidx.lifecycle.Observer { peers ->
            if (peers.isNotEmpty()) {
                devicesFragmentBinding.loadingIndicator.hide()

                for ((_, item) in peers.withIndex()) {
                    chatList.add(
                        ChatObject(
                            item, System.currentTimeMillis()
                        )
                    )
                }
                chatList.sortWith(Comparator { o1, o2 -> if (o1.timestamp > o2.timestamp) 1 else 0 })
                adapter = ChatsAdapter(requireActivity(), chatList) {
                    config.apply {
                        deviceAddress = chatList[it.first].wifiP2pDevice.deviceAddress
                        wps.setup = WpsInfo.PBC
                    }
                    manager.let {
                        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
                            override fun onSuccess() {
                                Log.d(TAG, "Device connected.")
                                Toasty.success(
                                    requireActivity(),
                                    "Connected to device",
                                    Toasty.LENGTH_LONG
                                ).show()

                            }

                            override fun onFailure(reason: Int) {
                                Toasty.error(
                                    requireActivity(),
                                    "Unable to connect to this device ${reason}",
                                    Toasty.LENGTH_LONG
                                ).show()
                            }
                        })
                    }
                }
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            } else {
                devicesFragmentBinding.loadingIndicator.show()
            }
        })
    }

    override fun peerList(list: ArrayList<WifiP2pDevice>) {
        peers.value = list
    }
}
