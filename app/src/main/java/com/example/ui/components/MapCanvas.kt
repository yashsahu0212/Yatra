package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TravelMode
import com.example.ui.theme.ModeAutoYellow
import com.example.ui.theme.ModeBikePurple
import com.example.ui.theme.ModeBusBlue
import com.example.ui.theme.ModeCarOrange
import com.example.ui.theme.ModeTrainIndigo
import com.example.ui.theme.ModeWalkGreen

data class DynamicMapStyleRules(
    val ambientBgColor: Color,
    val ambientTintGradient: List<Color>,
    val gridColor: Color,
    val gridStrokeWidth: Float,
    val arterialRoadColor: Color,
    val arterialStrokeWidth: Float,
    val primaryRouteColor: Color,
    val primaryRouteGlowColor: Color,
    val primaryRouteStrokeWidth: Float,
    val isDashedRoute: Boolean,
    val dashPattern: FloatArray,
    val showBusStops: Boolean,
    val showRailwaySleepers: Boolean,
    val showPedestrianTrails: Boolean,
    val showCycleTracks: Boolean,
    val styleSummary: String
)

fun getDynamicMapStyleRules(mode: TravelMode, isDark: Boolean): DynamicMapStyleRules {
    return when (mode) {
        TravelMode.WALK -> DynamicMapStyleRules(
            ambientBgColor = if (isDark) Color(0xFF091B14) else Color(0xFFF1FAF4),
            ambientTintGradient = if (isDark) listOf(Color(0x3310B981), Color(0x0510B981), Color.Transparent)
            else listOf(Color(0x2210B981), Color(0x0C10B981), Color.Transparent),
            gridColor = if (isDark) Color(0xFF162D24) else Color(0xFFD8EEDB),
            gridStrokeWidth = 1.6f,
            arterialRoadColor = if (isDark) Color(0xFF224436) else Color(0xFFC7E4CD),
            arterialStrokeWidth = 3.8f,
            primaryRouteColor = Color(0xFF10B981),
            primaryRouteGlowColor = Color(0x4010B981),
            primaryRouteStrokeWidth = 4.6f,
            isDashedRoute = true,
            dashPattern = floatArrayOf(8f, 6f),
            showBusStops = false,
            showRailwaySleepers = false,
            showPedestrianTrails = true,
            showCycleTracks = false,
            styleSummary = "Pedestrian Focus · Greener tint & lighter routes"
        )
        TravelMode.BUS -> DynamicMapStyleRules(
            ambientBgColor = if (isDark) Color(0xFF0B172E) else Color(0xFFF0F6FF),
            ambientTintGradient = if (isDark) listOf(Color(0x332D5BFF), Color(0x082D5BFF), Color.Transparent)
            else listOf(Color(0x202D5BFF), Color(0x082D5BFF), Color.Transparent),
            gridColor = if (isDark) Color(0xFF1A2E4E) else Color(0xFFD6E3F8),
            gridStrokeWidth = 2.4f,
            arterialRoadColor = if (isDark) Color(0xFF2A4A7F) else Color(0xFFBDD4F5),
            arterialStrokeWidth = 6.8f,
            primaryRouteColor = Color(0xFF2D5BFF),
            primaryRouteGlowColor = Color(0x442D5BFF),
            primaryRouteStrokeWidth = 7.6f,
            isDashedRoute = false,
            dashPattern = floatArrayOf(),
            showBusStops = true,
            showRailwaySleepers = false,
            showPedestrianTrails = false,
            showCycleTracks = false,
            styleSummary = "Transit Focus · Bold blue arterial corridors & stops"
        )
        TravelMode.TRAIN -> DynamicMapStyleRules(
            ambientBgColor = if (isDark) Color(0xFF0F142D) else Color(0xFFF3F3FE),
            ambientTintGradient = if (isDark) listOf(Color(0x336366F1), Color(0x086366F1), Color.Transparent)
            else listOf(Color(0x206366F1), Color(0x086366F1), Color.Transparent),
            gridColor = if (isDark) Color(0xFF21294F) else Color(0xFFDCDAF7),
            gridStrokeWidth = 2.2f,
            arterialRoadColor = if (isDark) Color(0xFF333E78) else Color(0xFFC7C3F2),
            arterialStrokeWidth = 5.5f,
            primaryRouteColor = Color(0xFF6366F1),
            primaryRouteGlowColor = Color(0x446366F1),
            primaryRouteStrokeWidth = 6.4f,
            isDashedRoute = false,
            dashPattern = floatArrayOf(),
            showBusStops = false,
            showRailwaySleepers = true,
            showPedestrianTrails = false,
            showCycleTracks = false,
            styleSummary = "Rail Focus · Cross-tie tracks & station nodes"
        )
        TravelMode.BICYCLE, TravelMode.BIKE -> DynamicMapStyleRules(
            ambientBgColor = if (isDark) Color(0xFF08221C) else Color(0xFFF0FDF8),
            ambientTintGradient = if (isDark) listOf(Color(0x330D9488), Color(0x080D9488), Color.Transparent)
            else listOf(Color(0x200D9488), Color(0x080D9488), Color.Transparent),
            gridColor = if (isDark) Color(0xFF143B32) else Color(0xFFD1F0E6),
            gridStrokeWidth = 2.0f,
            arterialRoadColor = if (isDark) Color(0xFF1E5649) else Color(0xFFB7E6D7),
            arterialStrokeWidth = 4.8f,
            primaryRouteColor = Color(0xFF0D9488),
            primaryRouteGlowColor = Color(0x400D9488),
            primaryRouteStrokeWidth = 5.2f,
            isDashedRoute = true,
            dashPattern = floatArrayOf(12f, 5f),
            showBusStops = false,
            showRailwaySleepers = false,
            showPedestrianTrails = false,
            showCycleTracks = true,
            styleSummary = "Cycle Focus · Dedicated agile bike corridors"
        )
        TravelMode.CAR, TravelMode.AUTO, TravelMode.OTHER -> DynamicMapStyleRules(
            ambientBgColor = if (isDark) Color(0xFF1C160B) else Color(0xFFFFFBF0),
            ambientTintGradient = if (isDark) listOf(Color(0x33F59E0B), Color(0x08F59E0B), Color.Transparent)
            else listOf(Color(0x20F59E0B), Color(0x08F59E0B), Color.Transparent),
            gridColor = if (isDark) Color(0xFF332A17) else Color(0xFFF5E8C9),
            gridStrokeWidth = 2.6f,
            arterialRoadColor = if (isDark) Color(0xFF554422) else Color(0xFFEBD49F),
            arterialStrokeWidth = 7.0f,
            primaryRouteColor = Color(0xFFF59E0B),
            primaryRouteGlowColor = Color(0x44F59E0B),
            primaryRouteStrokeWidth = 6.8f,
            isDashedRoute = false,
            dashPattern = floatArrayOf(),
            showBusStops = false,
            showRailwaySleepers = false,
            showPedestrianTrails = false,
            showCycleTracks = false,
            styleSummary = "Roadway Focus · Multi-lane avenues & intersections"
        )
    }
}

