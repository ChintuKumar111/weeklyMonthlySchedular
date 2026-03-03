package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Complaint
import com.example.freshyzoappmodule.databinding.ItemComplaintBinding

class ComplaintAdapter(private val complaints: List<Complaint>) :
    RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    class ComplaintViewHolder(private val binding: ItemComplaintBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(complaint: Complaint) {
            binding.tvCategory.text = complaint.category
            binding.tvIssueType.text = complaint.issueType
            binding.tvDescription.text = complaint.description
            binding.tvDate.text = complaint.date
            binding.tvStatus.text = complaint.status

            // Status color logic
            when (complaint.status.lowercase()) {
                "resolved" -> binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_pass)
                "pending" -> binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_amber)
                else -> binding.tvStatus.setBackgroundResource(R.drawable.bg_rounded_grey)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val binding = ItemComplaintBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ComplaintViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        holder.bind(complaints[position])
    }

    override fun getItemCount() = complaints.size
}
