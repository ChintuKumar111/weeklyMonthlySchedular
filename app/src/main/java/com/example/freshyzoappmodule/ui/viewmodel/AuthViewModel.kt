package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {    val phoneNumber = MutableLiveData<String>()
    val verificationId = MutableLiveData<String>() // For Firebase OTP

    fun setPhone(number: String) {
        phoneNumber.value = number
    }
}