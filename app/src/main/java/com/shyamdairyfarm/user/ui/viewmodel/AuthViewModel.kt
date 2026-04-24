package com.shyamdairyfarm.user.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.shyamdairyfarm.user.data.model.auth.res.OtpRes
import com.shyamdairyfarm.user.data.repository.AuthRepository
import com.shyamdairyfarm.user.data.repository.session.SessionRepository
import com.shyamdairyfarm.user.utils.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthViewModel(
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _isSignUpMode = MutableLiveData(false)
    val isSignUpMode: LiveData<Boolean> = _isSignUpMode

    private val _phoneNumber = MutableStateFlow<String>("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _verificationId = MutableLiveData<String>()
    val verificationId = _verificationId

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState


    private val _isLoggedIn = MutableLiveData<Boolean>(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {

        _isLoggedIn.value = sessionRepository.isLoggedIn()
    }

    fun toggleSignUpMode() {
        _isSignUpMode.value = !(_isSignUpMode.value ?: false)
    }

    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone
    }

    fun setVerificationId(id: String) {
        _verificationId.value = id
    }

    private val _requestOtpState = MutableStateFlow<UiState<OtpRes>>(UiState.Idle)
    val requestOtpState = _requestOtpState.asStateFlow()

    fun requestOtp() {
        viewModelScope.launch {
            try {
                if (!_phoneNumber.value.isNullOrEmpty()) {
                    authRepository.requestOtp(_phoneNumber.value).collectLatest {
                        _requestOtpState.value = it
                    }
                }


            } catch (e: Exception) {
                _requestOtpState.value = UiState.Error(e.message ?: "Something went wrong...")
            }
        }
    }

    fun resetOtpRequestState() {
        viewModelScope.launch {
            delay(800)

            _requestOtpState.value = UiState.Idle

        }
    }

    private val _verifyOtpState = MutableStateFlow<UiState<OtpRes>>(UiState.Idle)
    val verifyOtpState = _verifyOtpState.asStateFlow()


    fun signInWithOtp(otp : String){
        viewModelScope.launch {
            try {
                if (_phoneNumber.value.isNotEmpty() && !otp.isNullOrEmpty()) {
                    authRepository.verifyOtp(_phoneNumber.value, otp).collectLatest {
                        _verifyOtpState.value = it

                        when(it){
                            is UiState.Error -> _authState.value = AuthState.Error(it.message)
                            UiState.Idle -> _authState.value = AuthState.Idle
                            UiState.Loading -> _authState.value = AuthState.Loading
                            is UiState.Success ->  _authState.value = AuthState.Success

                        }


                    }
                }


            } catch (e: Exception) {
                _requestOtpState.value = UiState.Error(e.message ?: "Something went wrong...")
            }
        }
    }

    fun signInWithCredential(credential: PhoneAuthCredential) {

        println("Login ,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,")
        _authState.value = AuthState.Loading
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->


                println("Login $task")
                if (task.isSuccessful) {
                    _authState.value = AuthState.Success

                    // we will get the token we should store it in the sharedpref or some other secure store


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