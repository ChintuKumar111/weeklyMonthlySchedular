package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.data.model.ComboOffer
import com.example.freshyzoappmodule.databinding.ItemComboOfferBinding

class ComboOfferAdapter(
    private var comboOffers: List<ComboOffer>,
    private val onAddClick: (ComboOffer) -> Unit
) : RecyclerView.Adapter<ComboOfferAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemComboOfferBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemComboOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val combo = comboOffers[position]
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

    override fun getItemCount(): Int = comboOffers.size

    fun updateList(newList: List<ComboOffer>) {
        comboOffers = newList
        notifyDataSetChanged()
    }
}
