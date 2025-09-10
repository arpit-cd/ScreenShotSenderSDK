package com.cd.uielementmanager.presentation.utils

import android.content.Context
import android.widget.Toast
import com.cd.uielementmanager.R
import com.cd.uielementmanager.domain.domain_utils.AppErrorCodes
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus

internal object FunctionHelper {

    fun <T> DataResponseStatus<T>.mapToDataUiResponseStatus(): DataUiResponseStatus<T> {
        return when (this) {
            is DataResponseStatus.Success -> DataUiResponseStatus.success(data)
            is DataResponseStatus.Failure -> {
                DataUiResponseStatus.failure(errorMessage, errorCode)
            }
        }
    }


    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.getErrorMessage(errorMessage: String, errorCode: Int): String {
        return when (errorCode) {
            AppErrorCodes.NO_INTERNET_CONNECTION_ERROR -> {
                getString(R.string.no_internet_connection_please_make_sure_your_device_is_connected_to_an_active_internet_connection)
            }

            else -> {
                errorMessage.ifBlank {
                    getString(R.string.something_went_wrong_please_try_again_later)
                }
            }
        }
    }
}