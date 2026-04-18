package com.example.hcmus_quickhelper.features.booking.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.model.BookingInsert
import com.example.hcmus_quickhelper.features.booking.model.toBookingInsert
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlin.String

class BookingProcessHelperViewModel(
    private val bookingRepository: BookingRepository
): ViewModel() {
    private val _booking = MutableLiveData<Booking?>()
    val booking: LiveData<Booking?> = _booking

    private val _imageEvidence = MutableLiveData<MutableList<Uri>>(mutableListOf())
    val imageEvidence: LiveData<MutableList<Uri>> = _imageEvidence

    fun loadBooking(bookingId: Int) {
        viewModelScope.launch {
            try {
                val data = bookingRepository.getBookingByIdFullData(bookingId)
                _booking.value = data
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

    fun updateBookingStatus(newStatus: String) {
        Log.d("DEBUG", "Trạng thái mới: $newStatus")

        val currentBooking = _booking.value ?: return
        val updatedBooking = currentBooking.copy(status = newStatus)
        _booking.value = updatedBooking

        viewModelScope.launch {
            try {
                bookingRepository.updateBooking(
                    updatedBooking.id,
                    updatedBooking.toBookingInsert()
                )
                Log.d("DEBUG", "Cập nhật Server thành công")
            } catch (e: Exception) {
                Log.e("DEBUG", "Lỗi cập nhật Server: ${e.message}")
                e.printStackTrace()
                // Tùy chọn: Hoàn tác (rollback) UI nếu lưu thất bại
                // _booking.value = currentBooking
            }
        }
    }
}
