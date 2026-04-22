package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.features.booking.model.BookingHistory
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


enum class BookingTab {
    ONGOING, COMPLETED, CANCELLED
}
class BookingHistoryViewModel(private val repository: BookingRepository) : ViewModel() {

    private val allHistories = mutableListOf<BookingHistory>()

    private val _filteredHistories = MutableLiveData<List<BookingHistory>>()
    val filteredHistories: LiveData<List<BookingHistory>> = _filteredHistories

    private val _currentTab = MutableLiveData(BookingTab.ONGOING)
    val currentTab: LiveData<BookingTab> = _currentTab

    // Fix cứng ID customer trước để test
    private val currentCustomerId = 7

    fun loadHistories() {
        viewModelScope.launch {
            try {
                val data = repository.getBookingsByCustomerId(currentCustomerId)
                allHistories.clear()

                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val dateFormat = SimpleDateFormat("dd 'Th'MM, yyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

                val mappedData = data.map { booking ->
                    var dateStr = booking.schedule
                    var timeStr = ""
                    try {
                        val dateObj = inputFormat.parse(booking.schedule)
                        if (dateObj != null) {
                            dateStr = dateFormat.format(dateObj)
                            timeStr = timeFormat.format(dateObj)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // Ánh xạ an toàn chuỗi status từ DB sang Enum
                    val parsedStatus = try {
                        BookingStatus.valueOf(booking.status.uppercase(Locale.ROOT).trim())
                    } catch (e: Exception) {
                        BookingStatus.PENDING
                    }

                    BookingHistory(
                        id = booking.id,
                        serviceName = booking.service?.name ?: "Dịch vụ #${booking.serviceId}",
                        statusEnum = parsedStatus,
                        priceText = String.format("%,.0fđ", booking.totalPrice),
                        packageType = "${booking.quantity} giờ",
                        date = dateStr,
                        time = timeStr
                    )
                }

                allHistories.addAll(mappedData)
                filterHistories()
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

        // Lọc trực tiếp bằng Enum cực kỳ an toàn
        val filteredList = when (tab) {
            BookingTab.ONGOING -> allHistories.filter {
                it.statusEnum in listOf(BookingStatus.PENDING, BookingStatus.IN_PROGRESS, BookingStatus.CONFIRMED)
            }
            BookingTab.COMPLETED -> allHistories.filter { it.statusEnum == BookingStatus.COMPLETED }
            BookingTab.CANCELLED -> allHistories.filter { it.statusEnum == BookingStatus.REJECTED }
        }
        _filteredHistories.value = filteredList
    }
}