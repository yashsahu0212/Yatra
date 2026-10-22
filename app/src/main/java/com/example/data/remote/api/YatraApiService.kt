package com.example.data.remote.api

import com.example.data.remote.model.InsightsResponseDto
import com.example.data.remote.model.NetworkTelemetryDto
import com.example.data.remote.model.NetworkTripDto
import com.example.data.remote.model.SyncRequestDto
import com.example.data.remote.model.SyncResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface YatraApiService {

    @GET("health")
    suspend fun checkHealth(): Response<Map<String, Any>>

    @POST("api/trips/sync")
    suspend fun syncTrips(
        @Body request: SyncRequestDto
    ): Response<SyncResponseDto>

    @GET("api/trips")
    suspend fun getAllTrips(): Response<List<NetworkTripDto>>

    @GET("api/insights")
    suspend fun getInsights(): Response<InsightsResponseDto>

    @POST("api/telemetry")
    suspend fun sendTelemetry(
        @Body telemetry: NetworkTelemetryDto
    ): Response<Map<String, Any>>
}
