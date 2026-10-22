package com.example.data.remote

import com.example.data.remote.api.YatraApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 10.0.2.2 is the default IP to reach host machine from standard Android Emulator
    const val DEFAULT_EMULATOR_URL = "http://10.0.2.2:5000/"

    @Volatile
    private var currentBaseUrl: String = DEFAULT_EMULATOR_URL

    @Volatile
    private var apiServiceInstance: YatraApiService? = null

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .build()
    }

    fun getBaseUrl(): String = currentBaseUrl

    @Synchronized
    fun setBaseUrl(newUrl: String) {
        val formatted = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (formatted != currentBaseUrl) {
            currentBaseUrl = formatted
            apiServiceInstance = createService(formatted)
        }
    }

    @Synchronized
    fun getApiService(): YatraApiService {
        return apiServiceInstance ?: synchronized(this) {
            apiServiceInstance ?: createService(currentBaseUrl).also { apiServiceInstance = it }
        }
    }

    private fun createService(baseUrl: String): YatraApiService {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(YatraApiService::class.java)
    }
}
