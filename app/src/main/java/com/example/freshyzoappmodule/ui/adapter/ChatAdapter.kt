package com.example.freshyzoappmodule.ui.adapter

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.messageModel
import java.util.Calendar

class ChatAdapter(
    private val messageList: MutableList<messageModel>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_RIGHT = 1
    private val VIEW_TYPE_LEFT = 2

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].senderId == currentUserId) {
            VIEW_TYPE_RIGHT
        } else {
            VIEW_TYPE_LEFT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_RIGHT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_right, parent, false)
            RightViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_left, parent, false)
            LeftViewHolder(view)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = messageList[position]
        val timeString = formatTime(message.timestamp)

        if (holder is RightViewHolder) {
            holder.txtMessage.text = message.message
            holder.tvTime.text = timeString
        } else if (holder is LeftViewHolder) {
            holder.txtMessage.text = message.message
            holder.tvTime.text = timeString
        }
    }

    private fun formatTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "just now"
            DateUtils.isToday(timestamp) -> {
                DateUtils.getRelativeTimeSpanString(
                    timestamp,
                    now,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE
                ).toString()
            }
            isYesterday(timestamp) -> "Yesterday"
            else -> {
                DateUtils.formatDateTime(
                    null,
                    timestamp,
                    DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_ABBREV_MONTH
                )
            }
        }
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val time = Calendar.getInstance()
        time.timeInMillis = timestamp

        now.add(Calendar.DATE, -1)

        return now.get(Calendar.YEAR) == time.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == time.get(Calendar.DAY_OF_YEAR)
    }

    class RightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    class LeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }
}


