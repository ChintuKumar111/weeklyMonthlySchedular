package com.example.freshyzoappmodule.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.Product
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)

    private var productList: List<Product> = emptyList()
    private val hints = listOf("Search for milk", "Search ghee", "Search khowa", "Search buffalo milk", "Search paneer")

    private val _filteredList = MutableLiveData<List<Product>>(emptyList())
    val filteredList: LiveData<List<Product>> = _filteredList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isHintVisible = MutableLiveData(true)
    val isHintVisible: LiveData<Boolean> = _isHintVisible

    private val _currentHintIndex = MutableLiveData(0)
    val currentHintIndex: LiveData<Int> = _currentHintIndex

    private val _showNoMatch = MutableLiveData(false)
    val showNoMatch: LiveData<Boolean> = _showNoMatch

    private val _recentSearches = MutableLiveData<List<String>>(emptyList())
    val recentSearches: LiveData<List<String>> = _recentSearches

    private var searchJob: Job? = null
    private var hintJob: Job? = null

    init {
        loadRecentSearches()
        startHintRotation()
    }

    fun setInitialProductList(list: List<Product>) {
        productList = list
    }

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            showHintState()
            return
        }

        prepareSearchState()

        searchJob = viewModelScope.launch {
            delay(1000) // debounce
            val result = productList.filter {
                it.productName.contains(query, ignoreCase = true)
            }
            _filteredList.value = result
            _isLoading.value = false
            _showNoMatch.value = result.isEmpty()
            
            if (result.isNotEmpty()) {
                saveRecentSearch(result[0].productName)
            }
        }
    }

    private fun saveRecentSearch(name: String) {
        val current = _recentSearches.value?.toMutableList() ?: mutableListOf()
        current.remove(name)
        current.add(0, name)
        if (current.size > 10) current.removeAt(10)
        _recentSearches.value = current
        persistRecentSearches(current)
    }

    fun deleteRecentSearch(query: String) {
        val current = _recentSearches.value?.toMutableList() ?: mutableListOf()
        if (current.remove(query)) {
            _recentSearches.value = current
            persistRecentSearches(current)
        }
    }

    private fun persistRecentSearches(list: List<String>) {
        val stringData = list.joinToString(separator = "|")
        sharedPrefs.edit().putString("recent_ordered", stringData).apply()
    }

    private fun loadRecentSearches() {
        val stringData = sharedPrefs.getString("recent_ordered", "") ?: ""
        if (stringData.isNotEmpty()) {
            _recentSearches.value = stringData.split("|")
        }
    }

    private fun showHintState() {
        _isHintVisible.value = true
        _isLoading.value = false
        _filteredList.value = emptyList()
        _showNoMatch.value = false
        startHintRotation()
    }

    private fun prepareSearchState() {
        _isHintVisible.value = false
        _isLoading.value = true
        _showNoMatch.value = false
        _filteredList.value = emptyList()
        stopHintRotation()
    }

    private fun startHintRotation() {
        hintJob?.cancel()
        hintJob = viewModelScope.launch {
            while (true) {
                delay(2500)
                val nextIndex = ((_currentHintIndex.value ?: 0) + 1) % hints.size
                _currentHintIndex.value = nextIndex
            }
        }
    }

    private fun stopHintRotation() {
        hintJob?.cancel()
    }

    fun getHintText(index: Int): String = hints[index]

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        hintJob?.cancel()
    }
}
