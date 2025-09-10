package com.cd.screenshotsender.presentation.utils

import android.content.Context
import android.widget.Toast
import com.cd.screenshotsender.domain.domain_utils.DataResponseStatus

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

}