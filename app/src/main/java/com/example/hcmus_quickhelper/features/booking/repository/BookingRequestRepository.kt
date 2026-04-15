package com.example.hcmus_quickhelper.features.booking.repository

import com.example.hcmus_quickhelper.features.booking.datasource.MockBookingRequestDataSource
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest

class BookingRequestRepository (
    private val dataSource: MockBookingRequestDataSource
) {
    fun getAllBookingRequest() : List<BookingRequest>{
        return dataSource.getAll()
    }
}