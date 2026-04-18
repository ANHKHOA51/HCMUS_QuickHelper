package com.example.hcmus_quickhelper.features.booking.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.features.booking.datasource.BookingDataSource
import com.example.hcmus_quickhelper.features.booking.model.BookingInsert
import com.example.hcmus_quickhelper.features.service_browsing.model.HelperWithServicesDto
import com.example.hcmus_quickhelper.features.service_browsing.model.ServiceDto
import kotlinx.coroutines.launch

class BookingViewModel(private val dataSource: BookingDataSource = BookingDataSource()) : ViewModel() {

    private val _helperData = MutableLiveData<HelperWithServicesDto>()
    val helperData: LiveData<HelperWithServicesDto> get() = _helperData

    var selectedService: ServiceDto? = null
    var quantityHours: Int = 1

    private val _totalPrice = MutableLiveData<Double>(0.0)
    val totalPrice: LiveData<Double> get() = _totalPrice

    fun loadHelperInfo(helperId: Int) {
        viewModelScope.launch {
            try {
                val data = dataSource.getHelperWithServices(helperId)
                _helperData.postValue(data)
                if (data.services.isNotEmpty()) {
                    selectedService = data.services[0] // Mặc định chọn dịch vụ đầu tiên
                    calculateTotal()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun calculateTotal() {
        val price = selectedService?.basePrice ?: 0.0
        _totalPrice.value = price * quantityHours
    }

    fun createBooking(
        customerId: Int, helperId: Int, schedule: String, address: String, note: String, onSuccess: () -> Unit
    ) {
        if (selectedService == null) return

        viewModelScope.launch {
            try {
                val newBooking = BookingInsert(
                    schedule = schedule,
                    address = address,
                    customerId = customerId,
                    helperId = helperId,
                    serviceId = selectedService!!.id,
                    quantity = quantityHours,
                    status = "PENDING",
                    totalPrice = _totalPrice.value ?: 0.0,
                    note = note
                )
                dataSource.insertBooking(newBooking)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}