package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.data.model.Banner
import com.example.freshyzoappmodule.databinding.ItemImageSliderBinding

class ImageSliderAdapter(
    private val images: List<Banner>)
    : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
      val item = images[position]

        Glide.with(holder.itemView.context)
            .load(item.image)
            .into(holder.binding.imageView )
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            Glide.with(binding.root.context)
                .load(imageUrl)
                .into(binding.imageView)
        }
    }
}