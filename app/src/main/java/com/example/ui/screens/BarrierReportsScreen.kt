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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DatabaseSetupGuide
import com.example.data.DatabaseStorageMode
import com.example.model.BarrierCategory
import com.example.model.BarrierReport
import com.example.model.BarrierSeverity
import com.example.model.BarrierStatus
import com.example.model.ReportStatusHistoryItem
import com.example.model.UserRole
import com.example.ui.components.AccessibleButton
import com.example.ui.components.AccessibleConfirmationDialog
import com.example.ui.components.AccessibleTextField
import com.example.ui.components.BarrierProgressTimeline
import com.example.ui.components.BarrierStatusBadge
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.AccessibleAmber
import com.example.ui.theme.AccessibleGreen
import com.example.ui.theme.AccessibleRed

@Composable
fun BarrierReportsScreen(
    reports: List<BarrierReport>,
    statusFilter: BarrierStatus?,
    isDialogOpen: Boolean,
    prefilledLocation: Pair<String, String>?,
    currentUserRole: UserRole = UserRole.PUBLIC_USER,
    statusHistory: List<ReportStatusHistoryItem> = emptyList(),
    onFilterStatus: (BarrierStatus?) -> Unit,
    onOpenDialog: () -> Unit,
    onCloseDialog: () -> Unit,
    onSwitchUserRole: (UserRole) -> Unit = {},
    onSubmitReport: (String, BarrierCategory, String, String, String, BarrierSeverity, String?, String?) -> Unit,
    onUpdateReportStatus: (String, BarrierStatus, String, String, Boolean) -> Unit = { _, _, _, _, _ -> },
    onDeleteReport: (BarrierReport) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusScrollState = rememberScrollState()
    val roleScrollState = rememberScrollState()
    var reportToDelete by remember { mutableStateOf<BarrierReport?>(null) }
    var reportToReview by remember { mutableStateOf<BarrierReport?>(null) }
    var showDbGuideDialog by remember { mutableStateOf(false) }

    val filteredReports by remember(reports, statusFilter) {
        derivedStateOf {
            if (statusFilter == null) reports
            else reports.filter { it.status == statusFilter }
        }
    }

    Column(
        modifier = modifier
            .testTag("barrier_reports_screen")
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
                    text = "Barrier Reports & Data Layer",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Typed schema, SQLite persistence & verified status workflows",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AccessibleButton(
                text = "Send Report",
                icon = Icons.Default.Add,
                onClick = onOpenDialog,
                isPrimary = true,
                testTag = "report_new_barrier_button"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Data Layer Storage Mode Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Storage: Local SQLite (Room Database) Active",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "All submitted reports persist locally across app restarts.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(
                    onClick = { showDbGuideDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cloud Setup Path", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Role-Based Access Control Switcher
        Text(
            text = "Active Access Role (Permissions & Review):",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(roleScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserRole.entries.forEach { role ->
                val isSelected = currentUserRole == role
                FilterChip(
                    selected = isSelected,
                    onClick = { onSwitchUserRole(role) },
                    label = { Text(role.label) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (role) {
                                UserRole.PUBLIC_USER -> Icons.Default.Person
                                UserRole.ACCESSIBILITY_REVIEWER -> Icons.Default.AssignmentTurnedIn
                                UserRole.SYSTEM_ADMIN -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Role explanation banner
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (currentUserRole.canReviewReports) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = if (currentUserRole.canReviewReports) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${currentUserRole.roleTitle}: ${currentUserRole.description}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Chips Row (5 explicit workflow states)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(statusScrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = statusFilter == null,
                onClick = { onFilterStatus(null) },
                label = { Text("All Reports (${reports.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            BarrierStatus.entries.forEach { status ->
                val count = reports.count { it.status == status }
                FilterChip(
                    selected = statusFilter == status,
                    onClick = { onFilterStatus(status) },
                    label = { Text("${status.label} ($count)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reports list
        if (filteredReports.isEmpty()) {
            EmptyStateView(
                title = "No Reports Under This Status",
                description = "All reports under this status have either been resolved or not yet submitted.",
                icon = Icons.Default.ReportProblem,
                actionButtonText = "Send a Barrier Report",
                onActionClick = onOpenDialog
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredReports, key = { it.id }) { report ->
                    BarrierReportItemCard(
                        report = report,
                        currentUserRole = currentUserRole,
                        onDelete = { reportToDelete = report },
                        onReview = { reportToReview = report }
                    )
                }
            }
        }
    }

    // Confirmation dialog before deleting a report
    if (reportToDelete != null) {
        AccessibleConfirmationDialog(
            title = "Withdraw / Remove Report?",
            description = "Are you sure you want to withdraw the report for \"${reportToDelete?.title}\"? This action will remove the record from the database.",
            confirmButtonText = "Withdraw Report",
            cancelButtonText = "Keep Report",
            isDestructive = true,
            icon = Icons.Default.DeleteOutline,
            onConfirm = {
                reportToDelete?.let { onDeleteReport(it) }
                reportToDelete = null
            },
            onDismiss = { reportToDelete = null }
        )
    }

    // New Report Submission Dialog
    if (isDialogOpen) {
        NewBarrierReportDialog(
            prefilledLocation = prefilledLocation,
            onDismiss = onCloseDialog,
            onSubmit = onSubmitReport
        )
    }

    // Administrative Review & Status Update Dialog
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

    // Database Setup Guide Dialog
    if (showDbGuideDialog) {
        DatabaseSetupGuideDialog(onDismiss = { showDbGuideDialog = false })
    }
}

@Composable
private fun BarrierReportItemCard(
    report: BarrierReport,
    currentUserRole: UserRole,
    onDelete: () -> Unit,
    onReview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("barrier_report_${report.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Category, Status Badge & Delete / Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = report.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    BarrierStatusBadge(status = report.status)
                    Spacer(modifier = Modifier.width(4.dp))

                    // Delete / Withdraw button
                    if (report.isUserSubmitted || currentUserRole.canDeleteAnyReport) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(36.dp)
                                .semantics {
                                    contentDescription = "Withdraw report: ${report.title}"
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = report.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${report.locationName}, ${report.city}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = report.specificLocationDetails,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Conflict Warning Banner if report conflicts with audit
            if (report.hasConflictWithOfficialAudit) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Conflict Flagged: Reported condition differs from official accessibility audit; on-site re-inspection pending.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Timeline (5 status states)
            Text(
                text = "Workflow Stage (${report.status.label}):",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            BarrierProgressTimeline(currentStatus = report.status)

            Spacer(modifier = Modifier.height(12.dp))

            // Severity & Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val severityColor = when (report.severity) {
                        BarrierSeverity.URGENT -> AccessibleRed
                        BarrierSeverity.HIGH -> AccessibleRed
                        BarrierSeverity.MEDIUM -> AccessibleAmber
                        BarrierSeverity.LOW -> MaterialTheme.colorScheme.primary
                    }
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = severityColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Severity: ${report.severity.label}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = severityColor
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reported: ${report.dateReported}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Latest Review Note Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Review Status & Action Taken",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = report.statusNotes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (report.resolutionEstimate != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.resolutionEstimate,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Administrative Review Button (Visible to Reviewers and Admins)
            if (currentUserRole.canReviewReports) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onReview,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Review, Audit Trail & Update Status")
                }
            }
        }
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
                    modifier = Modifier.height(95.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                AccessibleTextField(
                    value = actionTaken,
                    onValueChange = { actionTaken = it },
                    label = "Action Taken (Optional)",
                    placeholder = "e.g. Dispatched civil repair crew, notified station controller"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Conflict Flagging Switch
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
                        onCheckedChange = { flagConflict = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.error)
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Chronological Audit Trail Section
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
                                        if (item.actionTaken.isNotBlank()) {
                                            Text(
                                                text = "Action: ${item.actionTaken}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewBarrierReportDialog(
    prefilledLocation: Pair<String, String>?,
    onDismiss: () -> Unit,
    onSubmit: (String, BarrierCategory, String, String, String, BarrierSeverity, String?, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(BarrierCategory.BROKEN_RAMP) }
    var locationName by remember { mutableStateOf(prefilledLocation?.first ?: "") }
    var city by remember { mutableStateOf(prefilledLocation?.second ?: "") }
    var details by remember { mutableStateOf("") }
    var selectedSeverity by remember { mutableStateOf(BarrierSeverity.MEDIUM) }
    var optionalContact by remember { mutableStateOf("") }
    var optionalImageNote by remember { mutableStateOf("") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var severityDropdownExpanded by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val isTitleValid = title.trim().length in 3..100
    val isLocationValid = locationName.trim().length in 3..100
    val isCityValid = city.trim().length in 2..50
    val isDetailsValid = details.trim().length in 8..500

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Report an Accessibility Barrier",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Logged to local SQLite database with unique tracking ID",
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
                AccessibleTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title (e.g. Broken Handrail on Entrance Ramp)",
                    placeholder = "Short descriptive summary",
                    errorMessage = if (hasAttemptedSubmit && !isTitleValid) "Title must be 3-100 characters" else null
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Barrier Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        BarrierCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.label) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AccessibleTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = "Place or Building Name",
                    placeholder = "e.g. Metro Station Gate 3, District Hospital",
                    errorMessage = if (hasAttemptedSubmit && !isLocationValid) "Location must be 3-100 characters" else null
                )

                Spacer(modifier = Modifier.height(10.dp))

                AccessibleTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = "City",
                    placeholder = "e.g. Bengaluru, Delhi, Mumbai, Chennai",
                    errorMessage = if (hasAttemptedSubmit && !isCityValid) "City must be 2-50 characters" else null
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Severity Dropdown
                ExposedDropdownMenuBox(
                    expanded = severityDropdownExpanded,
                    onExpandedChange = { severityDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSeverity.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("How Serious Is It?") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = severityDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = severityDropdownExpanded,
                        onDismissRequest = { severityDropdownExpanded = false }
                    ) {
                        BarrierSeverity.entries.forEach { sev ->
                            DropdownMenuItem(
                                text = { Text(sev.label) },
                                onClick = {
                                    selectedSeverity = sev
                                    severityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AccessibleTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = "Specific Details & Exact Spot",
                    placeholder = "Describe where this barrier is located, measurements, and who it affects...",
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier.height(95.dp),
                    errorMessage = if (hasAttemptedSubmit && !isDetailsValid) "Details must be 8-500 characters" else null
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Privacy-Compliant Optional Contact Details
                AccessibleTextField(
                    value = optionalContact,
                    onValueChange = { optionalContact = it },
                    label = "Optional Contact (Email or Phone)",
                    placeholder = "Only used if coordinators need clarification; not public"
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🔒 Privacy: Contact info is strictly optional. We do not expose personal visitor details on public facility pages.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Send Report",
                onClick = {
                    hasAttemptedSubmit = true
                    if (isTitleValid && isLocationValid && isCityValid && isDetailsValid) {
                        onSubmit(
                            title,
                            selectedCategory,
                            locationName,
                            city,
                            details,
                            selectedSeverity,
                            optionalContact,
                            null
                        )
                    }
                },
                testTag = "submit_barrier_form_button"
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DatabaseSetupGuideDialog(onDismiss: () -> Unit) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CloudQueue,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cloud Production Setup Guide",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
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
                    text = "Local Demonstration Mode vs. Production",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Currently, AccessBridge AI is running in Local Storage (Room SQLite) mode. Data is stored on-device with full audit trails. We never pretend demo data is written to a hosted cloud database without real credentials.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Production Hosted Database (Supabase / Firebase)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "To deploy to a production hosted database, configure the backend service using Row Level Security (RLS) so privileged service keys are never bundled in client code:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = DatabaseSetupGuide.SQL_SCHEMA_OVERVIEW.trim(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(10.dp)
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
