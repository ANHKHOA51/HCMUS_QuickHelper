package com.example.hcmus_quickhelper.features.booking.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.features.booking.model.toBookingInsert
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

    fun acceptBooking(helperId: Int) {
        val currentBooking = _booking.value ?: return
        val updatedBooking = currentBooking.copy(
            status = BookingStatus.CONFIRMED.toString(),
            helperId = helperId
        )
        _booking.value = updatedBooking

        _booking.value?.let { booking ->
            viewModelScope.launch {
                try {
                    bookingRepository.updateBooking(booking.id, booking.toBookingInsert())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun rejectBooking(helperId: Int) {
        val currentBooking = _booking.value ?: return
        val updatedBooking = currentBooking.copy(
            status = BookingStatus.REJECTED.toString(),
            helperId = helperId
        )
        _booking.value = updatedBooking

        _booking.value?.let { booking ->
            viewModelScope.launch {
                bookingRepository.updateBooking(booking.id, booking.toBookingInsert())
            }
        }
    }
}
