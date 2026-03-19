package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.HomeBlogs
import com.example.freshyzoappmodule.data.model.HomeComboOffers
import com.example.freshyzoappmodule.data.model.response.Banner
import com.example.freshyzoappmodule.data.model.HomeProductDeliveryCalendar
import com.example.freshyzoappmodule.data.repository.SliderRepository
import kotlinx.coroutines.launch

class HomeFragmentViewModel(private val repository: SliderRepository) : ViewModel() {

    private val _sliderData = MutableLiveData<List<Banner>>()
    val sliderData: LiveData<List<Banner>> = _sliderData
    private val _Home_comboOffers = MutableLiveData<List<HomeComboOffers>>()
    val homeComboOffers: LiveData<List<HomeComboOffers>> = _Home_comboOffers
    private val _blogReports = MutableLiveData<List<HomeBlogs>>()
    val blogReports: LiveData<List<HomeBlogs>> = _blogReports

    private val _deliveryProducts = MutableLiveData<List<HomeProductDeliveryCalendar>?>()
    val deliveryProducts: LiveData<List<HomeProductDeliveryCalendar>?> = _deliveryProducts

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
                    _Home_comboOffers.value = response.body()
                } else {
                    _Home_comboOffers.value = getFallbackComboOffers()
                }
            } catch (e: Exception) {
                _Home_comboOffers.value = getFallbackComboOffers()
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
    private fun getFallbackComboOffers(): List<HomeComboOffers> {
        // These represent the hardcoded data from the "previous design"
        return listOf(
            HomeComboOffers(
                comboId = "default_1",
                title = "Daily Essentials", 
                description = "Perfect mix of milk and dairy products for your family.",
                price = "350",
                imageUrl = "android.resource://com.example.freshyzoappmodule/drawable/combo_offer"
            ),
            HomeComboOffers(
                comboId = "default_2",
                title = "Breakfast Special",
                description = "Healthy start with our premium milk and fresh dahi.",
                price = "280",
                imageUrl = "android.resource://com.example.freshyzoappmodule/drawable/combo_offer"
            )
        )
    }

    private fun getFallbackBlogReports(): List<HomeBlogs> {
        // These represent the hardcoded data from the "previous design"
        return listOf(
            HomeBlogs(
                blogId = "blog_1",
                title = "Free Grazing",
                description = "Our cows are happy and healthy with free grazing practices.",
                imageUrl = "android.resource://com.example.freshyzoappmodule/drawable/blog1"
            ),
            HomeBlogs(
                blogId = "blog_2",
                title = "Farm to Door",
                description = "Experience the journey of milk from our farm to your doorstep.",
                imageUrl = "android.resource://com.example.freshyzoappmodule/drawable/blog2"
            )
        )
    }

    fun getDeliveryProducts(date: String){
        viewModelScope.launch {
            try {
                val response = repository.getCalendarDeliveryDetails(date)
                if (response.products.isNullOrEmpty()) {
                    _deliveryProducts.value = getDemoProducts()
                } else {
                    _deliveryProducts.value = response.products
                }
            } catch (e: Exception) {
                _deliveryProducts.value = getDemoProducts()
            }
        }
    }

    private fun getDemoProducts(): List<HomeProductDeliveryCalendar> {
        return listOf(
            HomeProductDeliveryCalendar("Cow Milk", "1 Ltr", "https://img.etimg.com/thumb/msid-71119567,width-1070,height-580,imgsize-327421,overlay-etpanache/photo.jpg"),
            HomeProductDeliveryCalendar("Ghee", "250 gm", "https://www.tradeindia.com/products/100-pure-cow-ghee-6433757.jpg"),
            HomeProductDeliveryCalendar("Buffalow Milk", "500 ml", "https://5.imimg.com/data5/SELLER/Default/2021/3/RE/UX/YV/125232770/buffalo-milk.jpg")
        )
    }
}
