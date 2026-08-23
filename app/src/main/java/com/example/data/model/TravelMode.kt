package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Train
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class TravelMode(
    val displayName: String,
    val emoji: String,
    val color: Color,
    val lightBgColor: Color,
    val co2GramsPerKm: Double
) {
    WALK("Walk", "🚶", Color(0xFF10B981), Color(0xFFECFDF5), 0.0),
    BUS("Bus", "🚌", Color(0xFF0284C7), Color(0xFFF0F9FF), 68.0),
    CAR("Car", "🚗", Color(0xFFF59E0B), Color(0xFFFFFBEB), 170.0),
    BIKE("Bike", "🏍", Color(0xFF8B5CF6), Color(0xFFF5F3FF), 95.0),
    TRAIN("Train", "🚆", Color(0xFF6366F1), Color(0xFFEEF2FF), 35.0),
    AUTO("Auto / Taxi", "🚕", Color(0xFFEAB308), Color(0xFFFEFCE8), 120.0),
    BICYCLE("Bicycle", "🚲", Color(0xFF059669), Color(0xFFD1FAE5), 0.0),
    OTHER("Other", "❓", Color(0xFF64748B), Color(0xFFF1F5F9), 80.0);

    val icon: ImageVector
        get() = when (this) {
            WALK -> Icons.AutoMirrored.Filled.DirectionsWalk
            BUS -> Icons.Default.DirectionsBus
            CAR -> Icons.Default.DirectionsCar
            BIKE -> Icons.Default.DirectionsBike
            TRAIN -> Icons.Default.Train
            AUTO -> Icons.Default.LocalTaxi
            BICYCLE -> Icons.Default.DirectionsBike
            OTHER -> Icons.Default.QuestionMark
        }
}

enum class TripPurpose(val displayName: String, val emoji: String) {
    WORK("Work", "💼"),
    EDUCATION("Education", "🎓"),
    SHOPPING("Shopping", "🛍"),
    PERSONAL("Personal", "🏠"),
    HEALTHCARE("Healthcare", "🏥"),
    LEISURE("Leisure", "☕"),
    OTHER("Other", "📍")
}

enum class CompanionType(val displayName: String, val emoji: String) {
    ALONE("Alone", "👤"),
    FAMILY("Family", "👨‍👩‍👧"),
    FRIENDS("Friends", "👥"),
    COLLEAGUES("Colleagues", "🤝"),
    OTHER("Other", "✨")
}

enum class SyncState {
    PENDING,
    SYNCING,
    SYNCED
}

data class CoordinatePoint(
    val lat: Double,
    val lng: Double,
    val timestamp: Long = 0L,
    val speedKmh: Double = 0.0
)

data class TripSegment(
    val mode: TravelMode,
    val originLabel: String,
    val destLabel: String,
    val durationMinutes: Int,
    val distanceKm: Double,
    val confidence: Float,
    val startTimeFormatted: String,
    val endTimeFormatted: String,
    val pathPoints: List<CoordinatePoint> = emptyList()
)
