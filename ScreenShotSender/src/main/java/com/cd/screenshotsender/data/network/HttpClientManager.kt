package com.cd.screenshotsender.data.network

import android.content.Context
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object HttpClientManager {

    @Volatile
    private var retrofit: Retrofit? = null

    @Volatile
    private var apiService: SendScreenShotApiService? = null

    fun getApiService(context: Context): SendScreenShotApiService {
        return apiService ?: synchronized(this) {
            apiService ?: createRetrofit(context).create(SendScreenShotApiService::class.java)
                .also {
                    apiService = it
                }
        }
    }

    private fun createRetrofit(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl("https://qa-stock.countrydelight.in/api/cd_training/")
                .client(createOkHttpClient(context))
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder()
                            .setPrettyPrinting()
                            .disableHtmlEscaping()
                            .create()
                    )
                )
                .build()
                .also { retrofit = it }
        }
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AppNetworkInterceptorImpl(context))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    fun clearInstance() {
        synchronized(this) {
            retrofit = null
            apiService = null
        }
    }
}