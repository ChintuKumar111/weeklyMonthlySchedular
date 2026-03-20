package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.HomeProductDeliveryCalendar

class CalendarProductDeliveryDetailsAdapter(private val products: List<HomeProductDeliveryCalendar>) :
    RecyclerView.Adapter<CalendarProductDeliveryDetailsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProductImage: ImageView = view.findViewById(R.id.ivProductImage)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvProductQuantity: TextView = view.findViewById(R.id.tvProductQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dialog_delivery_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvProductName.text = product.name
        holder.tvProductQuantity.text = product.quantity

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.logo)
            .error(R.drawable.ghee)
            .into(holder.ivProductImage)
    }

    override fun getItemCount() = products.size
}
