package com.example.freshyzoappmodule.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R

class RecentSearchAdapter(
    private var list: List<String>,
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = list[position]
        holder.tvRecentText.text = text
        holder.itemView.setOnClickListener { onItemClick(text) }
        holder.btnRemoveRecent.setOnClickListener { onDeleteClick(text) }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<String>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecentText = view.findViewById<TextView>(R.id.tvRecentText)
        val btnRemoveRecent = view.findViewById<ImageView>(R.id.btnRemoveRecent)
    }
}
