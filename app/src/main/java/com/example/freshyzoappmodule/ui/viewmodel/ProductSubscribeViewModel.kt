package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.ProductSubscribeUiState
import com.example.freshyzoappmodule.ui.activity.DeliveryFrequency
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProductSubscribeViewModel : ViewModel() {
    private var basePrice: Int = 0
    private var mrpPrice: Int = 0
    private val _uiState = MutableLiveData(ProductSubscribeUiState())
    val uiState: LiveData<ProductSubscribeUiState> = _uiState
    init {
        val calendar = Calendar.getInstance()
        // ✅ 8 AM Cut-off Logic
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 8) {
            calendar.add(Calendar.DATE, 1)
        }
        updateDate(calendar.time)
    }
    fun updateDate(date: Date) {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val shortDateFormat = SimpleDateFormat("MMM\ndd", Locale.getDefault())
        val state = _uiState.value ?: ProductSubscribeUiState()
        updateState(state.copy(
            startDate = dateFormat.format(date),
            shortDate = shortDateFormat.format(date),
            deliveryBeginsText = "Delivery begins ${dayFormat.format(date)}"
        ))
    }

    fun updateDateSelection(formattedDate: String, dayName: String) {
        val state = _uiState.value ?: ProductSubscribeUiState()
        var displayShort = "📅"
        try {
            val date = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).parse(formattedDate)
            if (date != null) {
                displayShort = SimpleDateFormat("MMM\ndd", Locale.getDefault()).format(date)
            }
        } catch (e: Exception) { e.printStackTrace() }

        updateState(state.copy(
            startDate = formattedDate,
            shortDate = displayShort,
            deliveryBeginsText = "Delivery begins $dayName"
        ))
    }

    fun setQuantity(quantity: Int) {
        val state = _uiState.value ?: return
        updateState(state.copy(simpleQty = quantity))
    }

    fun selectFrequency(freq: DeliveryFrequency) {
        val current = _uiState.value ?: return

        val newDays = when (freq) {
            DeliveryFrequency.WEEKLY -> {
                current.weeklyDayStates.map { day ->
                    day.copy(isOn = true, qty = if (day.qty == 0) 1 else day.qty)
                }
            }
            DeliveryFrequency.ALTERNATE -> {
                current.weeklyDayStates.mapIndexed { index, day ->
                    if (index % 2 == 0) {
                        day.copy(isOn = true, qty = if (day.qty == 0) 1 else day.qty)
                    } else {
                        day.copy(isOn = false, qty = 0)
                    }
                }
            }
            else -> current.weeklyDayStates
        }

        updateState(
            current.copy(
                selectedFrequency = freq,
                weeklyDayStates = newDays
            )
        )
    }

    fun increaseSimpleQty() {
        val state = _uiState.value ?: return
        if (state.simpleQty < 10) {
            updateState(state.copy(simpleQty = state.simpleQty + 1))
        }
    }

    fun decreaseSimpleQty() {
        val state = _uiState.value ?: return
        if (state.simpleQty > 1) {
            updateState(state.copy(simpleQty = state.simpleQty - 1))
        }
    }

    fun setPrices(sellingPrice: Int, mrp: Int) {
        this.basePrice = sellingPrice
        this.mrpPrice = mrp
        updateState(_uiState.value ?: ProductSubscribeUiState())
    }

    fun toggleDay(index: Int) {
        val state = _uiState.value ?: return
        val newDays = state.weeklyDayStates.mapIndexed { i, day ->
            if (i == index) {
                if (day.isOn)
                    day.copy(isOn = false, qty = 0)
                else
                    day.copy(isOn = true, qty = 1)
            } else day
        }
        updateState(state.copy(weeklyDayStates = newDays))
    }

    fun increaseDayQty(index: Int) {
        val state = _uiState.value ?: return
        val newDays = state.weeklyDayStates.mapIndexed { i, day ->
            if (i == index && day.isOn && day.qty < 10)
                day.copy(qty = day.qty + 1)
            else day
        }
        updateState(state.copy(weeklyDayStates = newDays))
    }

    fun decreaseDayQty(index: Int) {
        val state = _uiState.value ?: return
        val newDays = state.weeklyDayStates.mapIndexed { i, day ->
            if (i == index && day.isOn && day.qty > 1)
                day.copy(qty = day.qty - 1)
            else day
        }
        updateState(state.copy(weeklyDayStates = newDays))
    }

    fun setInterval(interval: Int) {
        val state = _uiState.value ?: return
        updateState(state.copy(selectedInterval = interval))
    }

    private fun updateState(state: ProductSubscribeUiState) {
        // ✅ Logic for deliveries per month based on Interval gaps
        val deliveriesPerMonth = when (state.selectedFrequency) {
            DeliveryFrequency.DAILY -> 30
            DeliveryFrequency.ALTERNATE -> {
                // If interval is 0 (Every Day): 30 / (0+1) = 30 days
                // If interval is 1 (Alt): 30 / (1+1) = 15 days
                // If interval is 2 (Every 2 days): 30 / (2+1) = 10 days
                // If interval is 3 (Every 3 days): 30 / (3+1) = 7.5 (approx 7)
                30 / (state.selectedInterval + 1)
            }
            DeliveryFrequency.WEEKLY -> state.weeklyDayStates.count { it.isOn } * 4
            DeliveryFrequency.MONTHLY -> 1
        }

        // Calculate Total Weekly Packets (Sum of all active days)
        val totalWeeklyPackets = state.weeklyDayStates.filter { it.isOn }.sumOf { it.qty }

        // Set packetsPerDelivery based on Frequency
        val packetsPerDelivery = if (state.selectedFrequency == DeliveryFrequency.WEEKLY) {
            totalWeeklyPackets
        } else {
            state.simpleQty
        }

        // Calculate total packets for the month
        val totalPacketsMonth = if (state.selectedFrequency == DeliveryFrequency.WEEKLY) {
            totalWeeklyPackets * 4
        } else {
            state.simpleQty * deliveriesPerMonth
        }

        val subtotalMrp = (mrpPrice * totalPacketsMonth).toDouble()
        val subtotalSelling = (basePrice * totalPacketsMonth).toDouble()
        val productDiscount = subtotalMrp - subtotalSelling
        val subscriptionDiscount = subtotalSelling * 0.25
        val totalMonthly = subtotalSelling - subscriptionDiscount

        val perDeliveryAvg = if (deliveriesPerMonth > 0) totalMonthly / deliveriesPerMonth else 0.0
        val perPacketAvg = if (totalPacketsMonth > 0) totalMonthly / totalPacketsMonth else 0.0

        val updated = state.copy(
            basePrice = basePrice,
            mrpPrice = mrpPrice,
            packetsPerDelivery = packetsPerDelivery,
            deliveriesPerMonth = deliveriesPerMonth,
            subtotalMrp = subtotalMrp,
            productDiscount = productDiscount,
            subscriptionDiscount = subscriptionDiscount,
            totalMonthly = totalMonthly,
            perDeliveryAvg = perDeliveryAvg,
            perPacketAvg = perPacketAvg,
            totalPriceText = "Subscribe @ ₹${String.format(Locale.getDefault(), "%,.0f", totalMonthly)}",
            daySummaryText = calculateSummary(state),
            simpleSummaryText = calculateSimpleSummary(state)
        )
        _uiState.value = updated
    }

    private fun calculateSimpleSummary(state: ProductSubscribeUiState): String {
        val qty = state.simpleQty
        val unit = if (qty > 1) "packets" else "packet"

        return when (state.selectedFrequency) {
            DeliveryFrequency.DAILY -> "$qty $unit × daily = ~${qty * 30}/month"
            DeliveryFrequency.ALTERNATE -> {
                val intervalText = when(state.selectedInterval) {
                    0 -> "every day"
                    1 -> "alternate days"
                    2 -> "every 2 days"
                    3 -> "every 3 days"
                    else -> "alternate days"
                }
                val totalMonth = qty * (30 / (state.selectedInterval + 1))
                "$qty $unit × $intervalText = ~$totalMonth/month"
            }
            DeliveryFrequency.MONTHLY -> "$qty $unit × monthly"
            else -> ""
        }
    }

    private fun calculateSummary(state: ProductSubscribeUiState): String {
        return when (state.selectedFrequency) {
            DeliveryFrequency.WEEKLY, DeliveryFrequency.ALTERNATE -> {
                val totalPackets = state.weeklyDayStates.sumOf { if (it.isOn) it.qty else 0 }
                val selectedDaysCount = state.weeklyDayStates.count { it.isOn }
                val dayOrDays = if (selectedDaysCount == 1) "day" else "days"
                "$totalPackets packets on $selectedDaysCount $dayOrDays/cycle"
            }
            else -> ""
        }
    }
}