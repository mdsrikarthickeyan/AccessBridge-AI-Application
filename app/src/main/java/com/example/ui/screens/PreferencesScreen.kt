package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AccessibleForward
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Blind
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccessibilityPreferences
import com.example.model.AppLanguage
import com.example.model.TtsVoiceTone
import com.example.ui.components.AccessibleButton
import com.example.ui.components.AccessibleConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PreferencesScreen(
    preferences: AccessibilityPreferences,
    onSetHighContrast: (Boolean) -> Unit,
    onSetFontScale: (Float) -> Unit,
    onSetDyslexiaSpacing: (Boolean) -> Unit,
    onSetReducedMotion: (Boolean) -> Unit,
    onSetKeyboardFriendly: (Boolean) -> Unit,
    onSetReduceUnnecessarySteps: (Boolean) -> Unit,
    onSetLanguage: (AppLanguage) -> Unit,
    onSetSpokenOutputEnabled: (Boolean) -> Unit,
    onSetSpeechRate: (Float) -> Unit,
    onSetVoiceTone: (TtsVoiceTone) -> Unit,
    onSetHaptic: (Boolean) -> Unit,
    onToggleFacilityRequirement: (String) -> Unit,
    onSavePreferences: (AccessibilityPreferences) -> Unit,
    onTestSpeech: () -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var languageDropdownExpanded by remember { mutableStateOf(false) }
    var voiceToneDropdownExpanded by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showSaveToast by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .testTag("preferences_screen")
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "My Accessibility Preferences",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Personalize reading, spoken audio, and building facility rankings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Preferences settings",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Respectful Non-Diagnostic Privacy Guarantee Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Respectful & Private by Design",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "You are never asked for medical diagnosis or condition disclosures. These settings simply tell the app how to present text, speak output, and highlight relevant facilities on maps. Stored strictly on this device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // My Access Passport (Personalized Facility Highlights)
        AccessPassportSummaryCard(
            preferences = preferences,
            onToggleFacility = onToggleFacilityRequirement
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Live Interactive Preview Box
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Live Display & Spacing Preview",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Scale: ${(preferences.fontScale * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Welcome to AccessBridge AI. Text scale, dyslexia spacing, and high contrast update in real-time across all forms, maps, and communication tools.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (preferences.dyslexiaSpacing) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✓ Dyslexia-friendly letter and line spacing active",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. Display & Visual Comfort
        PreferenceCategoryCard(title = "Display & Visual Comfort") {
            // High Contrast Switch
            PreferenceToggleRow(
                title = "High Contrast Mode",
                subtitle = "Applies dark background with bright yellow and cyan elements for high legibility",
                icon = Icons.Default.Contrast,
                checked = preferences.highContrast,
                onCheckedChange = onSetHighContrast,
                testTag = "pref_high_contrast_switch"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Font Scale Slider
            Text(
                text = "Text Size: ${(preferences.fontScale * 100).toInt()}%",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enlarge or scale down typography across all screens",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "A", style = MaterialTheme.typography.titleSmall)
                Slider(
                    value = preferences.fontScale,
                    onValueChange = onSetFontScale,
                    valueRange = 1.0f..1.5f,
                    steps = 3,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .testTag("pref_font_scale_slider")
                )
                Text(text = "A", style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dyslexia-friendly Spacing
            PreferenceToggleRow(
                title = "Dyslexia-Friendly Letter Spacing",
                subtitle = "Broadens line height and letter spacing to minimize visual crowding",
                icon = Icons.Default.TextFormat,
                checked = preferences.dyslexiaSpacing,
                onCheckedChange = onSetDyslexiaSpacing,
                testTag = "pref_dyslexia_spacing_switch"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Reduced Motion
            PreferenceToggleRow(
                title = "Reduced Motion",
                subtitle = "Minimizes animations, transitions, and pulsing indicator effects",
                icon = Icons.Default.Visibility,
                checked = preferences.reducedMotion,
                onCheckedChange = onSetReducedMotion,
                testTag = "pref_reduced_motion_switch"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Keyboard-Friendly Navigation
            PreferenceToggleRow(
                title = "Keyboard & Focus Enhancements",
                subtitle = "Provides prominent focus borders and larger tap targets for switch/keyboard users",
                icon = Icons.Default.Keyboard,
                checked = preferences.keyboardFriendlyNavigation,
                onCheckedChange = onSetKeyboardFriendly,
                testTag = "pref_keyboard_friendly_switch"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Language & Spoken Audio
        PreferenceCategoryCard(title = "Language & Spoken Output") {
            // Language Selection
            Text(
                text = "Preferred Language",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Used for speech output, card translations, and document simplify summaries",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = languageDropdownExpanded,
                onExpandedChange = { languageDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = "${preferences.preferredLanguage.displayName} (${preferences.preferredLanguage.localName})",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("App & Speech Language") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .testTag("pref_language_dropdown"),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = languageDropdownExpanded,
                    onDismissRequest = { languageDropdownExpanded = false }
                ) {
                    AppLanguage.entries.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text("${lang.displayName} - ${lang.localName}") },
                            onClick = {
                                onSetLanguage(lang)
                                languageDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Spoken Output On/Off Toggle
            PreferenceToggleRow(
                title = "Spoken Output",
                subtitle = "Enable audio playback for notices, cards, directions, and summaries",
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                checked = preferences.spokenOutputEnabled,
                onCheckedChange = onSetSpokenOutputEnabled,
                testTag = "pref_spoken_output_switch"
            )

            if (preferences.spokenOutputEnabled) {
                Spacer(modifier = Modifier.height(16.dp))

                // Voice Pitch / Tone
                Text(
                    text = "Text-to-Speech Voice Pitch / Tone",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose voice pitch style supported by device synthesizer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = voiceToneDropdownExpanded,
                    onExpandedChange = { voiceToneDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = preferences.voiceTone.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Voice Tone") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = voiceToneDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("pref_voice_tone_dropdown"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = voiceToneDropdownExpanded,
                        onDismissRequest = { voiceToneDropdownExpanded = false }
                    ) {
                        TtsVoiceTone.entries.forEach { tone ->
                            DropdownMenuItem(
                                text = { Text(tone.label) },
                                onClick = {
                                    onSetVoiceTone(tone)
                                    voiceToneDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Speech Rate Slider
                Text(
                    text = "Speech Rate: ${String.format("%.2f", preferences.speechRate)}x",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Adjust reading pace from slow to faster comprehension",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Slider(
                    value = preferences.speechRate,
                    onValueChange = onSetSpeechRate,
                    valueRange = 0.75f..1.35f,
                    steps = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pref_speech_rate_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Test Voice Button
                AccessibleButton(
                    text = "Test Spoken Voice",
                    icon = Icons.Default.RecordVoiceOver,
                    onClick = onTestSpeech,
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "pref_test_voice_button"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Haptics
            PreferenceToggleRow(
                title = "Vibration & Haptic Feedback",
                subtitle = "Tactile tap confirmation for button presses and speech activation",
                icon = Icons.Default.Vibration,
                checked = preferences.hapticFeedback,
                onCheckedChange = onSetHaptic,
                testTag = "pref_haptic_switch"
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Workflow & Step Reduction
        PreferenceCategoryCard(title = "Workflow Optimization") {
            PreferenceToggleRow(
                title = "Reduce Unnecessary Steps",
                subtitle = "Automatically expands simplified summaries and minimizes confirmation steps where safe",
                icon = Icons.Default.Speed,
                checked = preferences.reduceUnnecessarySteps,
                onCheckedChange = onSetReduceUnnecessarySteps,
                testTag = "pref_reduce_steps_switch"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save & Feedback Actions
        AnimatedVisibility(visible = showSaveToast) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Preferences applied and stored locally on your device.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        AccessibleButton(
            text = "Save Preferences",
            icon = Icons.Default.Save,
            onClick = {
                onSavePreferences(preferences)
                showSaveToast = true
            },
            isPrimary = true,
            modifier = Modifier.fillMaxWidth(),
            testTag = "pref_save_button"
        )

        Spacer(modifier = Modifier.height(12.dp))

        AccessibleButton(
            text = "Reset to Standard Settings",
            icon = Icons.Default.RestartAlt,
            onClick = { showResetDialog = true },
            isPrimary = false,
            isDestructive = true,
            modifier = Modifier.fillMaxWidth(),
            testTag = "pref_reset_button"
        )

        Spacer(modifier = Modifier.height(28.dp))
    }

    if (showResetDialog) {
        AccessibleConfirmationDialog(
            title = "Reset Preferences?",
            description = "This returns font scaling, contrast, language, and facility requirements back to standard defaults. Stored data remains local.",
            confirmButtonText = "Reset to Defaults",
            cancelButtonText = "Keep Current",
            isDestructive = true,
            icon = Icons.Default.RestartAlt,
            onConfirm = {
                onResetDefaults()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccessPassportSummaryCard(
    preferences: AccessibilityPreferences,
    onToggleFacility: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.AccessibleForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "My Access Passport",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Prioritized Facility Requirements",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (preferences.hasAnyFacilityRequirement()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${preferences.activeRequirementCount()} Selected",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (preferences.hasAnyFacilityRequirement()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap the building facilities you need. Places with documented records for these facilities will be ranked first on the Accessible Places map and list.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Facility Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PassportFacilityChip(
                    label = "Step-Free Entrance",
                    selected = preferences.requireStepFree,
                    icon = Icons.AutoMirrored.Filled.AccessibleForward,
                    onClick = { onToggleFacility("step_free") }
                )
                PassportFacilityChip(
                    label = "Wheelchair Ramp",
                    selected = preferences.requireRamp,
                    icon = Icons.Default.AccessibilityNew,
                    onClick = { onToggleFacility("ramp") }
                )
                PassportFacilityChip(
                    label = "Lift / Elevator",
                    selected = preferences.requireLift,
                    icon = Icons.Default.Elevator,
                    onClick = { onToggleFacility("lift") }
                )
                PassportFacilityChip(
                    label = "Accessible Toilet",
                    selected = preferences.requireAccessibleToilet,
                    icon = Icons.Default.Wc,
                    onClick = { onToggleFacility("toilet") }
                )
                PassportFacilityChip(
                    label = "Wheelchair Route",
                    selected = preferences.requireWheelchairRoute,
                    icon = Icons.AutoMirrored.Filled.AltRoute,
                    onClick = { onToggleFacility("wheelchair_route") }
                )
                PassportFacilityChip(
                    label = "Tactile Guidance",
                    selected = preferences.requireTactileGuidance,
                    icon = Icons.Default.Blind,
                    onClick = { onToggleFacility("tactile") }
                )
                PassportFacilityChip(
                    label = "Braille Signs",
                    selected = preferences.requireBrailleSignage,
                    icon = Icons.Default.TextFormat,
                    onClick = { onToggleFacility("braille") }
                )
                PassportFacilityChip(
                    label = "Audio Assistance",
                    selected = preferences.requireAudioAssistance,
                    icon = Icons.Default.Hearing,
                    onClick = { onToggleFacility("audio_assist") }
                )
                PassportFacilityChip(
                    label = "Visual Announcements",
                    selected = preferences.requireVisualAnnouncements,
                    icon = Icons.Default.Visibility,
                    onClick = { onToggleFacility("visual_announce") }
                )
                PassportFacilityChip(
                    label = "Accessible Parking",
                    selected = preferences.requireAccessibleParking,
                    icon = Icons.Default.LocalParking,
                    onClick = { onToggleFacility("parking") }
                )
            }
        }
    }
}

@Composable
private fun PassportFacilityChip(
    label: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = if (selected) Icons.Default.Check else icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
private fun PreferenceCategoryCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun PreferenceToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}
