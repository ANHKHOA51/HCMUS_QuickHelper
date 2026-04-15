package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest
import com.example.hcmus_quickhelper.features.booking.repository.BookingRequestRepository
import kotlinx.coroutines.launch

class BookingRequestDetail (
    private val bookingRequestRepository: BookingRequestRepository
) : ViewModel() {
    private val _booking = MutableLiveData<List<BookingRequest>>()
    val booking: LiveData<List<BookingRequest>> = _booking

    fun loadBooking(bookingId: Int) {
        viewModelScope.launch {

        }
    }

    fun acceptBooking() {

    }

    fun rejectBooking() {

    }
}