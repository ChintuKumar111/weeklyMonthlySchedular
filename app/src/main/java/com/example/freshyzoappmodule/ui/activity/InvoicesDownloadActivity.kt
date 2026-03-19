package com.example.freshyzoappmodule.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.TransactionHeaderRow
import com.example.freshyzoappmodule.databinding.ActivityInvoicesDownloadBinding
import com.example.freshyzoappmodule.databinding.LayoutBillPreviewBinding
import com.example.freshyzoappmodule.extensions.InvoicePrintExtensions
import com.example.freshyzoappmodule.ui.viewmodel.InvoiceViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoicesDownloadActivity : AppCompatActivity() {
    private var selectedStartDate: String = ""
    private var selectedEndDate: String = ""
    private val viewModel: InvoiceViewModel by viewModels()
    private val invoiceHelper by lazy { InvoicePrintExtensions(this) }
    private lateinit var binding: ActivityInvoicesDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInvoicesDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        binding.tvStartDate.setOnClickListener {

            showDatePicker { date ->

                binding.tvStartDate.text = date
                selectedStartDate = date

            }

        }

        binding.tvEndDate.setOnClickListener {

            showDatePicker { date ->

                binding.tvEndDate.text = date
                selectedEndDate = date

            }

        }

        binding.btnDownload.setOnClickListener {
            if (selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.loadInvoice(selectedStartDate, selectedEndDate)
        }
    }

    private fun setupObservers() {
        viewModel.invoiceData.observe(this) { data ->
            data?.let {
                showBillPreview(
                    it.summary.billMonth,
                    it.summary.customerName,
                    it.summary.mobileNo,
                    it.summary.totalSell,
                    it.summary.totalRecharges,
                    it.summary.availableBalance,
                    it.transactions
                )
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showBillPreview(
        billMonth: String, customerName: String, mobileNo: String,
        totalSell: String, recharges: String, balance: String,
        transactions: List<TransactionHeaderRow>
    ) {
        val dialog = BottomSheetDialog(this, R.style.CardStyle)
        val previewBinding = LayoutBillPreviewBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(previewBinding.root)

        dialog.behavior.peekHeight = resources.displayMetrics.heightPixels
        dialog.behavior.isHideable = false
        dialog.behavior.isDraggable = false

        // Bind Summary Data
        previewBinding.tvPreviewPeriod.text = "$selectedStartDate  –  $selectedEndDate"
        previewBinding.tvPreviewBillMonth.text = billMonth
        previewBinding.tvPreviewCustomerName.text = customerName
        previewBinding.tvPreviewMobileNo.text = mobileNo
        previewBinding.tvPreviewTotalSell.text = totalSell
        previewBinding.tvPreviewTotalRecharges.text = recharges
        previewBinding.tvPreviewBalance.text = balance
        //showing data dummy currently for bill items============================
        populateTransactions(previewBinding, transactions)

        previewBinding.btnClosePreview.setOnClickListener { dialog.dismiss() }

        previewBinding.btnDownloadPdf.setOnClickListener {
            generateAndAction(previewBinding, billMonth, action = "SAVE")
        }

        previewBinding.btnPrintInvoice.setOnClickListener {
            invoiceHelper.printInvoice(previewBinding.billContentLayout)
        }

        dialog.show()
        
        previewBinding.zoomLayout.post {
            previewBinding.zoomLayout.resetZoom()
        }
    }

    private fun generateAndAction(previewBinding: LayoutBillPreviewBinding, billMonth: String, action: String) {
        previewBinding.btnDownloadPdf.isEnabled = false
        previewBinding.btnPrintInvoice.isEnabled = false

        previewBinding.billContentLayout.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    previewBinding.billContentLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val bitmap = invoiceHelper.captureFullView(previewBinding.billContentLayout, previewBinding.llFullTableContainer)
                    val pdfFile = invoiceHelper.writeBitmapToPdf(bitmap, billMonth)

                    previewBinding.btnDownloadPdf.isEnabled = true
                    previewBinding.btnPrintInvoice.isEnabled = true

                    if (pdfFile != null) {
                        if (action == "PRINT") {
                            invoiceHelper.printInvoice(previewBinding.billContentLayout)
                        } else {
                            Toast.makeText(this@InvoicesDownloadActivity, "✅ PDF saved to Downloads", Toast.LENGTH_LONG).show()
                            invoiceHelper.openPdfFile(pdfFile)
                        }
                    } else {
                        Toast.makeText(this@InvoicesDownloadActivity, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        previewBinding.billContentLayout.requestLayout()
    }
    private fun populateTransactions(
        previewBinding: LayoutBillPreviewBinding,
        transactions: List<TransactionHeaderRow>
    ) {

        val inflater = LayoutInflater.from(this)

        previewBinding.llTransactionRows.removeAllViews()

        transactions.forEachIndexed { index, tx ->

            val row = inflater.inflate(
                R.layout.item_preview_invoice_section,
                previewBinding.llTransactionRows,
                false
            )

            row.findViewById<TextView>(R.id.tvRowSrNo).text = (index + 1).toString()
            row.findViewById<TextView>(R.id.tvRowDate).text = tx.date
            row.findViewById<TextView>(R.id.tvRowTransaction).text = tx.transaction
            row.findViewById<TextView>(R.id.tvRowQty).text = tx.qty
            row.findViewById<TextView>(R.id.tvRowSale).text = tx.totalSale
            row.findViewById<TextView>(R.id.tvRowRecharge).text = tx.recharge
            row.findViewById<TextView>(R.id.tvRowBalance).text = tx.balance

            previewBinding.llTransactionRows.addView(row)
        }
    }

    private fun showDatePicker(onDate: (String) -> Unit) {

        val picker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        picker.addOnPositiveButtonClickListener { ms ->
            val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            onDate(fmt.format(Date(ms)))
        }

        picker.show(supportFragmentManager, "date_picker")
    }
}