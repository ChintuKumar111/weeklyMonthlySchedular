package com.shyamdairyfarm.user.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shyamdairyfarm.user.data.model.HomeBlogs
import com.shyamdairyfarm.user.databinding.ItemBlogReportBinding

class HomeBlogAdapter(
    private var homeBlogs: List<HomeBlogs>,
    private val onBlogClick: (HomeBlogs) -> Unit
) : RecyclerView.Adapter<HomeBlogAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBlogReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlogReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blog = homeBlogs[position]
        holder.binding.apply {
            tvBlogTitle.text = blog.title
            tvBlogDesc.text = blog.description
            
            Glide.with(ivBlogImage.context)
                .load(blog.imageUrl)
                .centerCrop()
                .into(ivBlogImage)

            // Click listener for the "Learn More" button
            btnLearnMore.setOnClickListener { onBlogClick(blog) }
            
            // Click listener for the entire card for better UX
            root.setOnClickListener { onBlogClick(blog) }
        }
    }

    override fun getItemCount(): Int = homeBlogs.size

    fun updateList(newList: List<HomeBlogs>) {
        homeBlogs = newList
        notifyDataSetChanged()
    }
}
