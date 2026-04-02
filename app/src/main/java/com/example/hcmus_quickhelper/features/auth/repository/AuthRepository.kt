package com.example.hcmus_quickhelper.features.auth.repository

import com.example.hcmus_quickhelper.features.auth.datasource.AuthRemoteDataSource

class AuthRepository(private val dataSource: AuthRemoteDataSource) {
    suspend fun login(email: String, pass: String): Result<Unit> {
        return try {
            dataSource.loginWithEmail(email, pass)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String, fullname: String, phone: String): Result<Unit> {
        return try {
            dataSource.registerWithEmail(email, pass, fullname, phone)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
