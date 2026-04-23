package com.shyamdairyfarm.user.ui.activity
import com.shyamdairyfarm.user.helper.CustomDatePickerDialog
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.model.WeeklyDayState
import com.shyamdairyfarm.user.data.model.ProductDetails
import com.shyamdairyfarm.user.data.model.ProductSubscribeUiState
import com.shyamdairyfarm.user.databinding.ActivityProductSubscribeBinding
import com.shyamdairyfarm.user.databinding.DialogSuccessLottieBinding
import com.shyamdairyfarm.user.extensions.imageUrl
import com.shyamdairyfarm.user.extensions.variant
import com.shyamdairyfarm.user.helper.WeeklyDaySelectorViewHolder
import com.shyamdairyfarm.user.ui.viewmodel.ProductDetailsViewModel
import com.shyamdairyfarm.user.ui.viewmodel.ProductSubscribeViewModel
import java.util.Locale

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
            
            val sellingPrice = intentProductDetails.productPrice.toIntOrNull() ?: 0
            val mrp = intentProductDetails.dairyMrp.toIntOrNull() ?: 0
            viewModel.setPrices(sellingPrice, mrp)
            
        } else {
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
                
                // Sync RadioGroup with state
                val checkedId = when (state.selectedInterval) {
                    0 -> R.id.radioEveryDay
                    1 -> R.id.radioEveryAlternateDay
                    2 -> R.id.radioEvery2Days
                    3 -> R.id.radioEvery3Days
                    else -> R.id.radioEveryAlternateDay
                }
                if (binding.radioGroupInterval.checkedRadioButtonId != checkedId) {
                    binding.radioGroupInterval.check(checkedId)
                }
            }
        }

        // Update day rows (relevant for Weekly)
        state.weeklyDayStates.forEachIndexed { index, day ->
            applyDayRowUI(index, day)
        }
        
        // Update Price Summary UI
        renderPriceSummary(state)
    }

    private fun renderPriceSummary(state: ProductSubscribeUiState) {
        val summary = binding

        summary.tvPricePerPacket.text = "₹${state.basePrice}"

        // Logic for total packets display
        if (state.selectedFrequency == DeliveryFrequency.WEEKLY) {
            // Sum up quantities of all "ON" days
            val totalWeeklyQty = state.weeklyDayStates
                .filter { it.isOn }
                .sumOf { it.qty }

            summary.tvPackets.text = "$totalWeeklyQty pkt/week"
        } else {
            // Standard behavior for Daily/Alternate
            summary.tvPackets.text = "${state.packetsPerDelivery} pkt"
        }

        summary.tvDeliveriesPerMonth.text = "${state.deliveriesPerMonth} days"

        summary.tvTotalMonthly.text =
            "₹${String.format(Locale.getDefault(), "%,.0f", state.totalMonthly)}"
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
       
        binding.radioGroupInterval.setOnCheckedChangeListener { _, checkedId ->
            val interval = when (checkedId) {
                R.id.radioEveryDay -> 0
                R.id.radioEveryAlternateDay -> 1
                R.id.radioEvery2Days -> 2
                R.id.radioEvery3Days -> 3
                else -> 1
            }
            viewModel.setInterval(interval)
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
            holder.tvDayPrice.text = "₹${state.qty * viewModel.uiState.value!!.basePrice}"
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