@Composable
fun InteractiveMapCanvas(
    modifier: Modifier = Modifier,
    isRecordingLive: Boolean = false,
    activeMode: TravelMode = TravelMode.BUS,
    showDetailedRoute: Boolean = true,
    showHeatmapOrTransferNodes: Boolean = true,
    selectedSegmentIndex: Int = -1,
    originLabel: String = "Home",
    destinationLabel: String = "College",
    showModeSelectorBar: Boolean = false,
    onModeSelected: ((TravelMode) -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    var selectedOverrideMode by remember { mutableStateOf<TravelMode?>(null) }
    val effectiveMode = selectedOverrideMode ?: activeMode

    val styleRules = remember(effectiveMode, isDark) {
        getDynamicMapStyleRules(effectiveMode, isDark)
    }

    val animatedBgColor by animateColorAsState(
        targetValue = styleRules.ambientBgColor,
        animationSpec = tween(600),
        label = "map_bg_color"
    )

    // Animation for pulsing user location radar
    val infiniteTransition = rememberInfiniteTransition(label = "map_radar")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radar"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    // Live vehicle progression along the route
    val liveVehicleProgression by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vehicle_progress"
    )

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .background(animatedBgColor)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.7f, 2.5f)
                    offset = Offset(
                        (offset.x + pan.x).coerceIn(-300f, 300f),
                        (offset.y + pan.y).coerceIn(-300f, 300f)
                    )
                }
            }
            .testTag("interactive_map_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val centerX = width / 2f + offset.x
            val centerY = height / 2f + offset.y

            // Draw base map road grid and urban block textures with dynamic mode styling
            drawDynamicMapBase(
                w = width,
                h = height,
                cx = centerX,
                cy = centerY,
                scale = scale,
                styleRules = styleRules,
                isDark = isDark
            )

            // Draw POIs (Bus Stops, Rail tracks, Crosswalks, Cycleways)
            drawDynamicPois(
                w = width,
                h = height,
                cx = centerX,
                cy = centerY,
                scale = scale,
                styleRules = styleRules,
                isDark = isDark
            )

            if (showDetailedRoute) {
                // Draw Multimodal Journey Polyline with dynamic line weight & mode adaptation
                drawDynamicMultimodalJourney(
                    width = width,
                    height = height,
                    centerX = centerX,
                    centerY = centerY,
                    scale = scale,
                    isRecording = isRecordingLive,
                    activeMode = effectiveMode,
                    styleRules = styleRules,
                    vehicleProgress = if (isRecordingLive) liveVehicleProgression else 1f,
                    pulseRadius = pulseRadius,
                    pulseAlpha = pulseAlpha,
                    isDark = isDark
                )
            }
        }

        // Top Subtle Dynamic Map Style Badge (Floating)
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            shape = RoundedCornerShape(20.dp),
            color = if (isDark) Color(0xDD1E293B) else Color(0xDDFFFFFF),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                effectiveMode.color.copy(alpha = 0.35f)
            ),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(effectiveMode.color)
                )
                Text(
                    text = styleRules.styleSummary,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF334155)
                )
            }
        }

        // Optional Mode Style Selector / Switcher Bar
        if (showModeSelectorBar) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 12.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isDark) Color(0xEA1F2937) else Color(0xEAFFFFFF),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isDark) Color(0x334B5563) else Color(0xFFE5E7EB)
                ),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val modes = listOf(TravelMode.WALK, TravelMode.BUS, TravelMode.TRAIN, TravelMode.BICYCLE, TravelMode.CAR)
                    modes.forEach { mode ->
                        val isSelected = effectiveMode == mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) mode.color.copy(alpha = 0.18f) else Color.Transparent
                                )
                                .border(
                                    width = if (isSelected) 1.dp else 0.dp,
                                    color = if (isSelected) mode.color else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedOverrideMode = mode
                                    onModeSelected?.invoke(mode)
                                }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${mode.emoji} ${mode.displayName}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) mode.color else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Map overlay controls (Recenter, Zoom)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            shape = CircleShape,
            color = if (isDark) Color(0xFF1E293B) else Color.White,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
            ),
            shadowElevation = 4.dp
        ) {
            IconButton(
                onClick = {
                    scale = 1f
                    offset = Offset.Zero
                },
                modifier = Modifier.size(38.dp).testTag("recenter_map_button")
            ) {
                Icon(
                    imageVector = Icons.Default.GpsFixed,
                    contentDescription = "Recenter Map",
                    tint = effectiveMode.color,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun DrawScope.drawDynamicMapBase(
    w: Float,
    h: Float,
    cx: Float,
    cy: Float,
    scale: Float,
    styleRules: DynamicMapStyleRules,
    isDark: Boolean
) {
    // Ambient radial glow reflecting travel mode
    drawCircle(
        brush = Brush.radialGradient(
            colors = styleRules.ambientTintGradient,
            center = Offset(cx, cy),
            radius = w * 0.9f
        ),
        radius = w * 0.9f,
        center = Offset(cx, cy)
    )

    // Stylized urban parks & natural greenspaces
    val parkColor = if (isDark) Color(0x2410B981) else Color(0x2A10B981)
    val waterColor = if (isDark) Color(0x200284C7) else Color(0x200284C7)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(parkColor, Color.Transparent),
            center = Offset(cx - 180f * scale, cy - 140f * scale),
            radius = 170f * scale
        ),
        radius = 170f * scale,
        center = Offset(cx - 180f * scale, cy - 140f * scale)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(waterColor, Color.Transparent),
            center = Offset(cx + 240f * scale, cy + 220f * scale),
            radius = 210f * scale
        ),
        radius = 210f * scale,
        center = Offset(cx + 240f * scale, cy + 220f * scale)
    )

    // Dynamic Road Grid Lines with adapted stroke width & tint
    val step = 70f * scale
    val startX = (cx % step) - step
    val startY = (cy % step) - step

    var x = startX
    while (x < w + step) {
        drawLine(
            color = styleRules.gridColor,
            start = Offset(x, 0f),
            end = Offset(x, h),
            strokeWidth = styleRules.gridStrokeWidth * scale
        )
        x += step
    }

    var y = startY
    while (y < h + step) {
        drawLine(
            color = styleRules.gridColor,
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = styleRules.gridStrokeWidth * scale
        )
        y += step
    }

    // Arterial Avenue Corridors
    drawLine(
        color = styleRules.arterialRoadColor,
        start = Offset(cx - 320f * scale, cy - 260f * scale),
        end = Offset(cx + 360f * scale, cy + 310f * scale),
        strokeWidth = styleRules.arterialStrokeWidth * scale,
        cap = StrokeCap.Round
    )
    drawLine(
        color = styleRules.arterialRoadColor,
        start = Offset(cx - 330f * scale, cy + 190f * scale),
        end = Offset(cx + 350f * scale, cy - 230f * scale),
        strokeWidth = (styleRules.arterialStrokeWidth * 0.85f) * scale,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawDynamicPois(
    w: Float,
    h: Float,
    cx: Float,
    cy: Float,
    scale: Float,
    styleRules: DynamicMapStyleRules,
    isDark: Boolean
) {
    // 1. Bus Stops & Transit Shelters (prominent during BUS mode)
    if (styleRules.showBusStops) {
        val busStopPoints = listOf(
            Offset(cx - 90f * scale, cy - 80f * scale), // Transfer stop
            Offset(cx - 10f * scale, cy - 10f * scale), // Midpoint stop
            Offset(cx + 110f * scale, cy + 100f * scale) // Terminal stop
        )

        busStopPoints.forEachIndexed { idx, pt ->
            // Bus stop outer glow
            drawCircle(
                color = Color(0x332D5BFF),
                radius = 14f * scale,
                center = pt
            )
            // Bus stop shelter token
            drawCircle(
                color = if (isDark) Color(0xFF1E293B) else Color.White,
                radius = 9f * scale,
                center = pt
            )
            drawCircle(
                color = Color(0xFF2D5BFF),
                radius = 6.5f * scale,
                center = pt
            )
            // Stop label indicator
            drawRoundRect(
                color = Color(0xFF2D5BFF),
                topLeft = Offset(pt.x + 10f * scale, pt.y - 8f * scale),
                size = Size(26f * scale, 12f * scale),
                cornerRadius = CornerRadius(3f * scale, 3f * scale)
            )
        }
    }

    // 2. Railway Tracks & Sleepers (prominent during TRAIN mode)
    if (styleRules.showRailwaySleepers) {
        val railColor = if (isDark) Color(0xFF818CF8) else Color(0xFF6366F1)
        val railPath = Path().apply {
            moveTo(cx - 290f * scale, cy + 270f * scale)
            cubicTo(
                cx - 80f * scale, cy + 120f * scale,
                cx + 80f * scale, cy - 40f * scale,
                cx + 300f * scale, cy - 220f * scale
            )
        }

        // Draw cross-ties (sleepers)
        drawPath(
            path = railPath,
            color = railColor.copy(alpha = 0.8f),
            style = Stroke(
                width = 8f * scale,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f),
                cap = StrokeCap.Butt
            )
        )
        // Parallel track lines
        drawPath(
            path = railPath,
            color = railColor,
            style = Stroke(width = 2f * scale, cap = StrokeCap.Round)
        )

        // Station Hubs
        val stations = listOf(
            Offset(cx - 70f * scale, cy + 110f * scale),
            Offset(cx + 170f * scale, cy - 110f * scale)
        )
        stations.forEach { st ->
            drawCircle(color = Color(0xFF6366F1), radius = 10f * scale, center = st)
            drawCircle(color = Color.White, radius = 5f * scale, center = st)
        }
    }

    // 3. Pedestrian Walkways & Park Trails (prominent during WALK mode)
    if (styleRules.showPedestrianTrails) {
        val greenTrailColor = Color(0xFF10B981)
        val trail1 = Path().apply {
            moveTo(cx - 240f * scale, cy - 190f * scale)
            cubicTo(
                cx - 180f * scale, cy - 120f * scale,
                cx - 120f * scale, cy - 150f * scale,
                cx - 90f * scale, cy - 80f * scale
            )
        }
        drawPath(
            path = trail1,
            color = greenTrailColor.copy(alpha = 0.5f),
            style = Stroke(
                width = 3.5f * scale,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                cap = StrokeCap.Round
            )
        )

        // Crosswalk Zebra Stripes
        val crosswalkPt = Offset(cx - 90f * scale, cy - 80f * scale)
        for (i in -2..2) {
            drawLine(
                color = if (isDark) Color(0xFFE2E8F0) else Color.White,
                start = Offset(crosswalkPt.x + i * 4f * scale - 6f * scale, crosswalkPt.y - 8f * scale),
                end = Offset(crosswalkPt.x + i * 4f * scale + 6f * scale, crosswalkPt.y - 8f * scale),
                strokeWidth = 2.5f * scale
            )
        }
    }

    // 4. Dedicated Cycleways (prominent during BICYCLE mode)
    if (styleRules.showCycleTracks) {
        val bikeTrackColor = Color(0xFF0D9488)
        val bikePath = Path().apply {
            moveTo(cx - 300f * scale, cy - 250f * scale)
            lineTo(cx + 340f * scale, cy + 290f * scale)
        }
        drawPath(
            path = bikePath,
            color = bikeTrackColor.copy(alpha = 0.6f),
            style = Stroke(
                width = 4f * scale,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 6f), 0f),
                cap = StrokeCap.Round
            )
        )
    }
}

