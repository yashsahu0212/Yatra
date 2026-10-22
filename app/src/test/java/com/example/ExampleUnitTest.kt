package com.example

import com.example.data.model.CompanionType
import com.example.data.model.SyncState
import com.example.data.model.TravelMode
import com.example.data.model.TripEntity
import com.example.data.model.TripPurpose
import com.example.data.repository.toNetworkDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `verify TripEntity to NetworkTripDto mapping`() {
    val entity = TripEntity(
      id = 42L,
      originName = "Central Hub",
      destinationName = "NIT Campus",
      startTime = 1729000000000L,
      endTime = 1729001800000L,
      startTimeFormatted = "09:00 AM",
      endTimeFormatted = "09:30 AM",
      dateLabel = "Today",
      distanceKm = 5.4,
      durationMinutes = 30,
      primaryMode = TravelMode.BUS,
      modesChain = listOf(TravelMode.WALK, TravelMode.BUS, TravelMode.WALK),
      segmentsJson = "Walk:0.4km,Bus:5.0km",
      confidenceScore = 0.95f,
      isVerifiedByUser = true,
      purpose = TripPurpose.EDUCATION,
      companions = CompanionType.ALONE,
      syncState = SyncState.PENDING,
      gpsPointCount = 180,
      co2SavedKg = 1.15,
      rawSensorQuality = 98
    )

    val dto = entity.toNetworkDto()

    assertEquals(42L, dto.clientTripId)
    assertEquals("Central Hub", dto.originName)
    assertEquals("NIT Campus", dto.destinationName)
    assertEquals("BUS", dto.primaryMode)
    assertEquals(listOf("WALK", "BUS", "WALK"), dto.modesChain)
    assertEquals("EDUCATION", dto.purpose)
    assertEquals("ALONE", dto.companions)
    assertTrue(dto.isVerifiedByUser)
    assertEquals(1.15, dto.co2SavedKg, 0.001)
  }
}

