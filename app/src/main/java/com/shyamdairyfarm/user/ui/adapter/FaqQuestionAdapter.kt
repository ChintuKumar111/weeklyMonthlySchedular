package com.shyamdairyfarm.user.ui.adapter

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.model.FaqQuestion

class FaqQuestionAdapter  : RecyclerView.Adapter<FaqQuestionAdapter.FaqVH>() {

    private val items = mutableListOf<FaqQuestion>()
    private val expandedPositions = mutableSetOf<Int>()

    fun submitList(list: List<FaqQuestion>) {
        expandedPositions.clear()
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq_question, parent, false)
        return FaqVH(view)
    }


    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FaqVH, position: Int) {
        holder.bind(items[position], expandedPositions.contains(position))
        holder.questionRow.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
            val expanding = !expandedPositions.contains(pos)
            if (expanding) expandedPositions.add(pos) else expandedPositions.remove(pos)
            holder.animateToggle(expanding)
        }
    }

    inner class FaqVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionRow: View         = itemView.findViewById(R.id.faqQuestionRow)
        private val tvNum: TextView   = itemView.findViewById(R.id.tvFaqNumber)
        private val tvQ: TextView = itemView.findViewById(R.id.tvFaqQuestion)
        private val tvA: TextView     = itemView.findViewById(R.id.tvFaqAnswer)
        private val ivArrow: ImageView = itemView.findViewById(R.id.ivFaqArrow)
        private val divider: View     = itemView.findViewById(R.id.faqDivider)

        fun bind(item: FaqQuestion, isExpanded: Boolean) {
            tvNum.text = item.number.toString()
            tvQ.text   = item.question
            tvA.text   = item.answer

            tvA.visibility    = if (isExpanded) View.VISIBLE else View.GONE
            divider.visibility= if (isExpanded) View.VISIBLE else View.GONE

            ivArrow.rotation = if (isExpanded) 180f else 0f
            applyExpandedStyle(isExpanded)
        }

        fun animateToggle(expanding: Boolean) {
            ObjectAnimator.ofFloat(
                ivArrow, "rotation",
                if (expanding) 0f else 180f,
                if (expanding) 180f else 0f
            ).apply { duration = 250; start() }

            if (expanding) {
                divider.visibility = View.VISIBLE
                tvA.alpha          = 0f
                tvA.visibility     = View.VISIBLE
                tvA.animate().alpha(1f).setDuration(200).start()
            } else {
                tvA.animate().alpha(0f).setDuration(150).withEndAction {
                    tvA.visibility     = View.GONE
                    divider.visibility = View.GONE
                }.start()
            }

            applyExpandedStyle(expanding)
        }

        private fun applyExpandedStyle(expanded: Boolean) {
            val ctx = itemView.context

            tvNum.background = ContextCompat.getDrawable(
                ctx,
                if (expanded) R.drawable.bg_faq_number_active
                else          R.drawable.bg_faq_number_default
            )
            tvNum.setTextColor(
                ContextCompat.getColor(ctx,
                    if (expanded) R.color.white else R.color.green_mid)
            )
            ivArrow.background = ContextCompat.getDrawable(
                ctx,
                if (expanded) R.drawable.bg_faq_arrow_active
                else          R.drawable.bg_faq_arrow_default
            )
            ivArrow.imageTintList = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(ctx,
                    if (expanded) R.color.green_mid else R.color.text_muted)
            )
        }
    }
}
