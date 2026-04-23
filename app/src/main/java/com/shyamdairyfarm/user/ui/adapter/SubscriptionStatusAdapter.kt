package com.shyamdairyfarm.user.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.model.response.SubscriptionResponse
import java.util.Locale

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

        applyStatusStyle(holder, item.status)

        // Show/Hide buttons based on status
        val status = item.status.lowercase(Locale.getDefault())
        if (status == "active") {
            holder.btnPause.visibility = View.VISIBLE
            holder.btnCancel.visibility = View.VISIBLE
        } else {
            holder.btnPause.visibility = View.GONE
            holder.btnCancel.visibility = View.GONE
        }

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

    private fun applyStatusStyle(holder: ViewHolder, status: String) {
        val context = holder.itemView.context
        when (status.lowercase(Locale.getDefault())) {
            "active" -> {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.status.setBackgroundResource(R.drawable.bg_status_active)
                holder.status.backgroundTintList = ContextCompat.getColorStateList(context, R.color.primary_dark)
            }
            "pause" -> {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.status.setBackgroundResource(R.drawable.bg_status_active) // Reusing same drawable for shape
                holder.status.backgroundTintList = ContextCompat.getColorStateList(context, R.color.amber)
            }
            "cancel" -> {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.status.setBackgroundResource(R.drawable.bg_status_active)
                holder.status.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red_error)
            }
            else -> {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.status.setBackgroundResource(R.drawable.bg_status_active)
                holder.status.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gray_500)
            }
        }
    }

}
