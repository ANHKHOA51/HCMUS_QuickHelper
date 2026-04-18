package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import kotlinx.coroutines.launch

class BookingRequestDetailViewModel (
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _booking = MutableLiveData<Booking?>()
    val booking: LiveData<Booking?> = _booking

    fun loadBooking(bookingId: Int) {
        viewModelScope.launch {
            val data = bookingRepository.getBookingByIdFullData(bookingId)
            _booking.postValue(data)
        }
    }

    fun acceptBooking() {
        // Logic xử lý chấp nhận (gọi API qua repository)
    }

    fun rejectBooking() {
        // Logic xử lý từ chối (gọi API qua repository)
    }
}
