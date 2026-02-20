package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.data.model.chatListItem
import com.example.freshyzoappmodule.databinding.ItemChatListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatListAdapter(
    private var chatList: List<chatListItem>,
    private val onItemClick: (chatListItem) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = chatList[position]
        holder.binding.tvName.text = item.otherUserName
        holder.binding.tvLastMessage.text = item.lastMessage
        holder.binding.tvTime.text = formatTime(item.timestamp)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = chatList.size

    fun updateList(newList: List<chatListItem>) {
        chatList = newList
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
