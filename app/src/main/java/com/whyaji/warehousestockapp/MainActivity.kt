package com.whyaji.warehousestockapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.whyaji.warehousestockapp.data.api.ApiService
import com.whyaji.warehousestockapp.data.domain.repository.Repository
import com.whyaji.warehousestockapp.data.interceptor.AuthInterceptor
import com.whyaji.warehousestockapp.data.local.database.AppDatabase
import com.whyaji.warehousestockapp.data.local.preference.TokenManager
import com.whyaji.warehousestockapp.ui.navigation.Navigation
import com.whyaji.warehousestockapp.ui.theme.WarehouseStockAppTheme
import com.whyaji.warehousestockapp.viewmodel.MainViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
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
        val database = AppDatabase.getDatabase(this)
        val repository = Repository(apiService, database.itemDao())
        val viewModel = MainViewModel(repository, tokenManager)

        enableEdgeToEdge()
        setContent {
            WarehouseStockAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    Navigation(viewModel)
                }
            }
        }
    }
}