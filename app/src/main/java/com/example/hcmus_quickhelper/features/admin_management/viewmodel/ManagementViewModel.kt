package com.example.hcmus_quickhelper.features.admin_management.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hcmus_quickhelper.features.admin_management.repository.ManagementRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.core.utils.EmailUtils.sendEmail
import com.example.hcmus_quickhelper.features.community.model.Feed
import kotlinx.coroutines.launch
import kotlin.random.Random

class ManagementViewModel(
    private val repository: ManagementRepository
) : ViewModel() {

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _feeds = MutableLiveData<List<Feed>>()
    val feeds: LiveData<List<Feed>> get() = _feeds
    var isUserTabSelected: Boolean = true

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

    fun toggleBlockUser(userId: Int, isCurrentlyBlocked: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleBlockUser(userId, isCurrentlyBlocked)

            result.onSuccess {
                val currentList = _users.value ?: emptyList()

                val newList = currentList.map { user ->
                    if (user.id == userId) {

                        user.copy(isBLocked = !isCurrentlyBlocked)
                    } else {
                        user
                    }
                }

                _users.value = newList
            }
        }
    }

    fun resetPassword(userId: Int, userEmail: String) {
        val number = Random.nextInt(0, 1_000_000)
        val newPassword = String.format("%06d", number)

        viewModelScope.launch {
            val result = repository.resetPassword(userId, newPassword)

            result.onSuccess {
                sendEmail(
                    userEmail,
                    "Mật khẩu của bạn vừa được reset",
                    "Gửi ${userEmail},\n Chúng tôi vừa đặt lại mật khẩu mới cho bạn\n Mật khẩu mới là ${newPassword}, vui lòng đăng nhập và thay đổi mật khẩu sớm nhất\n Trân trong,\nĐội ngũ quản trị viên QuickHelper",
                )
            }

        }
    }

    fun warningUser(userEmail: String) {
        viewModelScope.launch {
            sendEmail(
                userEmail,
                "Cảnh cáo hành vì không phù hợp",
                "Gửi ${userEmail},\n Chúng tôi nhận được hành vi không phù hợp của bạn trên ứng dụng QuickHelper, nếu còn tái phạm thì tài khoản của bạn sẽ bị khóa\n Trân trong,\nĐội ngũ quản trị viên QuickHelper",
            )
        }
    }

    fun deleteFeed(feedId: Int) {
        viewModelScope.launch {
            val result = repository.deleteFeed(feedId)

            result.onSuccess {
                val currentList = _feeds.value ?: emptyList()
                val newList = currentList.filterNot { it.id == feedId }
                _feeds.value = newList
            }
        }
    }

}