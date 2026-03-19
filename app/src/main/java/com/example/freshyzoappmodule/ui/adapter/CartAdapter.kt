package com.example.freshyzoappmodule.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.ProductDetails
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.ItemCartProductRvBinding
import com.example.freshyzoappmodule.extensions.discountPercent
import com.example.freshyzoappmodule.extensions.id
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.variant
import com.example.freshyzoappmodule.ui.activity.HomeActivity

class CartAdapter(
    private var productDetails: List<ProductDetails>,
    private var quantities: Map<Int, Int>,
    private val cartRepository: CartRepository,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartProductRvBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductRvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun getItemCount() = productDetails.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = productDetails[position]
        val qty = quantities[product.id] ?: 0
        val size = product.variant.firstOrNull()

        holder.binding.tvProductName.text = product.productName
        holder.binding.tvQty.text = qty.toString()
        
        size?.let { s ->
            holder.binding.tvSellingPrice3.text = "₹${s.price}"
            holder.binding.tvOriginalPrice.text = "₹${s.originalPrice}"
            holder.binding.tvOriginalPrice.paintFlags = holder.binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.tvDiscount.text = "${s.discountPercent}% OFF"
            holder.binding.tvVolume.text = s.label
        }

        Glide.with(holder.binding.imgAddedInCart.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.milk_)
            .into(holder.binding.imgAddedInCart)

        holder.binding.btnPlus.setOnClickListener {
            val price = size?.price?.toDouble() ?: 0.0
            (holder.itemView.context as? HomeActivity)?.updateSharedCart(product, price, 1) { newState ->
                productDetails = newState.productDetails
                quantities = newState.productQuantities
                notifyDataSetChanged()
                onCartUpdated()
            }
        }

        holder.binding.btnMinus.setOnClickListener {
            val price = size?.price?.toDouble() ?: 0.0
            (holder.itemView.context as? HomeActivity)?.updateSharedCart(product, -price, -1) { newState ->
                productDetails = newState.productDetails
                quantities = newState.productQuantities
                notifyDataSetChanged()
                onCartUpdated()
            }
        }
    }

    fun updateData(newProductDetails: List<ProductDetails>, newQuantities: Map<Int, Int>) {
        productDetails = newProductDetails
        quantities = newQuantities
        notifyDataSetChanged()
    }
}
