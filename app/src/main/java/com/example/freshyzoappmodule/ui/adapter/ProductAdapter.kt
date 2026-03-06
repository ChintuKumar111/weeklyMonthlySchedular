package com.example.freshyzoappmodule.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.ProductSize
import com.example.freshyzoappmodule.databinding.ItemProductCardBinding
import com.example.freshyzoappmodule.extensions.badgeText
import com.example.freshyzoappmodule.extensions.discountPercent
import com.example.freshyzoappmodule.extensions.id
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.sizes
import com.example.freshyzoappmodule.extensions.tag

class ProductAdapter(
    private val onAddClick: (Product, ProductSize, Int) -> Unit,
    private val onQtyChange: (Product, ProductSize, Int) -> Unit,
    private val onSubscribeClick: (Product) -> Unit,
    private val onProductClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {
    private val selectedSizeMap = mutableMapOf<Int, Int>()
    private val qtyMap = mutableMapOf<Int, Int>()
    fun setInitialQuantities(quantities: Map<Int, Int>) {
        qtyMap.clear()
        qtyMap.putAll(quantities)
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(private val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(product: Product) {
            val selectedSizeIndex = selectedSizeMap[product.id] ?: 0
            val selectedSize = product.sizes.getOrNull(selectedSizeIndex) ?: product.sizes.firstOrNull()
            val qty = qtyMap[product.id] ?: 0

            binding.tvTag.text = product.tag
            binding.tvProductName.text = product.productName
            binding.tvDesc.text = product.shortDesc
            
            Glide.with(binding.ivProduct.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.logo)
                .into(binding.ivProduct)

            if (product.badgeText.isNotEmpty()) {
                binding.tvBadge.visibility = View.VISIBLE
                binding.tvBadge.text = product.badgeText
            } else {
                binding.tvBadge.visibility = View.GONE
            }

            selectedSize?.let { updatePrice(it) }
            renderSizeChips(product, selectedSizeIndex)
            selectedSize?.let { updateQtyUi(qty, product, it) }

            binding.tvMinus.setOnClickListener {
                val current = qtyMap[product.id] ?: 0
                if (current > 0) {
                    val newQty = current - 1
                    qtyMap[product.id] = newQty
                    selectedSize?.let { s -> 
                        updateQtyUi(newQty, product, s)
                        onQtyChange(product, s, -1)
                    }
                }
            }

            binding.tvPlus.setOnClickListener {
                val current = qtyMap[product.id] ?: 0
                val newQty = current + 1
                qtyMap[product.id] = newQty
                selectedSize?.let { s -> 
                    updateQtyUi(newQty, product, s)
                    onQtyChange(product, s, 1)
                }
            }

            binding.btnAdd.setOnClickListener {
                qtyMap[product.id] = 1
                selectedSize?.let { s -> 
                    updateQtyUi(1, product, s)
                    onAddClick(product, s, 1)
                }
            }

            binding.btnSubscribe.setOnClickListener {
                onSubscribeClick(product)
            }

            binding.root.setOnClickListener {
                onProductClick(product)
            }
        }

        private fun updatePrice(size: ProductSize)
        {
            binding.tvPrice.text = "₹${size.price}"
            binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvDiscount.text = "${size.discountPercent}% OFF"
        }

        private fun renderSizeChips(product: Product, selectedIndex: Int) {
            val sizes = product.sizes
            binding.tvSize1.text = sizes.getOrNull(0)?.label ?: ""
            binding.tvSize1.isSelected = true 
            binding.tvSize1.setBackgroundResource(R.drawable.bg_size_chip_active)
        }

        private fun updateQtyUi(qty: Int, product: Product, size: ProductSize) {
            if (qty > 0) {
                binding.llQtyCtrl.visibility = View.VISIBLE
                binding.btnAdd.visibility = View.GONE
                binding.tvQty.text = qty.toString()
            } else {
                binding.llQtyCtrl.visibility = View.GONE
                binding.btnAdd.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))

    }
}

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product) =
        oldItem == newItem
}
