package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NetworkTripDto(
    @Json(name = "clientTripId") val clientTripId: Long,
    @Json(name = "originName") val originName: String,
    @Json(name = "destinationName") val destinationName: String,
    @Json(name = "startTime") val startTime: Long,
    @Json(name = "endTime") val endTime: Long,
    @Json(name = "startTimeFormatted") val startTimeFormatted: String,
    @Json(name = "endTimeFormatted") val endTimeFormatted: String,
    @Json(name = "dateLabel") val dateLabel: String,
    @Json(name = "distanceKm") val distanceKm: Double,
    @Json(name = "durationMinutes") val durationMinutes: Int,
    @Json(name = "primaryMode") val primaryMode: String,
    @Json(name = "modesChain") val modesChain: List<String> = emptyList(),
    @Json(name = "segmentsJson") val segmentsJson: String = "",
    @Json(name = "confidenceScore") val confidenceScore: Float = 1.0f,
    @Json(name = "isVerifiedByUser") val isVerifiedByUser: Boolean = false,
    @Json(name = "purpose") val purpose: String? = null,
    @Json(name = "companions") val companions: String? = null,
    @Json(name = "gpsPointCount") val gpsPointCount: Int = 0,
    @Json(name = "co2SavedKg") val co2SavedKg: Double = 0.0,
    @Json(name = "rawSensorQuality") val rawSensorQuality: Int = 100
)

@JsonClass(generateAdapter = true)
data class NetworkTelemetryDto(
    @Json(name = "sensorFusionStatus") val sensorFusionStatus: String = "Calibrated",
    @Json(name = "motionCadenceSpm") val motionCadenceSpm: Int = 0,
    @Json(name = "gpsAccuracyMeters") val gpsAccuracyMeters: Float = 0.0f,
    @Json(name = "batteryOptimizationIgnored") val batteryOptimizationIgnored: Boolean = true,
    @Json(name = "unsyncedLocationPoints") val unsyncedLocationPoints: Int = 0,
    @Json(name = "isOnline") val isOnline: Boolean = true
)

@JsonClass(generateAdapter = true)
data class SyncRequestDto(
    @Json(name = "deviceUserId") val deviceUserId: String = "research_volunteer_01",
    @Json(name = "trips") val trips: List<NetworkTripDto>,
    @Json(name = "telemetry") val telemetry: NetworkTelemetryDto? = null
)

@JsonClass(generateAdapter = true)
data class SyncResponseDto(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "syncedTripIds") val syncedTripIds: List<Long> = emptyList(),
    @Json(name = "serverTimestamp") val serverTimestamp: Long = 0L,
    @Json(name = "backend") val backend: String? = null
)

@JsonClass(generateAdapter = true)
data class InsightsResponseDto(
    @Json(name = "totalTrips") val totalTrips: Int = 0,
    @Json(name = "totalDistanceKm") val totalDistanceKm: Double = 0.0,
    @Json(name = "totalCo2SavedKg") val totalCo2SavedKg: Double = 0.0,
    @Json(name = "averageConfidence") val averageConfidence: Float = 0.0f,
    @Json(name = "verifiedTripsCount") val verifiedTripsCount: Int = 0,
    @Json(name = "verificationRatePct") val verificationRatePct: Double = 0.0,
    @Json(name = "modeBreakdown") val modeBreakdown: Map<String, Int> = emptyMap()
)
