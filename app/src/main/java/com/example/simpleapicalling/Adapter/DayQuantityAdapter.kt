package com.example.freshyzoappmodule.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.simpleapicalling.model.DayDateModel

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

    // data binding from item_day.xml with DayDateModel
    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val item = list[position]

        holder.txtDay.text = item.day
        holder.txtQuantityNumber.text = item.quantity.toString()

        // Highlight if the item is selected OR has a quantity > 0
        val shouldHighlight = item.quantity > 0 || item.isSelected

        // Show quantity text only when day is selected or has quantity
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

            // Clear selection highlight from all items
            list.forEach { it.isSelected = false }

            if (wasSelected) {
                // Toggle off: Deselect AND reset quantity to 0
                clickedItem.isSelected = false
                clickedItem.quantity = 0
                
                // Update total price in Activity since quantity changed to 0
                val total = list.sumOf { it.quantity } * 60
                onQuantityChanged(total)

            } else {
                //  Toggle on: Select only the clicked item
                clickedItem.isSelected = true

                //  Initial Selection: Increase quantity to 1 if it was 0
                if (clickedItem.quantity == 0) {
                    clickedItem.quantity = 1

                    // Update total price in Activity
                    val total = list.sumOf { it.quantity } * 60
                    onQuantityChanged(total)
                }
            }

            // Update UI and notify selection status
            notifyDataSetChanged()
            val hasActiveSelection = list.any { it.isSelected }
            onSelectionChanged(hasActiveSelection)
        }
    }

    override fun getItemCount() = list.size

/////////////////////////////////////////////////////////////////////////////////////////////
    // View
    inner class DateViewHolder(view: View)
        : RecyclerView.ViewHolder(view)
    {
        val txtDay: TextView = view.findViewById(R.id.txtDay)
        val txtQuantityNumber: TextView = view.findViewById(R.id.txtQuantityNumber)
        val cardCircle: CardView = view.findViewById(R.id.cardCircle)
    }

}
