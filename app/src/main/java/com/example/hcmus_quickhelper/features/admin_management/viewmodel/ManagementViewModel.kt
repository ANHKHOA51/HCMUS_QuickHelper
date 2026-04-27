package com.example.hcmus_quickhelper.features.admin_management.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.features.admin_management.repository.ManagementRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.community.model.Feed
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ManagementViewModel(
    private val repository: ManagementRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _feeds = MutableLiveData<List<Feed>>()
    val feeds: LiveData<List<Feed>> get() = _feeds

    fun getUsers() {
        viewModelScope.launch {
            val result = repository.getUsers()

            result.onSuccess {
                _users.postValue(it)
            }
        }
    }

    fun getFeeds() {
        viewModelScope.launch {
            val result = repository.getFeeds()

            result.onSuccess {
                _feeds.postValue(it)
            }
        }
    }
}