package com.dorotatomczak.warehouseapp.data.remote.api.service

import com.dorotatomczak.warehouseapp.data.model.GoogleSignInRequest
import com.dorotatomczak.warehouseapp.data.model.OAuth
import com.dorotatomczak.warehouseapp.data.model.RegisterRequest
import com.dorotatomczak.warehouseapp.data.model.TokenInfo
import retrofit2.Call
import retrofit2.http.*

interface AuthService {

    companion object {
        const val API_URL = "http://10.0.2.2:9091/"
    }

    @FormUrlEncoded
    @POST("/oauth/token")
    suspend fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String = "password"
    ): OAuth

    @POST("/register")
    suspend fun register(@Body registerRequest: RegisterRequest)

    @GET("/oauth/check_token")
    suspend fun checkToken(@Query("token") token: String): TokenInfo

    @FormUrlEncoded
    @POST("/oauth/token")
    fun refreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grant_type: String = "refresh_token"
    ): Call<OAuth>

    @POST("/google/verify")
    suspend fun verifyGoogleToken(@Body googleSignInRequest: GoogleSignInRequest): OAuth
}
