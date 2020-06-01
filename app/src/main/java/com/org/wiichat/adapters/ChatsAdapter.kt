package com.org.wiichat.adapters

import android.content.Context
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.org.wiichat.R
import com.org.wiichat.pojo.ChatObject
import es.dmoral.toasty.Toasty

class ChatsAdapter(
    context: Context,
    chatList: ArrayList<ChatObject>,
    m: WifiP2pManager,
    c: WifiP2pManager.Channel
) :
    RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {
    var ctx = context
    var chats = chatList
    private val colorGenerator = ColorGenerator.DEFAULT
    private var manager = m
    private var channel = c
    private val TAG = "ChatsAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.chat_layout, parent, false)
        return ChatsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val co = chats[position]
        val layoutParams: LinearLayout.LayoutParams by lazy {
            holder.messageContainer.layoutParams as LinearLayout.LayoutParams
        }
        val config by lazy {
            WifiP2pConfig()
        }
        with(co) {
            val textDrawable = TextDrawable.builder()
                .buildRound(wifiP2pDevice.deviceName, colorGenerator.randomColor)
            holder.textHeader.text = wifiP2pDevice.deviceName
            //TODO.. change more info, switch drawable
            holder.moreInfo.text = timestamp.toString()
            holder.profileImage.setImageDrawable(textDrawable)

            layoutParams.gravity = Gravity.END
            holder.messageContainer.layoutParams = layoutParams
        }

        holder.messageContainer.setOnClickListener {
            config.apply {
                deviceAddress = co.wifiP2pDevice.deviceAddress
                wps.setup = WpsInfo.PBC
            }
            manager.let {
                manager.connect(channel, config, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d(TAG, "Device connected.")
                    }

                    override fun onFailure(reason: Int) {
                        Toasty.error(
                            ctx,
                            "Unable to connect to this device ${reason}",
                            Toasty.LENGTH_LONG
                        ).show()
                    }
                })
            }

        }
    }

    inner class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val textHeader: TextView = itemView.findViewById(R.id.chatTextHeader)
        val moreInfo: TextView = itemView.findViewById(R.id.chatMoreInfo)
        val messageContainer: LinearLayout = itemView.findViewById(R.id.messageContainer)
    }
}