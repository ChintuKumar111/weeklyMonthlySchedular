package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.data.model.categoryModel
import com.example.freshyzoappmodule.databinding.ItemCategorySidebarBinding

class CategoryAdapter(
    private val categories: List<categoryModel>,
    private val onCategoryClick: (categoryModel, Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(
        private val binding: ItemCategorySidebarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: categoryModel, isSelected: Boolean) {
            binding.tvCatName.text = category.name
            binding.ivCatIcon.setImageResource(category.iconRes)

            // Toggle active state — drives selector_cat_item.xml drawable
            binding.llCatItem.isActivated = isSelected

            binding.llCatItem.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                
                val previous = selectedPosition
                selectedPosition = currentPosition
                notifyItemChanged(previous)
                notifyItemChanged(selectedPosition)
                onCategoryClick(category, currentPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategorySidebarBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position], position == selectedPosition)
    }

    override fun getItemCount() = categories.size
}