private fun DrawScope.drawDynamicMultimodalJourney(
    width: Float,
    height: Float,
    centerX: Float,
    centerY: Float,
    scale: Float,
    isRecording: Boolean,
    activeMode: TravelMode,
    styleRules: DynamicMapStyleRules,
    vehicleProgress: Float,
    pulseRadius: Float,
    pulseAlpha: Float,
    isDark: Boolean
) {
    // Defined Waypoint Coordinates relative to center
    val originP = Offset(centerX - 130f * scale, centerY - 160f * scale) // Home (Start)
    val transfer1 = Offset(centerX - 90f * scale, centerY - 80f * scale) // Walk to Bus stop
    val waypoint2 = Offset(centerX + 20f * scale, centerY + 20f * scale) // Along arterial
    val transfer2 = Offset(centerX + 110f * scale, centerY + 100f * scale) // Bus terminal
    val destinationP = Offset(centerX + 140f * scale, centerY + 170f * scale) // College (Arrive)

    // Segment 1: WALKING (Emerald Green) - Dotted / Dashed
    val walkPath1 = Path().apply {
        moveTo(originP.x, originP.y)
        lineTo(transfer1.x, transfer1.y)
    }
    drawPath(
        path = walkPath1,
        color = ModeWalkGreen.copy(alpha = if (activeMode == TravelMode.WALK) 0.35f else 0.20f),
        style = Stroke(width = 12f * scale, cap = StrokeCap.Round)
    )
    drawPath(
        path = walkPath1,
        color = ModeWalkGreen,
        style = Stroke(
            width = if (activeMode == TravelMode.WALK) 5.5f * scale else 4.0f * scale,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
            cap = StrokeCap.Round
        )
    )

    // Segment 2: PRIMARY TRANSIT (Dynamic Color, Width & Style)
    val mainPath = Path().apply {
        moveTo(transfer1.x, transfer1.y)
        cubicTo(
            waypoint2.x - 30f * scale, waypoint2.y - 40f * scale,
            waypoint2.x + 20f * scale, waypoint2.y + 30f * scale,
            transfer2.x, transfer2.y
        )
    }

    // Outer glow
    drawPath(
        path = mainPath,
        color = styleRules.primaryRouteGlowColor,
        style = Stroke(width = (styleRules.primaryRouteStrokeWidth + 8f) * scale, cap = StrokeCap.Round)
    )

    // Main line
    if (styleRules.isDashedRoute) {
        drawPath(
            path = mainPath,
            color = styleRules.primaryRouteColor,
            style = Stroke(
                width = styleRules.primaryRouteStrokeWidth * scale,
                pathEffect = PathEffect.dashPathEffect(styleRules.dashPattern, 0f),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    } else {
        drawPath(
            path = mainPath,
            color = styleRules.primaryRouteColor,
            style = Stroke(
                width = styleRules.primaryRouteStrokeWidth * scale,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }

    // Segment 3: WALKING to Destination (Emerald Green)
    val walkPath2 = Path().apply {
        moveTo(transfer2.x, transfer2.y)
        lineTo(destinationP.x, destinationP.y)
    }
    drawPath(
        path = walkPath2,
        color = ModeWalkGreen.copy(alpha = if (activeMode == TravelMode.WALK) 0.35f else 0.20f),
        style = Stroke(width = 12f * scale, cap = StrokeCap.Round)
    )
    drawPath(
        path = walkPath2,
        color = ModeWalkGreen,
        style = Stroke(
            width = if (activeMode == TravelMode.WALK) 5.5f * scale else 4.0f * scale,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f),
            cap = StrokeCap.Round
        )
    )

    // Transfer Waypoint 1 Node
    drawCircle(
        color = if (isDark) Color(0xFF0F172A) else Color.White,
        radius = 8f * scale,
        center = transfer1
    )
    drawCircle(
        color = styleRules.primaryRouteColor,
        radius = 5.5f * scale,
        center = transfer1
    )

    // Transfer Waypoint 2 Node
    drawCircle(
        color = if (isDark) Color(0xFF0F172A) else Color.White,
        radius = 8f * scale,
        center = transfer2
    )
    drawCircle(
        color = ModeWalkGreen,
        radius = 5.5f * scale,
        center = transfer2
    )

    // Origin Marker (Home)
    drawCircle(
        color = ModeWalkGreen.copy(alpha = 0.2f),
        radius = 18f * scale,
        center = originP
    )
    drawCircle(
        color = ModeWalkGreen,
        radius = 9f * scale,
        center = originP
    )
    drawCircle(
        color = Color.White,
        radius = 4f * scale,
        center = originP
    )

    // Destination Marker (College Pin)
    drawCircle(
        color = ModeTrainIndigo.copy(alpha = 0.2f),
        radius = 20f * scale,
        center = destinationP
    )
    drawCircle(
        color = ModeTrainIndigo,
        radius = 10f * scale,
        center = destinationP
    )
    drawCircle(
        color = Color.White,
        radius = 4.5f * scale,
        center = destinationP
    )

    // Live Vehicle or Current Tracking Radar Point
    val currentTrackingPos = if (isRecording) {
        Offset(
            transfer1.x + (transfer2.x - transfer1.x) * vehicleProgress,
            transfer1.y + (transfer2.y - transfer1.y) * vehicleProgress
        )
    } else {
        transfer1
    }

    // Pulse radar ring around active vehicle with active mode color
    drawCircle(
        color = styleRules.primaryRouteColor.copy(alpha = pulseAlpha),
        radius = pulseRadius * scale,
        center = currentTrackingPos
    )
    drawCircle(
        color = Color.White,
        radius = 12f * scale,
        center = currentTrackingPos
    )
    drawCircle(
        color = styleRules.primaryRouteColor,
        radius = 8f * scale,
        center = currentTrackingPos
    )
    drawCircle(
        color = Color.White,
        radius = 3.5f * scale,
        center = currentTrackingPos
    )
}
