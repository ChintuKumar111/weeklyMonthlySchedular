package com.example.freshyzoappmodule.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.model.DayDateModel



class DayQuantityAdapter(
    private val list: MutableList<DayDateModel>,
    private val onQuantityChanged: (total: Int) -> Unit,
    private val onSelectionChanged: (hasSelection: Boolean) -> Unit
) : RecyclerView.Adapter<DayQuantityAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val item = list[position]

        holder.txtDay.text = item.day
        holder.txtQuantityNumber.text = item.quantity.toString()

        val shouldHighlight = item.quantity > 0 || item.isSelected
        holder.txtQuantityNumber.visibility = if (shouldHighlight) View.VISIBLE else View.GONE

        if (shouldHighlight) {
            holder.cardCircle.setCardBackgroundColor(Color.parseColor("#2E7D32"))
            holder.txtDay.setTextColor(Color.WHITE)
            holder.txtQuantityNumber.setTextColor(Color.WHITE)
        } else {
            holder.cardCircle.setCardBackgroundColor(Color.WHITE)
            holder.txtDay.setTextColor(Color.parseColor("#2E7D32"))
            holder.txtQuantityNumber.setTextColor(Color.parseColor("#2E7D32"))
        }

        holder.itemView.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

            val clickedItem = list[pos]
            val wasSelected = clickedItem.isSelected

            list.forEach { it.isSelected = false }

            if (wasSelected) {
                clickedItem.isSelected = false
                clickedItem.quantity = 0
                onQuantityChanged(list.sumOf { it.quantity } * 60)
            } else {
                clickedItem.isSelected = true
                if (clickedItem.quantity == 0) {
                    clickedItem.quantity = 1
                    onQuantityChanged(list.sumOf { it.quantity } * 60)
                }
            }

            notifyDataSetChanged()
            onSelectionChanged(list.any { it.isSelected })
        }
    }

    override fun getItemCount() = list.size

    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDay: TextView = view.findViewById(R.id.txtDay)
        val txtQuantityNumber: TextView = view.findViewById(R.id.txtQuantityNumber)
        val cardCircle: CardView = view.findViewById(R.id.cardCircle)
    }
}
