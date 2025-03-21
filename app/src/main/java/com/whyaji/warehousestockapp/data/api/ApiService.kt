package com.whyaji.warehousestockapp.data.api

import com.whyaji.warehousestockapp.model.ItemsResponse
import com.whyaji.warehousestockapp.model.LoginRequest
import com.whyaji.warehousestockapp.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("list-items")
    suspend fun getItems(): Response<ItemsResponse>
}