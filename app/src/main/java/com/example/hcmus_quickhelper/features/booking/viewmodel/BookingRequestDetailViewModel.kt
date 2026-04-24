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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

class BookingRequestDetailViewModel (
    private val bookingRepository: BookingRepository
) : ViewModel() {
    private val _booking = MutableLiveData<Booking?>()
    val booking: LiveData<Booking?> = _booking

    private val _conversationId = MutableLiveData<Int?>()
    val conversationId: LiveData<Int?> get() = _conversationId

    fun loadBooking(bookingId: Int) {
        viewModelScope.launch {
            val data = bookingRepository.getBookingByIdFullData(bookingId)
            _booking.postValue(data)

            val conv = bookingRepository.getConversationByBookingId(bookingId)
            _conversationId.value = conv?.id
        }
    }

    fun updateBookingStatus(helperId: Int, newStatus: BookingStatus) {
        val oldBooking = _booking.value ?: return

        val updatedBooking = oldBooking.copy(
            status = newStatus.toString(),
            helperId = helperId
        )
        _booking.value = updatedBooking

        viewModelScope.launch {
            try {
                withContext(NonCancellable) {
                    bookingRepository.updateBooking(updatedBooking.id, updatedBooking.toBookingInsert())
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    _booking.value = oldBooking
                }
            }
        }
    }

    fun acceptBooking(helperId: Int) {
        updateBookingStatus(helperId, BookingStatus.CONFIRMED)
    }

    fun rejectBooking(helperId: Int) {
        updateBookingStatus(helperId, BookingStatus.REJECTED)
    }
}
