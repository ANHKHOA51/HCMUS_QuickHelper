package com.example.hcmus_quickhelper.features.service_browsing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.repository.ServiceListRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.hcmus_quickhelper.R

class ServiceListViewModel(
    private val repository: ServiceListRepository
) : ViewModel() {

    // Danh sách gốc chứa toàn bộ data fetch từ Supabase
    private var originalHelpers = listOf<Helper>()
    private val _helpers = MutableLiveData<List<Helper>>()
    val helpers: LiveData<List<Helper>> get() = _helpers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Lưu trữ trạng thái query và filter  hiện tại
    private var currentSearchQuery = ""
    private var currentFilterId = R.id.chipAll

    fun loadHelpers() {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.getHelpers()
            result.onSuccess { data ->
                originalHelpers = data
                applyFiltersAndSearch()
            }.onFailure {
                // Xử lý báo lỗi ở đây nếu cần (Toast, Log...)
            }

            _isLoading.value = false
        }
    }

    fun search(query: String) {
        currentSearchQuery = query
        applyFiltersAndSearch()
    }

    fun setFilter(chipId: Int) {
        currentFilterId = chipId
        applyFiltersAndSearch()
    }

    // Hàm  xử lý logic gộp chung cả Tìm kiếm và Lọc
    private fun applyFiltersAndSearch() {
        var filteredList = originalHelpers

        if (currentSearchQuery.isNotBlank()) {
            filteredList = filteredList.filter { helper ->
                helper.name.contains(currentSearchQuery, ignoreCase = true) ||
                        helper.skills.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        filteredList = when (currentFilterId) {
            R.id.chipPrice -> filteredList.sortedBy { it.price }
            R.id.chipRating -> filteredList.filter { it.rating >= 4.5 }
            else -> filteredList
        }
        _helpers.value = filteredList
    }
}