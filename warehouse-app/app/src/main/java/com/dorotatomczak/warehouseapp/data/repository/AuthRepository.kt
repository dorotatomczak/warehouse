package com.dorotatomczak.warehouseapp.data.repository

import com.dorotatomczak.warehouseapp.data.model.GoogleSignInRequest
import com.dorotatomczak.warehouseapp.data.model.OAuth
import com.dorotatomczak.warehouseapp.data.model.RegisterRequest
import com.dorotatomczak.warehouseapp.data.model.TokenInfo
import com.dorotatomczak.warehouseapp.data.remote.api.ApiCaller.safeApiCall
import com.dorotatomczak.warehouseapp.data.remote.api.Result
import com.dorotatomczak.warehouseapp.data.remote.api.service.AuthService
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


class AuthRepository @Inject constructor(private val authService: AuthService) {

    suspend fun signIn(username: String, password: String): Result<OAuth> =
        safeApiCall(Dispatchers.IO) {
            authService.authenticate(username, password)
        }

    suspend fun signUp(username: String, password: String): Result<Unit> {
        val registerRequest = RegisterRequest(username, password)
        return safeApiCall(Dispatchers.IO) {
            authService.register(registerRequest)
        }
    }

    suspend fun checkToken(token: String): Result<TokenInfo> =
        safeApiCall(Dispatchers.IO) {
            authService.checkToken(token)
        }

    suspend fun verifyGoogleToken(token: String): Result<OAuth> =
        safeApiCall(Dispatchers.IO) {
            authService.verifyGoogleToken(GoogleSignInRequest(token))
        }

}
