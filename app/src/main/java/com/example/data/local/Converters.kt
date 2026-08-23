package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CompanionType
import com.example.data.model.SyncState
import com.example.data.model.TravelMode
import com.example.data.model.TripPurpose

class YatraTypeConverters {
    @TypeConverter
    fun fromTravelMode(mode: TravelMode): String = mode.name

    @TypeConverter
    fun toTravelMode(value: String): TravelMode = runCatching {
        TravelMode.valueOf(value)
    }.getOrDefault(TravelMode.WALK)

    @TypeConverter
    fun fromTravelModeList(modes: List<TravelMode>): String {
        return modes.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toTravelModeList(value: String): List<TravelMode> {
        if (value.isBlank()) return emptyList()
        return value.split(",").mapNotNull { name ->
            runCatching { TravelMode.valueOf(name.trim()) }.getOrNull()
        }
    }

    @TypeConverter
    fun fromTripPurpose(purpose: TripPurpose?): String? = purpose?.name

    @TypeConverter
    fun toTripPurpose(value: String?): TripPurpose? {
        if (value == null) return null
        return runCatching { TripPurpose.valueOf(value) }.getOrNull()
    }

    @TypeConverter
    fun fromCompanionType(companion: CompanionType?): String? = companion?.name

    @TypeConverter
    fun toCompanionType(value: String?): CompanionType? {
        if (value == null) return null
        return runCatching { CompanionType.valueOf(value) }.getOrNull()
    }

    @TypeConverter
    fun fromSyncState(state: SyncState): String = state.name

    @TypeConverter
    fun toSyncState(value: String): SyncState = runCatching {
        SyncState.valueOf(value)
    }.getOrDefault(SyncState.SYNCED)
}
