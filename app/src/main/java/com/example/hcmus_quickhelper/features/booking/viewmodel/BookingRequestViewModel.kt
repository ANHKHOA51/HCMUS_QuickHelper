package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import kotlinx.coroutines.launch

enum class BookingRequestTab {
    NEWEST, UPCOMING, COMPLETED
}

class BookingRequestViewModel (
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _currentTab = MutableLiveData<BookingRequestTab>(BookingRequestTab.NEWEST)
    val currentTab: LiveData<BookingRequestTab> = _currentTab

    private val _bookings = MutableLiveData<List<Booking>>()
    val bookings: LiveData<List<Booking>> = _bookings

    private val _filterBooking = MutableLiveData<List<Booking>>()
    val filterBooking: LiveData<List<Booking>> = _filterBooking

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
                            || booking.status == BookingStatus.REJECTED.toString()
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

    fun loadBookings(helperId: Int) {
        viewModelScope.launch {
            try {
                val data = bookingRepository.getBookingsByHelperId(helperId)
                _bookings.value = data
                filterBooking()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
