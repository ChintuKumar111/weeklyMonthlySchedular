package com.example.freshyzoappmodule.ViewPager_.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.freshyzoappmodule.ViewPager_.data.model.ProductResponse

import com.example.freshyzoappmodule.databinding.ItemProductTestBinding


class ProductAdapter(
    private val onProductClick: (ProductResponse) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var productList: List<ProductResponse> = emptyList()

    inner class ProductViewHolder(
        val binding: ItemProductTestBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {

        val binding = ItemProductTestBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {

        val product = productList[position]

        holder.binding.apply {

            productTitle.text = product.title
            productDesc.text = product.description
            productPrice.text = "₹ ${product.price}"

            // DummyJSON has thumbnail also
            productImage.load(product.images.firstOrNull())

            root.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    fun submitList(list: List<ProductResponse>) {
        productList = list
        notifyDataSetChanged()
    }
}
