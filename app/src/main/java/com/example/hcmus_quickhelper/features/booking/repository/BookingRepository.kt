package com.example.hcmus_quickhelper.features.booking.repository

import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource

class BookingRepository (
    private val dataSource: BookingDataSource
) {
    suspend fun getBookingById(id: Int): Booking {
        return dataSource.getById(id)
    }
}