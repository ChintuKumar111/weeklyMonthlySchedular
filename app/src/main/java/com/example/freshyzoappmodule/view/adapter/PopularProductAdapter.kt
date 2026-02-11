package com.example.freshyzoappmodule.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.PopularProductModel

class PopularProductAdapter(
    private val list: List<PopularProductModel>,
    private val onItemClick: (PopularProductModel) -> Unit
) : RecyclerView.Adapter<PopularProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgProduct)
        val name = view.findViewById<TextView>(R.id.tvPopularProductName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_popular_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.img.setImageResource(item.image)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = list.size
}