package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SensorTelemetry
import com.example.data.model.TravelMode
import com.example.data.model.TripEntity
import com.example.ui.components.BentoStatCard
import com.example.ui.components.ConfidenceBadge
import com.example.ui.components.GlassPanel
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.components.JourneyChainVisualizer
import com.example.ui.components.MiniRouteCanvas
import com.example.ui.components.PulsingStatusDot
import com.example.ui.components.TelemetryPill
import com.example.ui.theme.ModeBusBlue
import com.example.ui.theme.ModeWalkGreen
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.YatraNavyDark
import com.example.ui.theme.YatraNavySurface
import com.example.ui.theme.YatraPrimaryCyan

@Composable
fun HomeScreen(
    telemetry: SensorTelemetry,
    trips: List<TripEntity>,
    onStartActiveTrip: () -> Unit,
    onViewTripDetails: (Long) -> Unit,
    onVerifyTrip: (TripEntity) -> Unit,
    onOpenSync: () -> Unit,
    onOpenPrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    // Aggregate today's stats
    val todayTrips = trips.filter { it.dateLabel == "Today" }
    val todayTripsCount = if (todayTrips.isNotEmpty()) todayTrips.size else 3
    val totalDistanceKm = if (todayTrips.isNotEmpty()) {
        (todayTrips.sumOf { it.distanceKm } * 10).toInt() / 10.0
    } else 12.8
    val totalDurationMins = if (todayTrips.isNotEmpty()) {
        todayTrips.sumOf { it.durationMinutes }
    } else 48

    val lastTrip = trips.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky Top Header with Status & Participant info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) YatraNavyDark else Color(0xFFFBF9F6))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Good morning, Yash",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "YATRA",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color.White else Color(0xFF1A1C1E),
                        letterSpacing = (-0.5).sp
                    )
                }

                // Tracking Active Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFE7F3E8),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDark) Color(0xFF047857) else Color(0xFFD1E9D5)
                    ),
                    modifier = Modifier
                        .clickable { onOpenSync() }
                        .testTag("home_tracking_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Text(
                            text = "TRACKING ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFF6EE7B7) else Color(0xFF065F46),
                            fontSize = 11.sp,
                            letterSpacing = (-0.2).sp
                        )
                    }
                }
            }

            // Real-time Sensor Telemetry Bar
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TelemetryPill(label = "GPS", value = "±${telemetry.gpsAccuracyMeters}m")
                TelemetryPill(label = "Sensors", value = "Fusion 100Hz")
                TelemetryPill(label = "Buffer", value = "${telemetry.pendingUploadsCount} pending")
            }
        }

        // Scrollable Body containing Map Card + Bottom-Sheet Bento
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Interactive Map Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .height(330.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (isDark) Color(0xFF111827) else Color(0xFFE2E8F0))
                        .border(
                            1.dp,
                            if (isDark) Color(0xFF374151) else Color(0xFFE5E7EB),
                            RoundedCornerShape(32.dp)
                        )
                ) {
                    // Vector Map Canvas with Road Grid & multimodal route
                    InteractiveMapCanvas(
                        modifier = Modifier.fillMaxSize(),
                        isRecordingLive = false,
                        activeMode = lastTrip?.primaryMode ?: TravelMode.BUS,
                        showDetailedRoute = true,
                        originLabel = "Home",
                        destinationLabel = "College",
                        showModeSelectorBar = true
                    )

                    // Floating Glass Status Card (Top-Left overlay)
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = if (isDark) Color(0xEB1F2937) else Color(0xEBFFFFFF),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDark) Color(0x334B5563) else Color(0x80FFFFFF)
                        ),
                        shadowElevation = 6.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "CURRENT STATUS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280),
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Waiting for next journey",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) Color.White else Color(0xFF1A1C1E)
                            )
                        }
                    }

                    // Floating Bottom-Right Action Buttons
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Recenter / Map Button
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable { },
                            shape = CircleShape,
                            color = if (isDark) Color(0xFF1F2937) else Color.White,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDark) Color(0xFF374151) else Color(0xFFE5E7EB)
                            ),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.GpsFixed,
                                    contentDescription = "Recenter",
                                    tint = if (isDark) Color.White else Color(0xFF64748B),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Start / Simulate Trip Button
                        Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .clickable { onStartActiveTrip() }
                                .testTag("simulate_trip_detection_button"),
                            shape = CircleShape,
                            color = Color(0xFF2D5BFF),
                            shadowElevation = 6.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start Trip",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Bottom-Sheet Information Panel
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                        .background(if (isDark) MaterialTheme.colorScheme.surface else Color.White)
                        .border(
                            1.dp,
                            if (isDark) Color(0xFF374151) else Color(0xFFF3F4F6),
                            RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
                        )
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Grab Handle Bar
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isDark) Color(0xFF374151) else Color(0xFFE5E7EB))
                            .align(Alignment.CenterHorizontally)
                    )

                    // Section Title: Today's Activity + Date Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Today's Activity",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF1A1C1E)
                        )
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFFEFF3FF)
                        ) {
                            Text(
                                text = "Nov 14, 2026",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2D5BFF),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // 3-Column Bento Activity Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Trips Tile
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9),
                                    RoundedCornerShape(18.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF1F2937) else Color(0xFFF8FAFC)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "TRIPS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "%02d".format(todayTripsCount),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color(0xFF1A1C1E)
                                )
                            }
                        }

                        // Dist. Tile
                        Card(
                            modifier = Modifier
                                .weight(1.1f)
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9),
                                    RoundedCornerShape(18.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF1F2937) else Color(0xFFF8FAFC)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "DIST.",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$totalDistanceKm",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color(0xFF1A1C1E)
                                    )
                                    Text(
                                        text = "km",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF64748B),
                                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                                    )
                                }
                            }
                        }

                        // Time Tile
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0xFF374151) else Color(0xFFF1F5F9),
                                    RoundedCornerShape(18.dp)
                                ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF1F2937) else Color(0xFFF8FAFC)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "TIME",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$totalDurationMins",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color(0xFF1A1C1E)
                                    )
                                    Text(
                                        text = "m",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF64748B),
                                        modifier = Modifier.padding(bottom = 2.dp, start = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Last Detected Trip Card
                    if (lastTrip != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0xFF374151) else Color(0xFFE2E8F0),
                                    RoundedCornerShape(24.dp)
                                )
                                .testTag("home_last_trip_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDark) Color(0xFF1F2937) else Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "LAST DETECTED TRIP",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF64748B),
                                            fontSize = 10.sp,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${lastTrip.originName} → ${lastTrip.destinationName}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color.White else Color(0xFF1A1C1E),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    // 94% Confidence Badge
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isDark) Color(0xFF064E3B).copy(alpha = 0.4f) else Color(0xFFF0FDF4),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isDark) Color(0xFF047857) else Color(0xFFDCFCE7)
                                        )
                                    ) {
                                        Text(
                                            text = "${(lastTrip.confidenceScore * 100).toInt()}% Confidence",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFF6EE7B7) else Color(0xFF166534),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF10B981))
                                            )
                                            Text(
                                                text = "Walk",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF64748B)
                                            )
                                        }
                                        Text(
                                            text = "•",
                                            color = if (isDark) Color(0xFF4B5563) else Color(0xFFD1D5DB)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF2D5BFF))
                                            )
                                            Text(
                                                text = "Bus",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF64748B)
                                            )
                                        }
                                        Text(
                                            text = "•",
                                            color = if (isDark) Color(0xFF4B5563) else Color(0xFFD1D5DB)
                                        )
                                        Text(
                                            text = "${lastTrip.durationMinutes} min total",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (isDark) Color(0xFF9CA3AF) else Color(0xFF64748B)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onViewTripDetails(lastTrip.id) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(42.dp)
                                            .testTag("view_trip_button"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) Color(0xFF374151) else Color(0xFF1A1C1E)
                                        )
                                    ) {
                                        Text("View Details", style = MaterialTheme.typography.labelMedium)
                                    }

                                    if (!lastTrip.isVerifiedByUser) {
                                        OutlinedButton(
                                            onClick = { onVerifyTrip(lastTrip) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("verify_trip_button"),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Verify", style = MaterialTheme.typography.labelMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
