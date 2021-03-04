package com.dorotatomczak.warehouseapp.data.remote.api

import com.dorotatomczak.warehouseapp.BuildConfig
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BasicAuthInterceptor @Inject constructor(
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val credentials: String = Credentials.basic(BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)

        val request = chain.request().newBuilder()
            .addHeader("Authorization", credentials)
            .addHeader("Content-Type", "application/x-www-form-urlencoded;multipart/form-data")
            .addHeader("Accept", "application/json")

        return chain.proceed(request.build())
    }
}
