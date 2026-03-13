package com.example.freshyzoappmodule.ui.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.pdf.PrintedPdfDocument
import android.view.View
import java.io.FileOutputStream
import java.io.IOException

class InvoicePrintAdapter(
    private val context: Context,
    private val view: View
) : PrintDocumentAdapter() {

    private var pdfDocument: PrintedPdfDocument? = null
    private var totalPages = 1
    private var pageHeight = 0
    private var pageWidth = 0
    private var scale = 1f

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        pdfDocument = PrintedPdfDocument(context, newAttributes)

        // Calculate dimensions in Points (1/72 inch)
        // Mils to Points: (mils / 1000) * 72
        pageHeight = newAttributes.mediaSize?.heightMils?.let { it * 72 / 1000 } ?: 842
        pageWidth = newAttributes.mediaSize?.widthMils?.let { it * 72 / 1000 } ?: 595

        // Measure the view to get its content size
        // We want to know how tall the view is when its width is constrained to a reasonable desktop/mobile width
        // If the view is already measured, use that width as a hint, otherwise default to a standard width.
        val measureWidth = if (view.width > 0) view.width else 1080
        
        view.measure(
            View.MeasureSpec.makeMeasureSpec(measureWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        
        val contentWidth = view.measuredWidth
        val contentHeight = view.measuredHeight

        // Calculate scale to fit the page width (with some margins if needed, but here we fit to width)
        if (contentWidth > 0) {
            scale = pageWidth.toFloat() / contentWidth.toFloat()
        }

        // Calculate total pages based on scaled height
        val scaledContentHeight = contentHeight * scale
        totalPages = Math.ceil(scaledContentHeight.toDouble() / pageHeight.toDouble()).toInt()
        if (totalPages <= 0) totalPages = 1

        val info = PrintDocumentInfo.Builder("invoice.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(totalPages)
            .build()

        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        val pdf = pdfDocument ?: return

        for (i in 0 until totalPages) {
            if (cancellationSignal?.isCanceled == true) {
                callback?.onWriteCancelled()
                return
            }

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create()
            val page = pdf.startPage(pageInfo)

            val canvas: Canvas = page.canvas

            canvas.save()
            
            // Apply scaling to fit page width
            canvas.scale(scale, scale)
            
            // Translate to the correct vertical section for this page
            // The offset is in the view's coordinate system, so we divide pageHeight by scale
            val topOffset = -(i * pageHeight).toFloat() / scale
            canvas.translate(0f, topOffset)

            // Ensure the view is laid out properly before drawing
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            view.draw(canvas)
            
            canvas.restore()

            pdf.finishPage(page)
        }

        try {
            pdf.writeTo(FileOutputStream(destination?.fileDescriptor))
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: IOException) {
            callback?.onWriteFailed(e.message)
        } finally {
            pdf.close()
            pdfDocument = null
        }
    }
}
