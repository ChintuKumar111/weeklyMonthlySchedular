package com.example.freshyzoappmodule.search.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.search.model.ProductModel

class ProductAdapter(private val list: List<ProductModel>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_product_details, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = list[position]

        holder.productName.text = product.product_name
        holder.productSellingPrice.text = "₹${product.product_price}"
        
        holder.productMrp.text = "₹${product.dairy_mrp}"

        // Set strike-through effect programmatically
        holder.productMrp.paintFlags = holder.productMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        
        holder.productShortDesc.text = product.short_desc

        Glide.with(holder.itemView.context)
            .load("https://freshyzo.com/admin/uploads/product_image/" + product.dairy_product_image)
            .into(holder.productImage)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName = view.findViewById<TextView>(R.id.tvProductName)
        val productShortDesc = view.findViewById<TextView>(R.id.tvProductShortDesc)
        val productImage = view.findViewById<ImageView>(R.id.imgProductImage)
        val productMrp = view.findViewById<TextView>(R.id.tvMrp)
        val productSellingPrice = view.findViewById<TextView>(R.id.tvSellingPrice)
        val addToCart = view.findViewById<TextView>(R.id.tvAddToCart)
    }
}
