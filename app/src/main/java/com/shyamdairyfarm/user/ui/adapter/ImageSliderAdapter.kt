package com.shyamdairyfarm.user.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shyamdairyfarm.user.data.model.response.Banner
import com.shyamdairyfarm.user.databinding.ItemImageSliderBinding

class ImageSliderAdapter(
    private val images: List<Banner>,
    private val onBannerClick: (Banner) -> Unit = {})
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

        holder.itemView.setOnClickListener {
            onBannerClick(item)
        }
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root)
}