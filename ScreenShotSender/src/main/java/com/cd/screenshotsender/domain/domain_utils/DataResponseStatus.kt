package com.cd.screenshotsender.domain.domain_utils


internal sealed class DataResponseStatus<out T> {
    data class Success<out T>(val data: T) : DataResponseStatus<T>()

    data class Failure(val errorMessage: String, val errorCode: Int) : DataResponseStatus<Nothing>()

    companion object {
        fun <T> success(data: T): DataResponseStatus<T> = Success(data)

        fun failure(errorMessage: String, errorCode: Int): DataResponseStatus<Nothing> =
            Failure(errorMessage, errorCode)
    }
}