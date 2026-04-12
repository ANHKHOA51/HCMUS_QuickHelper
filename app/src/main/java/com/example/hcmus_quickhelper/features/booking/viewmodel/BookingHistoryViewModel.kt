package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.booking.model.BookingHistory
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepositoryTmp
import kotlinx.coroutines.launch


enum class BookingTab {
    ONGOING, COMPLETED, CANCELLED
}
class BookingHistoryViewModel(private val repository: BookingRepositoryTmp) : ViewModel() {

    private val allHistories = mutableListOf<BookingHistory>()

    // Dùng để hiển thị lên Adapter
    private val _filteredHistories = MutableLiveData<List<BookingHistory>>()
    val filteredHistories: LiveData<List<BookingHistory>> = _filteredHistories

    // Theo dõi tab hiện tại
    private val _currentTab = MutableLiveData(BookingTab.ONGOING)
    val currentTab: LiveData<BookingTab> = _currentTab

    fun loadHistories() {
        viewModelScope.launch {
            try {
                val data = repository.getBookingHistories()
                allHistories.clear()
                allHistories.addAll(data)
                filterHistories() // Lọc ngay lần đầu tiên load data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectTab(tab: BookingTab) {
        _currentTab.value = tab
        filterHistories()
    }

    private fun filterHistories() {
        val tab = _currentTab.value ?: BookingTab.ONGOING
        val filteredList = when (tab) {
            BookingTab.ONGOING -> allHistories.filter { it.status == "ĐANG THỰC HIỆN" || it.status == "ĐÃ XÁC NHẬN" }
            BookingTab.COMPLETED -> allHistories.filter { it.status == "ĐÃ HOÀN THÀNH" }
            BookingTab.CANCELLED -> allHistories.filter { it.status == "ĐÃ HỦY" }
        }
        _filteredHistories.value = filteredList
    }
}