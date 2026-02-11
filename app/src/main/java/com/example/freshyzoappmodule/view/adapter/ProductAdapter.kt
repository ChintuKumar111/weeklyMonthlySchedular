package com.example.freshyzoappmodule.view.adapter

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.ProductModel
import com.example.freshyzoappmodule.view.Activity.ProductDetailsActivity

class ProductAdapter(
    private var list: List<ProductModel>,
    private val onQuantityChanged: (ProductModel, Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

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
        holder.productMrp.paintFlags = holder.productMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        holder.productShortDesc.text = product.short_desc

        Glide.with(holder.itemView.context)
            .load("https://freshyzo.com/admin/uploads/product_image/" + product.dairy_product_image)
            .into(holder.productImage)

        // Handle Visibility based on quantity
        if (product.quantity > 0) {
            holder.addToCart.visibility = View.GONE
            holder.llQuantityContainer.visibility = View.VISIBLE
            holder.tvQuantity.text = product.quantity.toString()
        } else {
            holder.addToCart.visibility = View.VISIBLE
            holder.llQuantityContainer.visibility = View.GONE
        }

        holder.addToCart.setOnClickListener {
            product.quantity = 1
            notifyItemChanged(position)
            onQuantityChanged(product, 1)
        }

        holder.btnPlus.setOnClickListener {
            product.quantity++
            notifyItemChanged(position)
            onQuantityChanged(product, 1)
        }

        holder.btnMinus.setOnClickListener {
            if (product.quantity > 0) {
                product.quantity--
                notifyItemChanged(position)
                onQuantityChanged(product, -1)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailsActivity::class.java)
            intent.putExtra("product", product)
            holder.itemView.context.startActivity(intent)
        }
    }

    fun updateList(newList: List<ProductModel>) {
        list = newList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName = view.findViewById<TextView>(R.id.tvProductName)
        val productShortDesc = view.findViewById<TextView>(R.id.tvProductShortDesc)
        val productImage = view.findViewById<ImageView>(R.id.imgProductImage)
        val productMrp = view.findViewById<TextView>(R.id.tvMrp)
        val productSellingPrice = view.findViewById<TextView>(R.id.tvSellingPrice)
        val addToCart = view.findViewById<TextView>(R.id.tvAddToCart)
        val llQuantityContainer = view.findViewById<LinearLayout>(R.id.llQuantityContainer)
        val btnMinus = view.findViewById<TextView>(R.id.btnMinus)
        val btnPlus = view.findViewById<TextView>(R.id.btnPlus)
        val tvQuantity = view.findViewById<TextView>(R.id.tvQuantity)

    }
}
