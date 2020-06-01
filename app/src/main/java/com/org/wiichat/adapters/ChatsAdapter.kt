package com.org.wiichat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.org.wiichat.R
import com.org.wiichat.pojo.ChatObject

class ChatsAdapter (context: Context, chatList : ArrayList<ChatObject>): RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {
    var ctx = context
    var chats = chatList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.activity_main, parent, false)
        return  ChatsViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {

    }

   inner class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}