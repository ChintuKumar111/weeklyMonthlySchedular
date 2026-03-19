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
import com.example.freshyzoappmodule.data.model.ProductDetails
import com.example.freshyzoappmodule.data.model.ProductVariant
import com.example.freshyzoappmodule.databinding.ItemProductCardBinding
import com.example.freshyzoappmodule.extensions.badgeText
import com.example.freshyzoappmodule.extensions.discountPercent
import com.example.freshyzoappmodule.extensions.id
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.variant
import com.example.freshyzoappmodule.extensions.tag

class ProductDetailsAdapter(
    private val onAddClick: (ProductDetails, ProductVariant, Int) -> Unit,
    private val onQtyChange: (ProductDetails, ProductVariant, Int) -> Unit,
    private val onSubscribeClick: (ProductDetails) -> Unit,
    private val onProductClick: (ProductDetails) -> Unit
) : ListAdapter<ProductDetails, ProductDetailsAdapter.ProductViewHolder>(ProductDiffCallback()) {
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
        fun bind(productDetails: ProductDetails) {
            val selectedSizeIndex = selectedSizeMap[productDetails.id] ?: 0
            val selectedSize = productDetails.variant.getOrNull(selectedSizeIndex) ?: productDetails.variant.firstOrNull()
            val qty = qtyMap[productDetails.id] ?: 0

            binding.tvTag.text = productDetails.tag
            binding.tvProductName.text = productDetails.productName
            binding.tvDesc.text = productDetails.shortDesc
            
            Glide.with(binding.ivProduct.context)
                .load(productDetails.imageUrl)
                .placeholder(R.drawable.logo)
                .into(binding.ivProduct)

            if (productDetails.badgeText.isNotEmpty()) {
                binding.tvBadge.visibility = View.VISIBLE
                binding.tvBadge.text = productDetails.badgeText
            } else {
                binding.tvBadge.visibility = View.GONE
            }

            selectedSize?.let { updatePrice(it) }
            renderSizeChips(productDetails, selectedSizeIndex)
            selectedSize?.let { updateQtyUi(qty, productDetails, it) }

            binding.tvMinus.setOnClickListener {
                val current = qtyMap[productDetails.id] ?: 0
                if (current > 0) {
                    val newQty = current - 1
                    qtyMap[productDetails.id] = newQty
                    selectedSize?.let { s -> 
                        updateQtyUi(newQty, productDetails, s)
                        onQtyChange(productDetails, s, -1)
                    }
                }
            }

            binding.tvPlus.setOnClickListener {
                val current = qtyMap[productDetails.id] ?: 0
                val newQty = current + 1
                qtyMap[productDetails.id] = newQty
                selectedSize?.let { s -> 
                    updateQtyUi(newQty, productDetails, s)
                    onQtyChange(productDetails, s, 1)
                }
            }

            binding.btnAdd.setOnClickListener {
                qtyMap[productDetails.id] = 1
                selectedSize?.let { s -> 
                    updateQtyUi(1, productDetails, s)
                    onAddClick(productDetails, s, 1)
                }
            }

            binding.btnSubscribe.setOnClickListener {
                onSubscribeClick(productDetails)
            }

            binding.root.setOnClickListener {
                onProductClick(productDetails)
            }
        }

        private fun updatePrice(variant: ProductVariant)
        {
            binding.tvPrice.text = "₹${variant.price}"
            binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvDiscount.text = "${variant.discountPercent}% OFF"
        }

        private fun renderSizeChips(productDetails: ProductDetails, selectedIndex: Int) {
            val variant = productDetails.variant
            binding.tvSize1.text = variant.getOrNull(0)?.label ?: ""
            binding.tvSize1.isSelected = true 
            binding.tvSize1.setBackgroundResource(R.drawable.bg_size_chip_active)
        }

        private fun updateQtyUi(qty: Int, productDetails: ProductDetails, size: ProductVariant) {
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

class ProductDiffCallback : DiffUtil.ItemCallback<ProductDetails>() {
    override fun areItemsTheSame(oldItem: ProductDetails, newItem: ProductDetails) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ProductDetails, newItem: ProductDetails) =
        oldItem == newItem
}
