package com.example.hcmus_quickhelper.features.rating.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.service.MQService
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.rating.model.Rating
import com.example.hcmus_quickhelper.features.rating.repository.RatingRepository
import kotlinx.coroutines.launch

class RatingViewModel(
    private val ratingRepository: RatingRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _rating = MutableLiveData<Rating?>(null)
    val rating: LiveData<Rating?> = _rating

    private val _booking = MutableLiveData<Booking?>(null)
    val booking: LiveData<Booking?> = _booking

    private  val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loaData(bookingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            val bookingData = bookingRepository.getBookingById(bookingId)
            _booking.value = bookingData

            try {
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitRating(point: Int, comment: String) {
        viewModelScope.launch {
            val ratingData = Rating(
                id = null,
                bookingId = _booking.value?.id ?: 1,
                point = point,
                comment = comment,
                createdAt = null
            )

            ratingRepository.insertRating(ratingData)

            // update rating in user
            val listRating = ratingRepository.getRatingByHelperId(_booking.value?.helperId ?: 1)
            var sum = 0
            for (rating in listRating) {
                sum += rating.point
            }
            ratingRepository.updateRatingByHelperId(sum / listRating.size, _booking.value?.helperId ?: 1)
        }
    }

}