package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.response.SubscriptionResponse
class SubscriptionStatusAdapter(
    private var list: List<SubscriptionResponse>,
    private val onPauseClick: (SubscriptionResponse) -> Unit,
    private val onCancelClick: (SubscriptionResponse) -> Unit
) : RecyclerView.Adapter<SubscriptionStatusAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val name: TextView = view.findViewById(R.id.txtProductName)
        val price: TextView = view.findViewById(R.id.txtPrice)
        val delivery: TextView = view.findViewById(R.id.txtDeliveryType)
        val start: TextView = view.findViewById(R.id.txtStartDate)
        val end: TextView = view.findViewById(R.id.txtEndDate)
        val status: TextView = view.findViewById(R.id.txtStatus)

        val btnPause: TextView = view.findViewById(R.id.btnPause)
        val btnCancel: TextView = view.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscription_status, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.name.text = item.productName
        holder.price.text = "₹${item.price}"
        holder.delivery.text = "Delivery: ${item.deliveryType}"
        holder.start.text = "Start: ${item.startDate}"
        holder.end.text = "End: ${item.endDate}"
        holder.status.text = item.status

        holder.btnPause.setOnClickListener {
            onPauseClick(item)
        }

        holder.btnCancel.setOnClickListener {
            onCancelClick(item)
        }
    }

    fun updateList(newList: List<SubscriptionResponse>) {
        list = newList
        notifyDataSetChanged()
    }
}