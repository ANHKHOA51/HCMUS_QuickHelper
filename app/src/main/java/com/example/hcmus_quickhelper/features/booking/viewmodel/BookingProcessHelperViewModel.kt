package com.example.hcmus_quickhelper.features.booking.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest
import com.example.hcmus_quickhelper.features.booking.repository.BookingRequestRepository
import kotlinx.coroutines.launch

class BookingProcessHelperViewModel(
    private val bookingRequestRepository: BookingRequestRepository
): ViewModel() {
    private val _booking = MutableLiveData<BookingRequest?>()
    val booking: LiveData<BookingRequest?> = _booking

    private val _imageEvidence = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val imageEvidence: LiveData<MutableList<Uri>> = _imageEvidence

    fun loadBooking(bookingId: Int) {
        viewModelScope.launch {
            try {
                val allBookings = bookingRequestRepository.getAllBookingRequest()
                val foundBooking = allBookings.find { it.id == bookingId }
                _booking.postValue(foundBooking)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addEvidence(uris: List<Uri>) {
        val currentList = _imageEvidence.value ?: mutableListOf()
        currentList.addAll(uris)
        _imageEvidence.value = currentList
    }

    fun removeEvidence(position: Int) {
        val currentList = _imageEvidence.value ?: mutableListOf()
        if (position in currentList.indices) {
            currentList.removeAt(position)
            _imageEvidence.value = currentList
        }
    }
}
