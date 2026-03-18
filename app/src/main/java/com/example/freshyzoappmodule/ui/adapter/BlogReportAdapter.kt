package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.data.model.BlogReport
import com.example.freshyzoappmodule.databinding.ItemBlogReportBinding

class BlogReportAdapter(
    private var blogReports: List<BlogReport>,
    private val onBlogClick: (BlogReport) -> Unit
) : RecyclerView.Adapter<BlogReportAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBlogReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlogReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val blog = blogReports[position]
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

    override fun getItemCount(): Int = blogReports.size

    fun updateList(newList: List<BlogReport>) {
        blogReports = newList
        notifyDataSetChanged()
    }
}
