package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ModeBusBlue
import com.example.ui.theme.ModeCarOrange
import com.example.ui.theme.ModeTrainIndigo
import com.example.ui.theme.ModeWalkGreen
import java.util.UUID

enum class GoalMetricType(
    val displayName: String,
    val unit: String,
    val defaultWeeklyTarget: Float,
    val defaultMonthlyTarget: Float,
    val stepSize: Float,
    val maxTarget: Float
) {
    DISTANCE_KM("Distance Traveled", "km", 15f, 60f, 1f, 200f),
    TRIPS_COUNT("Trips Logged", "trips", 8f, 32f, 1f, 100f),
    ACTIVE_TIME_MINUTES("Active Commute Time", "min", 120f, 480f, 10f, 1500f),
    CO2_SAVED_KG("CO₂ Emissions Avoided", "kg", 8f, 35f, 0.5f, 100f)
}

enum class GoalTimeframe(val displayName: String, val shortName: String) {
    WEEKLY("Weekly Target", "Week"),
    MONTHLY("Monthly Target", "Month")
}

data class MobilityGoal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val metricType: GoalMetricType,
    val timeframe: GoalTimeframe = GoalTimeframe.WEEKLY,
    val targetMode: TravelMode? = null, // null means all modes or composite
    val targetValue: Float,
    val currentValue: Float = 0f,
    val isEnabled: Boolean = true,
    val streakDays: Int = 3
) {
    val progressPercentage: Float
        get() = if (targetValue > 0f) (currentValue / targetValue).coerceIn(0f, 1f) else 0f

    val isCompleted: Boolean
        get() = currentValue >= targetValue

    val remainingValue: Float
        get() = (targetValue - currentValue).coerceAtLeast(0f)

    val displayColor: Color
        get() = when {
            targetMode == TravelMode.WALK -> ModeWalkGreen
            targetMode == TravelMode.BUS -> ModeBusBlue
            targetMode == TravelMode.TRAIN -> ModeTrainIndigo
            targetMode == TravelMode.BICYCLE -> Color(0xFF059669)
            targetMode == TravelMode.CAR || targetMode == TravelMode.AUTO -> ModeCarOrange
            metricType == GoalMetricType.CO2_SAVED_KG -> Color(0xFF10B981)
            else -> Color(0xFF2D5BFF)
        }

    fun getEncouragingFeedback(): String {
        val pct = (progressPercentage * 100).toInt()
        return when {
            isCompleted -> "🎉 Outstanding! You reached your $timeframe target. Keep nurturing this sustainable travel rhythm."
            pct >= 80 -> "✨ Almost there! Just ${formatValue(remainingValue)} ${metricType.unit} left to achieve your personal target."
            pct >= 50 -> "🌱 Strong momentum! You are halfway through your ${timeframe.shortName.lowercase()} target."
            pct >= 25 -> "🚶 Off to a steady start. Every journey counts towards your personal mobility wellness."
            else -> "🎯 Ready when you are. Step by step, explore your daily routes at your own comfortable pace."
        }
    }

    private fun formatValue(v: Float): String {
        return if (v % 1.0f == 0f) v.toInt().toString() else String.format("%.1f", v)
    }
}
