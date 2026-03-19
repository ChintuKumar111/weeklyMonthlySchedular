package com.example.freshyzoappmodule.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.HomeProductDeliveryCalendar
import com.example.freshyzoappmodule.data.repository.DeliveryCalendarRepository
import kotlinx.coroutines.launch

class DeliveryCalendarViewModel(
        private val repository: DeliveryCalendarRepository
    ) : ViewModel() {

        private val _calendarDates = MutableLiveData<List<String>>()
        val calendarDates: LiveData<List<String>> = _calendarDates

        private val _deliveryProducts = MutableLiveData<List< HomeProductDeliveryCalendar>>()
        val deliveryProducts: LiveData<List<HomeProductDeliveryCalendar>> = _deliveryProducts


        fun loadCalendar(month: Int, year: Int) {

            viewModelScope.launch {

                try {

                    val response = repository.getCalendar(month, year)

                    _calendarDates.value = response.dates

                } catch (e: Exception) {

                    e.printStackTrace()

                }

            }
        }


        fun getDeliveryProducts(date: String) {

            viewModelScope.launch {

                try {

                    val response = repository.getCalendarDeliveryDetails(date)

                    _deliveryProducts.value = response.products

                } catch (e: Exception) {

                    e.printStackTrace()

                }


            }
        }
    }
