package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.YatraDatabase
import com.example.data.model.ActiveTripState
import com.example.data.model.CompanionType
import com.example.data.model.GoalMetricType
import com.example.data.model.GoalTimeframe
import com.example.data.model.MobilityGoal
import com.example.data.model.SensorTelemetry
import com.example.data.model.SyncState
import com.example.data.model.TravelMode
import com.example.data.model.TripEntity
import com.example.data.model.TripPurpose
import com.example.data.repository.TripRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.data.remote.RetrofitClient
import com.example.data.remote.model.NetworkTelemetryDto

enum class ScreenDestination {
    ONBOARDING,
    CONSENT,
    HOME,
    ACTIVE_TRIP,
    VERIFICATION,
    TRIP_COMPLETE,
    TRIPS_LIST,
    TRIP_DETAILS,
    INSIGHTS,
    PROFILE,
    PRIVACY_CENTER,
    SYNC_STATUS
}

data class SyncProgressState(
    val isSyncing: Boolean = false,
    val progress: Float = 0f, // 0.0 to 1.0
    val currentStepText: String = "",
    val isComplete: Boolean = false,
    val pendingTripsCount: Int = 0,
    val pendingLocationPoints: Int = 0,
    val pendingConfirmations: Int = 0,
    val serverUrl: String = RetrofitClient.DEFAULT_EMULATOR_URL,
    val errorMessage: String? = null
)

class YatraViewModel(application: Application) : AndroidViewModel(application) {

    private val database = YatraDatabase.getDatabase(application)
    private val repository = TripRepository(database.tripDao())

    val allTrips: StateFlow<List<TripEntity>> = repository.allTrips
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentScreen = MutableStateFlow(ScreenDestination.HOME)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val _selectedTripId = MutableStateFlow<Long?>(1L)
    val selectedTripId: StateFlow<Long?> = _selectedTripId.asStateFlow()

    private val _activeTrip = MutableStateFlow(ActiveTripState())
    val activeTrip: StateFlow<ActiveTripState> = _activeTrip.asStateFlow()

    private val _telemetry = MutableStateFlow(SensorTelemetry())
    val telemetry: StateFlow<SensorTelemetry> = _telemetry.asStateFlow()

    private val _syncProgress = MutableStateFlow(SyncProgressState())
    val syncProgress: StateFlow<SyncProgressState> = _syncProgress.asStateFlow()

    // For Trip Verification screen
    private val _verifyingTrip = MutableStateFlow<TripEntity?>(null)
    val verifyingTrip: StateFlow<TripEntity?> = _verifyingTrip.asStateFlow()
    private val _selectedVerifiedMode = MutableStateFlow(TravelMode.BUS)
    val selectedVerifiedMode: StateFlow<TravelMode> = _selectedVerifiedMode.asStateFlow()

    // For Trip Complete screen
    private val _justCompletedTrip = MutableStateFlow<TripEntity?>(null)
    val justCompletedTrip: StateFlow<TripEntity?> = _justCompletedTrip.asStateFlow()
    private val _selectedTripPurpose = MutableStateFlow<TripPurpose>(TripPurpose.EDUCATION)
    val selectedTripPurpose: StateFlow<TripPurpose> = _selectedTripPurpose.asStateFlow()
    private val _selectedCompanion = MutableStateFlow<CompanionType>(CompanionType.ALONE)
    val selectedCompanion: StateFlow<CompanionType> = _selectedCompanion.asStateFlow()

    // Filter for Trips List
    private val _tripsFilter = MutableStateFlow("All")
    val tripsFilter: StateFlow<String> = _tripsFilter.asStateFlow()

    // Mobility Goals Feature (Opt-in & Personal Improvement Focused)
    private val _isMobilityGoalsOptedIn = MutableStateFlow(true)
    val isMobilityGoalsOptedIn: StateFlow<Boolean> = _isMobilityGoalsOptedIn.asStateFlow()

    private val _mobilityGoals = MutableStateFlow<List<MobilityGoal>>(
        listOf(
            MobilityGoal(
                id = "goal_walk_weekly",
                title = "Weekly Walking Distance",
                description = "Brisk walks and pedestrian connections",
                metricType = GoalMetricType.DISTANCE_KM,
                timeframe = GoalTimeframe.WEEKLY,
                targetMode = TravelMode.WALK,
                targetValue = 15f,
                currentValue = 11.4f,
                isEnabled = true,
                streakDays = 4
            ),
            MobilityGoal(
                id = "goal_transit_weekly",
                title = "Public Transit Journeys",
                description = "Bus & Metro commute trips",
                metricType = GoalMetricType.TRIPS_COUNT,
                timeframe = GoalTimeframe.WEEKLY,
                targetMode = TravelMode.BUS,
                targetValue = 10f,
                currentValue = 8f,
                isEnabled = true,
                streakDays = 6
            ),
            MobilityGoal(
                id = "goal_active_commute",
                title = "Active Commute Time",
                description = "Walking & cycling minutes logged",
                metricType = GoalMetricType.ACTIVE_TIME_MINUTES,
                timeframe = GoalTimeframe.WEEKLY,
                targetMode = null,
                targetValue = 120f,
                currentValue = 95f,
                isEnabled = true,
                streakDays = 5
            ),
            MobilityGoal(
                id = "goal_co2_monthly",
                title = "Monthly Clean Commute",
                description = "CO₂ emissions avoided vs solo driving",
                metricType = GoalMetricType.CO2_SAVED_KG,
                timeframe = GoalTimeframe.MONTHLY,
                targetMode = null,
                targetValue = 25f,
                currentValue = 18.6f,
                isEnabled = true,
                streakDays = 12
            )
        )
    )
    val mobilityGoals: StateFlow<List<MobilityGoal>> = _mobilityGoals.asStateFlow()

