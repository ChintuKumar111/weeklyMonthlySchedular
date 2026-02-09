package com.example.freshyzoappmodule.utils
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

open class BaseActivityy : AppCompatActivity() {

    protected lateinit var analytics: FirebaseAnalytics
    private var startTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        analytics = Firebase.analytics

        FirebaseAnalytics.getInstance(this)
            .setAnalyticsCollectionEnabled(true)

    }

    override fun onResume() {
        super.onResume()
        startTime = System.currentTimeMillis()

        // Screen View
        analytics.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            Bundle().apply {
                putString(
                    FirebaseAnalytics.Param.SCREEN_NAME,
                    this@BaseActivityy::class.java.simpleName
                )
                putString(
                    FirebaseAnalytics.Param.SCREEN_CLASS,
                    this@BaseActivityy::class.java.simpleName
                )
            }
        )
    }


    override fun onPause() {
        super.onPause()

        val duration = System.currentTimeMillis() - startTime

        analytics.logEvent("time_spent", Bundle().apply {
            putString("screen", this@BaseActivityy::class.java.simpleName)
            putLong("duration_ms", duration)
        })
    }

    override fun onBackPressed() {
        analytics.logEvent("back_pressed", Bundle().apply {
            putString("screen", this@BaseActivityy::class.java.simpleName)
        })
        super.onBackPressed()
    }

    // ✅ BUTTON CLICK TRACKING (ADD THIS)
    protected fun logButtonClick(buttonName: String) {
        analytics.logEvent("button_click", Bundle().apply {
            putString("screen", this@BaseActivityy::class.java.simpleName)
            putString("button_name", buttonName)
        })
    }
}
