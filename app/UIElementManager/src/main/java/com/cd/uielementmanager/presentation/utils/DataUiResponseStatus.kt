package com.cd.uielementmanager.presentation.utils

  sealed class DataUiResponseStatus<out T> {
    class None<T> : DataUiResponseStatus<T>()

    class Loading<T> : DataUiResponseStatus<T>()

    data class Success<out T>(val data: T) : DataUiResponseStatus<T>()

    data class Failure(val errorMessage: String, val errorCode: Int) :
        DataUiResponseStatus<Nothing>()

    companion object {

        fun none(): DataUiResponseStatus<Nothing> = None()

        fun loading(): DataUiResponseStatus<Nothing> = Loading()

        fun <T> success(data: T): DataUiResponseStatus<T> = Success(data)

        fun failure(errorMessage: String, errorCode: Int): DataUiResponseStatus<Nothing> =
            Failure(errorMessage, errorCode)
    }
}