package com.org.wiichat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.org.wiichat.R
import com.org.wiichat.pojo.ChatObject

class MessageAdapter(context: Context, messageList: ArrayList<ChatObject>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    var ctx = context
    var messages = messageList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.message_layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}