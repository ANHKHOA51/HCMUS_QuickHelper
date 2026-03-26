package com.example.hcmus_quickhelper.features.service_browsing.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.features.service_browsing.model.Helper
import com.example.hcmus_quickhelper.features.service_browsing.repository.ServiceListRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ServiceListViewModel (
    private val repository: ServiceListRepository
) : ViewModel(){
    private val _helpers = MutableLiveData<List<Helper>>()
    val helpers: LiveData<List<Helper>> = _helpers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadHelpers(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = repository.getHelpers()
                _helpers.value = data
            } catch (e: Exception) {
                // handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}