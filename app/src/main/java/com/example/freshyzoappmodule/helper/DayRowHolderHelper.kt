package com.example.freshyzoappmodule.helper

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.freshyzoappmodule.R

class DayRowHolderHelper(val root: View) {
    val dayToggle: FrameLayout = root.findViewById(R.id.dayToggle)
    val ivCheck: ImageView = root.findViewById(R.id.ivDayCheck)
    val tvDayName: TextView = root.findViewById(R.id.tvDayName)
    val miniStepper: LinearLayout = root.findViewById(R.id.miniStepper)
    val btnMinus: ImageButton = root.findViewById(R.id.btnDayMinus)
    val btnPlus: ImageButton = root.findViewById(R.id.btnDayPlus)
    val tvDayQty: TextView = root.findViewById(R.id.tvDayQty)
    val tvDayPrice: TextView = root.findViewById(R.id.tvDayPrice)
}