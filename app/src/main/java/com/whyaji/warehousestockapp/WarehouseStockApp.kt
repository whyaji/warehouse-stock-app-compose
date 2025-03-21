package com.whyaji.warehousestockapp

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.whyaji.warehousestockapp.data.api.ApiService
import com.whyaji.warehousestockapp.data.domain.repository.Repository
import com.whyaji.warehousestockapp.data.interceptor.AuthInterceptor
import com.whyaji.warehousestockapp.data.local.database.AppDatabase
import com.whyaji.warehousestockapp.data.local.preference.TokenManager
import com.whyaji.warehousestockapp.ui.navigation.Navigation
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.whyaji.warehousestockapp.ui.theme.WarehouseStockAppTheme

@Composable
fun WarehouseStockApp(context: Context) {
    val tokenManager = TokenManager(context)
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenManager))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()
    val apiService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .build()
        .create(ApiService::class.java)
    val database = AppDatabase.getDatabase(context)
    val repository = Repository(apiService, database.itemDao())
    val viewModel = MainViewModel(repository, tokenManager)

    WarehouseStockAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Navigation(viewModel, isTokenEmpty = (tokenManager.getToken() == ""))
        }
    }
}