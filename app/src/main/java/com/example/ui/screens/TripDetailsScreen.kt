package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TravelMode
import com.example.data.model.TripEntity
import com.example.ui.components.ConfidenceBadge
import com.example.ui.components.InteractiveMapCanvas
import com.example.ui.components.JourneyChainVisualizer
import com.example.ui.theme.ModeBusBlue
import com.example.ui.theme.ModeWalkGreen
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.YatraNavyDark
import com.example.ui.theme.YatraPrimaryCyan

@Composable
fun TripDetailsScreen(
    trip: TripEntity?,
    onBack: () -> Unit,
    onEditTrip: (TripEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val nonNullTrip = trip ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sticky Header with Back Button and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (isDark) YatraNavyDark else Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_from_trip_details")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Column {
                    Text(
                        text = "Trip Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${nonNullTrip.dateLabel} • ${nonNullTrip.startTimeFormatted}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            ConfidenceBadge(confidence = nonNullTrip.confidenceScore)
        }

        // Scrollable Body
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Large interactive Map Canvas (Top 300dp)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    InteractiveMapCanvas(
                        modifier = Modifier.fillMaxSize(),
                        isRecordingLive = false,
                        activeMode = nonNullTrip.primaryMode,
                        showDetailedRoute = true,
                        originLabel = nonNullTrip.originName,
                        destinationLabel = nonNullTrip.destinationName,
                        showModeSelectorBar = true
                    )
                }
            }

            // Timeline & Metadata Container
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(if (isDark) MaterialTheme.colorScheme.surface else Color.White)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Origin & Destination Title
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${nonNullTrip.originName} → ${nonNullTrip.destinationName}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        JourneyChainVisualizer(modes = nonNullTrip.modesChain)
                    }

                    // Key Metric Cards (Distance, Duration, CO2 Saved)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            label = "Distance",
                            value = "${nonNullTrip.distanceKm} km",
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )
                        MetricCard(
                            label = "Duration",
                            value = "${nonNullTrip.durationMinutes} min",
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )
                        MetricCard(
                            label = "CO₂ Impact",
                            value = "-${nonNullTrip.co2SavedKg} kg",
                            textColor = ModeWalkGreen,
                            modifier = Modifier.weight(1f),
                            isDark = isDark
                        )
                    }

                    // Journey Timeline Section
                    Text(
                        text = "Journey Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(18.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF8FAFC)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TimelineStage(
                                time = nonNullTrip.startTimeFormatted,
                                title = "Started Journey",
                                description = nonNullTrip.originName,
                                color = ModeWalkGreen
                            )
                            TimelineStage(
                                time = "08:47 AM",
                                title = "Walk to Transit",
                                description = "0.6 km • Cadence 112 spm",
                                color = ModeWalkGreen
                            )
                            TimelineStage(
                                time = "08:51 AM",
                                title = "Boarded Bus Route 340",
                                description = "7.2 km • Speed 28.5 km/h (In Vehicle)",
                                color = ModeBusBlue
                            )
                            TimelineStage(
                                time = "09:08 AM",
                                title = "Alighted & Final Walk",
                                description = "0.6 km to Campus Gate",
                                color = ModeWalkGreen
                            )
                            TimelineStage(
                                time = nonNullTrip.endTimeFormatted,
                                title = "Arrived at Destination",
                                description = nonNullTrip.destinationName,
                                color = YatraPrimaryCyan,
                                isLast = true
                            )
                        }
                    }

                    // Research Metadata Grid
                    Text(
                        text = "Research Telemetry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                RoundedCornerShape(18.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF8FAFC)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ResearchMetaRow(label = "Trip Purpose", value = "${nonNullTrip.purpose?.emoji ?: "📍"} ${nonNullTrip.purpose?.displayName ?: "Education"}")
                            ResearchMetaRow(label = "Travel Companions", value = "${nonNullTrip.companions?.emoji ?: "👤"} ${nonNullTrip.companions?.displayName ?: "Alone"}")
                            ResearchMetaRow(label = "Inference Confidence", value = "${(nonNullTrip.confidenceScore * 100).toInt()}% (Supervised Fusion)")
                            ResearchMetaRow(label = "GPS Point Count", value = "${nonNullTrip.gpsPointCount} valid packets")
                            ResearchMetaRow(label = "Sensor Signal Integrity", value = "${nonNullTrip.rawSensorQuality}%")
                            ResearchMetaRow(
                                label = "Verification Status",
                                value = if (nonNullTrip.isVerifiedByUser) "Participant Verified" else "Model Auto-Detected"
                            )
                        }
                    }

                    // Action Buttons: Edit Trip & Re-classify
                    Button(
                        onClick = { onEditTrip(nonNullTrip) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("edit_trip_from_details_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YatraNavyDark
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Edit trip / Re-verify Mode",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5F9)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun TimelineStage(
    time: String,
    title: String,
    description: String,
    color: Color,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(30.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = YatraPrimaryCyan
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResearchMetaRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
