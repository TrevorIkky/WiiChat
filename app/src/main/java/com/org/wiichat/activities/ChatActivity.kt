package com.org.wiichat.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.org.wiichat.R
import com.org.wiichat.adapters.MessageAdapter
import com.org.wiichat.core.ObjectToString
import com.org.wiichat.core.receiver.WiiMessageResultReceiver
import com.org.wiichat.core.services.WiiMessageTransferService
import com.org.wiichat.databinding.ActivityChatBinding
import com.org.wiichat.pojo.MessageObject

class ChatActivity : AppCompatActivity() {

    lateinit var activityChatBinding: ActivityChatBinding

    private lateinit var adapter: MessageAdapter
    private lateinit var receiver: WiiMessageResultReceiver

    private var messageList = arrayListOf<MessageObject>()
    private var imageUris = arrayListOf<Uri>()

    private val GALLERY_REQUEST = 206
    private val TAG = "ChatActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding =
            DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)
        init()
    }

    private fun init() {

        receiver = WiiMessageResultReceiver(
            Handler(),
            this@ChatActivity
        )

        setSupportActionBar(activityChatBinding.chatToolbar)
        supportActionBar?.apply {
            title = "Title"
            titleColor = Color.WHITE
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        }

        activityChatBinding.chatRecyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
        initializeAdapter()

        activityChatBinding.addAttatchment.setOnClickListener {
            getImagesFromGallery()
        }
    }

    private fun initializeAdapter() {
        adapter = MessageAdapter(this@ChatActivity, messageList) {
            Log.d(TAG, "Item clicked at ${it.first}")
        }
        activityChatBinding.chatRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST -> {
                    imageUris.clear()
                    if (data!!.clipData != null) {
                        imageUris.add(data.data!!)
                    } else {
                        for (i in 0 until data.clipData!!.itemCount) {
                            imageUris.add(data.clipData!!.getItemAt(i).uri)
                        }
                    }
                }
            }
        }
    }

    private fun getImagesFromGallery() {
        val intent = Intent()
        intent.action = "images/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select images"),
            GALLERY_REQUEST
        )
    }

    private fun sendMessageViaService(o: MessageObject) {
        val i = Intent(this@ChatActivity, WiiMessageTransferService::class.java)
        i.action = WiiMessageTransferService.SEND_FILE_ACTION
        i.putExtra("jsonObject", ObjectToString.getJsonFromObject(o))
        i.putExtra("wiiResultService", receiver)
        //TODO..address and port from intent
        i.putExtra(WiiMessageTransferService.TO_ADDRESS, "")
        i.putExtra(WiiMessageTransferService.PORT, 0)
        startService(i)
    }
}
