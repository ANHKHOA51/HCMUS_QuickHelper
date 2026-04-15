package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest
import com.example.hcmus_quickhelper.features.booking.model.BookingStatus
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.booking.repository.BookingRequestRepository
import kotlinx.coroutines.launch

enum class BookingRequestTab {
    NEWEST, UPCOMING, COMPLETED
}

class BookingRequestViewModel (
    private val bookingRepository: BookingRequestRepository
) : ViewModel() {
    private val _currentTab = MutableLiveData<BookingRequestTab>(BookingRequestTab.NEWEST)
    val currentTab: LiveData<BookingRequestTab> = _currentTab

    private val _bookings = MutableLiveData<List<BookingRequest>>()
    val bookings: LiveData<List<BookingRequest>> = _bookings

    private val _filterBooking = MutableLiveData<List<BookingRequest>>()
    val filterBooking: LiveData<List<BookingRequest>> = _filterBooking

    fun selectTab(tab: BookingRequestTab) {
        _currentTab.value = tab
    }

    fun filterBooking() {
        val tab = _currentTab.value ?: BookingRequestTab.NEWEST

        val allBookings = _bookings.value ?: emptyList()
        _filterBooking.value = allBookings

        _filterBooking.value = allBookings.filter { booking ->
            when(tab) {
                BookingRequestTab.NEWEST -> {
                    booking.status == BookingStatus.PENDING.toString()
                }
                BookingRequestTab.COMPLETED -> {
                    booking.status == BookingStatus.COMPLETED.toString()
                }
                BookingRequestTab.UPCOMING -> {
                    booking.status == BookingStatus.CONFIRMED.toString()
                            || booking.status == BookingStatus.IN_PROGRESS.toString()
                }
            }
        }
    }

    fun loadBookings() {
        viewModelScope.launch {
            try {
                val data = bookingRepository.getAllBookingRequest()
                _bookings.value = data
                filterBooking()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
