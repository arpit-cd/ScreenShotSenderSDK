package com.cd.screenshotsender.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.cd.screenshotsender.domain.domain_utils.AppErrorCodes
import com.cd.screenshotsender.domain.domain_utils.DataResponseStatus
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

internal object NetworkCallHelper {

    suspend inline fun <reified NetworkReturn> networkCall(
        crossinline api: suspend () -> Response<NetworkReturn>
    ): DataResponseStatus<NetworkReturn> {
        return try {
            val response = api()
            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                DataResponseStatus.success(responseBody)
            } else {
                val errorDetails = getErrorDetails(response)
                DataResponseStatus.failure(
                    errorMessage = errorDetails.errorMessage,
                    errorCode = errorDetails.errorCode
                )
            }
        } catch (exception: Exception) {
            val networkErrorDetails = parseError(exception)
            DataResponseStatus.failure(
                errorMessage = networkErrorDetails.errorMessage,
                errorCode = networkErrorDetails.errorCode
            )
        }
    }

    // Specific function for upload operations returning Unit
    suspend fun networkCallForUpload(
        api: suspend () -> Response<ResponseBody>
    ): DataResponseStatus<Unit> {
        return try {
            val response = api()
            if (response.isSuccessful) {
                DataResponseStatus.success(Unit)
            } else {
                val errorDetails = getErrorDetails(response)
                DataResponseStatus.failure(
                    errorMessage = errorDetails.errorMessage,
                    errorCode = errorDetails.errorCode
                )
            }
        } catch (exception: Exception) {
            val networkErrorDetails = parseError(exception)
            DataResponseStatus.failure(
                errorMessage = networkErrorDetails.errorMessage,
                errorCode = networkErrorDetails.errorCode
            )
        }
    }

    private fun getErrorDetails(response: Response<*>): NetworkError {
        val responseCode = response.code()
        return try {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrEmpty()) {
                val gson = Gson()
                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                val detailMessage = jsonObject?.get("detail")?.asString
                if (detailMessage != null) {
                    NetworkError(detailMessage, responseCode)
                } else {
                    NetworkError(errorBody, responseCode)
                }
            } else {
                NetworkError("Unknown error occurred", responseCode)
            }
        } catch (_: Exception) {
            NetworkError("Failed to parse error response", responseCode)
        }
    }

    private fun parseError(exception: Exception?): NetworkError {
        return when (exception) {
            is NoInternetConnectionException -> NetworkError(
                "",
                AppErrorCodes.NO_INTERNET_CONNECTION_ERROR
            )

            is IOException -> NetworkError(
                "Network connection error",
                AppErrorCodes.NO_INTERNET_CONNECTION_ERROR
            )

            else -> NetworkError(
                exception?.message ?: "Unknown error",
                AppErrorCodes.UNKNOWN_ERROR
            )
        }
    }

    fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val networkCapabilities = connectivityManager?.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}