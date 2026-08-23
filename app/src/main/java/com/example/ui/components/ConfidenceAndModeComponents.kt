package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.StatusActiveGreen
import com.example.ui.theme.StatusWarningAmber

@Composable
fun ConfidenceBadge(
    confidence: Float, // 0.0 to 1.0 (e.g. 0.96)
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val percentage = (confidence * 100).toInt()
    val isHighConfidence = confidence >= 0.85f
    val isMediumConfidence = confidence in 0.60f..0.84f

    val badgeColor = when {
        isHighConfidence -> StatusActiveGreen
        isMediumConfidence -> StatusWarningAmber
        else -> Color(0xFFEF4444)
    }

    val badgeBg = badgeColor.copy(alpha = 0.12f)
    val labelText = when {
        isHighConfidence -> "High confidence"
        isMediumConfidence -> "Needs check"
        else -> "Uncertain"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(badgeBg)
            .border(1.dp, badgeColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(badgeColor)
        )
        Text(
            text = if (showLabel) "$percentage% $labelText" else "$percentage%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = badgeColor
        )
    }
}

@Composable
fun ModeGridSelector(
    selectedMode: TravelMode,
    onModeSelected: (TravelMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = TravelMode.values()

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(modes) { mode ->
            val isSelected = mode == selectedMode
            val borderColor = if (isSelected) mode.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            val bgColor = if (isSelected) mode.color.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(16.dp))
                    .clickable { onModeSelected(mode) }
                    .testTag("mode_option_${mode.name.lowercase()}"),
                colors = CardDefaults.cardColors(containerColor = bgColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = mode.emoji,
                        fontSize = 26.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) mode.color else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun ModeChipRow(
    selectedMode: TravelMode,
    onModeSelected: (TravelMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TravelMode.values().take(5).forEach { mode ->
            val isSelected = mode == selectedMode
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onModeSelected(mode) }
                    .testTag("mode_chip_${mode.name.lowercase()}"),
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) mode.color else MaterialTheme.colorScheme.surfaceVariant,
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = mode.emoji, fontSize = 14.sp)
                    Text(
                        text = mode.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun JourneyChainVisualizer(
    modes: List<TravelMode>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        modes.forEachIndexed { index, mode ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(mode.color.copy(alpha = 0.12f))
                        .border(1.dp, mode.color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(text = mode.emoji, fontSize = 11.sp)
                        Text(
                            text = mode.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = mode.color
                        )
                    }
                }
                if (index < modes.size - 1) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
