package com.org.wiichat.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.org.wiichat.R
import com.org.wiichat.adapters.MessageAdapter
import com.org.wiichat.core.MessageTaskHandler
import com.org.wiichat.core.PersistentStorage
import com.org.wiichat.core.room.Chat
import com.org.wiichat.core.room.WiiDatabase
import com.org.wiichat.databinding.ActivityChatBinding
import com.org.wiichat.models.MessageViewModel
import com.org.wiichat.pojo.MessageObject

class ChatActivity : AppCompatActivity() {

    lateinit var activityChatBinding: ActivityChatBinding

    private lateinit var adapter: MessageAdapter
    private lateinit var messageViewModel: MessageViewModel
    lateinit var messageTaskHandler: MessageTaskHandler

    lateinit var persistentStorage: PersistentStorage

    private var messageList = listOf<Chat>()
    private var imageUris = arrayListOf<Uri>()

    private val GALLERY_REQUEST = 206
    private val TAG = "ChatActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)
        activityChatBinding =
            DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)
        init()
    }

    private fun init() {

        val hostAddress = intent.getStringExtra("hostAddress")
        val isGroupFormed = intent.getBooleanExtra("isGroupFormed", false)
        val isGroupOwner = intent.getBooleanExtra("isGroupOwner", false)

        val dbInstance = WiiDatabase.getInstance(this)
        messageTaskHandler = MessageTaskHandler(this, dbInstance)
        messageViewModel = ViewModelProvider(this@ChatActivity).get(MessageViewModel::class.java)
        persistentStorage = PersistentStorage(this)

        hostAddress.let {
            if (isGroupOwner && isGroupFormed) {
                //HOST
                messageViewModel.createServer(messageTaskHandler)
            } else if (isGroupFormed) {
                //CLIENT
                messageViewModel.createClient(hostAddress!!, messageTaskHandler)
            }
        }

        setSupportActionBar(activityChatBinding.chatToolbar)
        supportActionBar?.apply {
            title = "Chat"
            titleColor = Color.WHITE
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        }
        activityChatBinding.chatToolbar.setTitleTextColor(Color.WHITE)

        activityChatBinding.chatRecyclerView.layoutManager =
            LinearLayoutManager(this@ChatActivity).apply {
                reverseLayout = true
            }

        initializeAdapter()

        activityChatBinding.addAttatchment.setOnClickListener {
            getImagesFromGallery()
        }

        activityChatBinding.sendMessage.setOnClickListener {
            sendMessage(
                MessageObject(
                    System.currentTimeMillis(),
                    activityChatBinding.emojiEditText.text.toString(),
                    deviceAddress = hostAddress,
                    baseImage = null,
                    userId = persistentStorage.getUserId()
                )
            )
        }

        dbInstance.chatDao().getAllMessages().observe(this@ChatActivity, Observer { chatList ->
            messageList = chatList
            initializeAdapter()

        })
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
        intent.action = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select images"),
            GALLERY_REQUEST
        )
    }

    private fun sendMessage(o: MessageObject) {
        messageViewModel.sendMessage(o, messageTaskHandler)
    }

}
