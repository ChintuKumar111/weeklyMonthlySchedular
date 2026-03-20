package com.example.freshyzoappmodule.ui.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.UserDelivery
import java.util.Locale
class UserDeliveryAdapter(
    private val context: Context,
    private val onItemClick: ((UserDelivery) -> Unit)? = null
) : ListAdapter<UserDelivery, UserDeliveryAdapter.DeliveryViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserDelivery>() {
            override fun areItemsTheSame(old: UserDelivery, new: UserDelivery) =
                old.id == new.id

            override fun areContentsTheSame(old: UserDelivery, new: UserDelivery) =
                old == new
        }
    }
    inner class DeliveryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView     = itemView.findViewById(R.id.ivProductImage)
        val tvProductName: TextView       = itemView.findViewById(R.id.tvProductName)
        val tvQuantity: TextView          = itemView.findViewById(R.id.tvQuantity)
        val tvStatus: TextView            = itemView.findViewById(R.id.tvStatus)
        val llStatus: LinearLayout        = itemView.findViewById(R.id.llStatus)
        val tvPrice: TextView             = itemView.findViewById(R.id.tvPrice)
        val tvTransactionId: TextView     = itemView.findViewById(R.id.tvTransactionId)
        val tvDate: TextView              = itemView.findViewById(R.id.tvDate)
        val tvRemainingBalance: TextView  = itemView.findViewById(R.id.tvRemainingBalance)
        val llRemark: LinearLayout        = itemView.findViewById(R.id.llRemark)
        val tvRemark: TextView            = itemView.findViewById(R.id.tvRemark)

        fun bind(item: UserDelivery) {

            // Product image
            if (item.productImageUrl.isNotBlank()) {
                Glide.with(context)
                    .load(item.productImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(ivProductImage)
            }
            else {
                ivProductImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            tvProductName.text = item.productName
            tvQuantity.text    = item.quantity

            // Status badge
            tvStatus.text = item.status
            applyStatusStyle(item.status)

            // Price  (₹ symbol + formatted)
            tvPrice.text = formatPrice(item.price)

            // Transaction & date
            tvTransactionId.text = "Transaction ID : ${item.transactionId}"
            tvDate.text           = "Date : ${item.date}"

            // Remaining balance
            val balanceText = if (item.remainingBalance < 0) {
                "Remaining Balance : -₹${formatAmount(-item.remainingBalance)}"
            } else {
                "Remaining Balance : ₹${formatAmount(item.remainingBalance)}"
            }
            tvRemainingBalance.text = balanceText

            // Remark (hide when blank / "N/A")
            val showRemark = item.remark.isNotBlank() && item.remark.uppercase() != "N/A"
            llRemark.visibility = if (showRemark) View.VISIBLE else View.GONE
            tvRemark.text = "Remark: ${item.remark}"

            // Click
            itemView.setOnClickListener { onItemClick?.invoke(item) }
        }

        // ── helpers ───────────────────────────────────────────────────────

        private fun applyStatusStyle(status: String) {
            when (status.lowercase(Locale.getDefault())) {
                "delivered" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_delivered_text))
                    llStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_delivery_banner)
                }
                "pending" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text))
                    llStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_pending)
                }
                "cancelled" -> {
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_cancelled_text))
                    llStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_badge_cancelled)
                }
            }
        }

        private fun formatPrice(price: Double): String =
            "₹${formatAmount(price)}"

        private fun formatAmount(amount: Double): String =
            if (amount % 1.0 == 0.0) amount.toInt().toString()
            else String.format("%.2f", amount)
    }

    // ───────────────────────────── Adapter overrides ──────────────────────

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_deliveries_details, parent, false)
        return DeliveryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}