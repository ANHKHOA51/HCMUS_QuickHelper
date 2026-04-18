package com.example.hcmus_quickhelper.features.profile.repository

import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.profile.datasource.ProfileRemoteDataSource

class ProfileRepository(private val remoteDataSource: ProfileRemoteDataSource) {
    suspend fun updateUserInfo(user: User): User {
        val updatedUser = remoteDataSource.updateProfile(user)
        // Update the global session immediately upon success
        SessionManager.login(updatedUser)
        return updatedUser
    }
}
