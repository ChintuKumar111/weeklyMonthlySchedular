package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.BlogReport
import com.example.freshyzoappmodule.data.model.ComboOffer
import com.example.freshyzoappmodule.data.model.Banner
import com.example.freshyzoappmodule.data.model.CalendarDay
import com.example.freshyzoappmodule.data.repository.SliderRepository
import kotlinx.coroutines.launch

class HomeFragmentViewModel(private val repository: SliderRepository) : ViewModel() {

    private val _sliderData = MutableLiveData<List<Banner>>()
    val sliderData: LiveData<List<Banner>> = _sliderData

    private val _comboOffers = MutableLiveData<List<ComboOffer>>()
    val comboOffers: LiveData<List<ComboOffer>> = _comboOffers

    private val _blogReports = MutableLiveData<List<BlogReport>>()
    val blogReports: LiveData<List<BlogReport>> = _blogReports

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
                    _sliderData.value = emptyList()  // trigger fallback in UI if needed
                }

            } catch (e: Exception) {
                _sliderData.value = emptyList()  // API failed
            }
        }
    }

    fun fetchComboOffers() {
        viewModelScope.launch {
            try {
                val response = repository.getComboOffers()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    _comboOffers.value = response.body()
                } else {
                    _comboOffers.value = getFallbackComboOffers()
                }
            } catch (e: Exception) {
                _comboOffers.value = getFallbackComboOffers()
            }
        }
    }

    fun fetchBlogReports() {
        viewModelScope.launch {
            try {
                val response = repository.getBlogReports()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    _blogReports.value = response.body()
                } else {
                    _blogReports.value = getFallbackBlogReports()
                }
            } catch (e: Exception) {
                _blogReports.value = getFallbackBlogReports()
            }
        }
    }

    private fun getFallbackComboOffers(): List<ComboOffer> {
        // These represent the hardcoded data from the "previous design"
        return listOf(
            ComboOffer(
                comboId = "default_1",
                title = "Daily Essentials", 
                description = "Perfect mix of milk and dairy products for your family.",
                price = "350",
                imageUrl = "android.resource://com.example.freshyzoappmodule/drawable/combo_offer"
            ),
            ComboOffer(
                comboId = "default_2",
                title = "Breakfast Special",
                description = "Healthy start with our premium milk and fresh dahi.",
                price = "280",
                imageUrl = "android.resource://com.example.freshyzoappmodule/drawable/combo_offer"
            )
        )
    }

    private fun getFallbackBlogReports(): List<BlogReport> {
        // These represent the hardcoded data from the "previous design"
        return listOf(
            BlogReport(
                blogId = "blog_1",
                title = "Free Grazing",
                description = "Our cows are happy and healthy with free grazing practices.",
                imageUrl = "android.resource://com.example.freshyzoappmodule/drawable/blog1"
            ),
            BlogReport(
                blogId = "blog_2",
                title = "Farm to Door",
                description = "Experience the journey of milk from our farm to your doorstep.",
                imageUrl = "android.resource://com.example.freshyzoappmodule/drawable/blog2"
            )
        )
    }

    fun getDeliveryProducts(calendarDay: String){
        // Logic to fetch delivery products based on date
    }
}
