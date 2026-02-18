package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.SliderItem
import com.example.freshyzoappmodule.data.repository.SliderRepository
import kotlinx.coroutines.launch


class HomeFragmentViewModel : ViewModel() {

    private val repository = SliderRepository()

    private val _sliderData = MutableLiveData<List<SliderItem>>()
    val sliderData: LiveData<List<SliderItem>> = _sliderData




    // image slider section on home screen
    fun fetchSlider() {
        viewModelScope.launch {
            try {
                val response = repository.getSliderImages()

                if (response.isSuccessful &&
                    response.body()?.status == true &&
                    !response.body()?.data.isNullOrEmpty()
                ) {
                    _sliderData.value = response.body()?.data
                } else {
                    _sliderData.value = emptyList()  // trigger fallback
                }

            } catch (e: Exception) {
                _sliderData.value = emptyList()  // API failed
            }
        }
    }
}
