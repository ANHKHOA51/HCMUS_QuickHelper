package com.example.hcmus_quickhelper.features.booking.repository

import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.model.BookingHistory
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.datasource.BookingLocalDataSource
import com.example.hcmus_quickhelper.features.booking.model.BookingRequest

class BookingRepository (
    private val dataSource: BookingDataSource
) {
    suspend fun getBookingById(id: Int): Booking {
        return dataSource.getById(id)
    }
}
class BookingRepositoryTmp(private val localDataSource: BookingLocalDataSource) {
    suspend fun getBookingHistories(): List<BookingHistory> {
        return localDataSource.getBookingHistories()
    }
}