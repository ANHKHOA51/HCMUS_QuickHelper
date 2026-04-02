package com.example.hcmus_quickhelper.features.rating.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.features.rating.model.Rating
import com.example.hcmus_quickhelper.features.rating.repository.RatingRepository

class RatingViewModel(
    private val ratingRepository: RatingRepository
) : ViewModel() {

    private val _rating = MutableLiveData<Rating?>(null)
    val rating: LiveData<Rating?> = _rating

    private  val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading



}