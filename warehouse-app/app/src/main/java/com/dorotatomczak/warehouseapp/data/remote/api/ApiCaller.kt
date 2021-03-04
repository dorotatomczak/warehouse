package com.dorotatomczak.warehouseapp.data.remote.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object ApiCaller {
    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): Result<T> {
        return withContext(dispatcher) {
            try {
                Result.Success(apiCall.invoke())
            } catch (exception: Exception) {
                when (exception) {
                    is IOException -> Result.NetworkError
                    is HttpException -> Result.HttpError(
                        exception.code(),
                        parseHttpErrorMessage(exception)
                    )
                    else -> Result.GenericError(exception)
                }
            }
        }
    }

    private fun parseHttpErrorMessage(exception: HttpException): String {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(ErrorBody::class.java)

        val json = exception.response()?.errorBody()?.string()

        return if (json != null) {
            val errorBody = jsonAdapter.fromJson(json)
            errorBody?.message ?: ""
        } else ""
    }

    data class ErrorBody(
        val timestamp: String,
        val status: String,
        val error: String,
        val message: String,
        val path: String
    )
}
