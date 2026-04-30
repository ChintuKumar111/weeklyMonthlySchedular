package com.shyamdairyfarm.user.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.shyamdairyfarm.user.data.model.auth.res.CustomerLoginRes
import com.shyamdairyfarm.user.data.model.auth.res.OtpRes
import com.shyamdairyfarm.user.data.model.auth.res.RegisterNewCustomerRes
import com.shyamdairyfarm.user.data.repository.AuthRepository
import com.shyamdairyfarm.user.data.repository.GeocoderRepository
import com.shyamdairyfarm.user.data.repository.session.SessionRepository
import com.shyamdairyfarm.user.utils.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthViewModel(
    private val sessionRepository: SessionRepository,
    private val authRepository: AuthRepository,
    private val geoRepo: GeocoderRepository
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


    private val _error = MutableLiveData("")
    val error = _error

    private val _name = MutableStateFlow<String>("")
    val name = _name.asStateFlow()


    fun updateName(value: String) {
        println("Name changed $value")
        _name.value = value
        sessionRepository.storeName(value)
    }

    private val _isNewCustomer = MutableLiveData<Boolean>(true)
    val isNewCustomer: LiveData<Boolean> = _isNewCustomer

    init {

        _isLoggedIn.value = sessionRepository.isLoggedIn()
        _isNewCustomer.value = sessionRepository.isNewCustomer()
        _phoneNumber.value = sessionRepository.getPhoneNumber() ?: ""
        _name.value = sessionRepository.getName() ?: ""

    }

    fun toggleSignUpMode() {
        _isSignUpMode.value = !(_isSignUpMode.value ?: false)
    }

    fun setPhoneNumber(phone: String) {
        _phoneNumber.value = phone
        sessionRepository.storePhoneNumber(phone)
    }

    fun setVerificationId(id: String) {
        _verificationId.value = id
    }

    private val _requestOtpState = MutableStateFlow<UiState<OtpRes>>(UiState.Idle)
    val requestOtpState = _requestOtpState.asStateFlow()

    fun requestOtp() {

        println("REquest Otp xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
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

    private val _verifyOtpState = MutableStateFlow<UiState<CustomerLoginRes>>(UiState.Idle)
    val verifyOtpState = _verifyOtpState.asStateFlow()


    fun signInWithOtp(otp: String) {
        viewModelScope.launch {
            try {
                if (_phoneNumber.value.isNotEmpty() && !otp.isNullOrEmpty()) {
                    authRepository.verifyOtp(_phoneNumber.value, otp).collectLatest {
                        _verifyOtpState.value = it

                        when (it) {
                            is UiState.Error -> _authState.value = AuthState.Error(it.message)
                            UiState.Idle -> _authState.value = AuthState.Idle
                            UiState.Loading -> _authState.value = AuthState.Loading
                            is UiState.Success -> {

                                sessionRepository.storeToken(it.data.data.token)
                                sessionRepository.storePhoneNumber(_phoneNumber.value)
                                sessionRepository.setAsNewCustomer(it.data.isNewCustomer)
                                _authState.value = AuthState.Success(it.data)
                                _isNewCustomer.value = it.data.isNewCustomer
                                _isLoggedIn.value = it.data.status
                            }
                            else->{

                            }

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
//                    _authState.value = AuthState.Success()

                    // we will get the token we should store it in the sharedpref or some other secure store


                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Login Failed")
                }
            }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }


    // for signup --------------------------------------------------


    private val _address = MutableLiveData<String>()
    val address: LiveData<String> = _address

    fun updateAddress(newAddress: String) {
        _address.value = newAddress
    }
    private val _newCustomerSignUpState = MutableStateFlow<UiState<RegisterNewCustomerRes>>(UiState.Idle)
    val newCustomerSignUpState = _newCustomerSignUpState.asStateFlow()


    fun fetchAddress(latLng: LatLng) {
        viewModelScope.launch {
            _address.value = "Fetching address..."
            val result = geoRepo.getAddress(latLng)
            _address.value = result
        }
    }


    private val _lat = MutableStateFlow<Double?>(null)
    private val _lng = MutableStateFlow<Double?>(null)

    fun updateLatLang(lat: Double, lng: Double) {
        _lat.value = lat
        _lng.value = lng
    }

    fun registerNewCustomer() {
        viewModelScope.launch {
            _newCustomerSignUpState.value = UiState.Loading
            if (_phoneNumber.value.isEmpty()) {
                _newCustomerSignUpState.value =
                    UiState.Error("Mobile number is missing...")
                return@launch
            }
            if (_lat.value == null || _lng.value == null || _address.value == null) {
                _newCustomerSignUpState.value =
                    UiState.Error("Address and location  is required...")

                return@launch
            }


            try {
                println("Name changed ${_name.value}")
                authRepository.registerNewCustomer(
                    mobileNo = _phoneNumber.value,
                    firstName = _name.value.trim().substringBefore(" "),
                    lastName = _name.value.trim()
                        .substringAfter(" ", "")
                        .takeIf { _name.value.trim().contains(" ") } ?: "",
                    lat = _lat.value?.toString() ?: "",
                    lng = _lng.value?.toString() ?: "",
                    address = _address.value ?: ""
                ).collectLatest {
                    _newCustomerSignUpState.value = it
                    if(it is UiState.ExpiredToken){

                    }

                    println("Sign Up new customer $it")
                }


            } catch (e: Exception) {
                _newCustomerSignUpState.value =
                    UiState.Error(e.message ?: "Something went wrong...", e)
            }


        }
    }


    fun logout(){
        viewModelScope.launch {
            sessionRepository.clearSession()
            _isNewCustomer.value = true
            _isLoggedIn.value = false
        }
    }
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        data class Success(val data: CustomerLoginRes) : AuthState()
        data object LogOut : AuthState()
        data class Error(val message: String) : AuthState()
    }
}