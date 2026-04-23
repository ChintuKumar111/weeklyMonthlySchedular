package com.shyamdairyfarm.user.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shyamdairyfarm.user.data.model.HomeComboOffers
import com.shyamdairyfarm.user.databinding.ItemComboOfferBinding

class HomeComboOfferAdapter(
    private var homeComboOffers: List<HomeComboOffers>,
    private val onAddClick: (HomeComboOffers) -> Unit
) : RecyclerView.Adapter<HomeComboOfferAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemComboOfferBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemComboOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val combo = homeComboOffers[position]
        holder.binding.apply {
            tvComboTitle.text = combo.title
            tvComboDesc.text = combo.description
            tvComboPrice.text = "₹${combo.price}"
            
            Glide.with(ivComboImage.context)
                .load(combo.imageUrl)
                .into(ivComboImage)

            btnAddToCart.setOnClickListener { onAddClick(combo) }
        }
    }

    override fun getItemCount(): Int = homeComboOffers.size

    fun updateList(newList: List<HomeComboOffers>) {
        homeComboOffers = newList
        notifyDataSetChanged()
    }
}
