package com.example.freshyzoappmodule.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.DeliveryModel
import com.example.freshyzoappmodule.data.model.DeliveryStatus
import com.example.freshyzoappmodule.data.model.ProductType

class DeliveriesViewModel : ViewModel() {

    // ── Active filter ────────────────────────────────────────
    private val _activeFilter = MutableLiveData<String>("all")
    val activeFilter: LiveData<String> = _activeFilter

    // ── All deliveries (replace with API/DB call) ────────────
    private val allDeliveries = listOf(
        DeliveryModel(
            id = 1,
            txnId = "370386",
            productName = "A2 Cow Milk",
            brandName = "FreshyZo Premium",
            emoji = "🥛",
            productType = ProductType.MILK,
            size = "500 ml",
            quantity = 2,
            amountPaid = 90.0,
            date = "Feb 26, 2026",
            status = DeliveryStatus.PLACED
        ),
        DeliveryModel(
            id = 2,
            txnId = "370385",
            productName = "Cow Ghee",
            brandName = "FreshyZo Gold",
            emoji = "🫙",
            productType = ProductType.GHEE,
            size = "1000 ml",
            quantity = 1,
            amountPaid = 890.0,
            date = "Feb 26, 2026",
            status = DeliveryStatus.PLACED
        ),
        DeliveryModel(
            id = 3,
            txnId = "370384",
            productName = "Cow Ghee",
            brandName = "FreshyZo Gold",
            emoji = "🫙",
            productType = ProductType.GHEE,
            size = "1000 ml",
            quantity = 1,
            amountPaid = 890.0,
            date = "Feb 25, 2026",
            status = DeliveryStatus.PENDING
        ),
        DeliveryModel(
            id = 4,
            txnId = "370383",
            productName = "A2 Cow Milk",
            brandName = "FreshyZo Premium",
            emoji = "🥛",
            productType = ProductType.MILK,
            size = "500 ml",
            quantity = 1,
            amountPaid = 45.0,
            date = "Feb 24, 2026",
            status = DeliveryStatus.CANCELLED
        )
    )

    // ── Filtered list ────────────────────────────────────────
    private val _filteredDeliveries = MutableLiveData<List<DeliveryModel>>(allDeliveries)
    val filteredDeliveries: LiveData<List<DeliveryModel>> = _filteredDeliveries

    // ── Stats ────────────────────────────────────────────────
    data class DeliveryStats(
        val total: Int,
        val placed: Int,
        val pending: Int,
        val cancelled: Int
    )

    private val _stats = MutableLiveData(computeStats())
    val stats: LiveData<DeliveryStats> = _stats

    // ── Public methods ───────────────────────────────────────

    fun applyFilter(filter: String) {
        _activeFilter.value = filter
        _filteredDeliveries.value = when (filter) {
            "Placed"    -> allDeliveries.filter { it.status == DeliveryStatus.PLACED }
            "Pending"   -> allDeliveries.filter { it.status == DeliveryStatus.PENDING }
            "Cancelled" -> allDeliveries.filter { it.status == DeliveryStatus.CANCELLED }
            else        -> allDeliveries
        }
    }

    fun refresh() {
        // In a real app: fetch from API here
        _filteredDeliveries.value = _filteredDeliveries.value
        _stats.value = computeStats()
    }

    // ── Private helpers ──────────────────────────────────────

    private fun computeStats() = DeliveryStats(
        total     = allDeliveries.size,
        placed    = allDeliveries.count { it.status == DeliveryStatus.PLACED },
        pending   = allDeliveries.count { it.status == DeliveryStatus.PENDING },
        cancelled = allDeliveries.count { it.status == DeliveryStatus.CANCELLED }
    )
}