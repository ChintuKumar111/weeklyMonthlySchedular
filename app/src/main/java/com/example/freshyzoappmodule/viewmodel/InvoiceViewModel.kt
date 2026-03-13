package com.example.freshyzoappmodule.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.freshyzoappmodule.data.model.BillSummaryData
import com.example.freshyzoappmodule.data.model.BillTransactionRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceViewModel : ViewModel() {
    data class InvoiceDataState(
        val summary: BillSummaryData,
        val transactions: List<BillTransactionRow>
    )
    private val _invoiceData = MutableLiveData<InvoiceDataState?>()
    val invoiceData: LiveData<InvoiceDataState?> = _invoiceData

    // Keep individual LiveData if needed for legacy support, but update them together
    val billSummary = MutableLiveData<BillSummaryData>()
    val transactions = MutableLiveData<List<BillTransactionRow>>()

    fun loadInvoice(startDate: String, endDate: String) {
        Log.d("InvoiceVM", "loadInvoice called")

        // can call api here===========================
        val summary = BillSummaryData(
            billMonth = extractMonthYear(startDate),
            customerName = "Sachin Baghel",
            mobileNo = "775452327",
            totalSell = "₹ 1870",
            totalRecharges = "₹ 334",
            availableBalance = "₹ 2000.0"
        )

        val transactionList = getSampleTransactions()
        
        // Update both individual and combined state
        billSummary.value = summary
        transactions.value = transactionList
        _invoiceData.value = InvoiceDataState(summary, transactionList)
    }

    private fun extractMonthYear(dateStr: String): String {
        return try {
            // Support multiple formats to be safe
            val inputFormats = listOf("dd MMM yyyy", "MMM d, yyyy", "yyyy-MM-dd")
            var date: Date? = null
            for (format in inputFormats) {
                try {
                    date = SimpleDateFormat(format, Locale.getDefault()).parse(dateStr)
                    if (date != null) break
                } catch (e: Exception) {}
            }
            
            val outFmt = SimpleDateFormat("MMM-yyyy", Locale.getDefault())
            outFmt.format(date ?: Date())
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun getSampleTransactions(): List<BillTransactionRow> {
        return listOf(
            BillTransactionRow(1, "11-Feb-2026", "Cash-To-Agent", "", "", "20", "-71"),
            BillTransactionRow(2, "12-Feb-2026", "Cash-To-Agent", "", "", "100", "29"),
            BillTransactionRow(3, "18-Feb-2026", "UPI Payment", "", "1", "", "131"),
            BillTransactionRow(4, "18-Feb-2026", "3+3 Trial Offer", "", "", "90", "221"),
          //  BillTransactionRow(5, "26-Feb-2026", "Cash-To-Agent", "", "", "123", "-546"),
          //  BillTransactionRow(6, "11-Mar-2026", "Cow Ghee 1000ml", "1", "890", "", "2980.0"),
           // BillTransactionRow(7, "11-Mar-2026", "Cow Ghee 1000ml", "1", "890", "", "2090.0"),
            BillTransactionRow(8, "11-Mar-2026", "A2 Cow Milk 500ml", "2", "90", "", "2000.0")
        )
    }
}

//viewModel.billSummary.observe(this) { summary ->
//
//    val transactions = viewModel.transactions.value ?: emptyList()
//
//    showBillPreview(
//        summary.billMonth,
//        summary.customerName,
//        summary.mobileNo,
//        summary.totalSell,
//        summary.totalRecharges,
//        summary.availableBalance,
//        transactions
//    )
//}

//
//class InvoiceRepository {
//
//    suspend fun getInvoice(startDate: String, endDate: String): InvoiceResponse? {
//
//        return try {
//
//            val response = RetrofitClient.api.getInvoice(startDate, endDate)
//
//            if (response.isSuccessful) {
//                response.body()
//            } else null
//
//        } catch (e: Exception) {
//            null
//        }
//    }
