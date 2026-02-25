package com.example.freshyzoappmodule.ui.activity

import DateHelperr
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.DayState
import com.example.freshyzoappmodule.data.model.Product
import com.example.freshyzoappmodule.data.model.ProductSubscribeUiState
import com.example.freshyzoappmodule.databinding.ActivityProductSubscribeBinding
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.sizes
import com.example.freshyzoappmodule.helper.DateHelper
import com.example.freshyzoappmodule.helper.DayRowHolder
import com.example.freshyzoappmodule.viewmodel.ProductDetailsViewModel
import com.example.freshyzoappmodule.viewmodel.ProductSubscribeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProductSubscribeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductSubscribeBinding
    private lateinit var product: Product
    private val vm: ProductDetailsViewModel by viewModels()
    private val viewModel: ProductSubscribeViewModel by viewModels()

    lateinit var dayRows: List<DayRowHolder>
    private lateinit var dateHelperr: DateHelperr

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductSubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dateHelperr = DateHelperr()

        bindDayRows()
        observeViewModel()
        setupClickListeners()
        initializeProduct()
        setupDayRowClicks()
        viewModel.setBasePrice(product.productPrice.toInt())

        // Initial setup for date display
        val initialDate = Calendar.getInstance()
        if (initialDate.get(Calendar.HOUR_OF_DAY) >= 8) {
            initialDate.add(Calendar.DATE, 1)
        }
        updateDateUI(initialDate.time)
    }

    private fun updateDateUI(date: Date) {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        
        binding.tvSelectedDate.text = dateFormat.format(date)
        binding.tvDeliveryBegins.text = "Delivery begins " + dayFormat.format(date)
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            render(state)
        }
    }
    private fun bindDayRows() {
        dayRows = listOf(
            DayRowHolder(binding.rowMonday.root),
            DayRowHolder(binding.rowTuesday.root),
            DayRowHolder(binding.rowWednesday.root),
            DayRowHolder(binding.rowThursday.root),
            DayRowHolder(binding.rowFriday.root),
            DayRowHolder(binding.rowSaturday.root),
            DayRowHolder(binding.rowSunday.root),
        )
    }
    private fun initializeProduct() {
        val intentProduct = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", Product::class.java)

        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Product>("product")
        }

        val quantity = intent.getIntExtra("quantity", 1)
        viewModel.setQuantity(quantity)

        if (intentProduct != null) {
            this.product = intentProduct
            vm.setProduct(intentProduct)
            displayProductData(intentProduct)
        } else {
            // Handle cases where product is missing - maybe finish the activity
            finish()
            return
        }
    }


    private fun displayProductData(product: Product) {
        val sizes = product.sizes
        binding.tvProductTitle.text = product.productName
        binding.tvPriceMain.text = "₹${product.productPrice}"

        binding.tvPriceOld.apply {
            text = "₹${product.dairyMrp}"
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        // Calculate discount percentage
        val price = product.productPrice.toDoubleOrNull() ?: 0.0
        val mrp = product.dairyMrp.toDoubleOrNull() ?: 0.0
        if (mrp > price) {
            val discount = ((mrp - price) / mrp * 100).toInt()
            binding.tvOffBadge.text = "$discount% OFF"
            binding.tvOffBadge.visibility = View.VISIBLE
        } else {
            binding.tvOffBadge.visibility = View.GONE
        }

        Glide.with(this)
            .load(product.imageUrl)
            .into(binding.ivProductImage)
    }

    private fun setupDayRowClicks() {


        dayRows.forEachIndexed { index, holder ->

            holder.dayToggle.setOnClickListener { 
                viewModel.toggleDay(index)
            }
            holder.tvDayName.setOnClickListener {
                viewModel.toggleDay(index)
            }

            holder.btnPlus.setOnClickListener { 
                viewModel.increaseDayQty(index)
            }

            holder.btnMinus.setOnClickListener { 
                viewModel.decreaseDayQty(index)
            }
        }
    }


    private fun render(state: ProductSubscribeUiState) {

        binding.btnSubscribeNow.text = state.totalPriceText
        binding.tvDayQtySummary.text = state.daySummaryText
        binding.tvQtyValue.text = state.simpleQty.toString()

        highlightFrequency(state.selectedFrequency)

        // Show/Hide cards based on frequency
        when (state.selectedFrequency) {
            DeliveryFrequency.DAILY, DeliveryFrequency.MONTHLY -> {
                binding.cardDayQty.visibility = View.GONE
                binding.cardSimpleQty.visibility = View.VISIBLE
                binding.cardIntervalOptions.visibility = View.GONE
            }
            DeliveryFrequency.WEEKLY -> {
                binding.cardDayQty.visibility = View.VISIBLE
                binding.cardSimpleQty.visibility = View.GONE
                binding.cardIntervalOptions.visibility = View.GONE
            }
            DeliveryFrequency.ALTERNATE -> {
                binding.cardDayQty.visibility = View.GONE
                binding.cardSimpleQty.visibility = View.VISIBLE
                binding.cardIntervalOptions.visibility = View.VISIBLE
            }
        }

        // Update day rows (relevant for Weekly)
        state.dayStates.forEachIndexed { index, day ->
            applyDayRowUI(index, day)
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

        binding.btnEditDate.setOnClickListener {
            showDatePicker()
        }

        binding.layoutDateSelector.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        dateHelperr.showMaterialDatePicker(this) { formattedDate, dayName ->
            binding.tvSelectedDate.text = formattedDate
            binding.tvDeliveryBegins.text = "Delivery begins " + dayName
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
    private fun applyDayRowUI(index: Int, state: DayState) {
        val holder = dayRows[index]
        holder.tvDayName.text = state.name

        if (state.isOn) {
            holder.dayToggle.background = 
                ContextCompat.getDrawable(this, R.drawable.day_toggle_on)

            holder.ivCheck.visibility = View.VISIBLE

            holder.tvDayName.setTextColor(
                ContextCompat.getColor(this, R.color.text_primary)
            )

            holder.miniStepper.alpha = 1f
            holder.btnMinus.isEnabled = true
            holder.btnPlus.isEnabled = true

            holder.tvDayQty.text = state.qty.toString()
            holder.tvDayPrice.text = "₹${state.qty * 70}"   // or pass basePrice
            holder.tvDayPrice.setTextColor(
                ContextCompat.getColor(this, R.color.green_dark)
            )

        } else {

            holder.dayToggle.background =
                ContextCompat.getDrawable(this, R.drawable.day_toggle_off)

            holder.ivCheck.visibility = View.GONE

            holder.tvDayName.setTextColor(
                ContextCompat.getColor(this, R.color.text_muted)
            )

            holder.miniStepper.alpha = 0.35f
            holder.btnMinus.isEnabled = false
            holder.btnPlus.isEnabled = false

            holder.tvDayQty.text = "0"
            holder.tvDayPrice.text = "—"
            holder.tvDayPrice.setTextColor(
                ContextCompat.getColor(this, R.color.text_muted)
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
