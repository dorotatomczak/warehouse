package com.dorotatomczak.warehouseapp.data.remote.api

import android.content.Context
import android.content.Intent
import com.dorotatomczak.warehouseapp.data.remote.api.service.AuthService
import com.dorotatomczak.warehouseapp.ui.signin.SignInActivity
import com.dorotatomczak.warehouseapp.ui.util.AppSettings
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val authService: AuthService,
    private val appSettings: AppSettings,
    private val context: Context,
    private val googleSignInClient: GoogleSignInClient
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        if (appSettings.refreshToken == null) {
            logoutUser()
            return null
        }

        val auth = authService.refreshToken(appSettings.refreshToken!!).execute().body()

        auth?.let {
            appSettings.refreshToken = it.refreshToken
            appSettings.accessToken = it.accessToken

            return response.request.newBuilder()
                .header("Authorization", "Bearer ${it.accessToken}")
                .build()
        }

        logoutUser()
        return null
    }

    private fun logoutUser() {
        if (appSettings.signedInWithGoogle) {
            googleSignInClient.signOut()
        }
        appSettings.removeAll()

        val intent = Intent(context, SignInActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(intent)
    }
}
