package com.example.hcmus_quickhelper.core.auth

import android.content.Context
import com.example.hcmus_quickhelper.core.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object SessionManager {
    private var userPreferences: UserPreferences? = null
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun init(context: Context) {
        userPreferences = UserPreferences(context)
        scope.launch {
            userPreferences?.userFlow?.collect { user ->
                _currentUser.value = user
            }
        }
    }

    val isLoggedIn: Flow<Boolean>
        get() = userPreferences?.isLoggedIn ?: kotlinx.coroutines.flow.flowOf(false)

    fun isSessionActive(): Boolean = runBlocking {
        userPreferences?.isLoggedIn?.firstOrNull() ?: false
    }

    suspend fun login(user: User) = withContext(Dispatchers.IO) {
        userPreferences?.saveSession(user)
        _currentUser.value = user
    }

    suspend fun updateCurrentUser(user: User) = withContext(Dispatchers.IO) {
        userPreferences?.saveSession(user)
        _currentUser.value = user
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userPreferences?.clearSession()
        _currentUser.value = null
    }
}
