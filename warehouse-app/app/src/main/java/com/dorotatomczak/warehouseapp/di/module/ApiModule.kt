package com.dorotatomczak.warehouseapp.di.module

import android.content.Context
import com.dorotatomczak.warehouseapp.BuildConfig
import com.dorotatomczak.warehouseapp.data.remote.api.BasicAuthInterceptor
import com.dorotatomczak.warehouseapp.data.remote.api.LocalDateTimeAdapter
import com.dorotatomczak.warehouseapp.data.remote.api.OAuthInterceptor
import com.dorotatomczak.warehouseapp.data.remote.api.TokenAuthenticator
import com.dorotatomczak.warehouseapp.data.remote.api.service.AuthService
import com.dorotatomczak.warehouseapp.data.remote.api.service.ProductsService
import com.dorotatomczak.warehouseapp.data.remote.api.service.WarehousesService
import com.dorotatomczak.warehouseapp.ui.util.AppSettings
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


@Module
@InstallIn(ActivityComponent::class)
object ApiModule {

    @Provides
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build()
        )
    }

    @Provides
    fun provideAuthService(): AuthService {
        return Retrofit.Builder()
            .baseUrl(AuthService.API_URL)
            .client(OkHttpClient.Builder().addInterceptor(BasicAuthInterceptor()).build())
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build()
            .create(AuthService::class.java)
    }

    @Provides
    fun provideTokenAuthenticator(
        authService: AuthService,
        appSettings: AppSettings,
        @ApplicationContext context: Context,
        googleSignInClient: GoogleSignInClient
    ): TokenAuthenticator {
        return TokenAuthenticator(
            authService,
            appSettings,
            context,
            googleSignInClient
        )
    }

    @Provides
    fun provideProductsService(
        appSettings: AppSettings,
        tokenAuthenticator: TokenAuthenticator
    ): ProductsService {
        return Retrofit.Builder()
            .baseUrl(ProductsService.API_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(OAuthInterceptor(appSettings))
                    .authenticator(tokenAuthenticator)
                    .build()
            )
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .add(LocalDateTimeAdapter())
                        .build()
                )
            )
            .build()
            .create(ProductsService::class.java)
    }

    @Provides
    fun provideWarehousesService(
        appSettings: AppSettings,
        tokenAuthenticator: TokenAuthenticator
    ): WarehousesService {
        return Retrofit.Builder()
            .baseUrl(WarehousesService.API_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(OAuthInterceptor(appSettings))
                    .authenticator(tokenAuthenticator)
                    .build()
            )
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
            )
            .build()
            .create(WarehousesService::class.java)
    }
}
