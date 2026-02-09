package com.example.freshyzoappmodule.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.freshyzoappmodule.data.model.ProductModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
class SearchViewModel : ViewModel() {

    // 1. DATA
    private var productList: List<ProductModel> = emptyList()
    private val hints = listOf(
        "Search for milk",
        "Search ghee",
        "Search khowa",
        "Search buffalo milk",
        "Search paneer"
    )

    // 2. LIVE DATA (UI STATES)
    private val _filteredList = MutableLiveData<List<ProductModel>>(emptyList())
    val filteredList: LiveData<List<ProductModel>> = _filteredList

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isHintVisible = MutableLiveData(true)
    val isHintVisible: LiveData<Boolean> = _isHintVisible

    private val _currentHintIndex = MutableLiveData(0)
    val currentHintIndex: LiveData<Int> = _currentHintIndex

    private val _showNoMatch = MutableLiveData(false)
    val showNoMatch: LiveData<Boolean> = _showNoMatch

    // 3. JOBS

    private var searchJob: Job? = null
    private var hintJob: Job? = null

    init {
        startHintRotation()
    }

    // 4. INITIAL DATA
    fun setInitialProductList(list: List<ProductModel>) {
        productList = list
    }

    // 5. SEARCH LOGIC
    fun onSearchQueryChanged(query: String) {

        // stop previous search
        searchJob?.cancel()

        // if search empty
        if (query.isBlank()) {
            showHintState()
            return
        }

        // prepare search UI
        prepareSearchState()

        // start new search
        searchJob = viewModelScope.launch {

            delay(1000) // debounce

            val result = productList.filter {
                it.product_name.contains(query, ignoreCase = true)
            }

            _filteredList.value = result
            _isLoading.value = false
            _showNoMatch.value = result.isEmpty()
        }
    }

    // 6. UI STATE HELPERS


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

    // 7. HINT ROTATION


    private fun startHintRotation() {
        hintJob?.cancel()

        hintJob = viewModelScope.launch {
            while (true) {
                delay(2500)
                val next = ((_currentHintIndex.value ?: 0) + 1) % hints.size
                _currentHintIndex.value = next
            }
        }
    }

    private fun stopHintRotation() {
        hintJob?.cancel()
    }

    fun getHintText(index: Int): String {
        return hints[index]
    }

    override fun onCleared() {
        searchJob?.cancel()
        hintJob?.cancel()
    }
}
