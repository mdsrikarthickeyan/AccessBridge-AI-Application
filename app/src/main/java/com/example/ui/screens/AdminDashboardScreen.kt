package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AccessiblePlace
import com.example.model.BarrierCategory
import com.example.model.BarrierReport
import com.example.model.BarrierSeverity
import com.example.model.BarrierStatus
import com.example.model.PlaceType
import com.example.model.ReportStatusHistoryItem
import com.example.model.UserRole
import com.example.model.VerificationStatus
import com.example.ui.components.AccessibleButton
import com.example.ui.components.AccessibleConfirmationDialog
import com.example.ui.components.AccessibleTextField
import com.example.ui.components.BarrierProgressTimeline
import com.example.ui.components.BarrierStatusBadge
import com.example.ui.components.EmptyStateView
import com.example.ui.components.VerificationBadge
import com.example.ui.theme.AccessibleAmber
import com.example.ui.theme.AccessibleGreen
import com.example.ui.theme.AccessibleRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AdminTab(val title: String) {
    OVERVIEW("Overview"),
    NEW_REPORTS("New reports"),
    UNDER_REVIEW("Under review"),
    RESOLVED("Resolved"),
    FACILITIES("Facilities"),
    TRENDS("Accessibility trends")
}

@Composable
fun AdminDashboardScreen(
    reports: List<BarrierReport>,
    places: List<AccessiblePlace>,
    statusHistory: List<ReportStatusHistoryItem>,
    currentUserRole: UserRole,
    onSwitchRole: (UserRole) -> Unit,
    onUpdateReportStatus: (String, BarrierStatus, String, String, Boolean) -> Unit,
    onSaveFacility: (AccessiblePlace) -> Unit,
    onMarkOutdated: (String) -> Unit,
    onResolveConflict: (String, Boolean, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var reportToReview by remember { mutableStateOf<BarrierReport?>(null) }
    var facilityToEdit by remember { mutableStateOf<AccessiblePlace?>(null) }
    var showAddFacilityDialog by remember { mutableStateOf(false) }

    // Security check: If not admin or reviewer, show access restricted notice
    if (!currentUserRole.canReviewReports) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Administrator Access Required",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The Accessibility Improvement Dashboard is restricted to authorised participating institution administrators and certified access auditors. Switch role below to test administrator privileges.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccessibleButton(
                    text = "Switch to Reviewer",
                    onClick = { onSwitchRole(UserRole.ACCESSIBILITY_REVIEWER) },
                    isPrimary = true
                )
                AccessibleButton(
                    text = "Switch to Administrator",
                    onClick = { onSwitchRole(UserRole.SYSTEM_ADMIN) },
                    isPrimary = true
                )
            }
        }
        return
    }

    Column(
        modifier = modifier
            .testTag("admin_dashboard_screen")
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Accessibility Improvement Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Institution portal to identify, verify and address accessibility barriers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Role Indicator Banner
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Active Role: ${currentUserRole.roleTitle} (${currentUserRole.label})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextButton(onClick = { onSwitchRole(UserRole.PUBLIC_USER) }) {
                        Text("Switch to Public", style = MaterialTheme.typography.labelSmall)
                    }
                    TextButton(onClick = { onSwitchRole(UserRole.SYSTEM_ADMIN) }) {
                        Text("Admin Mode", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            AdminTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = tab.title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content Switching
        when (AdminTab.entries[selectedTabIndex]) {
            AdminTab.OVERVIEW -> {
                AdminOverviewSection(
                    reports = reports,
                    places = places,
                    onNavigateTab = { selectedTabIndex = it }
                )
            }
            AdminTab.NEW_REPORTS -> {
                val newReports = reports.filter { it.status == BarrierStatus.RECEIVED || it.status == BarrierStatus.WAITING_FOR_REVIEW }
                AdminReportsListSection(
                    reports = newReports,
                    sectionTitle = "New Reports Awaiting Review",
                    onReviewReport = { reportToReview = it }
                )
            }
            AdminTab.UNDER_REVIEW -> {
                val reviewReports = reports.filter { it.status == BarrierStatus.BEING_CHECKED }
                AdminReportsListSection(
                    reports = reviewReports,
                    sectionTitle = "Reports Under Physical Check / Review",
                    onReviewReport = { reportToReview = it }
                )
            }
            AdminTab.RESOLVED -> {
                val resolvedReports = reports.filter { it.status == BarrierStatus.RESOLVED || it.status == BarrierStatus.UNABLE_TO_VERIFY }
                AdminReportsListSection(
                    reports = resolvedReports,
                    sectionTitle = "Resolved & Verified Reports",
                    onReviewReport = { reportToReview = it }
                )
            }
            AdminTab.FACILITIES -> {
                AdminFacilitiesSection(
                    places = places,
                    onAddFacility = { showAddFacilityDialog = true },
                    onEditFacility = { facilityToEdit = it },
                    onMarkOutdated = onMarkOutdated,
                    onResolveConflict = onResolveConflict
                )
            }
            AdminTab.TRENDS -> {
                AdminTrendsSection(
                    reports = reports,
                    places = places
                )
            }
        }
    }

    // Review & Status Update Dialog
    if (reportToReview != null) {
        AdminReviewDialog(
            report = reportToReview!!,
            currentUserRole = currentUserRole,
            history = statusHistory.filter { it.reportId == reportToReview!!.id },
            onDismiss = { reportToReview = null },
            onUpdateStatus = { newStatus, reviewNote, actionTaken, flagConflict ->
                onUpdateReportStatus(reportToReview!!.id, newStatus, reviewNote, actionTaken, flagConflict)
                reportToReview = null
            }
        )
    }

    // Add / Edit Facility Dialog
    if (showAddFacilityDialog || facilityToEdit != null) {
        FacilityEditDialog(
            facility = facilityToEdit,
            onDismiss = {
                showAddFacilityDialog = false
                facilityToEdit = null
            },
            onSave = { updated ->
                onSaveFacility(updated)
                showAddFacilityDialog = false
                facilityToEdit = null
            }
        )
    }
}

@Composable
private fun AdminOverviewSection(
    reports: List<BarrierReport>,
    places: List<AccessiblePlace>,
    onNavigateTab: (Int) -> Unit
) {
    val totalReceived = reports.size
    val awaitingReview = reports.count { it.status == BarrierStatus.RECEIVED || it.status == BarrierStatus.WAITING_FOR_REVIEW }
    val beingChecked = reports.count { it.status == BarrierStatus.BEING_CHECKED }
    val resolvedCount = reports.count { it.status == BarrierStatus.RESOLVED }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Cards Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryMetricCard(
                title = "Total Reports",
                value = totalReceived.toString(),
                subtitle = "All stored records",
                icon = Icons.Default.ReportProblem,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(1) }
            )
            SummaryMetricCard(
                title = "Awaiting Review",
                value = awaitingReview.toString(),
                subtitle = "New intake queue",
                icon = Icons.Default.HourglassEmpty,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(1) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryMetricCard(
                title = "Being Checked",
                value = beingChecked.toString(),
                subtitle = "Site audit active",
                icon = Icons.Default.Assessment,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(2) }
            )
            SummaryMetricCard(
                title = "Resolved",
                value = resolvedCount.toString(),
                subtitle = "Barriers removed",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f),
                onClick = { onNavigateTab(3) }
            )
        }

        // Facilities with recurring reports & common categories
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Common Barrier Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (reports.isEmpty()) {
                    Text(
                        text = "No barrier report records found. Empty dataset active.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val categoryCounts = reports.groupingBy { it.category }.eachCount().entries.sortedByDescending { it.value }
                    categoryCounts.forEach { (cat, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = cat.label, style = MaterialTheme.typography.bodyMedium)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "$count reports",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Facilities with recurring reports or conflicts
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Facilities Requiring Attention",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                val conflictPlaces = places.filter { it.hasConflict || it.verificationStatus == VerificationStatus.OUTDATED }
                if (conflictPlaces.isEmpty()) {
                    Text(
                        text = "All active facilities are verified with no unresolved conflicts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    conflictPlaces.forEach { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = place.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Text(text = place.city, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            VerificationBadge(status = place.verificationStatus)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AdminReportsListSection(
    reports: List<BarrierReport>,
    sectionTitle: String,
    onReviewReport: (BarrierReport) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (reports.isEmpty()) {
            EmptyStateView(
                title = "No Reports in This Section",
                description = "There are currently no records matching this workflow status.",
                icon = Icons.Default.CheckCircle
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(reports, key = { it.id }) { report ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = report.category.label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                BarrierStatusBadge(status = report.status)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = report.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "${report.locationName}, ${report.city}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = report.specificLocationDetails, style = MaterialTheme.typography.bodySmall, maxLines = 2)

                            Spacer(modifier = Modifier.height(10.dp))
                            AccessibleButton(
                                text = "Review & Update Status",
                                icon = Icons.Default.Edit,
                                onClick = { onReviewReport(report) },
                                isPrimary = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminFacilitiesSection(
    places: List<AccessiblePlace>,
    onAddFacility: () -> Unit,
    onEditFacility: (AccessiblePlace) -> Unit,
    onMarkOutdated: (String) -> Unit,
    onResolveConflict: (String, Boolean, String) -> Unit
) {
    var selectedCityFilter by remember { mutableStateOf("All") }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Facility Management",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${places.size} participating facilities registered",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AccessibleButton(
                text = "Add Facility",
                icon = Icons.Default.Add,
                onClick = onAddFacility,
                isPrimary = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Facilities list table alternative / cards
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(places, key = { it.id }) { place ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VerificationBadge(status = place.verificationStatus)
                            Text(text = place.city, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = place.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = place.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Source: ${place.informationSource} (Verified: ${place.lastVerifiedDate})", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        if (place.hasConflict) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = "Conflicting Report Flagged", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                    Text(text = place.conflictDescription ?: "Visitor feedback differs from audit.", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AccessibleButton(
                                text = "Edit Record",
                                icon = Icons.Default.Edit,
                                onClick = { onEditFacility(place) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )
                            if (place.verificationStatus != VerificationStatus.OUTDATED) {
                                AccessibleButton(
                                    text = "Mark Outdated",
                                    icon = Icons.Default.Warning,
                                    onClick = { onMarkOutdated(place.id) },
                                    isPrimary = false,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (place.hasConflict) {
                                AccessibleButton(
                                    text = "Resolve Conflict",
                                    icon = Icons.Default.CheckCircle,
                                    onClick = { onResolveConflict(place.id, true, "Verified by facility manager on-site") },
                                    isPrimary = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTrendsSection(
    reports: List<BarrierReport>,
    places: List<AccessiblePlace>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Accessibility Trends & Category Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Accessible table alternative to chart-only metrics for screen readers and keyboard users.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                val categories = BarrierCategory.entries
                categories.forEach { cat ->
                    val count = reports.count { it.category == cat }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = cat.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text(text = "$count reports logged", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Facility Verification Status Summary",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val verifiedCount = places.count { it.verificationStatus == VerificationStatus.VERIFIED }
                val visitorCount = places.count { it.verificationStatus == VerificationStatus.REPORTED_BY_VISITOR }
                val outdatedCount = places.count { it.verificationStatus == VerificationStatus.OUTDATED }

                TrendRow(label = "Officially Audited & Verified", count = verifiedCount)
                TrendRow(label = "Reported by Visitors (Pending Audit)", count = visitorCount)
                TrendRow(label = "Outdated / Needs Re-Inspection", count = outdatedCount)
            }
        }
    }
}

@Composable
private fun TrendRow(label: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = "$count facilities", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FacilityEditDialog(
    facility: AccessiblePlace?,
    onDismiss: () -> Unit,
    onSave: (AccessiblePlace) -> Unit
) {
    var name by remember { mutableStateOf(facility?.name ?: "") }
    var city by remember { mutableStateOf(facility?.city ?: "") }
    var address by remember { mutableStateOf(facility?.address ?: "") }
    var source by remember { mutableStateOf(facility?.informationSource ?: "Institutional On-Site Audit") }
    var stepFree by remember { mutableStateOf(facility?.stepFreeEntrance ?: true) }
    var ramp by remember { mutableStateOf(facility?.wheelchairRamp ?: true) }
    var lift by remember { mutableStateOf(facility?.liftAvailable ?: true) }
    var toilet by remember { mutableStateOf(facility?.accessibleToilet ?: true) }
    var tactile by remember { mutableStateOf(facility?.tactilePaving ?: false) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (facility == null) "Add New Facility Record" else "Edit Facility Record",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AccessibleTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Facility Name",
                    placeholder = "e.g. Central Library & Learning Hub"
                )
                AccessibleTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = "City",
                    placeholder = "e.g. Bengaluru"
                )
                AccessibleTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Full Address",
                    placeholder = "Street address and PIN code"
                )
                AccessibleTextField(
                    value = source,
                    onValueChange = { source = it },
                    label = "Information Source & Audit Reference",
                    placeholder = "e.g. Municipal Access Audit 2026"
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Accessibility Features:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)

                FeatureSwitchRow(label = "Step-Free Entrance", checked = stepFree, onCheckedChange = { stepFree = it })
                FeatureSwitchRow(label = "Wheelchair Ramp", checked = ramp, onCheckedChange = { ramp = it })
                FeatureSwitchRow(label = "Passenger Lift / Elevator", checked = lift, onCheckedChange = { lift = it })
                FeatureSwitchRow(label = "Accessible Restroom", checked = toilet, onCheckedChange = { toilet = it })
                FeatureSwitchRow(label = "Tactile Paving", checked = tactile, onCheckedChange = { tactile = it })
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Save Facility Record",
                onClick = {
                    if (name.isNotBlank() && city.isNotBlank()) {
                        val updated = AccessiblePlace(
                            id = facility?.id ?: "fac-${UUID.randomUUID().toString().take(6)}",
                            name = name.trim(),
                            type = facility?.type ?: PlaceType.GOVERNMENT,
                            city = city.trim(),
                            address = address.trim(),
                            verificationStatus = VerificationStatus.VERIFIED,
                            lastVerifiedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                            informationSource = source.trim(),
                            stepFreeEntrance = stepFree,
                            wheelchairRamp = ramp,
                            liftAvailable = lift,
                            accessibleToilet = toilet,
                            tactilePaving = tactile,
                            description = facility?.description ?: "Public facility updated by administrator.",
                            accessibilityNotes = facility?.accessibilityNotes ?: "Verified accessible features.",
                            nearestTransit = facility?.nearestTransit ?: "Main transit access"
                        )
                        onSave(updated)
                    }
                },
                isPrimary = true
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun FeatureSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminReviewDialog(
    report: BarrierReport,
    currentUserRole: UserRole,
    history: List<ReportStatusHistoryItem>,
    onDismiss: () -> Unit,
    onUpdateStatus: (BarrierStatus, String, String, Boolean) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(report.status) }
    var reviewNote by remember { mutableStateOf(report.statusNotes) }
    var actionTaken by remember { mutableStateOf("") }
    var flagConflict by remember { mutableStateOf(report.hasConflictWithOfficialAudit) }
    var statusDropdownExpanded by remember { mutableStateOf(false) }
    var showHistorySection by remember { mutableStateOf(history.isNotEmpty()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Review & Status Audit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Report: ${report.title} (${report.id})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Update Workflow Status:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = statusDropdownExpanded,
                    onExpandedChange = { statusDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedStatus.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = statusDropdownExpanded,
                        onDismissRequest = { statusDropdownExpanded = false }
                    ) {
                        BarrierStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(status.label, fontWeight = FontWeight.Bold)
                                        Text(status.description, style = MaterialTheme.typography.labelSmall)
                                    }
                                },
                                onClick = {
                                    selectedStatus = status
                                    statusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AccessibleTextField(
                    value = reviewNote,
                    onValueChange = {
                        reviewNote = it
                        errorMessage = null
                    },
                    label = "Review Note / Inspection Findings",
                    placeholder = "Describe findings from physical audit, phone check, or maintenance log...",
                    singleLine = false,
                    maxLines = 4,
                    errorMessage = errorMessage
                )

                Spacer(modifier = Modifier.height(10.dp))

                AccessibleTextField(
                    value = actionTaken,
                    onValueChange = { actionTaken = it },
                    label = "Action Taken (Optional)",
                    placeholder = "e.g. Dispatched civil repair crew, notified station controller"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Flag Information Conflict",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Marks discrepancy between user claim and official audit",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = flagConflict,
                        onCheckedChange = { flagConflict = it }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Audit Trail (${history.size} entries)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TextButton(onClick = { showHistorySection = !showHistorySection }) {
                        Text(if (showHistorySection) "Hide" else "Show")
                    }
                }

                AnimatedVisibility(visible = showHistorySection) {
                    Column(modifier = Modifier.padding(top = 6.dp)) {
                        if (history.isEmpty()) {
                            Text(
                                text = "No prior status changes recorded for this report.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            history.forEach { item ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${item.fromStatus?.label ?: "Intake"} → ${item.toStatus.label}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = item.timestamp,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = "By: ${item.changedBy} (${item.changedRole})",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Note: ${item.reviewNote}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Save Status & Record Audit",
                onClick = {
                    if (reviewNote.trim().length < 5) {
                        errorMessage = "Please enter an inspection review note of at least 5 characters."
                    } else {
                        onUpdateStatus(selectedStatus, reviewNote, actionTaken, flagConflict)
                    }
                },
                isPrimary = true
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
