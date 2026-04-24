package com.shyamdairyfarm.user.data.utils

fun getDeviceInfo(): String {
    val brand = android.os.Build.BRAND
    val model = android.os.Build.MODEL

    return "$brand $model"
}