package com.shyamdairyfarm.user.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    
    private val auth = FirebaseAuth.getInstance()

    private val _isSignUpMode = MutableLiveData(false)
    val isSignUpMode: LiveData<Boolean> = _isSignUpMode

    private val _phoneNumber = MutableLiveData<String>()
    val phoneNumber: LiveData<String> = _phoneNumber

    private val _verificationId = MutableLiveData<String>()
    val verificationId: LiveData<String> = _verificationId

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    fun toggleSignUpMode() {
        _isSignUpMode.value = !(_isSignUpMode.value ?: false)
    }

    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone
    }

    fun setVerificationId(id: String) {
        _verificationId.value = id
    }

    fun signInWithCredential(credential: PhoneAuthCredential) {
        _authState.value = AuthState.Loading
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login Failed")
                }
            }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }
}