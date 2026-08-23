package com.example.data.repository

import com.example.data.local.TripDao
import com.example.data.model.CompanionType
import com.example.data.model.SyncState
import com.example.data.model.TravelMode
import com.example.data.model.TripEntity
import com.example.data.model.TripPurpose
import kotlinx.coroutines.flow.Flow

class TripRepository(private val tripDao: TripDao) {

    val allTrips: Flow<List<TripEntity>> = tripDao.getAllTrips()
    val unsyncedTrips: Flow<List<TripEntity>> = tripDao.getUnsyncedTrips()

    fun getTripById(tripId: Long): Flow<TripEntity?> = tripDao.getTripById(tripId)

    suspend fun insertTrip(trip: TripEntity): Long = tripDao.insertTrip(trip)

    suspend fun updateTrip(trip: TripEntity) = tripDao.updateTrip(trip)

    suspend fun deleteTrip(trip: TripEntity) = tripDao.deleteTrip(trip)

    suspend fun clearAllData() = tripDao.clearAllTrips()

    suspend fun markAllSynced() {
        // Mock sync all pending
    }

    suspend fun seedInitialDataIfEmpty() {
        if (tripDao.getTripCount() > 0) return

        val initialTrips = listOf(
            TripEntity(
                id = 1,
                originName = "Home (Sector 14)",
                destinationName = "National Institute of Technology",
                startTime = System.currentTimeMillis() - 4 * 3600 * 1000,
                endTime = System.currentTimeMillis() - (4 * 3600 - 31 * 60) * 1000,
                startTimeFormatted = "08:42 AM",
                endTimeFormatted = "09:13 AM",
                dateLabel = "Today",
                distanceKm = 8.4,
                durationMinutes = 31,
                primaryMode = TravelMode.BUS,
                modesChain = listOf(TravelMode.WALK, TravelMode.BUS, TravelMode.WALK),
                segmentsJson = "Walk:0.6km,Bus:7.2km,Walk:0.6km",
                confidenceScore = 0.96f,
                isVerifiedByUser = true,
                purpose = TripPurpose.EDUCATION,
                companions = CompanionType.ALONE,
                syncState = SyncState.SYNCED,
                gpsPointCount = 284,
                co2SavedKg = 1.45,
                rawSensorQuality = 98
            ),
            TripEntity(
                id = 2,
                originName = "National Institute of Technology",
                destinationName = "Green Leaf Cafe & Library",
                startTime = System.currentTimeMillis() - 2 * 3600 * 1000,
                endTime = System.currentTimeMillis() - (2 * 3600 - 22 * 60) * 1000,
                startTimeFormatted = "01:20 PM",
                endTimeFormatted = "01:42 PM",
                dateLabel = "Today",
                distanceKm = 3.1,
                durationMinutes = 22,
                primaryMode = TravelMode.WALK,
                modesChain = listOf(TravelMode.WALK),
                segmentsJson = "Walk:3.1km",
                confidenceScore = 0.98f,
                isVerifiedByUser = true,
                purpose = TripPurpose.LEISURE,
                companions = CompanionType.FRIENDS,
                syncState = SyncState.SYNCED,
                gpsPointCount = 142,
                co2SavedKg = 0.62,
                rawSensorQuality = 96
            ),
            TripEntity(
                id = 3,
                originName = "Innovation Tech Park",
                destinationName = "Central Railway Station",
                startTime = System.currentTimeMillis() - 24 * 3600 * 1000 - 3 * 3600 * 1000,
                endTime = System.currentTimeMillis() - 24 * 3600 * 1000 - (3 * 3600 - 47 * 60) * 1000,
                startTimeFormatted = "06:15 PM",
                endTimeFormatted = "07:02 PM",
                dateLabel = "Yesterday",
                distanceKm = 14.2,
                durationMinutes = 47,
                primaryMode = TravelMode.TRAIN,
                modesChain = listOf(TravelMode.AUTO, TravelMode.TRAIN, TravelMode.WALK),
                segmentsJson = "Auto:2.4km,Train:11.2km,Walk:0.6km",
                confidenceScore = 0.92f,
                isVerifiedByUser = true,
                purpose = TripPurpose.WORK,
                companions = CompanionType.COLLEAGUES,
                syncState = SyncState.SYNCED,
                gpsPointCount = 412,
                co2SavedKg = 2.80,
                rawSensorQuality = 94
            ),
            TripEntity(
                id = 4,
                originName = "Central Market Complex",
                destinationName = "Residential Block B",
                startTime = System.currentTimeMillis() - 24 * 3600 * 1000 - 8 * 3600 * 1000,
                endTime = System.currentTimeMillis() - 24 * 3600 * 1000 - (8 * 3600 - 28 * 60) * 1000,
                startTimeFormatted = "04:10 PM",
                endTimeFormatted = "04:38 PM",
                dateLabel = "Yesterday",
                distanceKm = 5.6,
                durationMinutes = 28,
                primaryMode = TravelMode.BUS,
                modesChain = listOf(TravelMode.WALK, TravelMode.BUS),
                segmentsJson = "Walk:0.8km,Bus:4.8km",
                confidenceScore = 0.94f,
                isVerifiedByUser = true,
                purpose = TripPurpose.SHOPPING,
                companions = CompanionType.FAMILY,
                syncState = SyncState.PENDING,
                gpsPointCount = 198,
                co2SavedKg = 0.95,
                rawSensorQuality = 97
            ),
            TripEntity(
                id = 5,
                originName = "Public City Library",
                destinationName = "City Metro Junction",
                startTime = System.currentTimeMillis() - 48 * 3600 * 1000,
                endTime = System.currentTimeMillis() - (48 * 3600 - 35 * 60) * 1000,
                startTimeFormatted = "11:00 AM",
                endTimeFormatted = "11:35 AM",
                dateLabel = "21 Aug",
                distanceKm = 7.8,
                durationMinutes = 35,
                primaryMode = TravelMode.BICYCLE,
                modesChain = listOf(TravelMode.BICYCLE, TravelMode.TRAIN),
                segmentsJson = "Bicycle:2.2km,Metro:5.6km",
                confidenceScore = 0.97f,
                isVerifiedByUser = true,
                purpose = TripPurpose.EDUCATION,
                companions = CompanionType.ALONE,
                syncState = SyncState.PENDING,
                gpsPointCount = 275,
                co2SavedKg = 1.30,
                rawSensorQuality = 99
            )
        )

        tripDao.insertTrips(initialTrips)
    }
}
