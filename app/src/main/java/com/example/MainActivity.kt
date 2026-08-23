package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ActiveTripScreen
import com.example.ui.screens.ConsentScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.OfflineSyncScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacyCenterScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TripCompleteScreen
import com.example.ui.screens.TripDetailsScreen
import com.example.ui.screens.TripVerificationScreen
import com.example.ui.screens.TripsScreen
import com.example.ui.theme.YatraNavyDark
import com.example.ui.theme.YatraNavySurface
import com.example.ui.theme.YatraPrimaryCyan
import com.example.ui.theme.YatraTheme
import com.example.ui.viewmodel.ScreenDestination
import com.example.ui.viewmodel.YatraViewModel

data class BottomNavItem(
    val title: String,
    val destination: ScreenDestination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YatraTheme {
                YatraApp()
            }
        }
    }
}

@Composable
fun YatraApp(viewModel: YatraViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val allTrips by viewModel.allTrips.collectAsState()
    val activeTrip by viewModel.activeTrip.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val syncProgress by viewModel.syncProgress.collectAsState()
    val tripsFilter by viewModel.tripsFilter.collectAsState()
    val selectedTripId by viewModel.selectedTripId.collectAsState()
    val verifyingTrip by viewModel.verifyingTrip.collectAsState()
    val selectedVerifiedMode by viewModel.selectedVerifiedMode.collectAsState()
    val justCompletedTrip by viewModel.justCompletedTrip.collectAsState()
    val selectedTripPurpose by viewModel.selectedTripPurpose.collectAsState()
    val selectedCompanion by viewModel.selectedCompanion.collectAsState()
    val isGoalsOptedIn by viewModel.isMobilityGoalsOptedIn.collectAsState()
    val mobilityGoals by viewModel.mobilityGoals.collectAsState()

    val isDark = isSystemInDarkTheme()

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Home",
            destination = ScreenDestination.HOME,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "nav_home"
        ),
        BottomNavItem(
            title = "Trips",
            destination = ScreenDestination.TRIPS_LIST,
            selectedIcon = Icons.Filled.Route,
            unselectedIcon = Icons.Outlined.Route,
            testTag = "nav_trips"
        ),
        BottomNavItem(
            title = "Insights",
            destination = ScreenDestination.INSIGHTS,
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart,
            testTag = "nav_insights"
        ),
        BottomNavItem(
            title = "Profile",
            destination = ScreenDestination.PROFILE,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            testTag = "nav_profile"
        )
    )

    val showBottomBar = currentScreen in listOf(
        ScreenDestination.HOME,
        ScreenDestination.TRIPS_LIST,
        ScreenDestination.INSIGHTS,
        ScreenDestination.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = if (isDark) YatraNavyDark else Color.White,
                    shadowElevation = 0.dp,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else Color(0xFFF3F4F6)
                    )
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .height(72.dp)
                            .navigationBarsPadding()
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentScreen == item.destination
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.destination) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = if (isDark) Color.White else Color(0xFF2D5BFF),
                                    selectedTextColor = if (isDark) Color(0xFF60A5FA) else Color(0xFF2D5BFF),
                                    indicatorColor = if (isDark) Color(0xFF1E293B) else Color(0xFFEFF3FF),
                                    unselectedIconColor = if (isDark) Color(0xFF6B7280) else Color(0xFF94A3B8),
                                    unselectedTextColor = if (isDark) Color(0xFF6B7280) else Color(0xFF94A3B8)
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_navigation"
            ) { destination ->
                when (destination) {
                    ScreenDestination.ONBOARDING -> {
                        OnboardingScreen(
                            onFinishOnboarding = { viewModel.navigateTo(ScreenDestination.CONSENT) },
                            onSkipToConsent = { viewModel.navigateTo(ScreenDestination.CONSENT) }
                        )
                    }
                    ScreenDestination.CONSENT -> {
                        ConsentScreen(
                            onAllowAndContinue = { viewModel.navigateTo(ScreenDestination.HOME) },
                            onOpenPrivacyCenter = { viewModel.navigateTo(ScreenDestination.PRIVACY_CENTER) }
                        )
                    }
                    ScreenDestination.HOME -> {
                        HomeScreen(
                            telemetry = telemetry,
                            trips = allTrips,
                            onStartActiveTrip = { viewModel.startNewTripSimulation() },
                            onViewTripDetails = { tripId -> viewModel.selectTripForDetails(tripId) },
                            onVerifyTrip = { trip -> viewModel.startTripVerification(trip) },
                            onOpenSync = { viewModel.navigateTo(ScreenDestination.SYNC_STATUS) },
                            onOpenPrivacy = { viewModel.navigateTo(ScreenDestination.PRIVACY_CENTER) }
                        )
                    }
                    ScreenDestination.ACTIVE_TRIP -> {
                        ActiveTripScreen(
                            activeTrip = activeTrip,
                            onPauseToggle = { viewModel.pauseActiveTrip() },
                            onStopAndComplete = { viewModel.stopAndCompleteTrip() },
                            onVerifyModeNow = {
                                val currentTripEntity = allTrips.firstOrNull()
                                if (currentTripEntity != null) {
                                    viewModel.startTripVerification(currentTripEntity)
                                }
                            }
                        )
                    }
                    ScreenDestination.VERIFICATION -> {
                        TripVerificationScreen(
                            trip = verifyingTrip ?: allTrips.firstOrNull(),
                            selectedMode = selectedVerifiedMode,
                            onModeSelected = { viewModel.setSelectedVerifiedMode(it) },
                            onConfirm = { viewModel.confirmTripVerification() },
                            onDismiss = { viewModel.navigateTo(ScreenDestination.HOME) }
                        )
                    }
                    ScreenDestination.TRIP_COMPLETE -> {
                        TripCompleteScreen(
                            trip = justCompletedTrip ?: allTrips.firstOrNull(),
                            selectedPurpose = selectedTripPurpose,
                            onPurposeSelected = { viewModel.setTripPurpose(it) },
                            selectedCompanion = selectedCompanion,
                            onCompanionSelected = { viewModel.setCompanion(it) },
                            onSaveTrip = { viewModel.saveCompletedTrip() }
                        )
                    }
                    ScreenDestination.TRIPS_LIST -> {
                        TripsScreen(
                            trips = allTrips,
                            activeFilter = tripsFilter,
                            onFilterChange = { viewModel.setTripsFilter(it) },
                            onTripSelected = { tripId -> viewModel.selectTripForDetails(tripId) },
                            onVerifyTrip = { trip -> viewModel.startTripVerification(trip) }
                        )
                    }
                    ScreenDestination.TRIP_DETAILS -> {
                        val currentTrip = allTrips.find { it.id == selectedTripId } ?: allTrips.firstOrNull()
                        TripDetailsScreen(
                            trip = currentTrip,
                            onBack = { viewModel.navigateTo(ScreenDestination.TRIPS_LIST) },
                            onEditTrip = { trip -> viewModel.startTripVerification(trip) }
                        )
                    }
                    ScreenDestination.INSIGHTS -> {
                        InsightsScreen(
                            goals = mobilityGoals,
                            isGoalsOptedIn = isGoalsOptedIn,
                            onToggleGoalsOptIn = { viewModel.toggleMobilityGoalsOptIn(it) },
                            onAddGoal = { viewModel.addMobilityGoal(it) },
                            onUpdateGoal = { viewModel.updateMobilityGoal(it) }
                        )
                    }
                    ScreenDestination.PROFILE -> {
                        ProfileScreen(
                            onOpenPrivacyCenter = { viewModel.navigateTo(ScreenDestination.PRIVACY_CENTER) },
                            onOpenSync = { viewModel.navigateTo(ScreenDestination.SYNC_STATUS) },
                            onDeleteData = { viewModel.deleteResearchData() }
                        )
                    }
                    ScreenDestination.PRIVACY_CENTER -> {
                        PrivacyCenterScreen(
                            onBack = { viewModel.navigateTo(ScreenDestination.PROFILE) }
                        )
                    }
                    ScreenDestination.SYNC_STATUS -> {
                        OfflineSyncScreen(
                            syncState = syncProgress,
                            isOnline = telemetry.isOnline,
                            onToggleOnline = { viewModel.toggleOnlineStatus() },
                            onStartSync = { viewModel.startSyncSimulation() },
                            onBack = { viewModel.navigateTo(ScreenDestination.HOME) }
                        )
                    }
                }
            }
        }
    }
}
