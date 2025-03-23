package com.whyaji.warehousestockapp.data.domain.repository

import com.whyaji.warehousestockapp.data.api.ApiService
import com.whyaji.warehousestockapp.data.local.dao.UserDao
import com.whyaji.warehousestockapp.model.LoginRequest
import com.whyaji.warehousestockapp.model.LoginResponse
import com.whyaji.warehousestockapp.model.UserData
import retrofit2.Response

class AuthRepository(
    private val apiService: ApiService,
    private val userDao: UserDao
) {
    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return apiService.login(request)
    }

    suspend fun setUser(user: UserData) {
        userDao.insert(user)
    }

    suspend fun getUser(): UserData? {
        return userDao.getUser()
    }

    suspend fun deleteUser() {
        userDao.deleteUser()
    }
}