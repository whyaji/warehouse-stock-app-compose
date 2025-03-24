package com.whyaji.warehousestockapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.whyaji.warehousestockapp.data.api.ApiService
import com.whyaji.warehousestockapp.data.domain.repository.AuthRepository
import com.whyaji.warehousestockapp.data.domain.repository.CartRepository
import com.whyaji.warehousestockapp.data.domain.repository.ItemRepository
import com.whyaji.warehousestockapp.data.interceptor.AuthInterceptor
import com.whyaji.warehousestockapp.data.local.database.AppDatabase
import com.whyaji.warehousestockapp.data.local.preference.TokenManager
import com.whyaji.warehousestockapp.ui.navigation.Navigation
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.whyaji.warehousestockapp.ui.theme.WarehouseStockAppTheme
import com.whyaji.warehousestockapp.viewmodel.MainViewModel

@Composable
fun WarehouseStockApp() {
    val context = LocalContext.current
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

    val authRepository = AuthRepository(apiService, database.userDao())
    val itemRepository = ItemRepository(apiService, database.itemDao())
    val cartRepository = CartRepository(database.cartDao())
    val mainViewModel = MainViewModel(authRepository, itemRepository, cartRepository, tokenManager)

    WarehouseStockAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Navigation(mainViewModel, isAuthenticated = tokenManager.getToken().isNotEmpty())
        }
    }
}