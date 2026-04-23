package com.example.hcmus_quickhelper.features.auth.repository

import com.example.hcmus_quickhelper.core.auth.SessionManager
import com.example.hcmus_quickhelper.core.model.User
import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource

class AuthRepository(private val dataSource: AuthRemoteDataSource) {
    suspend fun login(email: String, pass: String, fcmToken: String?): Result<User> {
        return try {
            val user = dataSource.loginWithEmail(email, pass)
            fcmToken?.let { dataSource.saveFcmToken(user.id, it) } 
            SessionManager.login(user) 
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOldPassword(oldPass: String): Result<Unit> {
        return try {
            dataSource.verifyPassword(oldPass)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String, fcmToken: String?): Result<User> {
        return try {
            val user = dataSource.loginWithGoogle(idToken)
            fcmToken?.let { dataSource.saveFcmToken(user.id, it) }
            SessionManager.login(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String, fullname: String, phone: String, username: String? = null, role: String = "CUSTOMER"): Result<Unit> {
        return try {
            dataSource.registerWithEmail(email, pass, fullname, phone, username, role)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePassword(userId: Int, newPass: String): Result<Unit> {
        return try {
            dataSource.updatePassword(userId, newPass)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePasswordByEmail(email: String, newPass: String): Result<Unit> {
        return try {
            dataSource.updatePasswordByEmail(email, newPass)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendOtp(email: String): Result<Unit> {
        return try {
            dataSource.sendOtp(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyEmailOtp(email: String, token: String): Result<Unit> {
        return try {
            dataSource.verifyOtp(email, token)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
