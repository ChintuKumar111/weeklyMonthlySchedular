package com.example.freshyzoappmodule.ui.adapter

// ── adapters/ProductAdapter.kt ──

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

class ProductAdapterr(
    private val onAddClick: (Product, ProductSize, Int) -> Unit,
    private val onSubscribeClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapterr.ProductViewHolder>(ProductDiffCallback()) {

    // Tracks selected size index per product id
    private val selectedSizeMap = mutableMapOf<Int, Int>()

    // Tracks quantity per product id
    private val qtyMap = mutableMapOf<Int, Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ProductViewHolder(private val binding: ItemProductCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            val selectedSizeIndex = selectedSizeMap[product.id] ?: 0
            val selectedSize =
                product.sizes.getOrNull(selectedSizeIndex) ?: product.sizes.firstOrNull()
            val qty = qtyMap[product.id] ?: 0

            // ── Basic Info ──
            binding.tvTag.text = product.tag
            binding.tvProductName.text = product.name
            binding.tvDesc.text = product.short_description

            Glide.with(binding.ivProduct.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.app_icon)
                .into(binding.ivProduct)

            // ── Badge ──
            if (product.badgeText.isNotEmpty()) {
                binding.tvBadge.visibility = View.VISIBLE
                binding.tvBadge.text = product.badgeText
            } else {
                binding.tvBadge.visibility = View.GONE
            }

            // ── Price ──
            selectedSize?.let { updatePrice(it) }

            // ── Size Chips ──
            renderSizeChips(product, selectedSizeIndex)

            // ── Quantity ──
            selectedSize?.let { updateQtyUi(qty, product, it) }

            // ── Minus ──
            binding.tvMinus.setOnClickListener {
                val current = qtyMap[product.id] ?: 0
                if (current > 0) {
                    qtyMap[product.id] = current - 1
                    selectedSize?.let { s -> updateQtyUi(current - 1, product, s) }
                }
            }

            // ── Plus ──
            binding.tvPlus.setOnClickListener {
                val current = qtyMap[product.id] ?: 0
                qtyMap[product.id] = current + 1
                selectedSize?.let { s -> updateQtyUi(current + 1, product, s) }
            }

            // ── Add to Cart ──
//            binding.btnAdd.setOnClickListener {
//                val sizeIndex = selectedSizeMap[product.id] ?: 0
//                val size = product.sizes.getOrNull(sizeIndex) ?: product.sizes.first()
//                val q = qtyMap[product.id] ?: 1
//                onAddClick(product, size, if (q == 0) 1 else q)
//            }

            // ── Subscribe ──
            binding.btnSubscribe.setOnClickListener {
                onSubscribeClick(product)
            }


        }

        private fun updatePrice(size: ProductSize) {
            binding.tvPrice.text = "₹${size.price}"
            binding.tvOriginalPrice.text = "₹${size.originalPrice}"
            binding.tvDiscount.text = "${size.discountPercent}% OFF"
        }

        private fun renderSizeChips(product: Product, selectedIndex: Int) {
            val sizes = product.sizes

            binding.tvSize1.text = sizes.getOrNull(0)?.label ?: ""
            binding.tvSize2.text = sizes.getOrNull(1)?.label ?: ""

            binding.tvSize1.isSelected = (selectedIndex == 0)
            binding.tvSize2.isSelected = (selectedIndex == 1)

            binding.tvSize1.setBackgroundResource(
                if (selectedIndex == 0) R.drawable.bg_size_chip_active
                else R.drawable.bg_size_chip_inactive
            )
            binding.tvSize2.setBackgroundResource(
                if (selectedIndex == 1) R.drawable.bg_size_chip_active
                else R.drawable.bg_size_chip_inactive
            )

            binding.tvSize1.setOnClickListener {
                if (product.sizes.isNotEmpty()) {
                    selectedSizeMap[product.id] = 0
                    renderSizeChips(product, 0)
                    updatePrice(product.sizes[0])
                }
            }
            binding.tvSize2.setOnClickListener {
                if (product.sizes.size > 1) {
                    selectedSizeMap[product.id] = 1
                    renderSizeChips(product, 1)
                    updatePrice(product.sizes[1])
                }
            }
        }

        private fun updateQtyUi(qty: Int, product: Product, size: ProductSize) {
            binding.tvQty.text = qty.toString()
            // Dim minus button when qty is 0
            binding.tvMinus.alpha = if (qty > 0) 1f else 0.4f
        }
    }
}


// ── DiffUtil for efficient list updates ──
class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Product, newItem: Product) =
        oldItem == newItem
}
