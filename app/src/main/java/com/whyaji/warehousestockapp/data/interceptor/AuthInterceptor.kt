package com.whyaji.warehousestockapp.data.interceptor

import com.whyaji.warehousestockapp.data.local.preference.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().apply {
            addHeader("Authorization", "Bearer ${tokenManager.getToken()}")
        }.build()
        return chain.proceed(request)
    }
}