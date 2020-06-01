package com.org.wiichat.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.org.wiichat.R
import com.org.wiichat.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    lateinit var  activityChatBinding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding = DataBindingUtil.setContentView(this@ChatActivity, R.layout.activity_chat)
        setSupportActionBar(activityChatBinding.chatToolbar)
        supportActionBar?.apply {
            title = "Title"
            titleColor = Color.WHITE
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        }
        activityChatBinding.chatRecyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
    }
}
