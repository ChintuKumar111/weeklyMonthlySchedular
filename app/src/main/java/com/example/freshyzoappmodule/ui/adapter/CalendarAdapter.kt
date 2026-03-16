package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.CalendarDay
class CalendarAdapter(
    private val list: MutableList<CalendarDay>,
    private val click: (CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.VH>() {
    var selectedPosition = -1
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvToday = view.findViewById<TextView>(R.id.tvToday)
        val layoutMonth = view.findViewById<View>(R.id.layoutMonth)
        val tvMonthName = view.findViewById<TextView>(R.id.tvMonthName)
        val tvDay = view.findViewById<TextView>(R.id.tvDay)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val deliveryDot = view.findViewById<View>(R.id.deliveryDot)
        val innerLayout = view.findViewById<LinearLayout>(R.id.innerLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return VH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        // Month Header
        if (item.isMonthHeader) {
            holder.layoutMonth.visibility = View.VISIBLE
            holder.tvMonthName.text = item.monthName
        } else {
            holder.layoutMonth.visibility = View.GONE
        }

        holder.tvDay.text = item.dayName
        holder.tvDate.text = item.dateNumber.toString().padStart(2, '0')

        // Today Badge
        if (item.isToday) {
            holder.tvToday.visibility = View.VISIBLE
        } else {
            holder.tvToday.visibility = View.INVISIBLE
        }

        // Delivery Dot
        holder.deliveryDot.setBackgroundResource(
            if (item.hasDelivery) R.drawable.dot_emerald else R.drawable.dot_gray
        )

        // Styling based on selection or today
        val context = holder.itemView.context
        if (position == selectedPosition) {
            holder.innerLayout.setBackgroundResource(R.drawable.bg_selected_date)
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.primary))
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.primary))
        } else if (item.isToday) {
            holder.innerLayout.setBackgroundResource(R.drawable.bg_btn_outline)
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.orange_semi_primary))
        } else {
            holder.innerLayout.setBackgroundResource(0)
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            holder.tvDay.setTextColor(ContextCompat.getColor(context, R.color.orange_semi_primary))
        }

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            click(item)
        }
    }
}