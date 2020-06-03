package com.org.wiichat.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.org.wiichat.R
import com.org.wiichat.core.room.User
import com.org.zoner.utils.date.DateTimeStyle
import com.org.zoner.utils.date.DateTimeUtils

class RecentAdapter(
    context: Context,
    userList: List<User>,
    cb: (Pair<Int, View>) -> Unit
) :
    RecyclerView.Adapter<RecentAdapter.ChatsViewHolder>() {
    var ctx = context
    var users = userList
    private val colorGenerator = ColorGenerator.DEFAULT
    private val TAG = "ChatsAdapter"
    private var callback = cb

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.chat_layout, parent, false)
        return ChatsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val co = users[position]
        with(co) {
            val textDrawable = TextDrawable.builder()
                .buildRound(deviceName!![0].toString(), colorGenerator.randomColor)
            holder.textHeader.text = deviceName!!
            val date = DateTimeUtils.formatDate(dateAdded)
            holder.moreInfo.text =
                DateTimeUtils.formatWithStyle(date, DateTimeStyle.AGO_SHORT_STRING)
            holder.profileImage.setImageDrawable(textDrawable)
        }

    }

    inner class ChatsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val textHeader: TextView = itemView.findViewById(R.id.chatTextHeader)
        val moreInfo: TextView = itemView.findViewById(R.id.chatMoreInfo)
        override fun onClick(v: View?) {
            callback(Pair(adapterPosition, v!!))
        }
    }
}