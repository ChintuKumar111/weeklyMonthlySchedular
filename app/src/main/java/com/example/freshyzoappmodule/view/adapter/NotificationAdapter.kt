package com.example.freshyzoappmodule.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.NotificationModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class NotificationAdapter(
    private var list: List<NotificationModel>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.message.text = item.message
        holder.time.text = getTimeAgo(item.time)
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitle)
        val message: TextView = view.findViewById(R.id.tvMessage)
        val time: TextView = view.findViewById(R.id.tvTime)
    }

    // keep UI helper inside adapter (cleaner)
    private fun getTimeAgo(time: Long): String {
        val diff = System.currentTimeMillis() - time

        return when {
            diff < 60_000 -> "Just now"
            diff < 3_600_000 -> "${diff / 60_000} min ago"
            diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
            else -> SimpleDateFormat(
                "dd MMM, hh:mm a",
                Locale.getDefault()
            ).format(Date(time))
        }
    }

    fun updateList(newList: List<NotificationModel>) {
        list = newList
        notifyDataSetChanged()
    }
}
