package com.example.hcmus_quickhelper.features.booking.repository

import com.example.hcmus_quickhelper.features.booking.datasource.BookingLocalDataSource
import com.example.hcmus_quickhelper.features.booking.model.BookingHistory

class BookingRepository(private val localDataSource: BookingLocalDataSource) {
    suspend fun getBookingHistories(): List<BookingHistory> {
        return localDataSource.getBookingHistories()
    }
}