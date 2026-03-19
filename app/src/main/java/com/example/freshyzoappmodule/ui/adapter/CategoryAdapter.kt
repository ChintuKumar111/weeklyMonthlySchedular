package com.example.freshyzoappmodule.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.data.model.ProductCategory
import com.example.freshyzoappmodule.databinding.ItemCategorySidebarBinding

class CategoryAdapter(
    private val categories: List<ProductCategory>,
    private val onCategoryClick: (ProductCategory, Int) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0

    inner class CategoryViewHolder(
        private val binding: ItemCategorySidebarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(productCategory: ProductCategory, isSelected: Boolean) {
            binding.tvCatName.text = productCategory.name
            binding.ivCatIcon.setImageResource(productCategory.iconRes)

            // Toggle active state — drives selector_cat_item.xml drawable
            binding.llCatItem.isActivated = isSelected

            binding.llCatItem.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition == RecyclerView.NO_POSITION) return@setOnClickListener
                
                updateSelection(currentPosition)
                onCategoryClick(productCategory, currentPosition)
            }
        }
    }

    fun updateSelection(newPosition: Int) {
        if (newPosition == selectedPosition || newPosition !in categories.indices) return
        val previous = selectedPosition
        selectedPosition = newPosition
        notifyItemChanged(previous)
        notifyItemChanged(selectedPosition)
    }

    fun getCategoryIdAt(position: Int): Int? {
        return if (position in categories.indices) categories[position].id else null
    }

    fun getPositionForId(categoryId: Int): Int {
        return categories.indexOfFirst { it.id == categoryId }
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
