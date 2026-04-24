package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.model.BookingEvidence
import com.example.hcmus_quickhelper.features.payment.datasource.PaymentDataSource
import com.example.hcmus_quickhelper.features.payment.model.PaymentStatus
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class BookingTrackingViewModel : ViewModel() {
    private val dataSource = BookingDataSource()
    private val paymentDataSource = PaymentDataSource()

    private val _booking = MutableLiveData<Booking>()
    val booking: LiveData<Booking> get() = _booking

    private val _evidences = MutableLiveData<List<BookingEvidence>>()
    val evidences: LiveData<List<BookingEvidence>> get() = _evidences

    private val _conversationId = MutableLiveData<Int?>()
    val conversationId: LiveData<Int?> get() = _conversationId
    private val _isPaid = MutableLiveData<Boolean>()
    val isPaid: LiveData<Boolean> get() = _isPaid

    fun loadData(bookingId: Int) {
        viewModelScope.launch {
            try {

                val payment = paymentDataSource.getByBookingId(bookingId)
                _isPaid.value = (payment != null && payment.status == PaymentStatus.SUCCESS.toString())

                // Lấy thông tin booking (full data có helper/service)
                val currentBooking = dataSource.getByIdFullData(bookingId)
                _booking.value = currentBooking

                // Lấy conversation để chat
                val conv = dataSource.getConversationByBookingId(bookingId)
                _conversationId.value = conv?.id

                // Nếu không phải pending/rejected thì load evidences
                if (currentBooking.status != "PENDING" && currentBooking.status != "REJECTED") {
                    _evidences.value = dataSource.getEvidences(bookingId)
                }

                subscribeToBookingChanges(bookingId)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // nghe status
    private fun subscribeToBookingChanges(bookingId: Int) {
        val channel = SupabaseClient.client.channel("booking_tracker_$bookingId")

        // Lắng nghe sự kiện Update trên bảng bookings của đúng bookingId này
        val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "bookings"
            filter("id", FilterOperator.EQ, bookingId)
        }

        changeFlow.onEach { action ->
            val updatedBooking = dataSource.getByIdFullData(bookingId)
            _booking.postValue(updatedBooking)

            if (updatedBooking.status in listOf("IN_PROGRESS", "CONFIRMED", "COMPLETED")) {
                _evidences.postValue(dataSource.getEvidences(bookingId))
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch { channel.subscribe() }
    }

    fun deleteConversationIfRejected(bookingId: Int) {
        viewModelScope.launch {
            try {
                dataSource.deleteConversationByBookingId(bookingId)
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}