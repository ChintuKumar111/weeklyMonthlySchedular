package com.example.freshyzoappmodule.ui.Fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.BillSummaryData
import com.example.freshyzoappmodule.data.model.BillTransactionRow
import com.example.freshyzoappmodule.databinding.FragmentInvoiceBinding
import com.example.freshyzoappmodule.databinding.LayoutBillPreviewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceFragment : Fragment() {

    private var _binding: FragmentInvoiceBinding? = null
    private val binding get() = _binding!!

    // ── Selected dates ──
    private var selectedStartDate: String = ""
    private var selectedEndDate: String   = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── Date pickers (your existing logic — unchanged) ──
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

        // ── Download / Continue button → show bill preview ──
        binding.btnDownload.setOnClickListener {
            if (selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please select both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showBillPreview(selectedStartDate, selectedEndDate)
        }
    }

    // ════════════════════════════════════════════════════
    //  Your existing showDatePicker — UNCHANGED
    // ════════════════════════════════════════════════════
    private fun showDatePicker(onDate: (String) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker().build()
        picker.addOnPositiveButtonClickListener { ms ->
            val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            onDate(fmt.format(Date(ms)))
        }
        picker.show(parentFragmentManager, "date_picker")
    }

    // ════════════════════════════════════════════════════
    //  Show Bill Preview as BottomSheet
    // ════════════════════════════════════════════════════
    @SuppressLint("MissingInflatedId")
    private fun showBillPreview(startDate: String, endDate: String) {
        val dialog       = BottomSheetDialog(requireContext(), R.style.CardStyle)
        val previewBinding = LayoutBillPreviewBinding.inflate(LayoutInflater.from(requireContext()))
        dialog.setContentView(previewBinding.root)
        dialog.behavior.peekHeight = resources.displayMetrics.heightPixels // full screen

        // ── Populate summary data (replace with your real API data) ──
        val summary = BillSummaryData(
            billMonth = extractMonthYear(startDate, endDate),
            customerName = "Sachin Baghel",
            mobileNo = "775452327",
            totalSell = "₹ 1870",
            totalRecharges = "₹ 334",
            availableBalance = "₹ 2000.0"
        )

       previewBinding.tvPreviewPeriod.text       = "$startDate  –  $endDate"
        previewBinding.tvPreviewBillMonth.text    = summary.billMonth
        previewBinding.tvPreviewCustomerName.text = summary.customerName
        previewBinding.tvPreviewMobileNo.text     = summary.mobileNo
        previewBinding.tvPreviewTotalSell.text    = summary.totalSell
        previewBinding.tvPreviewTotalRecharges.text = summary.totalRecharges
        previewBinding.tvPreviewBalance.text      = summary.availableBalance

        // ── Populate transaction table rows ──
        val transactions = getSampleTransactions() // replace with your real data
        val tableContainer = previewBinding.llTransactionRows
        tableContainer.removeAllViews()

        transactions.forEachIndexed { index, tx ->
            val rowView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_preview_invoice_section, tableContainer, false)

            rowView.findViewById<android.widget.TextView>(R.id.tvRowSrNo).text       = tx.srNo.toString()
            rowView.findViewById<android.widget.TextView>(R.id.tvRowDate).text        = tx.date
            rowView.findViewById<android.widget.TextView>(R.id.tvRowTransaction).text = tx.transaction
            rowView.findViewById<android.widget.TextView>(R.id.tvRowQty).text         = tx.qty
            rowView.findViewById<android.widget.TextView>(R.id.tvRowSale).text        = tx.totalSale
            rowView.findViewById<android.widget.TextView>(R.id.tvRowRecharge).text    = tx.recharge

            val balTv = rowView.findViewById<android.widget.TextView>(R.id.tvRowBalance)
            balTv.text = tx.balance
            val balVal = tx.balance.replace(",", "").replace("₹", "").trim().toDoubleOrNull() ?: 0.0
            balTv.setTextColor(
                when {
                    balVal < 0 -> Color.parseColor("#E53935")
                    balVal > 0 -> Color.parseColor("#2E7D32")
                    else       -> Color.parseColor("#333333")
                }
            )

            // Alternate row background
            rowView.setBackgroundColor(
                if (index % 2 == 0) Color.parseColor("#FFFFFF")
                else Color.parseColor("#F8F8FB")
            )

            tableContainer.addView(rowView)
        }

        // ── Close button ──
        previewBinding.btnClosePreview.setOnClickListener {
            dialog.dismiss()
        }

        // ── Download PDF button ──
        previewBinding.btnDownloadPdf.setOnClickListener {
            previewBinding.btnDownloadPdf.isEnabled = false
            previewBinding.btnDownloadPdf.text      = "Generating…"

            // Wait for full layout then capture
            previewBinding.billContentLayout.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        previewBinding.billContentLayout.viewTreeObserver
                            .removeOnGlobalLayoutListener(this)

                        val bitmap  = captureFullView(previewBinding.billContentLayout)
                        val pdfFile = writeBitmapToPdf(bitmap, summary.billMonth)

                        previewBinding.btnDownloadPdf.isEnabled = true
                        previewBinding.btnDownloadPdf.text      = "⬇  Download PDF"

                        if (pdfFile != null) {
                            Toast.makeText(
                                requireContext(),
                                "✅ PDF saved to Downloads",
                                Toast.LENGTH_LONG
                            ).show()
                            openPdfFile(pdfFile)
                        } else {
                            Toast.makeText(
                                requireContext(), "Failed to generate PDF", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
            previewBinding.billContentLayout.requestLayout()
        }

        dialog.show()
    }

    // ════════════════════════════════════════════════════
    //  Capture entire view (including off-screen content)
    // ════════════════════════════════════════════════════
    private fun captureFullView(view: View): Bitmap {
        // Measure to full unconstrained height so off-screen rows are included
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }

    // ════════════════════════════════════════════════════
    //  Write Bitmap → PDF file (auto multi-page if tall)
    // ════════════════════════════════════════════════════
    private fun writeBitmapToPdf(bitmap: Bitmap, billMonth: String): File? {
        return try {
            val pageW  = 595   // A4 points width
            val pageH  = 842   // A4 points height
            val margin = 24

            val contentWidth = pageW - margin * 2
            val scale        = contentWidth.toFloat() / bitmap.width
            val usablePageH  = pageH - margin * 2
            val pxPerPage    = (usablePageH / scale).toInt()

            val doc        = PdfDocument()
            var pageNum    = 1
            var srcTopPx   = 0

            val footerPaint = Paint().apply {
                color       = Color.parseColor("#AAAAAA")
                textSize    = 8f
                isAntiAlias = true
                textAlign   = Paint.Align.CENTER
            }
            val timestamp = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())

            while (srcTopPx < bitmap.height) {
                val srcBottom = minOf(srcTopPx + pxPerPage, bitmap.height)
                val srcRect   = Rect(0, srcTopPx, bitmap.width, srcBottom)
                val dstH      = ((srcBottom - srcTopPx) * scale).toInt()
                val dstRect   = android.graphics.RectF(
                    margin.toFloat(), margin.toFloat(),
                    (margin + contentWidth).toFloat(), (margin + dstH).toFloat()
                )

                val pageInfo = PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create()
                val page     = doc.startPage(pageInfo)
                val canvas   = page.canvas

                canvas.drawColor(Color.WHITE)
                canvas.drawBitmap(bitmap, srcRect, dstRect, null)
                canvas.drawText(
                    "Page $pageNum  •  $timestamp",
                    (pageW / 2).toFloat(), (pageH - 10).toFloat(), footerPaint
                )

                doc.finishPage(page)
                srcTopPx = srcBottom
                pageNum++
            }

            // Save file
            val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            } else {
                @Suppress("DEPRECATION")
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            }
            dir?.mkdirs()

            val safeMonth = billMonth.replace(" ", "_").replace("/", "-")
            val dateSuffix = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
            val file = File(dir, "Invoice_${safeMonth}_$dateSuffix.pdf")

            doc.writeTo(FileOutputStream(file))
            doc.close()
            bitmap.recycle()
            file

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ════════════════════════════════════════════════════
    //  Open PDF with FileProvider
    // ════════════════════════════════════════════════════
    private fun openPdfFile(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
            }
            startActivity(Intent.createChooser(intent, "Open PDF with…"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No PDF viewer installed", Toast.LENGTH_SHORT).show()
        }
    }

    // ════════════════════════════════════════════════════
    //  Helpers
    // ════════════════════════════════════════════════════
    private fun extractMonthYear(start: String, end: String): String {
        return try {
            val fmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val outFmt = SimpleDateFormat("MMM-yyyy", Locale.getDefault())
            outFmt.format(fmt.parse(start) ?: Date())
        } catch (e: Exception) {
            "$start to $end"
        }
    }

    /** Replace this with your real API/DB call */
    private fun getSampleTransactions(): List<BillTransactionRow> = listOf(
        BillTransactionRow(1, "11-Feb-2026", "Cash-To-Agent",     "",  "",    "20",  "-71"),
        BillTransactionRow(2, "12-Feb-2026", "Cash-To-Agent",     "",  "",    "100", "29"),
        BillTransactionRow(3, "18-Feb-2026", "UPI",               "",  "1",   "",    "131"),
        BillTransactionRow(4, "18-Feb-2026", "3+3 Trial Offer",   "",  "",    "90",  "221"),
        BillTransactionRow(5, "26-Feb-2026", "Cash-To-Agent",     "",  "",    "123", "-546"),
        BillTransactionRow(6, "11-Mar-2026", "Cow Ghee 1000ml",   "1", "890", "",    "2980.0"),
        BillTransactionRow(7, "11-Mar-2026", "Cow Ghee 1000ml",   "1", "890", "",    "2090.0"),
        BillTransactionRow(8, "11-Mar-2026", "A2 Cow Milk 500ml", "2", "90",  "",    "2000.0")
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



