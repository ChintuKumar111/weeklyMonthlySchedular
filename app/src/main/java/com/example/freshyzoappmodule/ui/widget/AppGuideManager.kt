package com.example.freshyzoappmodule.ui.widget

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
        val radius: Int = 44
    )

    private var isGuideRunning = false
    /**
     * Shows a welcome dialog asking the user if they want a tour.
     * If user skips, sets a global flag to prevent any subsequent guides.
     */
    fun showWelcomeDialog(prefKey: String, items: List<GuideItem>, onComplete: (() -> Unit)? = null) {
        if (prefs.getBoolean(prefKey, false) || prefs.getBoolean(GLOBAL_SKIP_KEY, false)) {
            onComplete?.invoke()
            return
        }

        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_app_guide_welcome, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Make background transparent to show card corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<MaterialButton>(R.id.btnStartGuide).setOnClickListener {
            dialog.dismiss()
            startGuide(prefKey, items, onComplete)
        }

        dialogView.findViewById<MaterialButton>(R.id.btnSkipGuide).setOnClickListener {
            // SET GLOBAL SKIP FLAG
            prefs.edit().putBoolean(GLOBAL_SKIP_KEY, true).apply()
            prefs.edit().putBoolean(prefKey, true).apply()
            dialog.dismiss()
            onComplete?.invoke()
        }

        dialog.show()
    }

    /**
     * Starts a guide sequence. Added check for global skip.
     */
    fun startGuide(prefKey: String, items: List<GuideItem>, onComplete: (() -> Unit)? = null) {
        // GLOBAL SKIP CHECK
        if (prefs.getBoolean(GLOBAL_SKIP_KEY, false) || prefs.getBoolean(prefKey, false)) {
            onComplete?.invoke()
            return
        }

        if (isGuideRunning || items.isEmpty()) return
        isGuideRunning = true
        showStep(prefKey, items, 0, onComplete)
    }

    private fun showStep(prefKey: String, items: List<GuideItem>, index: Int, onComplete: (() -> Unit)?) {
        if (index >= items.size) {
            prefs.edit().putBoolean(prefKey, true).apply()
            isGuideRunning = false
            activity.window.decorView.postDelayed({ onComplete?.invoke() }, 300)
            return
        }

        // PREVENT FURTHER STEPS IF GLOBAL SKIP IS SET
        if (prefs.getBoolean(GLOBAL_SKIP_KEY, false)) {
            isGuideRunning = false
            onComplete?.invoke()
            return
        }

        val item = items[index]

        // UX: Add stepper (e.g. 1/5) and clear instructions
        val stepperTitle = "${item.title} (${index + 1}/${items.size})"
        val stepperDesc = "${item.description}\n\n[Tap circle to Continue]\n[Tap outside to Skip All]"

        item.view.post {
            scrollToViewIfNeeded(item.view) {
                TapTargetView.showFor(activity,
                    TapTarget.forView(item.view, stepperTitle, stepperDesc)
                        .transparentTarget(true)
                        .outerCircleAlpha(0.96f)
                        .targetRadius(item.radius)
                        .cancelable(true)
                        .drawShadow(true)
                        .cancelable(false )
                        .tintTarget(true),
                    object : TapTargetView.Listener() {
                        private var isHandled = false

                        override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
                            super.onTargetDismissed(view, userInitiated)
                            if (!isHandled) {
                                isHandled = true
                                if (userInitiated) {
                                    // User clicked the target -> CONTINUE
                                    showStep(prefKey, items, index + 1, onComplete)
                                } else {
                                    // User clicked the outer circle -> SKIP ALL
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