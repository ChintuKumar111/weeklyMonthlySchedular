package com.shyamdairyfarm.user.ui.adapter
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.model.OrderHistoryModel
import com.shyamdairyfarm.user.data.model.DeliveryStatus
import com.shyamdairyfarm.user.data.model.ProductType
import com.shyamdairyfarm.user.databinding.ItemOrderHistoryCardBinding

class UserOrderHistoryAdapter(
    private val onCardClick: (OrderHistoryModel) -> Unit
) : ListAdapter<OrderHistoryModel,
        UserOrderHistoryAdapter.DeliveryViewHolder>(DiffCallback()) {
    class DiffCallback : DiffUtil.ItemCallback<OrderHistoryModel>() {
        override fun areItemsTheSame(old: OrderHistoryModel, new: OrderHistoryModel) = old.id == new.id
        override fun areContentsTheSame(old: OrderHistoryModel, new: OrderHistoryModel) = old == new
    }
    inner class DeliveryViewHolder(
        private val binding: ItemOrderHistoryCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderHistoryModel) {
            val ctx = binding.root.context
            binding.tvProductEmoji.text = item.emoji
            binding.tvProductName.text = item.productName
            binding.tvDate.text = item.date


//            // ── Product image background ────────────────────────
            binding.productImageBox.setBackgroundResource(
                when (item.productType) {
                    ProductType.MILK -> R.drawable.bg_product_image_milk
                    ProductType.GHEE -> R.drawable.bg_product_image_ghee
                }
            )

            applyStatus(ctx, item)
            binding.root.setOnClickListener {
                it.animate()
                    .scaleX(0.97f).scaleY(0.97f).setDuration(80)
                    .withEndAction {
                        it.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                        onCardClick(item)
                    }.start()
            }
        }

        private fun applyStatus(ctx: Context, item: OrderHistoryModel) {
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