    private var activeTripTimerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
        viewModelScope.launch {
            repository.unsyncedTrips.collect { pending ->
                _syncProgress.update { current ->
                    current.copy(
                        pendingTripsCount = pending.size,
                        pendingLocationPoints = pending.sumOf { it.gpsPointCount },
                        pendingConfirmations = pending.count { it.isVerifiedByUser }
                    )
                }
                _telemetry.update { current ->
                    current.copy(
                        pendingUploadsCount = pending.size,
                        unsyncedLocationPoints = pending.sumOf { it.gpsPointCount }
                    )
                }
            }
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun selectTripForDetails(tripId: Long) {
        _selectedTripId.value = tripId
        navigateTo(ScreenDestination.TRIP_DETAILS)
    }

    fun startTripVerification(trip: TripEntity) {
        _verifyingTrip.value = trip
        _selectedVerifiedMode.value = trip.primaryMode
        navigateTo(ScreenDestination.VERIFICATION)
    }

    fun setSelectedVerifiedMode(mode: TravelMode) {
        _selectedVerifiedMode.value = mode
    }

    fun confirmTripVerification() {
        val trip = _verifyingTrip.value ?: return
        val newMode = _selectedVerifiedMode.value
        viewModelScope.launch {
            val updated = trip.copy(
                primaryMode = newMode,
                modesChain = listOf(TravelMode.WALK, newMode, TravelMode.WALK),
                confidenceScore = 1.0f,
                isVerifiedByUser = true,
                syncState = SyncState.PENDING
            )
            repository.updateTrip(updated)
            _verifyingTrip.value = null
            navigateTo(ScreenDestination.TRIPS_LIST)
        }
    }

    // Active Trip Simulation
    fun startNewTripSimulation() {
        _activeTrip.value = ActiveTripState(
            isActive = true,
            isPaused = false,
            origin = "Sector 14 Campus",
            destinationEstimate = "National Transport Hub",
            currentMode = TravelMode.BUS,
            confidence = 0.93f,
            startTimestamp = System.currentTimeMillis(),
            durationSeconds = 120L,
            distanceKm = 1.2,
            currentSpeedKmh = 32.0,
            pointsCount = 36
        )
        navigateTo(ScreenDestination.ACTIVE_TRIP)

        startLiveTripTimer()
    }

    private fun startLiveTripTimer() {
        activeTripTimerJob?.cancel()
        activeTripTimerJob = viewModelScope.launch {
            while (_activeTrip.value.isActive && !_activeTrip.value.isPaused) {
                delay(1000)
                _activeTrip.update { current ->
                    val newDuration = current.durationSeconds + 1
                    val newDistance = current.distanceKm + 0.008
                    current.copy(
                        durationSeconds = newDuration,
                        distanceKm = (newDistance * 100).toInt() / 100.0,
                        pointsCount = current.pointsCount + 1
                    )
                }
            }
        }
    }

    fun pauseActiveTrip() {
        _activeTrip.update { it.copy(isPaused = !it.isPaused) }
        if (_activeTrip.value.isPaused) {
            activeTripTimerJob?.cancel()
        } else {
            startLiveTripTimer()
        }
    }

    fun stopAndCompleteTrip() {
        activeTripTimerJob?.cancel()
        val active = _activeTrip.value
        val durationMins = (active.durationSeconds / 60).toInt().coerceAtLeast(1)

        val newTrip = TripEntity(
            originName = active.origin,
            destinationName = active.destinationEstimate.replace("Moving toward ", ""),
            startTime = active.startTimestamp,
            endTime = System.currentTimeMillis(),
            startTimeFormatted = "08:42 AM",
            endTimeFormatted = "09:13 AM",
            dateLabel = "Today",
            distanceKm = (active.distanceKm * 10).toInt() / 10.0,
            durationMinutes = durationMins,
            primaryMode = active.currentMode,
            modesChain = listOf(TravelMode.WALK, active.currentMode, TravelMode.WALK),
            segmentsJson = "Walk:0.4km,${active.currentMode.displayName}:${active.distanceKm}km,Walk:0.4km",
            confidenceScore = 0.96f,
            isVerifiedByUser = false,
            purpose = TripPurpose.EDUCATION,
            companions = CompanionType.ALONE,
            syncState = SyncState.PENDING,
            gpsPointCount = active.pointsCount,
            co2SavedKg = 1.12,
            rawSensorQuality = 98
        )

        _justCompletedTrip.value = newTrip
        _activeTrip.value = ActiveTripState(isActive = false)
        navigateTo(ScreenDestination.TRIP_COMPLETE)
    }

    fun setTripPurpose(purpose: TripPurpose) {
        _selectedTripPurpose.value = purpose
    }

    fun setCompanion(companion: CompanionType) {
        _selectedCompanion.value = companion
    }

    fun saveCompletedTrip() {
        val completed = _justCompletedTrip.value ?: return
        viewModelScope.launch {
            val toSave = completed.copy(
                purpose = _selectedTripPurpose.value,
                companions = _selectedCompanion.value,
                isVerifiedByUser = true,
                syncState = SyncState.PENDING
            )
            repository.insertTrip(toSave)
            _justCompletedTrip.value = null
            navigateTo(ScreenDestination.HOME)
        }
    }

    fun setTripsFilter(filter: String) {
        _tripsFilter.value = filter
    }

    fun toggleOnlineStatus() {
        _telemetry.update { it.copy(isOnline = !it.isOnline) }
    }

    fun setBackendServerUrl(url: String) {
        RetrofitClient.setBaseUrl(url)
        _syncProgress.update { it.copy(serverUrl = RetrofitClient.getBaseUrl()) }
    }

    fun startSyncSimulation() {
        viewModelScope.launch {
            if (!_telemetry.value.isOnline) {
                _syncProgress.update {
                    it.copy(
                        isSyncing = false,
                        errorMessage = "Device is in Offline Mode. Sensor data is securely buffered locally."
                    )
                }
                navigateTo(ScreenDestination.SYNC_STATUS)
                return@launch
            }

            _syncProgress.update {
                it.copy(
                    isSyncing = true,
                    progress = 0.25f,
                    currentStepText = "Connecting to Cloud Gateway (${RetrofitClient.getBaseUrl()})...",
                    errorMessage = null,
                    isComplete = false
                )
            }
            navigateTo(ScreenDestination.SYNC_STATUS)

            delay(350)
            _syncProgress.update {
                it.copy(
                    progress = 0.60f,
                    currentStepText = "Transmitting trips and telemetry to Supabase PostgreSQL database..."
                )
            }

            val telemetryDto = NetworkTelemetryDto(
                sensorFusionStatus = _telemetry.value.sensorFusionStatus,
                motionCadenceSpm = _telemetry.value.motionCadenceSpm,
                gpsAccuracyMeters = _telemetry.value.gpsAccuracyMeters,
                batteryOptimizationIgnored = _telemetry.value.batteryOptimizationIgnored,
                unsyncedLocationPoints = _telemetry.value.unsyncedLocationPoints,
                isOnline = _telemetry.value.isOnline
            )

            val result = repository.syncPendingTripsOnline(telemetryDto)

            if (result.isSuccess) {
                val syncData = result.getOrNull()
                _syncProgress.update {
                    it.copy(
                        progress = 1.0f,
                        currentStepText = syncData?.message ?: "Everything is up to date",
                        isComplete = true,
                        isSyncing = false,
                        errorMessage = null
                    )
                }
                _telemetry.update {
                    it.copy(
                        lastSyncFormatted = "Just now"
                    )
                }
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "Connection error"
                delay(300)
                _syncProgress.update {
                    it.copy(
                        isSyncing = false,
                        progress = 0f,
                        errorMessage = "Gateway unreachable ($errorMsg)",
                        currentStepText = "Offline buffer active. Trips safely preserved in local Room DB until next sync."
                    )
                }
            }
        }
    }

    fun deleteResearchData() {
        viewModelScope.launch {
            repository.clearAllData()
            _telemetry.update {
                it.copy(
                    pendingUploadsCount = 0,
                    unsyncedLocationPoints = 0
                )
            }
            navigateTo(ScreenDestination.HOME)
        }
    }

    fun seedSampleData() {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun toggleMobilityGoalsOptIn(optIn: Boolean) {
        _isMobilityGoalsOptedIn.value = optIn
    }

    fun addMobilityGoal(goal: MobilityGoal) {
        _mobilityGoals.update { current ->
            current + goal
        }
    }

    fun updateMobilityGoal(updatedGoal: MobilityGoal) {
        _mobilityGoals.update { current ->
            current.map { if (it.id == updatedGoal.id) updatedGoal else it }
        }
    }

    fun deleteMobilityGoal(goalId: String) {
        _mobilityGoals.update { current ->
            current.filterNot { it.id == goalId }
        }
    }

    fun toggleMobilityGoalEnabled(goalId: String) {
        _mobilityGoals.update { current ->
            current.map {
                if (it.id == goalId) it.copy(isEnabled = !it.isEnabled) else it
            }
        }
    }
}
