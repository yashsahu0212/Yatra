package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GoalMetricType
import com.example.data.model.GoalTimeframe
import com.example.data.model.MobilityGoal
import com.example.data.model.TravelMode
import com.example.ui.components.getDynamicMapStyleRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("YATRA", appName)
  }

  @Test
  fun `verify travel modes and confidence categories`() {
    assertEquals("Walk", TravelMode.WALK.displayName)
    assertEquals("Bus", TravelMode.BUS.displayName)
    assertEquals("Train", TravelMode.TRAIN.displayName)
    assertEquals("Auto / Taxi", TravelMode.AUTO.displayName)
  }

  @Test
  fun `verify mobility goal progress and feedback`() {
    val goal = MobilityGoal(
      title = "Weekly Walking",
      metricType = GoalMetricType.DISTANCE_KM,
      timeframe = GoalTimeframe.WEEKLY,
      targetMode = TravelMode.WALK,
      targetValue = 15f,
      currentValue = 11.4f
    )

    assertEquals(0.76f, goal.progressPercentage, 0.01f)
    assertFalse(goal.isCompleted)
    assertEquals(3.6f, goal.remainingValue, 0.01f)
    assertTrue(goal.getEncouragingFeedback().contains("Almost there") || goal.getEncouragingFeedback().contains("momentum"))
  }

  @Test
  fun `verify dynamic map styling rules for walk and bus`() {
    val walkRules = getDynamicMapStyleRules(TravelMode.WALK, isDark = false)
    assertTrue(walkRules.isDashedRoute)
    assertTrue(walkRules.showPedestrianTrails)
    assertFalse(walkRules.showBusStops)
    assertTrue(walkRules.primaryRouteStrokeWidth < 5.0f)

    val busRules = getDynamicMapStyleRules(TravelMode.BUS, isDark = false)
    assertFalse(busRules.isDashedRoute)
    assertTrue(busRules.showBusStops)
    assertFalse(busRules.showPedestrianTrails)
    assertTrue(busRules.primaryRouteStrokeWidth > 7.0f)
  }
}

