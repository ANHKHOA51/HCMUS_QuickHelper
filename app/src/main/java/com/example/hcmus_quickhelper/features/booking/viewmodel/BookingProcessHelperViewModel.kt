package com.example.hcmus_quickhelper.features.booking.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.database.SupabaseClient
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.core.service.MQService
import com.example.hcmus_quickhelper.core.service.StorageService
import com.example.hcmus_quickhelper.features.booking.model.BookingEvidence
import com.example.hcmus_quickhelper.features.booking.model.toBookingInsert
import com.example.hcmus_quickhelper.features.booking.repository.BookingRepository
import com.example.hcmus_quickhelper.features.payment.model.Payment
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingProcessHelperViewModel(
    private val bookingRepository: BookingRepository
): ViewModel() {
    private val _booking = MutableLiveData<Booking?>()
    val booking: LiveData<Booking?> = _booking

    private val _payment = MutableLiveData<Payment?>()
    val payment: LiveData<Payment?> = _payment


    private val _evidences = MutableLiveData<MutableList<BookingEvidence>>(mutableListOf())
    val evidences: LiveData<MutableList<BookingEvidence>> = _evidences

    fun loadBooking(bookingId: Int) {
        viewModelScope.launch {
            try {
                val data = bookingRepository.getBookingByIdFullData(bookingId)
                _booking.value = data
                val evidencesData = bookingRepository.getEvidences(bookingId)
                _evidences.value = evidencesData.toMutableList()

                MQService.postTask {
                    subscribeToPayment(bookingId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun subscribeToPayment(bookingId: Int) {
        val channel = SupabaseClient.client.channel("payment_tracker_$bookingId")

        val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
            table = "payments"
            filter("booking_id", FilterOperator.EQ, bookingId)
        }

        changeFlow.onEach { action ->
            try {
                val paymentData = bookingRepository.getPayment(bookingId)
                _payment.postValue(paymentData)
            } catch (e: Exception) {
                Log.e("REALTIME", "Error updating booking from realtime: ${e.message}")
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch { channel.subscribe() }
    }

    fun addEvidence(context: Context, uris: List<Uri>) {
        val bookingId = _booking.value?.id ?: -1

        for (uri in uris) {
            MQService.postTask {
                val url = StorageService.uploadImage(uri, context)

                if (url.isNotEmpty()) {
                    val newEvidence = BookingEvidence(
                        bookingId = bookingId,
                        evidenceUrl = url
                    )

                    withContext(Dispatchers.Main) {
                        val currentList = _evidences.value?.toMutableList() ?: mutableListOf()
                        currentList.add(newEvidence)

                        _evidences.value = currentList
                    }
                }
            }
        }
    }

    fun removeEvidence(position: Int) {
        val currentList = _evidences.value?.toMutableList() ?: return

        if (position in currentList.indices) {
            val evidenceToDelete = currentList[position]

            currentList.removeAt(position)
            _evidences.value = currentList

            if (evidenceToDelete.evidenceUrl.isNotEmpty()) {
                MQService.postTask {
                    try {
                        val fileName = evidenceToDelete.evidenceUrl.substringAfterLast("/")
                        StorageService.deleteImage(fileName)
                    } catch (e: Exception) {
                        Log.e("ERROR", "Lỗi khi xóa ảnh trên Storage: ${e.message}")
                    }
                }
            }
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

                if(newStatus == BookingStatus.COMPLETED.toString()) {
                    bookingRepository.createEvidences(_evidences.value?.toList()!!)
                }
                Log.d("DEBUG", "Cập nhật Server thành công")
            } catch (e: Exception) {
                Log.e("DEBUG", "Lỗi cập nhật Server: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
