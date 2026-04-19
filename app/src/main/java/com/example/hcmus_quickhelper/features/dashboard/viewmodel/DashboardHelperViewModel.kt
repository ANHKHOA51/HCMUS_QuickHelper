package com.example.hcmus_quickhelper.features.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.Booking
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.features.dashboard.model.DashboardHelper
import com.example.hcmus_quickhelper.features.dashboard.repository.DashboardHelperRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardHelperViewModel (
    private val dashboardHelperRepository: DashboardHelperRepository
) : ViewModel() {

    private val _dataHelper = MutableLiveData<DashboardHelper>()
    val dataHelper: LiveData<DashboardHelper> = _dataHelper
    
    private val _filterBooking = MutableLiveData<List<Booking>>()
    val filterBooking: LiveData<List<Booking>> = _filterBooking

    private val _totalIncome = MutableLiveData<Double>(0.0)
    val totalIncome: LiveData<Double> = _totalIncome

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    fun loadData(helperId: Int) {
        viewModelScope.launch {
            try {
                val data = dashboardHelperRepository.getDashboardClientData(helperId)
                _dataHelper.value = data
                filter("Tất cả")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun filter(duration: String) {
        val allBookings = _dataHelper.value?.bookings ?: return

        val calendar = Calendar.getInstance()
        val now = calendar.time

        val filtered = allBookings.filter { booking ->
            val bookingDate = parseDate(booking.schedule) ?: return@filter false

            when (duration) {
                "Tuần", "Tuần này" -> isSameWeek(bookingDate, now)
                "Tháng", "Tháng này" -> isSameMonth(bookingDate, now)
                else -> true // Cho trường hợp "Tất cả"
            }
        }

        _filterBooking.value = filtered

        val income = filtered.sumOf { booking ->
            if (booking.status == BookingStatus.COMPLETED.name) booking.totalPrice else 0.0
        }

        _totalIncome.value = income
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isSameWeek(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR)
    }

    private fun isSameMonth(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }
}
