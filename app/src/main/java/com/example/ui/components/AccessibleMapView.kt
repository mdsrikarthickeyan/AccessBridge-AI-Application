package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccessiblePlace
import com.example.model.VerificationStatus
import com.example.ui.theme.AccessibleAmberContainer
import com.example.ui.theme.AccessibleBlueContainer
import com.example.ui.theme.AccessibleGreenContainer
import kotlin.math.roundToInt

@Composable
fun AccessibleMapView(
    places: List<AccessiblePlace>,
    selectedPlace: AccessiblePlace?,
    onSelectPlace: (AccessiblePlace?) -> Unit,
    onOpenDetails: (AccessiblePlace) -> Unit,
    userLocation: Pair<Double, Double>? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Zoom and pan state
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Bounds calculation based on places
    val (minLat, maxLat, minLng, maxLng) = remember(places) {
        if (places.isEmpty()) {
            listOf(12.0, 29.0, 72.0, 81.0)
        } else {
            val lats = places.map { it.latitude }
            val lngs = places.map { it.longitude }
            listOf(
                (lats.minOrNull() ?: 12.0) - 0.05,
                (lats.maxOrNull() ?: 29.0) + 0.05,
                (lngs.minOrNull() ?: 72.0) - 0.05,
                (lngs.maxOrNull() ?: 81.0) + 0.05
            )
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .testTag("accessible_map_view")
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                RoundedCornerShape(16.dp)
            )
            .background(Color(0xFFE8ECEF)) // OpenStreetMap style land neutral
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(0.6f, 3.5f)
                    panOffsetX += pan.x
                    panOffsetY += pan.y
                }
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // Background styled map canvas (OpenStreetMap / Cartographic style)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSpacing = 60.dp.toPx() * zoomScale

            // Draw cartographic grid roads
            val roadColor = Color(0xFFD4DADF)
            val majorRoadColor = Color(0xFFF9D689)
            val parkGreen = Color(0xFFD8EED8)
            val waterBlue = Color(0xFFC7E2F7)

            // Draw decorative park and water areas
            drawCircle(
                color = waterBlue,
                radius = 180.dp.toPx() * zoomScale,
                center = Offset(widthPx * 0.85f + panOffsetX, heightPx * 0.25f + panOffsetY)
            )
            drawCircle(
                color = parkGreen,
                radius = 140.dp.toPx() * zoomScale,
                center = Offset(widthPx * 0.15f + panOffsetX, heightPx * 0.7f + panOffsetY)
            )

            // Road network lines
            var x = (panOffsetX % gridSpacing)
            while (x < widthPx) {
                val isMajor = ((x / gridSpacing).toInt() % 3 == 0)
                drawLine(
                    color = if (isMajor) majorRoadColor else roadColor,
                    strokeWidth = if (isMajor) 4.dp.toPx() else 2.dp.toPx(),
                    start = Offset(x, 0f),
                    end = Offset(x, heightPx)
                )
                x += gridSpacing
            }

            var y = (panOffsetY % gridSpacing)
            while (y < heightPx) {
                val isMajor = ((y / gridSpacing).toInt() % 3 == 0)
                drawLine(
                    color = if (isMajor) majorRoadColor else roadColor,
                    strokeWidth = if (isMajor) 4.dp.toPx() else 2.dp.toPx(),
                    start = Offset(0f, y),
                    end = Offset(widthPx, y)
                )
                y += gridSpacing
            }

            // Transit / railway line dashed representation
            val metroLineColor = Color(0xFF2563EB)
            val metroPath = Path().apply {
                moveTo(0f, heightPx * 0.45f + panOffsetY)
                cubicTo(
                    widthPx * 0.35f, heightPx * 0.4f + panOffsetY,
                    widthPx * 0.65f, heightPx * 0.55f + panOffsetY,
                    widthPx, heightPx * 0.5f + panOffsetY
                )
            }
            drawPath(
                path = metroPath,
                color = metroLineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(16f, 10f), 0f
                    )
                )
            )
        }

        val density = LocalDensity.current
        val paddingX = with(density) { 50.dp.toPx() }
        val paddingY = with(density) { 60.dp.toPx() }

        // Map Pins for Places
        places.forEach { place ->
            val latRange = (maxLat - minLat).coerceAtLeast(0.001)
            val lngRange = (maxLng - minLng).coerceAtLeast(0.001)

            val normalizedX = ((place.longitude - minLng) / lngRange).toFloat()
            val normalizedY = (1.0f - ((place.latitude - minLat) / latRange).toFloat())

            val basePosX = normalizedX * (widthPx - paddingX * 2f) + paddingX
            val basePosY = normalizedY * (heightPx - paddingY * 2f) + paddingY

            val posX = (basePosX - widthPx / 2f) * zoomScale + (widthPx / 2f) + panOffsetX
            val posY = (basePosY - heightPx / 2f) * zoomScale + (heightPx / 2f) + panOffsetY

            val isSelected = selectedPlace?.id == place.id

            MapPinItem(
                place = place,
                isSelected = isSelected,
                modifier = Modifier
                    .offset { IntOffset(posX.roundToInt() - 24, posY.roundToInt() - 48) }
                    .testTag("map_pin_${place.id}"),
                onClick = {
                    onSelectPlace(if (isSelected) null else place)
                }
            )
        }

        // User Location Blue Pulsing Dot
        if (userLocation != null) {
            val userPosX = (widthPx * 0.5f) + panOffsetX
            val userPosY = (heightPx * 0.5f) + panOffsetY
            Box(
                modifier = Modifier
                    .offset { IntOffset(userPosX.roundToInt() - 14, userPosY.roundToInt() - 14) }
                    .size(28.dp)
                    .background(Color(0x332563EB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(Color(0xFF2563EB), CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }

        // Map Controls Overlay (Top Right: Zoom In, Zoom Out, Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column {
                    IconButton(
                        onClick = { zoomScale = (zoomScale * 1.25f).coerceAtMost(3.5f) },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Zoom In")
                    }
                    Box(
                        modifier = Modifier
                            .width(38.dp)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )
                    IconButton(
                        onClick = { zoomScale = (zoomScale / 1.25f).coerceAtLeast(0.6f) },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
                    }
                }
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                modifier = Modifier.size(38.dp)
            ) {
                IconButton(
                    onClick = {
                        zoomScale = 1.0f
                        panOffsetX = 0f
                        panOffsetY = 0f
                    }
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "Reset map center",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Map Attribution & Verification Legend (Top Left)
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LegendIndicator(Color(0xFF16A34A), "Verified")
                LegendIndicator(Color(0xFF2563EB), "Visitor")
                LegendIndicator(Color(0xFFD97706), "Unchecked")
                LegendIndicator(Color(0xFF64748B), "Outdated")
            }
        }

        // Selected Place Bottom Card Preview
        AnimatedVisibility(
            visible = selectedPlace != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            if (selectedPlace != null) {
                SelectedPlaceMapCard(
                    place = selectedPlace,
                    onOpenDetails = { onOpenDetails(selectedPlace) },
                    onDismiss = { onSelectPlace(null) },
                    onGetDirections = {
                        val gmmIntentUri = Uri.parse("geo:${selectedPlace.latitude},${selectedPlace.longitude}?q=${Uri.encode(selectedPlace.name + ", " + selectedPlace.city)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(selectedPlace.name + " " + selectedPlace.city)}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MapPinItem(
    place: AccessiblePlace,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val pinColor = when (place.verificationStatus) {
        VerificationStatus.VERIFIED -> Color(0xFF16A34A)
        VerificationStatus.REPORTED_BY_VISITOR -> Color(0xFF2563EB)
        VerificationStatus.NOT_YET_CHECKED -> Color(0xFFD97706)
        VerificationStatus.OUTDATED -> Color(0xFF64748B)
    }

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "Map marker for ${place.name}, ${place.verificationStatus.label}. Tap for facility details."
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Name pill tag when selected
        if (isSelected) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.inverseSurface,
                shadowElevation = 4.dp,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = place.name.take(24) + if (place.name.length > 24) "..." else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(if (isSelected) 44.dp else 34.dp)
                .shadow(elevation = if (isSelected) 8.dp else 4.dp, shape = CircleShape)
                .background(pinColor, CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(if (isSelected) 24.dp else 18.dp)
            )
        }
    }
}

@Composable
private fun LegendIndicator(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SelectedPlaceMapCard(
    place: AccessiblePlace,
    onOpenDetails: () -> Unit,
    onDismiss: () -> Unit,
    onGetDirections: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VerificationBadge(status = place.verificationStatus)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${place.type.label} • ${place.city}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close place preview",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = place.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = place.entranceDetails,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AccessibleButton(
                    text = "Directions",
                    icon = Icons.Default.Directions,
                    onClick = onGetDirections,
                    isPrimary = false,
                    modifier = Modifier.weight(1f)
                )

                AccessibleButton(
                    text = "View Facilities",
                    icon = Icons.Default.Info,
                    onClick = onOpenDetails,
                    isPrimary = true,
                    modifier = Modifier.weight(1.2f)
                )
            }
        }
    }
}
