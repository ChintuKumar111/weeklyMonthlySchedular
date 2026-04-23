package com.shyamdairyfarm.user.extensions

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.print.PrintManager
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.ui.adapter.InvoicePrintAdapter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoicePrintExtensions(private val activity: AppCompatActivity) {

    fun printInvoice(view: View) {
        // 1. Force the view to calculate its full height based on its content (all rows)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(widthSpec, heightSpec)

        // 2. Lay it out so the PrintAdapter sees the full height
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        val printManager = activity.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${activity.getString(R.string.app_name)} Invoice Print"

        // 3. Pass the view to the adapter
        val printAdapter = InvoicePrintAdapter(activity, view)

        printManager.print(jobName, printAdapter, null)
    }

    fun openPdfFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NO_HISTORY
            }
            activity.startActivity(Intent.createChooser(intent, "Open PDF"))
        } catch (e: Exception) {
            Toast.makeText(activity, "No PDF viewer found", Toast.LENGTH_SHORT).show()
        }
    }

    fun captureFullView(mainView: View, wideContainer: View? = null): Bitmap {
        // Measure the full required width
        val tableWidth = wideContainer?.let {
            it.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            it.measuredWidth + mainView.paddingStart + mainView.paddingEnd
        } ?: 0

        val targetWidth = maxOf(mainView.width, tableWidth)

        mainView.measure(
            View.MeasureSpec.makeMeasureSpec(targetWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val targetHeight = mainView.measuredHeight
        mainView.layout(0, 0, targetWidth, targetHeight)

        val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        mainView.draw(canvas)
        return bitmap
    }

    fun writeBitmapToPdf(bitmap: Bitmap, billMonth: String): File? {
        return try {
            val pageW = 595 // A4 points
            val pageH = 842 // A4 points
            val margin = 24

            val contentWidth = pageW - margin * 2
            val scale = contentWidth.toFloat() / bitmap.width
            val usablePageH = pageH - margin * 2
            val pxPerPage = (usablePageH / scale).toInt()

            val doc = PdfDocument()
            var pageNum = 1
            var srcTopPx = 0

            val footerPaint = Paint().apply {
                color = Color.parseColor("#AAAAAA")
                textSize = 8f
                textAlign = Paint.Align.CENTER
            }
            val timestamp = SimpleDateFormat(
                "dd MMM yyyy, HH:mm",
                Locale.getDefault()
            ).format(Date())

            while (srcTopPx < bitmap.height) {
                val srcBottom = minOf(srcTopPx + pxPerPage, bitmap.height)
                val srcRect = Rect(0, srcTopPx, bitmap.width, srcBottom)
                val dstH = ((srcBottom - srcTopPx) * scale).toInt()
                val dstRect = RectF(
                    margin.toFloat(), margin.toFloat(),
                    (margin + contentWidth).toFloat(), (margin + dstH).toFloat()
                )

                val pageInfo = PdfDocument.PageInfo.Builder(pageW, pageH, pageNum).create()
                val page = doc.startPage(pageInfo)
                page.canvas.drawBitmap(bitmap, srcRect, dstRect, null)
                page.canvas.drawText("Page $pageNum  •  $timestamp", (pageW / 2).toFloat(), (pageH - 10).toFloat(), footerPaint)
                doc.finishPage(page)

                srcTopPx = srcBottom
                pageNum++
            }

            val dir = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            dir?.mkdirs()
            val file = File(dir, "Invoice_${billMonth}_${System.currentTimeMillis()}.pdf")
            doc.writeTo(FileOutputStream(file))
            doc.close()
            bitmap.recycle()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}