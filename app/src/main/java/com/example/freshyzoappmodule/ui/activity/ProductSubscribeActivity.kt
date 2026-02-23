package com.example.freshyzoappmodule.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.ActivityProductSubscribeBinding
import com.example.freshyzoappmodule.viewmodel.ProductSubscribeViewModel

class ProductSubscribeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductSubscribeBinding
    private val viewModel: ProductSubscribeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductSubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->

            // Simple Qty UI
            binding.tvQtyValue.text = state.simpleQty.toString()
            binding.tvQtySub.text = state.simpleSummaryText

            // CTA
            binding.btnSubscribeNow.text = state.totalPriceText

            // Day summary
            binding.tvDayQtySummary.text = state.daySummaryText

            // Frequency highlight
            highlightFrequency(state.selectedFrequency)

            // Toggle cards
            val isDayMode = state.isDayMode
            binding.cardDayQty.visibility =
                if (isDayMode) View.VISIBLE else View.GONE
            binding.cardSimpleQty.visibility =
                if (isDayMode) View.GONE else View.VISIBLE
        }
    }

    private fun setupClickListeners() {

        binding.btnQtyPlus.setOnClickListener {
            viewModel.increaseSimpleQty()
        }

        binding.btnQtyMinus.setOnClickListener {
            viewModel.decreaseSimpleQty()
        }

        binding.optionDaily.setOnClickListener {
            viewModel.selectFrequency(DeliveryFrequency.DAILY)
        }

        binding.optionAlternate.setOnClickListener {
            viewModel.selectFrequency(DeliveryFrequency.ALTERNATE)
        }

        binding.optionWeekly.setOnClickListener {
            viewModel.selectFrequency(DeliveryFrequency.WEEKLY)
        }

        binding.optionMonthly.setOnClickListener {
            viewModel.selectFrequency(DeliveryFrequency.MONTHLY)
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun highlightFrequency(freq: DeliveryFrequency) {
        val optionMap = mapOf(
            DeliveryFrequency.DAILY to binding.optionDaily,
            DeliveryFrequency.ALTERNATE to binding.optionAlternate,
            DeliveryFrequency.WEEKLY to binding.optionWeekly,
            DeliveryFrequency.MONTHLY to binding.optionMonthly
        )

        optionMap.forEach { (frequency, view) ->
            view.background = ContextCompat.getDrawable(
                this,
                if (frequency == freq)
                    R.drawable.freq_option_selected
                else
                    R.drawable.freq_option_default
            )
        }
    }
}

enum class DeliveryFrequency(val label: String) {
    DAILY("daily"),
    ALTERNATE("alternate days"),
    WEEKLY("weekly"),
    MONTHLY("monthly")
}