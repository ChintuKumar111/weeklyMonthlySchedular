package com.example.freshyzoappmodule.ui.adapter

import android.app.Activity
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.cartStateModel
import com.example.freshyzoappmodule.data.repository.CartRepository
import com.example.freshyzoappmodule.databinding.ItemCartProductBinding
import com.example.freshyzoappmodule.extensions.discountPercent
import com.example.freshyzoappmodule.extensions.id
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.price
import com.example.freshyzoappmodule.extensions.sizes
import com.example.freshyzoappmodule.ui.activity.NewHomeActivity

class CartAdapter(
    private var products: List<Product>,
    private var quantities: Map<Int, Int>,
    private val cartRepository: CartRepository,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartProductBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = products[position]
        val qty = quantities[product.id] ?: 0
        val size = product.sizes.firstOrNull()

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
            (holder.itemView.context as? NewHomeActivity)?.updateSharedCart(product, price, 1) { newState ->
                products = newState.products
                quantities = newState.productQuantities
                notifyDataSetChanged()
                onCartUpdated()
            }
        }

        holder.binding.btnMinus.setOnClickListener {
            val price = size?.price?.toDouble() ?: 0.0
            (holder.itemView.context as? NewHomeActivity)?.updateSharedCart(product, -price, -1) { newState ->
                products = newState.products
                quantities = newState.productQuantities
                notifyDataSetChanged()
                onCartUpdated()
            }
        }
    }

    fun updateData(newProducts: List<Product>, newQuantities: Map<Int, Int>) {
        products = newProducts
        quantities = newQuantities
        notifyDataSetChanged()
    }
}
