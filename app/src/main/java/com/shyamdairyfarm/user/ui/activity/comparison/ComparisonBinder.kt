package com.shyamdairyfarm.user.ui.activity.comparison

import android.widget.TextView
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.databinding.ActivityProductDetailsBinding

class ComparisonBinder(private val binding: ActivityProductDetailsBinding) {

    fun bind(items: List<ComparisonItem>) {

        val rows = listOf(
            binding.rowA2Milk,
            binding.rowNoPreservatives,
            binding.rowBilonaGhee,
            binding.rowNoStarch,
            binding.rowFarmDirect,
            binding.rowDelivered,
            binding.rowGlassBottle,
            binding.rowLabTested
        )

        items.forEachIndexed { index, item ->
            val row = rows[index]

            row.tvFeatureName.text = item.feature
            setStatus(row.tvAmulValue, item.amul)
            setStatus(row.tvMotherDairyValue, item.motherDairy)
            setStatus(row.tvNestleValue, item.nestle)
        }
    }

    private fun setStatus(textView: TextView, status: ComparisonStatus) {
        when (status) {
            ComparisonStatus.YES -> {
                textView.text = "✔"
                textView.setTextColor(textView.context.getColor(R.color.green_mid))
            }
            ComparisonStatus.NO -> {
                textView.text = "✘"
                textView.setTextColor(textView.context.getColor(R.color.red_error))
            }
            ComparisonStatus.PARTIAL -> {
                textView.text = "~"
                textView.setTextColor(textView.context.getColor(R.color.amber_warn))
            }
        }
    }
}