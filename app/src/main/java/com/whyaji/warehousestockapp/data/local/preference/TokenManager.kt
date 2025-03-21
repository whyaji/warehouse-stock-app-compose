package com.whyaji.warehousestockapp.data.local.preference

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class TokenManager(context: Context) {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_token_prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit() { putString("api_token", token) }
    }

    fun getToken(): String? {
        return sharedPreferences.getString("api_token", null)
    }

    fun clearToken() {
        sharedPreferences.edit() { remove("api_token") }
    }
}