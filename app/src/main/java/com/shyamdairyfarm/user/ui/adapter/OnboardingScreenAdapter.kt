package com.shyamdairyfarm.user.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shyamdairyfarm.user.data.model.OnboardingItems
import com.shyamdairyfarm.user.databinding.ItemOnboardingBinding

class OnboardingScreenAdapter(private val items: List<OnboardingItems>) :
    RecyclerView.Adapter<OnboardingScreenAdapter.OnboardingViewHolder>() {
    inner class OnboardingViewHolder(private val binding: ItemOnboardingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OnboardingItems) {
            binding.animationView.setAnimation(item.animationRes)
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            ItemOnboardingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}