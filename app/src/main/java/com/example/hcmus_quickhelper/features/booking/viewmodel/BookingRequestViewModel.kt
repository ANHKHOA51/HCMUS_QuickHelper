package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.booking.repository.BookingRequestRepository
import kotlinx.coroutines.launch

enum class BookingRequestTab {
    NEWEST, UPCOMING, COMPLETED
}

class BookingRequestViewModel (
    private val bookingRepository: BookingRequestRepository
) : ViewModel() {
    private val _currentTab = MutableLiveData(BookingRequestTab.NEWEST)
    val currentTab: LiveData<BookingRequestTab> = _currentTab

    private val _bookings = MutableLiveData<List<BookingRequest>>()
    val bookings: LiveData<List<BookingRequest>> = _bookings

    fun selectTab(tab: BookingRequestTab) {
        _currentTab.value = tab
    }

    fun loadBookings() {
        viewModelScope.launch {
            try {
                val data = bookingRepository.getAllBookingRequest()
                _bookings.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
