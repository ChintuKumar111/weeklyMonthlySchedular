package com.shyamdairyfarm.user.data.manager

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import kotlin.math.max
import kotlin.math.min

/**
 * A custom FrameLayout that supports pinch-to-zoom and panning.
 * It ensures the child content is measured to its full size (for long/wide invoices)
 * and provides a fit-to-screen scaling logic.
 */
class ZoomLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var scaleFactor = 1.0f
    private var minScale = 0.1f
    private var maxScale = 5.0f

    private var posX = 0f
    private var posY = 0f

    private var isFirstLayout = true

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(minScale, min(scaleFactor, maxScale))
            
            updateBounds()
            invalidate()
            return true
        }
    })

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            posX -= distanceX
            posY -= distanceY
            
            updateBounds()
            invalidate()
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (scaleFactor > minScale * 1.5f) {
                resetZoom()
            } else {
                scaleFactor = 1.0f
                updateBounds()
                invalidate()
            }
            return true
        }
    })

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)

        if (childCount > 0) {
            val child = getChildAt(0)
            // 1. Measure child with UNSPECIFIED to allow it to grow to its full content size
            val childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            val childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            child.measure(childWidthSpec, childHeightSpec)
            
            // 2. Ensure child is at least as wide as the container
            if (child.measuredWidth < widthSize) {
                child.measure(
                    MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
                    childHeightSpec
                )
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount > 0) {
            val child = getChildAt(0)
            child.layout(0, 0, child.measuredWidth, child.measuredHeight)

            if (isFirstLayout && width > 0 && height > 0 && child.measuredHeight > 0) {
                resetZoom()
                isFirstLayout = false
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean = true

    private fun updateBounds() {
        val child = if (childCount > 0) getChildAt(0) else return
        
        val scaledWidth = child.measuredWidth * scaleFactor
        val scaledHeight = child.measuredHeight * scaleFactor

        if (scaledWidth <= width) {
            posX = (width - scaledWidth) / 2f
        } else {
            posX = max(width - scaledWidth, min(posX, 0f))
        }

        if (scaledHeight <= height) {
            posY = (height - scaledHeight) / 2f
        } else {
            posY = max(height - scaledHeight, min(posY, 0f))
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.translate(posX, posY)
        canvas.scale(scaleFactor, scaleFactor)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    /**
     * Resets zoom and position to fit the entire content within the screen.
     */
    fun resetZoom() {
        val child = if (childCount > 0) getChildAt(0) else return
        if (child.measuredWidth == 0 || child.measuredHeight == 0) return

        val scaleX = width.toFloat() / child.measuredWidth
        val scaleY = height.toFloat() / child.measuredHeight
        
        minScale = min(scaleX, scaleY)
        if (minScale > 1.0f) minScale = 1.0f
        
        scaleFactor = minScale
        
        posX = (width - child.measuredWidth * scaleFactor) / 2f
        posY = (height - child.measuredHeight * scaleFactor) / 2f
        invalidate()
    }
}
