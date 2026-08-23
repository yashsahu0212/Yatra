package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originName: String,
    val destinationName: String,
    val startTime: Long,
    val endTime: Long,
    val startTimeFormatted: String,
    val endTimeFormatted: String,
    val dateLabel: String, // e.g. "Today", "Yesterday", "21 Aug"
    val distanceKm: Double,
    val durationMinutes: Int,
    val primaryMode: TravelMode,
    val modesChain: List<TravelMode>,
    val segmentsJson: String, // Serialized list of TripSegment
    val confidenceScore: Float, // 0.0 - 1.0 (e.g. 0.96)
    val isVerifiedByUser: Boolean,
    val purpose: TripPurpose? = null,
    val companions: CompanionType? = null,
    val syncState: SyncState = SyncState.SYNCED,
    val gpsPointCount: Int = 180,
    val co2SavedKg: Double = 0.85,
    val rawSensorQuality: Int = 98 // 0-100%
)

data class ActiveTripState(
    val isActive: Boolean = false,
    val isPaused: Boolean = false,
    val origin: String = "Home",
    val destinationEstimate: String = "Moving toward College",
    val currentMode: TravelMode = TravelMode.BUS,
    val confidence: Float = 0.91f,
    val startTimestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Long = 378L, // 06:18
    val distanceKm: Double = 2.4,
    val currentSpeedKmh: Double = 28.5,
    val pointsCount: Int = 48,
    val segmentsRecorded: List<TripSegment> = emptyList()
)

data class SensorTelemetry(
    val isGpsActive: Boolean = true,
    val gpsAccuracyMeters: Float = 3.8f,
    val sensorFusionStatus: String = "Calibrated (Accel + Gyro + Baro)",
    val motionCadenceSpm: Int = 114,
    val batteryOptimizationIgnored: Boolean = true,
    val pendingUploadsCount: Int = 3,
    val unsyncedLocationPoints: Int = 24,
    val isOnline: Boolean = true,
    val lastSyncFormatted: String = "Today, 10:42 AM"
)
