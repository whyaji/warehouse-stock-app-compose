package com.whyaji.warehousestockapp.data.local.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.OutputStream

private const val TOKEN_DATASTORE = "secure_token_prefs.pb"
private const val KEYSET_NAME = "token_keyset"
private const val PREFERENCE_FILE = "token_pref"
private const val MASTER_KEY_URI = "android-keystore://token_master_key"

// Helper
object EncryptionHelper {
    fun getAead(context: Context): Aead {
        AeadConfig.register()
        val keysetHandle: KeysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREFERENCE_FILE)
            .withKeyTemplate(com.google.crypto.tink.KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
        return keysetHandle.getPrimitive(Aead::class.java)
    }
}

// DataStore Serializer with encryption
object TokenSerializer : Serializer<String> {
    private lateinit var aead: Aead

    fun initialize(context: Context) {
        aead = EncryptionHelper.getAead(context)
    }

    override val defaultValue: String = ""

    override suspend fun readFrom(input: InputStream): String {
        return try {
            val encryptedData = input.readBytes()
            String(aead.decrypt(encryptedData, null))
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun writeTo(t: String, output: OutputStream) {
        val encryptedData = aead.encrypt(t.toByteArray(), null)
        output.write(encryptedData)
    }
}

// Extension property for DataStore
private val Context.tokenDataStore: DataStore<String> by dataStore(
    fileName = TOKEN_DATASTORE,
    serializer = TokenSerializer
)

class TokenManager(private val context: Context) {

    init {
        TokenSerializer.initialize(context)
    }

    fun saveToken(token: String) {
        runBlocking { context.tokenDataStore.updateData { token } }
    }

    fun getToken(): String {
        return runBlocking { context.tokenDataStore.data.first() }
    }

    fun clearToken() {
        runBlocking { context.tokenDataStore.updateData { "" } }
    }
}
