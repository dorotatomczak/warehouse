package com.dorotatomczak.warehouseapp.data.remote.api

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    object NetworkError : Result<Nothing>()
    data class HttpError(val code: Int, val message: String = "") : Result<Nothing>()
    data class GenericError(val exception: Exception) : Result<Nothing>()
}
