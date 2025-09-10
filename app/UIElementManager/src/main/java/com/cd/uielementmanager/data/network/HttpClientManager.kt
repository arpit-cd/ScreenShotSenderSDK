package com.cd.uielementmanager.data.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal object HttpClientManager {

    @Volatile
    private var retrofit: Retrofit? = null
    
    @Volatile
    private var apiService: UIElementsApiService? = null

    fun getApiService(context: Context): UIElementsApiService {
        return apiService ?: synchronized(this) {
            apiService ?: createRetrofit(context).create(UIElementsApiService::class.java).also { 
                apiService = it 
            }
        }
    }

    private fun createRetrofit(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/")
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
            .addInterceptor(getChuckerInterceptor(context))
            .addInterceptor(AppNetworkInterceptorImpl(context))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun getChuckerInterceptor(context: Context): ChuckerInterceptor {
        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        return ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .build()
    }

    fun clearInstance() {
        synchronized(this) {
            retrofit = null
            apiService = null
        }
    }
}