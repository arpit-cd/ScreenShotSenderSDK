package com.cd.screenshotsender.data.network

import android.content.Context
import com.cd.screenshotsender.data.network.NetworkCallHelper.isNetworkAvailable
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Network interceptor to check internet connectivity and add headers
 * Following the same pattern as training flow feature
 *
 */
internal class AppNetworkInterceptorImpl(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        if (!context.isNetworkAvailable()) {
            throw NoInternetConnectionException()
        }

        val response = chain.proceed(request)

        return response
    }
}