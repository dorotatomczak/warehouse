package com.dorotatomczak.warehouseapp.ui.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppSettings @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        const val PREF_FILE_NAME = "PREF_APP_SETTINGS"
        private const val PREF_ACCESS_TOKEN = "PREF_ACCESS_TOKEN"
        private const val PREF_REFRESH_TOKEN = "PREF_REFRESH_TOKEN"
        private const val PREF_DELETE_PERMISSION = "PREF_DELETE_PERMISSION"
        private const val PREF_SIGNED_IN_WITH_GOOGLE = "PREF_SIGNED_IN_WITH_GOOGLE"
        private const val PREF_IN_ONLINE_MODE = "PREF_IN_ONLINE_MODE"
        private const val PREF_CURRENT_WAREHOUSE_ID = "PREF_CURRENT_WAREHOUSE_ID"
    }

    private var sharedPreferences: SharedPreferences

    var accessToken: String?
        get() = sharedPreferences.getString(PREF_ACCESS_TOKEN, null)
        set(value) = sharedPreferences.edit().putString(PREF_ACCESS_TOKEN, value).apply()

    var refreshToken: String?
        get() = sharedPreferences.getString(PREF_REFRESH_TOKEN, null)
        set(value) = sharedPreferences.edit().putString(PREF_REFRESH_TOKEN, value).apply()

    var hasDeletePermission: Boolean
        get() = sharedPreferences.getBoolean(PREF_DELETE_PERMISSION, false)
        set(value) = sharedPreferences.edit().putBoolean(PREF_DELETE_PERMISSION, value).apply()

    var signedInWithGoogle: Boolean
        get() = sharedPreferences.getBoolean(PREF_SIGNED_IN_WITH_GOOGLE, false)
        set(value) = sharedPreferences.edit().putBoolean(PREF_SIGNED_IN_WITH_GOOGLE, value).apply()

    val signedIn: Boolean
        get() = accessToken != null && refreshToken != null

    var inOnlineMode: Boolean
        get() = sharedPreferences.getBoolean(PREF_IN_ONLINE_MODE, false)
        set(value) = sharedPreferences.edit().putBoolean(PREF_IN_ONLINE_MODE, value).apply()

    var currentWarehouseId: Int
        get() = sharedPreferences.getInt(PREF_CURRENT_WAREHOUSE_ID, 1)
        set(value) = sharedPreferences.edit().putInt(PREF_CURRENT_WAREHOUSE_ID, value).apply()

    fun removeAll() {
        accessToken = null
        refreshToken = null
        hasDeletePermission = false
        signedInWithGoogle = false
    }

    init {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREF_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
