package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.AppLanguage
import com.example.model.CommCategory
import com.example.model.CommunicationCard
import com.example.service.SpeechRecognitionState
import com.example.service.TranslationResult
import com.example.ui.components.AccessibleButton
import com.example.ui.components.AccessibleTextField
import com.example.ui.components.AudioPulseIndicator
import com.example.ui.components.EmptyStateView
import kotlinx.coroutines.launch

enum class CommunicateTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SPEECH_TO_TEXT("Turn speech into text", Icons.Default.Mic),
    SPEAK_MY_MESSAGE("Speak my message", Icons.Default.VolumeUp),
    TRANSLATE_MESSAGE("Translate a message", Icons.Default.Translate)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CommunicateScreen(
    cards: List<CommunicationCard>,
    selectedCategory: CommCategory?,
    searchQuery: String,
    customInput: String,
    preferredLanguage: AppLanguage,
    isSpeaking: Boolean,
    flashcardToShow: CommunicationCard?,
    speechRecognitionState: SpeechRecognitionState,
    isListeningToSpeech: Boolean,
    recognizedSpeechText: String,
    translatedResult: TranslationResult.Success?,
    translationError: String?,
    onSelectCategory: (CommCategory?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCustomInputChange: (String) -> Unit,
    onAddCustomPhrase: (String, CommCategory) -> Unit,
    onUpdateCard: (String, String) -> Unit,
    onSpeakCard: (String, AppLanguage) -> Unit,
    onStopSpeaking: () -> Unit,
    onShowFlashcard: (CommunicationCard?) -> Unit,
    onStartSpeechToText: (AppLanguage) -> Unit,
    onStopSpeechToText: () -> Unit,
    onClearSpeechToText: () -> Unit,
    onTranslateMessage: (String, AppLanguage, AppLanguage) -> Unit,
    onUpdateTranslatedText: (String) -> Unit,
    onClearTranslation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddPhraseDialog by remember { mutableStateOf(false) }
    var editingCard by remember { mutableStateOf<CommunicationCard?>(null) }
    var fullScreenMessageText by remember { mutableStateOf<Pair<String, String?>?>(null) }
    var confirmShareText by remember { mutableStateOf<String?>(null) }

    // Microphone Permission Launcher
    var showMicPermissionRationale by remember { mutableStateOf(false) }
    var micSttLanguage by remember { mutableStateOf(preferredLanguage) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartSpeechToText(micSttLanguage)
        } else {
            showMicPermissionRationale = true
        }
    }

    fun requestStartSpeech(language: AppLanguage) {
        micSttLanguage = language
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onStartSpeechToText(language)
        } else {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun copyTextToClipboard(text: String, label: String = "AccessBridge") {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Copied to clipboard.")
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .testTag("communicate_screen")
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Help Me Communicate",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Spoken text, live typing, translations & large cards",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                AccessibleButton(
                    text = "New Card",
                    icon = Icons.Default.Add,
                    onClick = { showAddPhraseDialog = true },
                    isPrimary = false
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Three Primary Actions Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                CommunicateTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = tab.title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // TAB 1: Turn Speech Into Text
                    SpeechToTextTabContent(
                        speechState = speechRecognitionState,
                        isListening = isListeningToSpeech,
                        recognizedText = recognizedSpeechText,
                        preferredLanguage = preferredLanguage,
                        onStartListening = { lang -> requestStartSpeech(lang) },
                        onStopListening = onStopSpeaking.let { { onStopSpeechToText() } },
                        onClearText = onClearSpeechToText,
                        onCopyText = { copyTextToClipboard(it) },
                        onShowLargeCard = { text -> fullScreenMessageText = Pair(text, null) },
                        onSpeakText = { text -> onSpeakCard(text, preferredLanguage) },
                        onShareConfirm = { text -> confirmShareText = text }
                    )
                }
                1 -> {
                    // TAB 2: Speak My Message & Everyday Quick Phrases
                    SpeakMyMessageTabContent(
                        cards = cards,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        customInput = customInput,
                        preferredLanguage = preferredLanguage,
                        isSpeaking = isSpeaking,
                        onSelectCategory = onSelectCategory,
                        onSearchQueryChange = onSearchQueryChange,
                        onCustomInputChange = onCustomInputChange,
                        onSpeak = onSpeakCard,
                        onStopSpeaking = onStopSpeaking,
                        onShowFlashcard = onShowFlashcard,
                        onEditCard = { editingCard = it },
                        onAddPhraseClick = { showAddPhraseDialog = true },
                        onCopyCardText = { copyTextToClipboard(it) },
                        onPresentLargeCard = { text, trans -> fullScreenMessageText = Pair(text, trans) }
                    )
                }
                2 -> {
                    // TAB 3: Translate a Message
                    TranslateMessageTabContent(
                        translatedResult = translatedResult,
                        translationError = translationError,
                        preferredLanguage = preferredLanguage,
                        isSpeaking = isSpeaking,
                        onTranslate = onTranslateMessage,
                        onUpdateTranslatedText = onUpdateTranslatedText,
                        onClearTranslation = onClearTranslation,
                        onSpeak = onSpeakCard,
                        onStopSpeaking = onStopSpeaking,
                        onCopyText = { copyTextToClipboard(it) },
                        onShowLargeCard = { orig, trans -> fullScreenMessageText = Pair(orig, trans) },
                        onShareConfirm = { text -> confirmShareText = text }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    // Microphone Permission Rationale Dialog
    if (showMicPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showMicPermissionRationale = false },
            icon = { Icon(Icons.Default.MicOff, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Microphone Access Needed", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "To convert spoken words into live text, AccessBridge AI needs permission to use your device microphone.\n\n" +
                            "Audio is processed securely and is never recorded in the background or stored permanently. You can always use manual text entry if you prefer."
                )
            },
            confirmButton = {
                AccessibleButton(
                    text = "Grant Permission",
                    onClick = {
                        showMicPermissionRationale = false
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showMicPermissionRationale = false }) {
                    Text("Type Manually Instead")
                }
            }
        )
    }

    // Add New Custom Phrase Dialog
    if (showAddPhraseDialog) {
        AddCustomPhraseDialog(
            onDismiss = { showAddPhraseDialog = false },
            onSubmit = { text, category ->
                onAddCustomPhrase(text, category)
                showAddPhraseDialog = false
            }
        )
    }

    // Edit Existing Phrase Dialog
    if (editingCard != null) {
        val targetCard = editingCard!!
        EditPhraseDialog(
            card = targetCard,
            onDismiss = { editingCard = null },
            onSave = { updatedText ->
                onUpdateCard(targetCard.id, updatedText)
                editingCard = null
            }
        )
    }

    // Flashcard Presentation Modal for Model Cards
    if (flashcardToShow != null) {
        FlashcardModal(
            card = flashcardToShow,
            preferredLanguage = preferredLanguage,
            onDismiss = { onShowFlashcard(null) },
            onSpeak = { onSpeakCard(flashcardToShow.getTextForLanguage(preferredLanguage), preferredLanguage) },
            onCopy = { copyTextToClipboard(flashcardToShow.englishText) },
            onShare = { confirmShareText = flashcardToShow.englishText }
        )
    }

    // Standalone Fullscreen Message Display Modal (for Stt / Translation / Custom)
    if (fullScreenMessageText != null) {
        val (primary, secondary) = fullScreenMessageText!!
        LargeMessageDisplayModal(
            primaryText = primary,
            secondaryText = secondary,
            onDismiss = { fullScreenMessageText = null },
            onSpeak = { onSpeakCard(primary, preferredLanguage) },
            onCopy = { copyTextToClipboard(primary) },
            onShare = { confirmShareText = primary }
        )
    }

    // Explicit Confirmation Dialog Before Sharing Externally
    if (confirmShareText != null) {
        val textToShare = confirmShareText!!
        AlertDialog(
            onDismissRequest = { confirmShareText = null },
            icon = { Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Confirm Sharing Message", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Are you sure you want to share this message externally with other apps or messaging services?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "\"$textToShare\"",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {
                AccessibleButton(
                    text = "Share Message",
                    icon = Icons.Default.Share,
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, textToShare)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share communication message"))
                        confirmShareText = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { confirmShareText = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------------------
// TAB 1: SPEECH TO TEXT
// -------------------------------------------------------------------------
@Composable
private fun SpeechToTextTabContent(
    speechState: SpeechRecognitionState,
    isListening: Boolean,
    recognizedText: String,
    preferredLanguage: AppLanguage,
    onStartListening: (AppLanguage) -> Unit,
    onStopListening: () -> Unit,
    onClearText: () -> Unit,
    onCopyText: (String) -> Unit,
    onShowLargeCard: (String) -> Unit,
    onSpeakText: (String) -> Unit,
    onShareConfirm: (String) -> Unit
) {
    var manualTextFallback by remember { mutableStateOf("") }
    var selectedSttLang by remember { mutableStateOf(preferredLanguage) }
    var languageDropdownExpanded by remember { mutableStateOf(false) }

    val displayText = recognizedText.ifBlank {
        when (speechState) {
            is SpeechRecognitionState.Processing -> speechState.partialText
            is SpeechRecognitionState.Success -> speechState.recognizedText
            else -> manualTextFallback
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Recognition Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isListening) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                    else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(
                    1.dp,
                    if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Turn Speech into Text",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isListening) "Listening right now — speak into microphone" else "Tap below to listen to someone speaking",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isListening) {
                            AudioPulseIndicator(isSpeaking = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Language Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Spoken Language:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { languageDropdownExpanded = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${selectedSttLang.displayName} (${selectedSttLang.localName})",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            androidx.compose.material3.DropdownMenu(
                                expanded = languageDropdownExpanded,
                                onDismissRequest = { languageDropdownExpanded = false }
                            ) {
                                AppLanguage.entries.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text("${lang.displayName} - ${lang.localName}") },
                                        onClick = {
                                            selectedSttLang = lang
                                            languageDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Mic Action Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AccessibleButton(
                            text = if (isListening) "Stop Listening" else "Start Microphone",
                            icon = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                            onClick = {
                                if (isListening) {
                                    onStopListening()
                                } else {
                                    onStartListening(selectedSttLang)
                                }
                            },
                            isPrimary = !isListening,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("stt_toggle_button")
                        )

                        if (displayText.isNotBlank()) {
                            AccessibleButton(
                                text = "Clear",
                                icon = Icons.Default.Clear,
                                onClick = {
                                    manualTextFallback = ""
                                    onClearText()
                                },
                                isPrimary = false,
                                modifier = Modifier.testTag("stt_clear_button")
                            )
                        }
                    }

                    // Active Microphone Indicator & Notice
                    if (isListening) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Microphone is active. Never records continuously in background.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Error Notice if any
                    if (speechState is SpeechRecognitionState.Error) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = speechState.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live or Captured Text Display Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Transcribed Text",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (displayText.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = displayText,
                                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions row: Show Large, Speak, Copy, Share
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AccessibleButton(
                                text = "Show Large",
                                icon = Icons.Default.Fullscreen,
                                onClick = { onShowLargeCard(displayText) },
                                isPrimary = true,
                                modifier = Modifier.weight(1f)
                            )

                            AccessibleButton(
                                text = "Replay Voice",
                                icon = Icons.Default.Replay,
                                onClick = { onSpeakText(displayText) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AccessibleButton(
                                text = "Copy Text",
                                icon = Icons.Default.ContentCopy,
                                onClick = { onCopyText(displayText) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )

                            AccessibleButton(
                                text = "Share",
                                icon = Icons.Default.Share,
                                onClick = { onShareConfirm(displayText) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        // Empty state / prompt
                        Text(
                            text = "Spoken speech will appear here in large, high-contrast text. Tap 'Start Microphone' above or type below if you cannot speak.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Manual Text Entry Fallback (Always available)
                    Text(
                        text = "Manual Text-Entry Fallback (Type instead):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = manualTextFallback,
                            onValueChange = { manualTextFallback = it },
                            placeholder = { Text("Type words to display or read...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        AccessibleButton(
                            text = "Show",
                            icon = Icons.Default.Fullscreen,
                            onClick = {
                                if (manualTextFallback.isNotBlank()) {
                                    onShowLargeCard(manualTextFallback)
                                }
                            },
                            enabled = manualTextFallback.isNotBlank()
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// TAB 2: SPEAK MY MESSAGE & QUICK PHRASES
// -------------------------------------------------------------------------
@Composable
private fun SpeakMyMessageTabContent(
    cards: List<CommunicationCard>,
    selectedCategory: CommCategory?,
    searchQuery: String,
    customInput: String,
    preferredLanguage: AppLanguage,
    isSpeaking: Boolean,
    onSelectCategory: (CommCategory?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCustomInputChange: (String) -> Unit,
    onSpeak: (String, AppLanguage) -> Unit,
    onStopSpeaking: () -> Unit,
    onShowFlashcard: (CommunicationCard) -> Unit,
    onEditCard: (CommunicationCard) -> Unit,
    onAddPhraseClick: () -> Unit,
    onCopyCardText: (String) -> Unit,
    onPresentLargeCard: (String, String?) -> Unit
) {
    val categoryScrollState = rememberScrollState()

    val filteredCards by remember(cards, selectedCategory, searchQuery, preferredLanguage) {
        derivedStateOf {
            cards.filter { card ->
                val matchesCategory = selectedCategory == null || card.category == selectedCategory
                val matchesQuery = searchQuery.isBlank() ||
                        card.englishText.contains(searchQuery, ignoreCase = true) ||
                        card.hindiText.contains(searchQuery, ignoreCase = true) ||
                        card.tamilText.contains(searchQuery, ignoreCase = true) ||
                        card.teluguText.contains(searchQuery, ignoreCase = true) ||
                        card.kannadaText.contains(searchQuery, ignoreCase = true) ||
                        (card.phoneticGuide?.contains(searchQuery, ignoreCase = true) ?: false)

                matchesCategory && matchesQuery
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Custom Text to Speech input card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Speak My Message (Type Anything)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (isSpeaking) {
                            AudioPulseIndicator(isSpeaking = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customInput,
                            onValueChange = onCustomInputChange,
                            placeholder = { Text("Enter message to speak or present...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_speech_input_field"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        AccessibleButton(
                            text = if (isSpeaking) "Stop" else "Speak",
                            icon = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                            onClick = {
                                if (isSpeaking) {
                                    onStopSpeaking()
                                } else if (customInput.isNotBlank()) {
                                    onSpeak(customInput, preferredLanguage)
                                }
                            },
                            enabled = isSpeaking || customInput.isNotBlank(),
                            testTag = "custom_speak_button"
                        )
                    }

                    if (customInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AccessibleButton(
                                text = "Show on Screen",
                                icon = Icons.Default.Fullscreen,
                                onClick = { onPresentLargeCard(customInput, null) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )
                            AccessibleButton(
                                text = "Copy",
                                icon = Icons.Default.ContentCopy,
                                onClick = { onCopyCardText(customInput) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Phrase Search Field & Category Chips
        item {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search everyday phrases (e.g. slow, ramp, ticket, doctor)...") },
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(categoryScrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        label = { Text("All Phrases (${cards.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    CommCategory.entries.forEach { category ->
                        val count = cards.count { it.category == category }
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { onSelectCategory(category) },
                            label = { Text("${category.title} ($count)") }
                        )
                    }
                }
            }
        }

        // Cards List
        if (filteredCards.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No Everyday Phrases Found",
                    description = "Try searching with a different word or create your own custom card.",
                    icon = Icons.Default.Search,
                    actionButtonText = "Create Custom Card",
                    onActionClick = onAddPhraseClick
                )
            }
        } else {
            items(filteredCards, key = { it.id }) { card ->
                CommunicationCardItem(
                    card = card,
                    preferredLanguage = preferredLanguage,
                    isSpeaking = isSpeaking,
                    onSpeak = onSpeak,
                    onShowFullscreen = { onShowFlashcard(card) },
                    onEdit = { onEditCard(card) },
                    onCopy = { onCopyCardText(card.englishText) }
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// TAB 3: TRANSLATE A MESSAGE
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TranslateMessageTabContent(
    translatedResult: TranslationResult.Success?,
    translationError: String?,
    preferredLanguage: AppLanguage,
    isSpeaking: Boolean,
    onTranslate: (String, AppLanguage, AppLanguage) -> Unit,
    onUpdateTranslatedText: (String) -> Unit,
    onClearTranslation: () -> Unit,
    onSpeak: (String, AppLanguage) -> Unit,
    onStopSpeaking: () -> Unit,
    onCopyText: (String) -> Unit,
    onShowLargeCard: (String, String?) -> Unit,
    onShareConfirm: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var sourceLang by remember { mutableStateOf(AppLanguage.ENGLISH) }
    var targetLang by remember {
        mutableStateOf(if (preferredLanguage != AppLanguage.ENGLISH) preferredLanguage else AppLanguage.HINDI)
    }

    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var targetDropdownExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Translation Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Translate Between Languages",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Convert everyday communication across regional languages",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Language Selector Row (Source -> Target)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Source Language
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { sourceDropdownExpanded = true }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("From", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        sourceLang.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            androidx.compose.material3.DropdownMenu(
                                expanded = sourceDropdownExpanded,
                                onDismissRequest = { sourceDropdownExpanded = false }
                            ) {
                                AppLanguage.entries.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text("${lang.displayName} - ${lang.localName}") },
                                        onClick = {
                                            sourceLang = lang
                                            sourceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Swap Button
                        IconButton(
                            onClick = {
                                val temp = sourceLang
                                sourceLang = targetLang
                                targetLang = temp
                            },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Swap source and target language")
                        }

                        // Target Language
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { targetDropdownExpanded = true }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("To", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        targetLang.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            androidx.compose.material3.DropdownMenu(
                                expanded = targetDropdownExpanded,
                                onDismissRequest = { targetDropdownExpanded = false }
                            ) {
                                AppLanguage.entries.forEach { lang ->
                                    DropdownMenuItem(
                                        text = { Text("${lang.displayName} - ${lang.localName}") },
                                        onClick = {
                                            targetLang = lang
                                            targetDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Type message to translate (e.g. Please speak slowly)...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccessibleButton(
                            text = "Translate Message",
                            icon = Icons.Default.Translate,
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    onTranslate(inputText, sourceLang, targetLang)
                                }
                            },
                            enabled = inputText.isNotBlank(),
                            isPrimary = true,
                            modifier = Modifier.weight(1f)
                        )

                        if (translatedResult != null || inputText.isNotBlank()) {
                            AccessibleButton(
                                text = "Clear",
                                icon = Icons.Default.Clear,
                                onClick = {
                                    inputText = ""
                                    onClearTranslation()
                                },
                                isPrimary = false
                            )
                        }
                    }

                    if (translationError != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = translationError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Translation Result Card
        if (translatedResult != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Original
                        Text(
                            text = "Original (${translatedResult.sourceLanguage.displayName}):",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = translatedResult.originalText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Translated (Editable)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Translation (${translatedResult.targetLanguage.displayName} - ${translatedResult.targetLanguage.localName}):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Editable",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = translatedResult.translatedText,
                            onValueChange = { onUpdateTranslatedText(it) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Important accuracy notice
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Translations are not guaranteed 100% accurate. You can edit the text before presenting or sharing.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AccessibleButton(
                                text = if (isSpeaking) "Stop Speaking" else "Speak Translation",
                                icon = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                onClick = {
                                    if (isSpeaking) onStopSpeaking()
                                    else onSpeak(translatedResult.translatedText, translatedResult.targetLanguage)
                                },
                                isPrimary = true,
                                modifier = Modifier.weight(1f)
                            )

                            AccessibleButton(
                                text = "Show Card",
                                icon = Icons.Default.Fullscreen,
                                onClick = { onShowLargeCard(translatedResult.translatedText, translatedResult.originalText) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AccessibleButton(
                                text = "Copy",
                                icon = Icons.Default.ContentCopy,
                                onClick = { onCopyText(translatedResult.translatedText) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )

                            AccessibleButton(
                                text = "Share",
                                icon = Icons.Default.Share,
                                onClick = { onShareConfirm(translatedResult.translatedText) },
                                isPrimary = false,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// REUSABLE CARD ITEM
// -------------------------------------------------------------------------
@Composable
private fun CommunicationCardItem(
    card: CommunicationCard,
    preferredLanguage: AppLanguage,
    isSpeaking: Boolean,
    onSpeak: (String, AppLanguage) -> Unit,
    onShowFullscreen: () -> Unit,
    onEdit: () -> Unit,
    onCopy: () -> Unit
) {
    val translatedText = card.getTextForLanguage(preferredLanguage)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("comm_card_${card.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = card.category.title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Edit phrase
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.semantics {
                            contentDescription = "Edit phrase: ${card.englishText}"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Copy phrase
                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.semantics {
                            contentDescription = "Copy phrase text"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Show screen / fullscreen icon for presenting to someone
                    IconButton(
                        onClick = onShowFullscreen,
                        modifier = Modifier.semantics {
                            contentDescription = "Show large flashcard on screen for: ${card.englishText}"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Primary English sentence
            Text(
                text = card.englishText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Local translation if not English
            if (preferredLanguage != AppLanguage.ENGLISH && translatedText != card.englishText) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = translatedText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (card.phoneticGuide != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pronounce: \"${card.phoneticGuide}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row: Speak in English or Speak in Local Language
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AccessibleButton(
                    text = "Speak English",
                    icon = Icons.Default.VolumeUp,
                    onClick = { onSpeak(card.englishText, AppLanguage.ENGLISH) },
                    isPrimary = true,
                    modifier = Modifier.weight(1f)
                )

                if (preferredLanguage != AppLanguage.ENGLISH) {
                    AccessibleButton(
                        text = "Speak ${preferredLanguage.localName}",
                        icon = Icons.Default.VolumeUp,
                        onClick = { onSpeak(translatedText, preferredLanguage) },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// DIALOGS: ADD, EDIT, FLASHCARD & LARGE MESSAGE
// -------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomPhraseDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, CommCategory) -> Unit
) {
    var phrase by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CommCategory.DAILY_NEEDS) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    val errorMessage = if (hasAttemptedSubmit && phrase.trim().isEmpty()) {
        "Please enter the phrase you wish to speak or display."
    } else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Custom Card",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Create a communication card tailored to your personal needs:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        CommCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.title) },
                                onClick = {
                                    selectedCategory = cat
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AccessibleTextField(
                    value = phrase,
                    onValueChange = { phrase = it },
                    label = "Phrase Text",
                    placeholder = "e.g. Please help me find Platform 4",
                    errorMessage = errorMessage
                )
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Add Card",
                onClick = {
                    hasAttemptedSubmit = true
                    if (phrase.trim().isNotEmpty()) {
                        onSubmit(phrase.trim(), selectedCategory)
                    }
                }
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
private fun EditPhraseDialog(
    card: CommunicationCard,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var editedText by remember { mutableStateOf(card.englishText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Everyday Phrase", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "You can customize this phrase before speaking or presenting it:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = editedText,
                    onValueChange = { editedText = it },
                    label = { Text("Phrase Text") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Save Changes",
                onClick = {
                    if (editedText.isNotBlank()) {
                        onSave(editedText)
                    }
                }
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
private fun FlashcardModal(
    card: CommunicationCard,
    preferredLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    var isInverted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Show Screen to Someone",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close flashcard")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Turn your phone towards the person so they can read clearly:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                val cardBg = if (isInverted) Color.White else Color.Black
                val cardFg = if (isInverted) Color.Black else Color.White
                val accentColor = if (isInverted) Color(0xFF1E3A8A) else Color(0xFFFDE047)

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg,
                    border = BorderStroke(2.dp, if (isInverted) Color.Black else Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = card.englishText,
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.Black,
                            color = cardFg,
                            textAlign = TextAlign.Center
                        )

                        val translated = card.getTextForLanguage(preferredLanguage)
                        if (preferredLanguage != AppLanguage.ENGLISH && translated != card.englishText) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = translated,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { isInverted = !isInverted }) {
                        Text(if (isInverted) "Dark Flashcard" else "Light Flashcard")
                    }

                    Row {
                        IconButton(onClick = onCopy) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy message")
                        }
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = "Share message")
                        }
                    }
                }
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Speak Aloud",
                icon = Icons.Default.VolumeUp,
                onClick = onSpeak,
                isPrimary = true
            )
        },
        dismissButton = {
            AccessibleButton(
                text = "Close",
                onClick = onDismiss,
                isPrimary = false
            )
        }
    )
}

@Composable
private fun LargeMessageDisplayModal(
    primaryText: String,
    secondaryText: String?,
    onDismiss: () -> Unit,
    onSpeak: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    var isInverted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Large Message Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close message card")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val cardBg = if (isInverted) Color.White else Color.Black
                val cardFg = if (isInverted) Color.Black else Color.White
                val secondaryColor = if (isInverted) Color(0xFF1E3A8A) else Color(0xFFFDE047)

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg,
                    border = BorderStroke(2.dp, if (isInverted) Color.Black else Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = primaryText,
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.Black,
                            color = cardFg,
                            textAlign = TextAlign.Center
                        )

                        if (!secondaryText.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = secondaryText,
                                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                                fontWeight = FontWeight.Bold,
                                color = secondaryColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { isInverted = !isInverted }) {
                        Text(if (isInverted) "Dark Card" else "Light Card")
                    }

                    Row {
                        IconButton(onClick = onCopy) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy message")
                        }
                        IconButton(onClick = onShare) {
                            Icon(Icons.Default.Share, contentDescription = "Share message")
                        }
                    }
                }
            }
        },
        confirmButton = {
            AccessibleButton(
                text = "Speak Aloud",
                icon = Icons.Default.VolumeUp,
                onClick = onSpeak,
                isPrimary = true
            )
        },
        dismissButton = {
            AccessibleButton(
                text = "Close",
                onClick = onDismiss,
                isPrimary = false
            )
        }
    )
}
