package com.example.freshyzoappmodule.ui.activity

import com.example.freshyzoappmodule.helper.CustomDatePickerDialog
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.WeeklyDayState
import com.example.freshyzoappmodule.data.model.ProductDetails
import com.example.freshyzoappmodule.data.model.ProductSubscribeUiState
import com.example.freshyzoappmodule.databinding.ActivityProductSubscribeBinding
import com.example.freshyzoappmodule.databinding.DialogSuccessLottieBinding
import com.example.freshyzoappmodule.extensions.imageUrl
import com.example.freshyzoappmodule.extensions.variant
import com.example.freshyzoappmodule.helper.WeeklyDaySelectorViewHolder
import com.example.freshyzoappmodule.ui.viewmodel.ProductDetailsViewModel
import com.example.freshyzoappmodule.ui.viewmodel.ProductSubscribeViewModel

class ProductSubscribeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductSubscribeBinding
    private lateinit var productDetails: ProductDetails
    private val vm: ProductDetailsViewModel by viewModels()
    private val viewModel: ProductSubscribeViewModel by viewModels()

    lateinit var dayRows: List<WeeklyDaySelectorViewHolder>
    private lateinit var customDatePickerDialog: CustomDatePickerDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductSubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customDatePickerDialog = CustomDatePickerDialog()
        
        bindDayRows()
        observeViewModel()
        setupClickListeners()
        initializeProduct()
        setupDayRowClicks()
        viewModel.setBasePrice(productDetails.productPrice.toInt())
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            render(state)
        }
    }
    private fun bindDayRows() {
        dayRows = listOf(
            WeeklyDaySelectorViewHolder(binding.rowMonday.root),
            WeeklyDaySelectorViewHolder(binding.rowTuesday.root),
            WeeklyDaySelectorViewHolder(binding.rowWednesday.root),
            WeeklyDaySelectorViewHolder(binding.rowThursday.root),
            WeeklyDaySelectorViewHolder(binding.rowFriday.root),
            WeeklyDaySelectorViewHolder(binding.rowSaturday.root),
            WeeklyDaySelectorViewHolder(binding.rowSunday.root),
        )
    }
    private fun initializeProduct() {
        val intentProductDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("product", ProductDetails::class.java)

        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<ProductDetails>("product")
        }

        val quantity = intent.getIntExtra("quantity", 1)
        viewModel.setQuantity(quantity)

        if (intentProductDetails != null) {
            this.productDetails = intentProductDetails
            vm.setProduct(intentProductDetails)
            displayProductData(intentProductDetails)
        } else {
            // Handle cases where product is missing - maybe finish the activity
            finish()
            return
        }
    }


    private fun displayProductData(productDetails: ProductDetails) {
        val sizes = productDetails.variant
        binding.tvProductTitle.text = productDetails.productName
        binding.tvPriceMain.text = "₹${productDetails.productPrice}"

        binding.tvPriceOld.apply {
            text = "₹${productDetails.dairyMrp}"
            paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }

        // Calculate discount percentage
        val price = productDetails.productPrice.toDoubleOrNull() ?: 0.0
        val mrp = productDetails.dairyMrp.toDoubleOrNull() ?: 0.0
        if (mrp > price) {
            val discount = ((mrp - price) / mrp * 100).toInt()
            binding.tvOffBadge.text = "$discount% OFF"
            binding.tvOffBadge.visibility = View.VISIBLE
        } else {
            binding.tvOffBadge.visibility = View.GONE
        }

        Glide.with(this)
            .load(productDetails.imageUrl)
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
        binding.btnQtyMinus.apply {
            isEnabled = state.simpleQty > 1
            alpha = if (state.simpleQty > 1) 1.0f else 0.5f
        }
        binding.tvQtySub.text = state.simpleSummaryText
        binding.tvSelectedDate.text = state.startDate
        binding.tvDeliveryBegins.text = state.deliveryBeginsText

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
                android.util.Log.d("DEBUG", "Weekly selected")
                binding.cardIntervalOptions.visibility = View.GONE
            }
            DeliveryFrequency.ALTERNATE -> {
                binding.cardDayQty.visibility = View.GONE
                binding.cardSimpleQty.visibility = View.VISIBLE
                binding.cardIntervalOptions.visibility = View.VISIBLE
            }
        }

        // Update day rows (relevant for Weekly)
        state.weeklyDayStates.forEachIndexed { index, day ->
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

//        binding.optionMonthly.setOnClickListener {
//            viewModel.selectFrequency(DeliveryFrequency.MONTHLY)
//        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnEditDate.setOnClickListener {
            showDatePicker()
        }

        binding.layoutDateSelector.setOnClickListener {
            showDatePicker()
        }

        binding.btnSubscribeNow.setOnClickListener {
            showSuccessDialog("Subscription Successful!", "Your subscription has been successfully created.\nFresh delivery starts from ${viewModel.uiState.value?.startDate}")
        }
    }

    private fun showSuccessDialog(title: String, message: String) {
        val dialogBinding = DialogSuccessLottieBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.tvSuccessTitle.text = title
        dialogBinding.tvSuccessMessage.text = message

        dialogBinding.btnDone.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun showDatePicker() {
        customDatePickerDialog.showMaterialDatePicker(this) { formattedDate, dayName ->
            viewModel.updateDateSelection(formattedDate, dayName)
        }
    }

    private fun highlightFrequency(freq: DeliveryFrequency) {
        val optionMap = mapOf(
            DeliveryFrequency.DAILY to binding.optionDaily,
            DeliveryFrequency.ALTERNATE to binding.optionAlternate,
            DeliveryFrequency.WEEKLY to binding.optionWeekly,
//            DeliveryFrequency.MONTHLY to binding.optionMonthly
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
    private fun applyDayRowUI(index: Int, state: WeeklyDayState) {
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
            holder.btnMinus.apply {
                isEnabled = state.qty > 1
                alpha = if (state.qty > 1) 1.0f else 0.5f
            }
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
