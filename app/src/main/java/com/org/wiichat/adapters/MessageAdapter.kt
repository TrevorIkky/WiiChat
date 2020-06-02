package com.org.wiichat.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.org.wiichat.R
import com.org.wiichat.pojo.MessageObject

class MessageAdapter(
    context: Context,
    messageList: ArrayList<MessageObject>,
    cb: (Pair<Int, View?>) -> Unit
) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    var ctx = context
    var messages = messageList
    var callback = cb

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.message_layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messageObj = messages[position]
        val layoutParams: LinearLayout.LayoutParams by lazy {
            holder.messageContainer.layoutParams as LinearLayout.LayoutParams
        }
        with(messageObj) {
            holder.message.text = message
            val date = DateTimeUtils.formatDate(timestamp)
            holder.timeAgo.text =
                DateTimeUtils.formatWithStyle(date, DateTimeStyle.AGO_SHORT_STRING)
            layoutParams.gravity = Gravity.END
            holder.messageContainer.layoutParams = layoutParams
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var messageRoot: RelativeLayout = itemView.findViewById(R.id.messageRoot)
        var message: TextView = itemView.findViewById(R.id.message)
        var image: ImageView = itemView.findViewById(R.id.image)
        var timeAgo: TextView = itemView.findViewById(R.id.timeAgo)
        var messageContainer: LinearLayout = itemView.findViewById(R.id.messageContainer)
        override fun onClick(v: View?) {
            callback(Pair(adapterPosition, v))
        }

    }
}