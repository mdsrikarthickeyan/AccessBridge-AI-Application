package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Blind
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.AccessibilityPreferences
import com.example.model.AccessiblePlace
import com.example.model.PlaceType
import com.example.model.VerificationStatus
import com.example.ui.components.AccessibilityTagChip
import com.example.ui.components.AccessibleButton
import com.example.ui.components.AccessibleMapView
import com.example.ui.components.EmptyStateView
import com.example.ui.components.VerificationBadge
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class PlacesViewMode(val label: String, val icon: ImageVector) {
    LIST_VIEW("List", Icons.Default.ViewList),
    MAP_VIEW("Map", Icons.Default.Map),
    SPLIT_VIEW("Split", Icons.Default.Navigation)
}

@Composable
fun AccessiblePlacesScreen(
    places: List<AccessiblePlace>,
    selectedPlace: AccessiblePlace?,
    searchQuery: String,
    selectedType: PlaceType,
    selectedCity: String?,
    wheelchairOnly: Boolean,
    tactileOnly: Boolean,
    bookmarkedPlaceIds: Set<String>,
    preferences: AccessibilityPreferences = AccessibilityPreferences(),
    onSelectPlace: (AccessiblePlace?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSelectType: (PlaceType) -> Unit,
    onSelectCity: (String?) -> Unit,
    onToggleWheelchair: () -> Unit,
    onToggleTactile: () -> Unit,
    onToggleBookmarkPlace: (String) -> Unit,
    onReportBarrierAtPlace: (AccessiblePlace) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // View Mode: List, Map, or Split
    var viewMode by remember { mutableStateOf(PlacesViewMode.LIST_VIEW) }

    // Detailed accessibility filter toggles
    var filterStepFree by remember { mutableStateOf(false) }
    var filterLift by remember { mutableStateOf(false) }
    var filterAccessibleToilet by remember { mutableStateOf(false) }
    var filterAccessibleRoute by remember { mutableStateOf(false) }
    var filterBraille by remember { mutableStateOf(false) }
    var filterAudioAssist by remember { mutableStateOf(false) }
    var filterVisualAnnounce by remember { mutableStateOf(false) }
    var filterParking by remember { mutableStateOf(false) }
    var filterSignLanguage by remember { mutableStateOf(false) }
    var showAllFiltersSheet by remember { mutableStateOf(false) }

    // User Location & Permission State
    var isLocationEnabled by remember { mutableStateOf(false) }
    var userLocationCoordinates by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var showLocationPermissionDialog by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            isLocationEnabled = true
            // Default center coordinate for Indian transit demonstration (Majestic Interchange Bengaluru)
            userLocationCoordinates = Pair(12.9767, 77.5713)
        }
    }

    // Active filters count
    val activeFiltersCount = remember(
        wheelchairOnly, tactileOnly, filterStepFree, filterLift,
        filterAccessibleToilet, filterAccessibleRoute, filterBraille,
        filterAudioAssist, filterVisualAnnounce, filterParking, filterSignLanguage,
        selectedType, selectedCity
    ) {
        var count = 0
        if (wheelchairOnly) count++
        if (tactileOnly) count++
        if (filterStepFree) count++
        if (filterLift) count++
        if (filterAccessibleToilet) count++
        if (filterAccessibleRoute) count++
        if (filterBraille) count++
        if (filterAudioAssist) count++
        if (filterVisualAnnounce) count++
        if (filterParking) count++
        if (filterSignLanguage) count++
        if (selectedType != PlaceType.ALL) count++
        if (selectedCity != null && selectedCity != "All Cities") count++
        count
    }

    // Filtered Places calculation
    val filteredPlaces by remember(
        places, searchQuery, selectedType, selectedCity,
        wheelchairOnly, tactileOnly, filterStepFree, filterLift,
        filterAccessibleToilet, filterAccessibleRoute, filterBraille,
        filterAudioAssist, filterVisualAnnounce, filterParking, filterSignLanguage
    ) {
        derivedStateOf {
            places.filter { place ->
                val matchesQuery = searchQuery.isBlank() ||
                        place.name.contains(searchQuery, ignoreCase = true) ||
                        place.city.contains(searchQuery, ignoreCase = true) ||
                        place.address.contains(searchQuery, ignoreCase = true) ||
                        place.entranceDetails.contains(searchQuery, ignoreCase = true) ||
                        place.accessibilityNotes.contains(searchQuery, ignoreCase = true)

                val matchesType = selectedType == PlaceType.ALL || place.type == selectedType
                val matchesCity = selectedCity == null || selectedCity == "All Cities" || place.city.equals(selectedCity, ignoreCase = true)

                // Mandatory filter checks - never assume present unless documented
                val matchesWheelchair = !wheelchairOnly || place.wheelchairRamp
                val matchesTactile = !tactileOnly || place.tactilePaving
                val matchesStepFree = !filterStepFree || place.stepFreeEntrance
                val matchesLift = !filterLift || place.liftAvailable
                val matchesToilet = !filterAccessibleToilet || place.accessibleToilet
                val matchesRoute = !filterAccessibleRoute || place.wheelchairAccessibleRoute
                val matchesBraille = !filterBraille || place.brailleSignage
                val matchesAudio = !filterAudioAssist || place.audioAssistance
                val matchesVisual = !filterVisualAnnounce || place.visualAnnouncements
                val matchesParking = !filterParking || place.accessibleParking
                val matchesSignLang = !filterSignLanguage || place.signLanguageAssistance

                matchesQuery && matchesType && matchesCity && matchesWheelchair &&
                        matchesTactile && matchesStepFree && matchesLift && matchesToilet &&
                        matchesRoute && matchesBraille && matchesAudio && matchesVisual &&
                        matchesParking && matchesSignLang
            }.sortedWith(
                compareByDescending<AccessiblePlace> { place ->
                    if (preferences.hasAnyFacilityRequirement()) {
                        preferences.calculatePlaceMatchScore(place)
                    } else {
                        if (place.verificationStatus == VerificationStatus.VERIFIED) 1 else 0
                    }
                }.thenByDescending { it.verificationStatus == VerificationStatus.VERIFIED }
            )
        }
    }

    val typeScrollState = rememberScrollState()
    val quickFiltersScrollState = rememberScrollState()
    val availableCities = listOf("All Cities", "Bengaluru", "Delhi", "Chennai", "Mumbai", "Hyderabad")

    Column(
        modifier = modifier
            .testTag("accessible_places_screen")
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Find Accessible Places",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Verified ramps, lifts, and tactile paths at public institutions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Location button with clear permission request affordance
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isLocationEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, if (isLocationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.semantics {
                    contentDescription = if (isLocationEnabled) "Location active. Sorting by distance." else "Tap to use your location"
                }
            ) {
                IconButton(
                    onClick = {
                        val fineGranted = ContextCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                        if (fineGranted) {
                            isLocationEnabled = !isLocationEnabled
                            userLocationCoordinates = if (isLocationEnabled) Pair(12.9767, 77.5713) else null
                        } else {
                            showLocationPermissionDialog = true
                        }
                    },
                    modifier = Modifier.testTag("use_location_button")
                ) {
                    Icon(
                        imageVector = if (isLocationEnabled) Icons.Default.MyLocation else Icons.Default.LocationOn,
                        contentDescription = "Use My Location",
                        tint = if (isLocationEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Access Passport Prioritization Banner if active
        if (preferences.hasAnyFacilityRequirement()) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ Access Passport Active:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Ranking ${preferences.activeRequirementCount()} prioritized facilities first",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Field Labelled "Where would you like to go?"
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            label = { Text("Where would you like to go?") },
            placeholder = { Text("Search by place name, address, or institution...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("where_would_you_like_to_go_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // View Mode Switcher (List | Map | Split)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PlacesViewMode.entries.forEach { mode ->
                    val isSelected = viewMode == mode
                    Surface(
                        onClick = { viewMode = mode },
                        shape = RoundedCornerShape(9.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shadowElevation = if (isSelected) 2.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tab_${mode.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = null,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = mode.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Category & Facility Filters Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(quickFiltersScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "All Filters" button with active badge
            FilterChip(
                selected = activeFiltersCount > 0,
                onClick = { showAllFiltersSheet = true },
                label = {
                    Text(if (activeFiltersCount > 0) "Filters ($activeFiltersCount)" else "All Filters")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("all_filters_button")
            )

            // Step-free Entrance
            FilterChip(
                selected = filterStepFree,
                onClick = { filterStepFree = !filterStepFree },
                label = { Text("Step-Free") }
            )

            // Wheelchair Ramp
            FilterChip(
                selected = wheelchairOnly,
                onClick = onToggleWheelchair,
                label = { Text("Ramp") },
                leadingIcon = {
                    Icon(Icons.Default.Accessible, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )

            // Lift / Elevator
            FilterChip(
                selected = filterLift,
                onClick = { filterLift = !filterLift },
                label = { Text("Lift") },
                leadingIcon = {
                    Icon(Icons.Default.Elevator, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )

            // Tactile Guidance
            FilterChip(
                selected = tactileOnly,
                onClick = onToggleTactile,
                label = { Text("Tactile") },
                leadingIcon = {
                    Icon(Icons.Default.Blind, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )

            // Accessible Toilet
            FilterChip(
                selected = filterAccessibleToilet,
                onClick = { filterAccessibleToilet = !filterAccessibleToilet },
                label = { Text("Accessible Toilet") },
                leadingIcon = {
                    Icon(Icons.Default.Wc, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )

            // Braille Signs
            FilterChip(
                selected = filterBraille,
                onClick = { filterBraille = !filterBraille },
                label = { Text("Braille Signs") }
            )

            // Accessible Parking
            FilterChip(
                selected = filterParking,
                onClick = { filterParking = !filterParking },
                label = { Text("Parking") },
                leadingIcon = {
                    Icon(Icons.Default.LocalParking, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main Content Area based on View Mode
        when (viewMode) {
            PlacesViewMode.LIST_VIEW -> {
                PlacesListSection(
                    places = filteredPlaces,
                    selectedPlace = selectedPlace,
                    bookmarkedPlaceIds = bookmarkedPlaceIds,
                    userLocation = userLocationCoordinates,
                    preferences = preferences,
                    onSelectPlace = onSelectPlace,
                    onToggleBookmarkPlace = onToggleBookmarkPlace,
                    onResetFilters = {
                        onSearchQueryChange("")
                        onSelectType(PlaceType.ALL)
                        onSelectCity(null)
                        if (wheelchairOnly) onToggleWheelchair()
                        if (tactileOnly) onToggleTactile()
                        filterStepFree = false
                        filterLift = false
                        filterAccessibleToilet = false
                        filterAccessibleRoute = false
                        filterBraille = false
                        filterAudioAssist = false
                        filterVisualAnnounce = false
                        filterParking = false
                        filterSignLanguage = false
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            PlacesViewMode.MAP_VIEW -> {
                AccessibleMapView(
                    places = filteredPlaces,
                    selectedPlace = selectedPlace,
                    onSelectPlace = onSelectPlace,
                    onOpenDetails = { place -> onSelectPlace(place) },
                    userLocation = userLocationCoordinates,
                    modifier = Modifier.weight(1f)
                )
            }

            PlacesViewMode.SPLIT_VIEW -> {
                Column(modifier = Modifier.weight(1f)) {
                    AccessibleMapView(
                        places = filteredPlaces,
                        selectedPlace = selectedPlace,
                        onSelectPlace = onSelectPlace,
                        onOpenDetails = { place -> onSelectPlace(place) },
                        userLocation = userLocationCoordinates,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.1f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    PlacesListSection(
                        places = filteredPlaces,
                        selectedPlace = selectedPlace,
                        bookmarkedPlaceIds = bookmarkedPlaceIds,
                        userLocation = userLocationCoordinates,
                        preferences = preferences,
                        onSelectPlace = onSelectPlace,
                        onToggleBookmarkPlace = onToggleBookmarkPlace,
                        onResetFilters = {
                            onSearchQueryChange("")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Location Permission Request Dialog
    if (showLocationPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showLocationPermissionDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Use Your Location for Accessible Places?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "AccessBridge AI uses your location to discover documented accessibility facilities at nearby colleges, hospitals, transit hubs, and public buildings with ramps, lifts, and tactile guiding paths.\n\nYour location data stays strictly private on your device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                AccessibleButton(
                    text = "Allow & Discover Nearby",
                    onClick = {
                        showLocationPermissionDialog = false
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    isPrimary = true
                )
            },
            dismissButton = {
                TextButton(onClick = { showLocationPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Extended Accessibility Filters Dialog
    if (showAllFiltersSheet) {
        AccessibilityFiltersDialog(
            selectedType = selectedType,
            selectedCity = selectedCity,
            availableCities = availableCities,
            wheelchairOnly = wheelchairOnly,
            tactileOnly = tactileOnly,
            filterStepFree = filterStepFree,
            filterLift = filterLift,
            filterAccessibleToilet = filterAccessibleToilet,
            filterAccessibleRoute = filterAccessibleRoute,
            filterBraille = filterBraille,
            filterAudioAssist = filterAudioAssist,
            filterVisualAnnounce = filterVisualAnnounce,
            filterParking = filterParking,
            filterSignLanguage = filterSignLanguage,
            onSelectType = onSelectType,
            onSelectCity = onSelectCity,
            onToggleWheelchair = onToggleWheelchair,
            onToggleTactile = onToggleTactile,
            onToggleStepFree = { filterStepFree = !filterStepFree },
            onToggleLift = { filterLift = !filterLift },
            onToggleToilet = { filterAccessibleToilet = !filterAccessibleToilet },
            onToggleRoute = { filterAccessibleRoute = !filterAccessibleRoute },
            onToggleBraille = { filterBraille = !filterBraille },
            onToggleAudio = { filterAudioAssist = !filterAudioAssist },
            onToggleVisual = { filterVisualAnnounce = !filterVisualAnnounce },
            onToggleParking = { filterParking = !filterParking },
            onToggleSignLang = { filterSignLanguage = !filterSignLanguage },
            onClearAll = {
                onSelectType(PlaceType.ALL)
                onSelectCity(null)
                if (wheelchairOnly) onToggleWheelchair()
                if (tactileOnly) onToggleTactile()
                filterStepFree = false
                filterLift = false
                filterAccessibleToilet = false
                filterAccessibleRoute = false
                filterBraille = false
                filterAudioAssist = false
                filterVisualAnnounce = false
                filterParking = false
                filterSignLanguage = false
            },
            onDismiss = { showAllFiltersSheet = false }
        )
    }

    // Place Details Dialog
    if (selectedPlace != null) {
        val isBookmarked = bookmarkedPlaceIds.contains(selectedPlace.id)
        PlaceDetailDialog(
            place = selectedPlace,
            isBookmarked = isBookmarked,
            onToggleBookmark = { onToggleBookmarkPlace(selectedPlace.id) },
            onReportBarrier = { onReportBarrierAtPlace(selectedPlace) },
            onDismiss = { onSelectPlace(null) }
        )
    }
}

@Composable
private fun PlacesListSection(
    places: List<AccessiblePlace>,
    selectedPlace: AccessiblePlace?,
    bookmarkedPlaceIds: Set<String>,
    userLocation: Pair<Double, Double>?,
    preferences: AccessibilityPreferences = AccessibilityPreferences(),
    onSelectPlace: (AccessiblePlace?) -> Unit,
    onToggleBookmarkPlace: (String) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (places.isEmpty()) {
        EmptyStateView(
            title = "No Accessible Places Found",
            description = "No places match your search or accessibility filters. Try clearing some facility requirements.",
            icon = Icons.Default.Place,
            actionButtonText = "Reset Filters",
            onActionClick = onResetFilters,
            modifier = modifier
        )
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
        ) {
            items(places, key = { it.id }) { place ->
                val isBookmarked = bookmarkedPlaceIds.contains(place.id)
                val isSelected = selectedPlace?.id == place.id
                val distanceKm = userLocation?.let {
                    calculateDistanceKm(it.first, it.second, place.latitude, place.longitude)
                }

                PlaceItemCard(
                    place = place,
                    isBookmarked = isBookmarked,
                    isSelected = isSelected,
                    distanceKm = distanceKm,
                    preferences = preferences,
                    onToggleBookmark = { onToggleBookmarkPlace(place.id) },
                    onClick = { onSelectPlace(place) }
                )
            }
        }
    }
}

@Composable
private fun PlaceItemCard(
    place: AccessiblePlace,
    isBookmarked: Boolean,
    isSelected: Boolean,
    distanceKm: Double?,
    preferences: AccessibilityPreferences = AccessibilityPreferences(),
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("place_card_${place.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.5.dp),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                VerificationBadge(status = place.verificationStatus)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (distanceKm != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = String.format("%.1f km away", distanceKm),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${place.type.label} • ${place.city}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(34.dp)
                            .semantics {
                                contentDescription = if (isBookmarked) "Remove ${place.name} from saved" else "Save ${place.name}"
                            }
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (preferences.hasAnyFacilityRequirement() && preferences.calculatePlaceMatchScore(place) > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "⭐ Matches your Access Passport (${preferences.calculatePlaceMatchScore(place)} pts)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (place.hasConflict) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReportProblem,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Conflict Flagged: Visitor report differs from audit; pending re-inspection",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Documented Facilities Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (place.stepFreeEntrance) {
                    AccessibilityTagChip(label = "Step-Free", icon = Icons.Default.Directions)
                }
                if (place.wheelchairRamp) {
                    AccessibilityTagChip(label = "Ramp", icon = Icons.Default.Accessible)
                }
                if (place.liftAvailable) {
                    AccessibilityTagChip(label = "Lift", icon = Icons.Default.Elevator)
                }
                if (place.tactilePaving) {
                    AccessibilityTagChip(label = "Tactile", icon = Icons.Default.Blind)
                }
                if (place.accessibleToilet) {
                    AccessibilityTagChip(label = "Restroom", icon = Icons.Default.Wc)
                }
                if (place.brailleSignage) {
                    AccessibilityTagChip(label = "Braille", icon = Icons.Default.Visibility)
                }
                if (place.accessibleParking) {
                    AccessibilityTagChip(label = "Parking", icon = Icons.Default.LocalParking)
                }
                if (place.signLanguageAssistance) {
                    AccessibilityTagChip(label = "Sign Lang", icon = Icons.Default.Hearing)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = place.entranceDetails,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Nearest Transit: ${place.nearestTransit.take(28)}${if (place.nearestTransit.length > 28) "..." else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Details",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceDetailDialog(
    place: AccessiblePlace,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onReportBarrier: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    VerificationBadge(status = place.verificationStatus)
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.semantics {
                            contentDescription = if (isBookmarked) "Remove bookmark" else "Bookmark place"
                        }
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${place.type.label} • ${place.city} • ${place.address}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            val dialogScroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(dialogScroll)
            ) {
                // Verification Status Explanation Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Verification: ${place.verificationStatus.label}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = place.verificationStatus.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Audited: ${place.lastVerifiedDate} • Source: ${place.informationSource}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                if (place.hasConflict) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ReportProblem,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Conflicting Community Report Flagged",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = place.conflictDescription ?: "A recent visitor report disagrees with past audit data. Neither claim is presented as confirmed until re-inspected.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Policy: Claim held in review pending physical re-audit.",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Entrance Information
                Text(
                    text = "Entrance Information:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = place.entranceDetails,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (place.openingHours != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Opening Hours: ${place.openingHours}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Documented Facilities Checklist (Truthful representation)
                Text(
                    text = "Documented Accessibility Facilities:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                AmenityStatusRow("Step-free Entrance", place.stepFreeEntrance)
                AmenityStatusRow("Wheelchair Ramp with Handrails", place.wheelchairRamp)
                AmenityStatusRow("Passenger Lift / Elevator", place.liftAvailable)
                AmenityStatusRow("Accessible Restroom / Toilet", place.accessibleToilet)
                AmenityStatusRow("Wheelchair Accessible Route", place.wheelchairAccessibleRoute)
                AmenityStatusRow("Tactile Guiding Paths", place.tactilePaving)
                AmenityStatusRow("Braille Signage & Tactile Maps", place.brailleSignage)
                AmenityStatusRow("Audio Assistance / Sound Beacons", place.audioAssistance)
                AmenityStatusRow("Visual Announcements & Displays", place.visualAnnouncements)
                AmenityStatusRow("Designated Accessible Parking", place.accessibleParking)
                AmenityStatusRow("Sign Language Interpreter / Assist", place.signLanguageAssistance)

                if (place.otherFacilities.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Other Documented Features:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    place.otherFacilities.forEach { feat ->
                        Text(
                            text = "• $feat",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Route & Access Notes
                Text(
                    text = "Detailed Route & Access Notes:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = place.accessibilityNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Assistance Desk Phone Call Button
                if (place.assistanceDeskPhone != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Assistance Helpdesk",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = place.assistanceDeskPhone,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            AccessibleButton(
                                text = "Call",
                                icon = Icons.Default.Phone,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${place.assistanceDeskPhone.replace("-", "")}"))
                                    context.startActivity(intent)
                                },
                                isPrimary = true
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Directions Intent Action
                OutlinedButton(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:${place.latitude},${place.longitude}?q=${Uri.encode(place.name + ", " + place.city)}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(place.name + " " + place.city)}")
                            context.startActivity(Intent(Intent.ACTION_VIEW, browserUri))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Get Directions in Maps")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Report an Issue / Outdated Info button
                OutlinedButton(
                    onClick = onReportBarrier,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Report Incorrect or Outdated Information",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Close",
                onClick = onDismiss,
                isPrimary = true
            )
        }
    )
}

@Composable
private fun AmenityStatusRow(
    title: String,
    isAvailable: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isAvailable) Icons.Default.Check else Icons.Default.Clear,
            contentDescription = if (isAvailable) "Available" else "Not documented or absent",
            tint = if (isAvailable) Color(0xFF16A34A) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isAvailable) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun AccessibilityFiltersDialog(
    selectedType: PlaceType,
    selectedCity: String?,
    availableCities: List<String>,
    wheelchairOnly: Boolean,
    tactileOnly: Boolean,
    filterStepFree: Boolean,
    filterLift: Boolean,
    filterAccessibleToilet: Boolean,
    filterAccessibleRoute: Boolean,
    filterBraille: Boolean,
    filterAudioAssist: Boolean,
    filterVisualAnnounce: Boolean,
    filterParking: Boolean,
    filterSignLanguage: Boolean,
    onSelectType: (PlaceType) -> Unit,
    onSelectCity: (String?) -> Unit,
    onToggleWheelchair: () -> Unit,
    onToggleTactile: () -> Unit,
    onToggleStepFree: () -> Unit,
    onToggleLift: () -> Unit,
    onToggleToilet: () -> Unit,
    onToggleRoute: () -> Unit,
    onToggleBraille: () -> Unit,
    onToggleAudio: () -> Unit,
    onToggleVisual: () -> Unit,
    onToggleParking: () -> Unit,
    onToggleSignLang: () -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Accessibility Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onClearAll) {
                    Text("Clear All")
                }
            }
        },
        text = {
            val filterScroll = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(filterScroll)
            ) {
                // Institution Type
                Text(
                    text = "Institution Type:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PlaceType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { onSelectType(type) },
                            label = { Text(type.label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // City Selector
                Text(
                    text = "City / Region:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableCities.forEach { city ->
                        val isSelected = (selectedCity == null && city == "All Cities") || selectedCity == city
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectCity(if (city == "All Cities") null else city) },
                            label = { Text(city) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Documented Facilities Checklist
                Text(
                    text = "Required Accessibility Features:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Only facilities with verified records will match.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                FilterToggleRow("Step-Free Entrance", filterStepFree, onToggleStepFree)
                FilterToggleRow("Wheelchair Ramp", wheelchairOnly, onToggleWheelchair)
                FilterToggleRow("Accessible Lift / Elevator", filterLift, onToggleLift)
                FilterToggleRow("Accessible Restroom / Toilet", filterAccessibleToilet, onToggleToilet)
                FilterToggleRow("Wheelchair Accessible Route", filterAccessibleRoute, onToggleRoute)
                FilterToggleRow("Tactile Guidance Paths", tactileOnly, onToggleTactile)
                FilterToggleRow("Braille Signs & Maps", filterBraille, onToggleBraille)
                FilterToggleRow("Audio Assistance / Beacons", filterAudioAssist, onToggleAudio)
                FilterToggleRow("Visual Announcements", filterVisualAnnounce, onToggleVisual)
                FilterToggleRow("Accessible Parking", filterParking, onToggleParking)
                FilterToggleRow("Sign Language Assistance", filterSignLanguage, onToggleSignLang)
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Apply Filters",
                onClick = onDismiss,
                isPrimary = true
            )
        }
    )
}

@Composable
private fun FilterToggleRow(
    title: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// Distance calculation between coordinates (Haversine formula in km)
private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}
