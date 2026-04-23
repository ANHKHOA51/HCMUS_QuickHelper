package com.example.hcmus_quickhelper.features.booking.repository

import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.model.BookingConversation
import com.example.hcmus_quickhelper.features.booking.model.BookingEvidence
import com.example.hcmus_quickhelper.features.booking.model.BookingInsert
import com.example.hcmus_quickhelper.features.payment.model.Payment

class BookingRepository (
    private val dataSource: BookingDataSource
) {
    suspend fun getBookingById(id: Int): Booking {
        return dataSource.getById(id)
    }

    suspend fun getBookingByIdFullData(id: Int): Booking {
        return dataSource.getByIdFullData(id)
    }

    suspend fun getAllBooking(): List<Booking> {
        return dataSource.getAll()
    }

    suspend fun getAllBookingFullData(): List<Booking> {
        return dataSource.getAllFullData()
    }

    suspend fun getBookingsByHelperId(helperId: Int): List<Booking> {
        return dataSource.getAllByHelperIdFullData(helperId)
    }

    suspend fun updateBooking(id: Int, booking: BookingInsert) {
        dataSource.update(id, booking)
    }

    suspend fun getEvidences(bookingId: Int): List<BookingEvidence> {
        return dataSource.getEvidences(bookingId)
    }

    suspend fun createEvidences(evidences: List<BookingEvidence>) {
        dataSource.createEvidences(evidences)
    }

    suspend fun deleteEvidence(evidence: BookingEvidence) {
        dataSource.deleteEvidence(evidence)
    }


    suspend fun getPayment(bookingId: Int): Payment? {
        return dataSource.getPayment(bookingId)
    }

    suspend fun getConversationByBookingId(bookingId: Int): BookingConversation? {
        return dataSource.getConversationByBookingId(bookingId)
    }

    suspend fun getBookingsByCustomerId(customerId: Int): List<Booking> {
        return dataSource.getBookingsByCustomerIdFullData(customerId)
    }
}