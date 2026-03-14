package com.example.freshyzoappmodule.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.SubscriptionResponse
import com.example.freshyzoappmodule.data.repository.SubscriptionRepository
import kotlinx.coroutines.launch

class SubscriptionStatusViewModel(
    private val repository: SubscriptionRepository
) : ViewModel() {

    val activeList = MutableLiveData<List<SubscriptionResponse>>()
    val pauseList = MutableLiveData<List<SubscriptionResponse>>()
    val cancelList = MutableLiveData<List<SubscriptionResponse>>()

    // Load all subscriptions
    fun loadSubscriptions() {

        viewModelScope.launch {

            repository.getSubscriptions()?.let { list ->

                activeList.postValue(
                    list.filter { it.status == "ACTIVE" }
                )

                pauseList.postValue(
                    list.filter { it.status == "PAUSE" }
                )

                cancelList.postValue(
                    list.filter { it.status == "CANCEL" }
                )
            }
        }
    }

    // Pause subscription
    fun pauseSubscription(item: SubscriptionResponse) {

        viewModelScope.launch {

            repository.pauseSubscription(item)

            loadSubscriptions() // refresh list
        }
    }

    // Cancel subscription
    fun cancelSubscription(item: SubscriptionResponse) {

        viewModelScope.launch {

            repository.cancelSubscription(item)

            loadSubscriptions() // refresh list
        }
    }
}