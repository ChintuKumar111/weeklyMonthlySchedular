package com.example.freshyzoappmodule.ui.adapter  // ← your package

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.FaqItem

class FaqAdapter : ListAdapter<FaqItem, FaqAdapter.FaqViewHolder>(FaqDiffCallback()) {

    private val expandedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq_row, parent, false)
        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        holder.bind(getItem(position), expandedPositions.contains(position))

        holder.questionRow.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_ID.toInt()) return@setOnClickListener

            val expanding = !expandedPositions.contains(pos)
            if (expanding) expandedPositions.add(pos) else expandedPositions.remove(pos)

            // Animate arrow smoothly — no rebind flash
            holder.animateArrow(expanding)

            // Fade answer in / out
            holder.toggleAnswer(expanding)
        }
    }

    inner class FaqViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val questionRow: View            = itemView.findViewById(R.id.faqQuestionRow)
        private val tvQuestion: TextView = itemView.findViewById(R.id.tvFaqQuestion)
        private val tvAnswer: TextView   = itemView.findViewById(R.id.tvFaqAnswer)
        private val ivArrow: ImageView   = itemView.findViewById(R.id.ivFaqArrow)

        fun bind(item: FaqItem, isExpanded: Boolean) {
            tvQuestion.text     = item.question
            tvAnswer.text       = item.answer
            tvAnswer.visibility = if (isExpanded) View.VISIBLE else View.GONE

            // Snap to correct rotation instantly on bind
            // (avoids glitch when RecyclerView recycles a view while scrolling)
            ivArrow.rotation = if (isExpanded) 180f else 0f
            ivArrow.alpha    = 1f
        }

        // 0° = chevron pointing DOWN (collapsed)
        // 180° = chevron pointing UP   (expanded)
        fun animateArrow(expanding: Boolean) {
            ObjectAnimator.ofFloat(
                ivArrow, "rotation",
                if (expanding) 0f else 180f,
                if (expanding) 180f else 0f
            ).apply {
                duration = 250
                start()
            }
        }

        fun toggleAnswer(expanding: Boolean) {
            if (expanding) {
                tvAnswer.alpha      = 0f
                tvAnswer.visibility = View.VISIBLE
                tvAnswer.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            } else {
                tvAnswer.animate()
                    .alpha(0f)
                    .setDuration(150)
                    .withEndAction { tvAnswer.visibility = View.GONE }
                    .start()
            }
        }
    }

    class FaqDiffCallback : DiffUtil.ItemCallback<FaqItem>() {
        override fun areItemsTheSame(old: FaqItem, new: FaqItem) =
            old.question == new.question
        override fun areContentsTheSame(old: FaqItem, new: FaqItem) =
            old == new
    }
}