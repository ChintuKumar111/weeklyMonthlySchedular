//package com.example.freshyzoappmodule.ui.Fragments
//
//import android.os.Bundle
//import android.view.View
//import android.widget.FrameLayout
//import android.widget.ImageButton
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.example.freshyzoappmodule.R
//import com.example.freshyzoappmodule.databinding.ActivityProductSubscribeBinding
//
//class ProductSubscribeActivity : AppCompatActivity() {
//    // ── Binding ────────────────────────────────────────────
//    private lateinit var binding: ActivityProductSubscribeBinding
//    // ── State ──────────────────────────────────────────────
//    private var selectedFrequency = DeliveryFrequency.DAILY
//    private var simpleQty = 1
//    private val basePrice = 70
//
//    // Day states
//    private val dayStates = listOf(
//        DayState("Mon", isOn = true,  qty = 1),
//        DayState("Tue", isOn = true,  qty = 1),
//        DayState("Wed", isOn = false, qty = 0),
//        DayState("Thu", isOn = false, qty = 0),
//        DayState("Fri", isOn = true,  qty = 2),
//        DayState("Sat", isOn = false, qty = 0),
//        DayState("Sun", isOn = false, qty = 0),
//    )
//
//    // Alternate days = Mon Wed Fri Sun (indices 0,2,4,6)
//    private val alternateDayIndices = listOf(0, 2, 4, 6)
//
//    // Bound day-row view holders
//    private lateinit var dayRows: List<DayRowHolder>
//
//    // ── Lifecycle ──────────────────────────────────────────
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityProductSubscribeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        bindDayRows()        // must come first before setupDayRows
//        setupDayRows()
//        setupClickListeners()
//        setupFrequencyOptions()
//        setupSimpleStepper()
//        refreshUI()
//    }
//
//    // ── Bind day row views from included layouts ───────────
//    private fun bindDayRows() {
//        dayRows = listOf(
//            DayRowHolder(binding.rowMonday.root),
//            DayRowHolder(binding.rowTuesday.root),
//            DayRowHolder(binding.rowWednesday.root),
//            DayRowHolder(binding.rowThursday.root),
//            DayRowHolder(binding.rowFriday.root),
//            DayRowHolder(binding.rowSaturday.root),
//            DayRowHolder(binding.rowSunday.root),
//        )
//    }
//
//    // ── Wire each day row's toggle + stepper ──────────────
//    private fun setupDayRows() {
//        dayRows.forEachIndexed { index, holder ->
//            val state = dayStates[index]
//
//            // Set day name label
//            holder.tvDayName.text = state.name
//
//            // Toggle tap — enable/disable that day
//            holder.dayToggle.setOnClickListener {
//                state.isOn = !state.isOn
//                state.qty  = if (state.isOn) 1 else 0
//                applyDayRowUI(index)
//                updateDaySummary()
//                updateCTA()
//            }
//
//            // Minus tap
//            holder.btnMinus.setOnClickListener {
//                if (state.isOn && state.qty > 1) {
//                    state.qty--
//                    applyDayRowUI(index)
//                    updateDaySummary()
//                    updateCTA()
//                }
//            }
//
//            // Plus tap
//            holder.btnPlus.setOnClickListener {
//                if (state.isOn && state.qty < 10) {
//                    state.qty++
//                    applyDayRowUI(index)
//                    updateDaySummary()
//                    updateCTA()
//                }
//            }
//
//            // Render initial state
//            applyDayRowUI(index)
//        }
//    }
//
//    // ── Apply visual state to one day row ─────────────────
//    private fun applyDayRowUI(index: Int) {
//        val state  = dayStates[index]
//        val holder = dayRows[index]
//
//        if (state.isOn) {
//            holder.dayToggle.background =
//                ContextCompat.getDrawable(this, R.drawable.day_toggle_on)
//            holder.ivCheck.visibility = View.VISIBLE
//            holder.tvDayName.setTextColor(
//                ContextCompat.getColor(this, R.color.text_primary)
//            )
//            holder.miniStepper.alpha  = 1f
//            holder.btnMinus.isEnabled = true
//            holder.btnPlus.isEnabled  = true
//            holder.tvDayQty.text      = state.qty.toString()
//            holder.tvDayPrice.text    = "₹${basePrice * state.qty}"
//            holder.tvDayPrice.setTextColor(
//                ContextCompat.getColor(this, R.color.green_dark)
//            )
//        } else {
//            holder.dayToggle.background =
//                ContextCompat.getDrawable(this, R.drawable.day_toggle_off)
//            holder.ivCheck.visibility = View.GONE
//            holder.tvDayName.setTextColor(
//                ContextCompat.getColor(this, R.color.text_muted)
//            )
//            holder.miniStepper.alpha  = 0.35f
//            holder.btnMinus.isEnabled = false
//            holder.btnPlus.isEnabled  = false
//            holder.tvDayQty.text      = "0"
//            holder.tvDayPrice.text    = "—"
//            holder.tvDayPrice.setTextColor(
//                ContextCompat.getColor(this, R.color.text_muted)
//            )
//        }
//    }
//
//    // ── Click Listeners ────────────────────────────────────
//    private fun setupClickListeners() {
//        binding.btnBack.setOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
//        }
//        binding.layoutDateSelector.setOnClickListener {
//            // open DatePickerDialog here
//        }
//    }
//
//    // ── Frequency Selection ────────────────────────────────
//    private fun setupFrequencyOptions() {
//        binding.optionDaily.setOnClickListener     { selectFrequency(DeliveryFrequency.DAILY) }
//        binding.optionAlternate.setOnClickListener { selectFrequency(DeliveryFrequency.ALTERNATE) }
//        binding.optionWeekly.setOnClickListener    { selectFrequency(DeliveryFrequency.WEEKLY) }
//        binding.optionMonthly.setOnClickListener   { selectFrequency(DeliveryFrequency.MONTHLY) }
//    }
//
//    private fun selectFrequency(freq: DeliveryFrequency) {
//        selectedFrequency = freq
//        refreshUI()
//    }
//
//    // ── Simple Stepper (Daily / Monthly) ──────────────────
//    private fun setupSimpleStepper() {
//        binding.btnQtyMinus.setOnClickListener {
//            if (simpleQty > 1) {
//                simpleQty--
//                updateSimpleQtyUI()
//                updateCTA()
//            }
//        }
//        binding.btnQtyPlus.setOnClickListener {
//            if (simpleQty < 10) {
//                simpleQty++
//                updateSimpleQtyUI()
//                updateCTA()
//            }
//        }
//    }
//
//    private fun updateSimpleQtyUI() {
//        binding.tvQtyValue.text = simpleQty.toString()
//        val monthly = when (selectedFrequency) {
//            DeliveryFrequency.DAILY   -> simpleQty * 30
//            DeliveryFrequency.MONTHLY -> simpleQty
//            else                      -> simpleQty
//        }
//        binding.tvQtySub.text = buildString {
//            append(simpleQty)
//            append(" packet")
//            if (simpleQty > 1) append("s")
//            append(" × ")
//            append(selectedFrequency.label)
//            append(" = ~")
//            append(monthly)
//            append("/month")
//        }
//    }
//
//    // ── Day Summary Badge ──────────────────────────────────
//    private fun updateDaySummary() {
//        val activeDayIndices = if (selectedFrequency == DeliveryFrequency.ALTERNATE)
//            alternateDayIndices
//        else
//            dayStates.indices.toList()
//
//        val total = activeDayIndices.sumOf { i ->
//            if (dayStates[i].isOn) dayStates[i].qty else 0
//        }
//        val unit = if (selectedFrequency == DeliveryFrequency.WEEKLY) "week" else "cycle"
//        binding.tvDayQtySummary.text = "$total packet${if (total != 1) "s" else ""}/$unit"
//    }
//
//    // ── CTA Button Price ───────────────────────────────────
//    private fun updateCTA() {
//        val text = when (selectedFrequency) {
//            DeliveryFrequency.WEEKLY, DeliveryFrequency.ALTERNATE -> {
//                val activeDayIndices = if (selectedFrequency == DeliveryFrequency.ALTERNATE)
//                    alternateDayIndices
//                else
//                    dayStates.indices.toList()
//
//                val total = activeDayIndices.sumOf { i ->
//                    if (dayStates[i].isOn) dayStates[i].qty * basePrice else 0
//                }
//                "Subscribe Now  ·  ₹$total/cycle"
//            }
//            else -> "Subscribe Now  ·  ₹${simpleQty * basePrice}"
//        }
//        binding.btnSubscribeNow.text = text
//    }
//
//    // ── Refresh All UI ─────────────────────────────────────
//    private fun refreshUI() {
//        applyFrequencyHighlight()
//        toggleCardVisibility()
//        showCorrectDayRows()
//        updateDaySummary()
//        updateSimpleQtyUI()
//        updateCTA()
//    }
//
//    private fun applyFrequencyHighlight() {
//        val optionMap = mapOf(
//            DeliveryFrequency.DAILY     to binding.optionDaily,
//            DeliveryFrequency.ALTERNATE to binding.optionAlternate,
//            DeliveryFrequency.WEEKLY    to binding.optionWeekly,
//            DeliveryFrequency.MONTHLY   to binding.optionMonthly
//        )
//        optionMap.forEach { (freq, view) ->
//            view.background = ContextCompat.getDrawable(
//                this,
//                if (freq == selectedFrequency) R.drawable.freq_option_selected
//                else R.drawable.freq_option_default
//            )
//        }
//    }
//
//    // Show day-qty card OR simple-qty card depending on frequency
//    private fun toggleCardVisibility() {
//        val isDayMode = selectedFrequency == DeliveryFrequency.WEEKLY ||
//                selectedFrequency == DeliveryFrequency.ALTERNATE
//
//        binding.cardDayQty.visibility    = if (isDayMode) View.VISIBLE else View.GONE
//        binding.cardSimpleQty.visibility = if (isDayMode) View.GONE    else View.VISIBLE
//
//        binding.tvDayQtyTitle.text = when (selectedFrequency) {
//            DeliveryFrequency.ALTERNATE -> "ALTERNATE DAYS & QUANTITY"
//            else                        -> "SELECT DAYS & QUANTITY"
//        }
//    }
//
//    // For Alternate: show only Mon Wed Fri Sun rows
//    private fun showCorrectDayRows() {
//        dayRows.forEachIndexed { index, holder ->
//            val show = when (selectedFrequency) {
//                DeliveryFrequency.ALTERNATE -> index in alternateDayIndices
//                DeliveryFrequency.WEEKLY    -> true
//                else                        -> false
//            }
//            holder.root.visibility = if (show) View.VISIBLE else View.GONE
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//    }
//}
//
//// ── Data class for each day's state ───────────────────────
//data class DayState(
//    val name: String,
//    var isOn: Boolean,
//    var qty: Int
//)
//
//// ── ViewHolder for item_day_row.xml ───────────────────────
//class DayRowHolder(val root: View) {
//    val dayToggle:   FrameLayout  = root.findViewById(R.id.dayToggle)
//    val ivCheck:     android.widget.ImageView = root.findViewById(R.id.ivDayCheck)
//    val tvDayName:   TextView     = root.findViewById(R.id.tvDayName)
//    val miniStepper: LinearLayout = root.findViewById(R.id.miniStepper)
//    val btnMinus:    ImageButton  = root.findViewById(R.id.btnDayMinus)
//    val btnPlus:     ImageButton  = root.findViewById(R.id.btnDayPlus)
//    val tvDayQty:    TextView     = root.findViewById(R.id.tvDayQty)
//    val tvDayPrice:  TextView     = root.findViewById(R.id.tvDayPrice)
//}
//
//// ── Enum ───────────────────────────────────────────────────
//enum class DeliveryFrequency(val label: String) {
//    DAILY("daily"),
//    ALTERNATE("alternate days"),
//    WEEKLY("weekly"),
//    MONTHLY("monthly")
//}