package com.example.freshyzoappmodule.ui.adapter

// ─────────────────────────────────────────────────────────────

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.DeliveryModel
import com.example.freshyzoappmodule.data.model.DeliveryStatus
import com.example.freshyzoappmodule.data.model.ProductType
import com.example.freshyzoappmodule.databinding.ItemOrderHistoryCardBinding

class OrderHistoryAdapter(
    private val onCardClick: (DeliveryModel) -> Unit
) : ListAdapter<DeliveryModel,
        OrderHistoryAdapter.DeliveryViewHolder>(DiffCallback()) {

    // ── DiffUtil ────────────────────────────────────────────────
    class DiffCallback : DiffUtil.ItemCallback<DeliveryModel>() {
        override fun areItemsTheSame(old: DeliveryModel, new: DeliveryModel) = old.id == new.id
        override fun areContentsTheSame(old: DeliveryModel, new: DeliveryModel) = old == new
    }

    // ── ViewHolder ──────────────────────────────────────────────
    inner class DeliveryViewHolder(
        private val binding: ItemOrderHistoryCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DeliveryModel) {
            val ctx = binding.root.context

            // ── Product info ────────────────────────────────────
            binding.tvProductEmoji.text = item.emoji
            binding.tvProductName.text = item.productName
            binding.tvBrandName.text = item.brandName
            // binding.tvTxnId.text         = "#${item.txnId}"
            binding.tvSize.text = item.size
            binding.tvQty.text = "×${item.quantity} unit${if (item.quantity > 1) "s" else ""}"
            binding.tvDate.text = item.date


//            // ── Product image background ────────────────────────
            binding.productImageBox.setBackgroundResource(
                when (item.productType) {
                    ProductType.MILK -> R.drawable.bg_product_image_milk
                    ProductType.GHEE -> R.drawable.bg_product_image_ghee
                }
            )

            // ── Size tag color ───────────────────────────────────
            binding.tvSize.setBackgroundResource(
                when (item.productType) {
                    ProductType.MILK -> R.drawable.bg_tag_green
                    ProductType.GHEE -> R.drawable.bg_tag_gold
                }
            )
            binding.tvSize.setTextColor(
                ContextCompat.getColor(
                    ctx, when (item.productType) {
                        ProductType.MILK -> R.color.emerald
                        ProductType.GHEE -> R.color.gold
                    }
                )
            )

            // ── Brand name color ─────────────────────────────────
            binding.tvBrandName.setTextColor(
                ContextCompat.getColor(
                    ctx, when (item.productType) {
                        ProductType.MILK -> R.color.emerald
                        ProductType.GHEE -> R.color.gold
                    }
                )
            )


            // ── Status badge ─────────────────────────────────────
            applyStatus(ctx, item)

            // ── Click listener ───────────────────────────────────
            binding.root.setOnClickListener {
                it.animate()
                    .scaleX(0.97f).scaleY(0.97f).setDuration(80)
                    .withEndAction {
                        it.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                        onCardClick(item)
                    }.start()
            }
        }

        private fun applyStatus(ctx: Context, item: DeliveryModel) {
            when (item.status) {

                DeliveryStatus.PLACED -> {
                    binding.tvStatusBadge.text = "● Placed"
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.emerald))
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_placed)

                    // Normal appearance
                    binding.tvProductName.paintFlags =
                        binding.tvProductName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    binding.root.alpha = 1f

                }

                DeliveryStatus.PENDING -> {
                    binding.tvStatusBadge.text = "⏳ Pending"
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.amber))
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending)

                    binding.tvProductName.paintFlags =
                        binding.tvProductName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    binding.root.alpha = 1f

                }

                DeliveryStatus.CANCELLED -> {
                    binding.tvStatusBadge.text = "✕ Cancelled"
                    binding.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.coral))
                    binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_cancelled)




                }
            }
        }
    }

    // ── Inflate ─────────────────────────────────────────────────
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = ItemOrderHistoryCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        // Staggered slide-up animation
        val anim = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.slide_up)
        anim.startOffset = (position * 80L).coerceAtMost(400L)
        holder.itemView.startAnimation(anim)
        holder.bind(getItem(position))
    }
}