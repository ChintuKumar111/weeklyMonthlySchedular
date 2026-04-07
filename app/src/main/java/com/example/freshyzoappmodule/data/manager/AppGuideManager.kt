package com.example.freshyzoappmodule.data.manager

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewParent
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import com.example.freshyzoappmodule.R
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.button.MaterialButton

/**
 * Robust App Guide Manager with Welcome Dialog, Steppers, and Global Skip logic.
 */
class AppGuideManager(private val activity: Activity) {
    private val prefs = activity.getSharedPreferences("app_guide_prefs", Context.MODE_PRIVATE)
    private val GLOBAL_SKIP_KEY = "global_guide_skip"

    data class GuideItem(
        val view: View,
        val title: String,
        val description: String,
        val radius: Int = 44,
        val performClick: Boolean = false
    )

    private var isGuideRunning = false

    /**
     * Shows a welcome dialog asking the user if they want a tour.
     */
    fun showWelcomeDialog(prefKey: String, onStart: () -> Unit, onSkip: () -> Unit) {
        if (prefs.getBoolean(prefKey, false) || prefs.getBoolean(GLOBAL_SKIP_KEY, false)) {
            onSkip()
            return
        }

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_app_guide_welcome, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<MaterialButton>(R.id.btnStartGuide).setOnClickListener {
            dialog.dismiss()
            onStart()
        }

        dialogView.findViewById<MaterialButton>(R.id.btnSkipGuide).setOnClickListener {
            prefs.edit().putBoolean(GLOBAL_SKIP_KEY, true).apply()
            prefs.edit().putBoolean(prefKey, true).apply()
            dialog.dismiss()
            onSkip()
        }

        dialog.show()
    }

    /**
     * Starts a guide sequence.
     */
    fun startGuide(prefKey: String, items: List<GuideItem>, onComplete: (() -> Unit)? = null) {
        if (prefs.getBoolean(GLOBAL_SKIP_KEY, false) || prefs.getBoolean(prefKey, false)) {
            onComplete?.invoke()
            return
        }

        if (isGuideRunning || items.isEmpty()) {
            onComplete?.invoke()
            return
        }
        isGuideRunning = true
        showStep(prefKey, items, 0, onComplete)
    }

    private fun showStep(prefKey: String, items: List<GuideItem>, index: Int, onComplete: (() -> Unit)?) {
        if (index >= items.size) {
            prefs.edit().putBoolean(prefKey, true).apply()
            isGuideRunning = false
            showCompletionDialog(onComplete)
            return
        }

        if (prefs.getBoolean(GLOBAL_SKIP_KEY, false)) {
            isGuideRunning = false
            onComplete?.invoke()
            return
        }

        val item = items[index]
        val stepperTitle = "${item.title} (${index + 1}/${items.size})"
        val stepperDesc = "${item.description}\n\n[Tap circle to Continue]\n[Tap outside to Skip All]"

        item.view.post {
            scrollToViewIfNeeded(item.view) {
                TapTargetView.showFor(
                    activity,
                    TapTarget.forView(item.view, stepperTitle, stepperDesc)
                        .transparentTarget(true)
                        .outerCircleAlpha(0.96f)
                        .targetRadius(item.radius)
                        .cancelable(true)
                        .drawShadow(true)
                        .tintTarget(true),
                    object : TapTargetView.Listener() {
                        private var isHandled = false

                        override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                            super.onTargetDismissed(view, userInitiated)
                            if (!isHandled) {
                                isHandled = true
                                if (userInitiated) {
                                    if (item.performClick) {
                                        item.view.performClick()
                                        // Delay next step to allow UI/Fragment to react
                                        item.view.postDelayed({
                                            showStep(prefKey, items, index + 1, onComplete)
                                        }, 400)
                                    } else {
                                        showStep(prefKey, items, index + 1, onComplete)
                                    }
                                } else {
                                    isGuideRunning = false
                                    prefs.edit().putBoolean(GLOBAL_SKIP_KEY, true).apply()
                                    prefs.edit().putBoolean(prefKey, true).apply()
                                    onComplete?.invoke()
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    /**
     * Shows a beautiful completion dialog at the end of the tour.
     */
    private fun showCompletionDialog(onComplete: (() -> Unit)?) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_tour_completed, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<MaterialButton>(R.id.btnFinishTour).setOnClickListener {
            dialog.dismiss()
            onComplete?.invoke()
        }

        dialog.show()
    }

    private fun scrollToViewIfNeeded(view: View, onReady: () -> Unit) {
        val nestedScroll = findNestedScrollView(view)
        if (nestedScroll != null) {
            val rect = Rect()
            nestedScroll.getHitRect(rect)
            if (view.getLocalVisibleRect(rect)) {
                onReady()
            } else {
                val childRect = Rect()
                view.getDrawingRect(childRect)
                nestedScroll.offsetDescendantRectToMyCoords(view, childRect)
                nestedScroll.smoothScrollTo(0, childRect.top - 250)
                view.postDelayed({ onReady() }, 600)
            }
        } else {
            onReady()
        }
    }

    private fun findNestedScrollView(view: View): NestedScrollView? {
        var parent: ViewParent? = view.parent
        while (parent != null) {
            if (parent is NestedScrollView) return parent
            parent = parent.parent
        }
        return null
    }
}
