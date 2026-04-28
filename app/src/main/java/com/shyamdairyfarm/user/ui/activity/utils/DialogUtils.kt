package com.shyamdairyfarm.user.ui.activity.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.ui.activity.AuthActivity

object DialogUtils {

    fun showErrorDialog(
        context: Context,
        title : String = "Error",
        msg : String = "Something went wrong...",
        btnText : String = "Ok",
        logout: () -> Unit
    ) {
        val dialog = Dialog(context)

        val view = LayoutInflater.from(context)
            .inflate(R.layout.dialog_custom, null)

        dialog.setContentView(view)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.setGravity(Gravity.CENTER)

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)
        val btnOk = view.findViewById<MaterialButton>(R.id.btnOk)

        // ✅ Custom content
        tvTitle.text = title
        tvMessage.text = msg
        btnOk.text = btnText

        // 🔴 Red button
        btnOk.setBackgroundTintList(
            ContextCompat.getColorStateList(context, R.color.red_error)
        )

        btnOk.setOnClickListener {
            dialog.dismiss()

            logout()

            val intent = Intent(context, AuthActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            context.startActivity(intent)
        }

        dialog.show()
    }
}