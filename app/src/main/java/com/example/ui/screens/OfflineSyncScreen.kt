package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusWarningAmber
import com.example.ui.theme.YatraNavyDark
import com.example.ui.theme.YatraPrimaryCyan
import com.example.ui.viewmodel.SyncProgressState

@Composable
fun OfflineSyncScreen(
    syncState: SyncProgressState,
    isOnline: Boolean,
    onToggleOnline: () -> Unit,
    onStartSync: () -> Unit,
    onBack: () -> Unit,
    onUpdateServerUrl: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val animatedProgress by animateFloatAsState(
        targetValue = syncState.progress,
        label = "sync_progress"
    )
    var showUrlDialog by remember { mutableStateOf(false) }
    var editUrlText by remember(syncState.serverUrl) { mutableStateOf(syncState.serverUrl) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Header
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_from_sync")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOnline) YatraPrimaryCyan.copy(alpha = 0.12f) else Color(0xFFF1F5F9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isOnline) YatraPrimaryCyan else Color(0xFF64748B),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isOnline) "Cloud Connected" else "Offline Storage Active",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) YatraPrimaryCyan else Color(0xFF64748B)
                        )
                    }
                }
            }

            // Hero Status Section
            if (syncState.isComplete) {
                SyncCompleteHero()
            } else if (syncState.isSyncing) {
                SyncInProgressHero(progress = animatedProgress, statusText = syncState.currentStepText)
            } else if (!isOnline) {
                OfflineHero()
            } else {
                ReadyToSyncHero()
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Pending Queue Breakdown Card
            Text(
                text = "Offline Buffer Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    QueueItem(
                        icon = Icons.Default.Route,
                        title = "Completed Trips",
                        count = if (syncState.isComplete) "0 pending" else "${syncState.pendingTripsCount} trips",
                        isSynced = syncState.isComplete
                    )
                    QueueItem(
                        icon = Icons.Default.GpsFixed,
                        title = "High-Rate GPS Telemetry Packets",
                        count = if (syncState.isComplete) "0 pending" else "${syncState.pendingLocationPoints} clusters",
                        isSynced = syncState.isComplete
                    )
                    QueueItem(
                        icon = Icons.Default.CheckCircle,
                        title = "User Mode Confirmations",
                        count = if (syncState.isComplete) "0 pending" else "${syncState.pendingConfirmations} verified",
                        isSynced = syncState.isComplete
                    )
                }
            }

            // Cloud Gateway & Database Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else Color(0xFFF8FAFC)
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = YatraPrimaryCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Gateway & Cloud Database",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        TextButton(onClick = { showUrlDialog = true }) {
                            Text(
                                text = "Change URL",
                                style = MaterialTheme.typography.labelSmall,
                                color = YatraPrimaryCyan
                            )
                        }
                    }
                    Text(
                        text = "Server: ${syncState.serverUrl}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Database: Supabase PostgreSQL Cloud",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (syncState.errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusWarningAmber.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusWarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = syncState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Bottom Actions (Sync Now & Simulate Network Toggle)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onStartSync,
                enabled = !syncState.isSyncing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sync_now_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YatraNavyDark
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (syncState.isComplete) Icons.Default.Refresh else Icons.Default.Sync,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (syncState.isComplete) "Force Re-Sync" else "Synchronize Now",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            OutlinedButton(
                onClick = onToggleOnline,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("toggle_offline_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isOnline) "Simulate Offline Mode (Airplane)" else "Simulate Online Connection (Wi-Fi)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (showUrlDialog) {
            AlertDialog(
                onDismissRequest = { showUrlDialog = false },
                title = { Text("Configure Gateway URL") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Enter the URL for your YATRA backend server:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        OutlinedTextField(
                            value = editUrlText,
                            onValueChange = { editUrlText = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { editUrlText = "http://10.0.2.2:5000/" },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Emulator", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        onUpdateServerUrl(editUrlText.trim())
                        showUrlDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUrlDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun OfflineHero() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "You're offline",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "Don't worry, your trips are safely encrypted and stored on this device. They will be sent when you're back online.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun SyncInProgressHero(progress: Float, statusText: String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(YatraPrimaryCyan.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = null,
                    tint = YatraPrimaryCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Syncing your journeys",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = YatraPrimaryCyan,
            trackColor = YatraPrimaryCyan.copy(alpha = 0.2f),
        )

        Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = YatraPrimaryCyan
        )
    }
}

@Composable
private fun SyncCompleteHero() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(StatusActiveGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = StatusActiveGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Everything is up to date",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = "All trips, sensor points, and survey answers have been successfully synchronized to the academic research repository.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ReadyToSyncHero() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Cloud Synchronization",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Sensor telemetry is encrypted on device. Tap Synchronize Now to transmit pending batches securely.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun QueueItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    count: String,
    isSynced: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = count,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (isSynced) StatusActiveGreen else YatraNavyDark
        )
    }
}
