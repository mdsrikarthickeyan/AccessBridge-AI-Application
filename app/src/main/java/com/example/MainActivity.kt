package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AccessBridgeBottomNav
import com.example.ui.components.AccessBridgeTopBar
import com.example.ui.components.FeedbackBanner
import com.example.ui.screens.AccessiblePlacesScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.BarrierReportsScreen
import com.example.ui.screens.CommunicateScreen
import com.example.ui.screens.HelpScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PreferencesScreen
import com.example.ui.screens.ScanExplainScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.AccessBridgeTheme
import com.example.ui.viewmodel.AccessBridgeViewModel
import com.example.ui.viewmodel.AppScreen

class MainActivity : ComponentActivity() {
    private val viewModel: AccessBridgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences by viewModel.preferences.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()
            val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()

            AccessBridgeTheme(
                highContrast = preferences.highContrast,
                fontScale = preferences.fontScale,
                dyslexiaSpacing = preferences.dyslexiaSpacing
            ) {
                // Predictive back handler
                BackHandler(enabled = currentScreen != AppScreen.HOME) {
                    viewModel.navigateBack()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    topBar = {
                        if (currentScreen != AppScreen.WELCOME) {
                            AccessBridgeTopBar(
                                currentScreen = currentScreen,
                                preferredLanguage = preferences.preferredLanguage,
                                isHighContrast = preferences.highContrast,
                                fontScale = preferences.fontScale,
                                onNavigateBack = { viewModel.navigateBack() },
                                onToggleHighContrast = {
                                    viewModel.setHighContrast(!preferences.highContrast)
                                },
                                onStepFontScale = {
                                    val nextScale = when {
                                        preferences.fontScale < 1.14f -> 1.20f
                                        preferences.fontScale < 1.29f -> 1.35f
                                        preferences.fontScale < 1.49f -> 1.50f
                                        else -> 1.0f
                                    }
                                    viewModel.setFontScale(nextScale)
                                },
                                onSelectLanguage = { lang ->
                                    viewModel.setPreferredLanguage(lang)
                                },
                                onOpenPreferences = {
                                    viewModel.navigateTo(AppScreen.PREFERENCES)
                                },
                                onOpenHelp = {
                                    viewModel.navigateTo(AppScreen.HELP)
                                }
                            )
                        }
                    },
                    bottomBar = {
                        if (currentScreen != AppScreen.WELCOME) {
                            AccessBridgeBottomNav(
                                currentScreen = currentScreen,
                                onNavigate = { screen -> viewModel.navigateTo(screen) }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // User feedback notification banner
                        FeedbackBanner(
                            message = feedbackMessage,
                            onDismiss = { viewModel.clearFeedbackMessage() }
                        )

                        // Main Screen Routing
                        when (currentScreen) {
                            AppScreen.WELCOME -> {
                                WelcomeScreen(
                                    onGetStarted = { viewModel.navigateTo(AppScreen.HOME) },
                                    onAdjustPreferences = { viewModel.navigateTo(AppScreen.PREFERENCES) }
                                )
                            }

                            AppScreen.HOME -> {
                                val recentlyUsed by viewModel.recentlyUsed.collectAsStateWithLifecycle()
                                HomeScreen(
                                    recentlyUsed = recentlyUsed,
                                    preferences = preferences,
                                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                                    onQuickToggleContrast = {
                                        viewModel.setHighContrast(!preferences.highContrast)
                                    },
                                    onClearRecentlyUsed = {
                                        viewModel.clearRecentlyUsed()
                                    },
                                    isHighContrast = preferences.highContrast
                                )
                            }

                            AppScreen.SCAN_EXPLAIN -> {
                                val notices by viewModel.notices.collectAsStateWithLifecycle()
                                val selectedDoc by viewModel.currentExplainedDoc.collectAsStateWithLifecycle()
                                val isProcessing by viewModel.isProcessingNotice.collectAsStateWithLifecycle()
                                val processingStepText by viewModel.processingNoticeStep.collectAsStateWithLifecycle()
                                val documentError by viewModel.documentError.collectAsStateWithLifecycle()
                                val categoryFilter by viewModel.noticeCategoryFilter.collectAsStateWithLifecycle()
                                val showOnlySaved by viewModel.showOnlySavedNotices.collectAsStateWithLifecycle()
                                val isTranslating by viewModel.isTranslating.collectAsStateWithLifecycle()
                                val isAnsweringQuestion by viewModel.isAnsweringQuestion.collectAsStateWithLifecycle()

                                ScanExplainScreen(
                                    notices = notices,
                                    selectedDocument = selectedDoc,
                                    isProcessing = isProcessing,
                                    processingStepText = processingStepText,
                                    documentError = documentError,
                                    categoryFilter = categoryFilter,
                                    showOnlySaved = showOnlySaved,
                                    isSpeaking = isSpeaking,
                                    isTranslating = isTranslating,
                                    isAnsweringQuestion = isAnsweringQuestion,
                                    onSelectSampleNotice = { notice -> viewModel.selectSampleNotice(notice) },
                                    onSetCategoryFilter = { cat -> viewModel.setNoticeCategoryFilter(cat) },
                                    onToggleShowOnlySaved = { viewModel.toggleShowOnlySavedNotices() },
                                    onProcessDocument = { uri, text, category, title ->
                                        viewModel.processDocument(uri, text, category, title)
                                    },
                                    onTranslateDocument = { lang -> viewModel.translateCurrentDocument(lang) },
                                    onAskQuestion = { q -> viewModel.askFollowUpQuestion(q) },
                                    onToggleSave = { viewModel.toggleSaveCurrentDocument() },
                                    onResetScan = { viewModel.resetScanToNew() },
                                    onSpeak = { text -> viewModel.speakText(text) },
                                    onStopSpeaking = { viewModel.stopSpeaking() },
                                    onToggleChecklistItem = { id -> viewModel.toggleChecklistItem(id) },
                                    onAddChecklistItem = { text -> viewModel.addPersonalChecklistItem(text) },
                                    onRemoveChecklistItem = { id -> viewModel.removePersonalChecklistItem(id) }
                                )
                            }

                            AppScreen.PLACES -> {
                                val places by viewModel.places.collectAsStateWithLifecycle()
                                val selectedPlace by viewModel.selectedPlace.collectAsStateWithLifecycle()
                                val searchQuery by viewModel.placesSearchQuery.collectAsStateWithLifecycle()
                                val selectedType by viewModel.selectedPlaceType.collectAsStateWithLifecycle()
                                val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
                                val wheelchairOnly by viewModel.filterWheelchairOnly.collectAsStateWithLifecycle()
                                val tactileOnly by viewModel.filterTactileOnly.collectAsStateWithLifecycle()
                                val bookmarkedPlaceIds by viewModel.bookmarkedPlaceIds.collectAsStateWithLifecycle()

                                AccessiblePlacesScreen(
                                    places = places,
                                    selectedPlace = selectedPlace,
                                    searchQuery = searchQuery,
                                    selectedType = selectedType,
                                    selectedCity = selectedCity,
                                    wheelchairOnly = wheelchairOnly,
                                    tactileOnly = tactileOnly,
                                    bookmarkedPlaceIds = bookmarkedPlaceIds,
                                    preferences = preferences,
                                    onSelectPlace = { place -> viewModel.selectPlace(place) },
                                    onSearchQueryChange = { query -> viewModel.setPlacesSearchQuery(query) },
                                    onSelectType = { type -> viewModel.setPlaceTypeFilter(type) },
                                    onSelectCity = { city -> viewModel.setCityFilter(city) },
                                    onToggleWheelchair = { viewModel.toggleWheelchairFilter() },
                                    onToggleTactile = { viewModel.toggleTactileFilter() },
                                    onToggleBookmarkPlace = { id -> viewModel.toggleBookmarkPlace(id) },
                                    onReportBarrierAtPlace = { place -> viewModel.reportBarrierAtPlace(place) }
                                )
                            }

                            AppScreen.COMMUNICATE -> {
                                val cards by viewModel.commCards.collectAsStateWithLifecycle()
                                val selectedCategory by viewModel.selectedCommCategory.collectAsStateWithLifecycle()
                                val searchQuery by viewModel.commSearchQuery.collectAsStateWithLifecycle()
                                val customInput by viewModel.customCommInput.collectAsStateWithLifecycle()
                                val flashcardToShow by viewModel.flashcardToShow.collectAsStateWithLifecycle()
                                val speechRecognitionState by viewModel.speechRecognitionState.collectAsStateWithLifecycle()
                                val isListeningToSpeech by viewModel.isListeningToSpeech.collectAsStateWithLifecycle()
                                val recognizedSpeechText by viewModel.recognizedSpeechText.collectAsStateWithLifecycle()
                                val translatedResult by viewModel.translatedCommResult.collectAsStateWithLifecycle()
                                val translationError by viewModel.translationError.collectAsStateWithLifecycle()

                                CommunicateScreen(
                                    cards = cards,
                                    selectedCategory = selectedCategory,
                                    searchQuery = searchQuery,
                                    customInput = customInput,
                                    preferredLanguage = preferences.preferredLanguage,
                                    isSpeaking = isSpeaking,
                                    flashcardToShow = flashcardToShow,
                                    speechRecognitionState = speechRecognitionState,
                                    isListeningToSpeech = isListeningToSpeech,
                                    recognizedSpeechText = recognizedSpeechText,
                                    translatedResult = translatedResult,
                                    translationError = translationError,
                                    onSelectCategory = { cat -> viewModel.selectCommCategory(cat) },
                                    onSearchQueryChange = { query -> viewModel.setCommSearchQuery(query) },
                                    onCustomInputChange = { text -> viewModel.setCustomCommInput(text) },
                                    onAddCustomPhrase = { text, cat -> viewModel.addCustomPhrase(text, cat) },
                                    onUpdateCard = { id, text -> viewModel.updateCommunicationCard(id, text) },
                                    onSpeakCard = { text, lang -> viewModel.speakText(text, lang) },
                                    onStopSpeaking = { viewModel.stopSpeaking() },
                                    onShowFlashcard = { card -> viewModel.showFlashcard(card) },
                                    onStartSpeechToText = { lang -> viewModel.startSpeechToText(lang) },
                                    onStopSpeechToText = { viewModel.stopSpeechToText() },
                                    onClearSpeechToText = { viewModel.clearSpeechToText() },
                                    onTranslateMessage = { text, src, target -> viewModel.translateMessage(text, src, target) },
                                    onUpdateTranslatedText = { text -> viewModel.updateTranslatedText(text) },
                                    onClearTranslation = { viewModel.clearTranslation() }
                                )
                            }

                            AppScreen.REPORTS -> {
                                val reports by viewModel.barrierReports.collectAsStateWithLifecycle()
                                val statusFilter by viewModel.selectedReportStatusFilter.collectAsStateWithLifecycle()
                                val isDialogOpen by viewModel.isReportDialogOpen.collectAsStateWithLifecycle()
                                val prefilledLocation by viewModel.prefilledLocationForReport.collectAsStateWithLifecycle()
                                val currentUserRole by viewModel.currentUserRole.collectAsStateWithLifecycle()
                                val statusHistory by viewModel.statusHistory.collectAsStateWithLifecycle()

                                BarrierReportsScreen(
                                    reports = reports,
                                    statusFilter = statusFilter,
                                    isDialogOpen = isDialogOpen,
                                    prefilledLocation = prefilledLocation,
                                    currentUserRole = currentUserRole,
                                    statusHistory = statusHistory,
                                    onFilterStatus = { filter -> viewModel.setReportStatusFilter(filter) },
                                    onOpenDialog = { viewModel.openReportDialog() },
                                    onCloseDialog = { viewModel.closeReportDialog() },
                                    onSwitchUserRole = { role -> viewModel.setUserRole(role) },
                                    onSubmitReport = { title, cat, loc, city, details, sev, contact, img ->
                                        viewModel.submitNewBarrierReport(title, cat, loc, city, details, sev, contact, img)
                                    },
                                    onUpdateReportStatus = { reportId, newStatus, note, action, conflict ->
                                        viewModel.updateReportStatus(reportId, newStatus, note, action, conflict)
                                    },
                                    onDeleteReport = { report -> viewModel.deleteBarrierReport(report) }
                                )
                            }

                            AppScreen.PREFERENCES -> {
                                PreferencesScreen(
                                    preferences = preferences,
                                    onSetHighContrast = { enabled -> viewModel.setHighContrast(enabled) },
                                    onSetFontScale = { scale -> viewModel.setFontScale(scale) },
                                    onSetDyslexiaSpacing = { enabled -> viewModel.setDyslexiaSpacing(enabled) },
                                    onSetReducedMotion = { enabled -> viewModel.setReducedMotion(enabled) },
                                    onSetKeyboardFriendly = { enabled -> viewModel.setKeyboardFriendly(enabled) },
                                    onSetReduceUnnecessarySteps = { enabled -> viewModel.setReduceUnnecessarySteps(enabled) },
                                    onSetLanguage = { lang -> viewModel.setPreferredLanguage(lang) },
                                    onSetSpokenOutputEnabled = { enabled -> viewModel.setSpokenOutputEnabled(enabled) },
                                    onSetSpeechRate = { rate -> viewModel.setSpeechRate(rate) },
                                    onSetVoiceTone = { tone -> viewModel.setVoiceTone(tone) },
                                    onSetHaptic = { enabled -> viewModel.setHapticFeedback(enabled) },
                                    onToggleFacilityRequirement = { key -> viewModel.toggleFacilityRequirement(key) },
                                    onSavePreferences = { prefs -> viewModel.savePreferences(prefs) },
                                    onTestSpeech = { viewModel.testSpeech() },
                                    onResetDefaults = { viewModel.resetPreferences() }
                                )
                            }

                            AppScreen.HELP -> {
                                HelpScreen()
                            }

                            AppScreen.ADMIN_DASHBOARD -> {
                                val reports by viewModel.barrierReports.collectAsStateWithLifecycle()
                                val places by viewModel.places.collectAsStateWithLifecycle()
                                val statusHistory by viewModel.statusHistory.collectAsStateWithLifecycle()
                                val currentUserRole by viewModel.currentUserRole.collectAsStateWithLifecycle()

                                AdminDashboardScreen(
                                    reports = reports,
                                    places = places,
                                    statusHistory = statusHistory,
                                    currentUserRole = currentUserRole,
                                    onSwitchRole = { role -> viewModel.setUserRole(role) },
                                    onUpdateReportStatus = { reportId, newStatus, note, action, conflict ->
                                        viewModel.updateReportStatus(reportId, newStatus, note, action, conflict)
                                    },
                                    onSaveFacility = { place -> viewModel.saveOrUpdateFacility(place) },
                                    onMarkOutdated = { facilityId -> viewModel.markFacilityOutdated(facilityId) },
                                    onResolveConflict = { facilityId, resolve, note -> viewModel.resolveFacilityConflict(facilityId, resolve, note) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
