package com.example.model

data class DocumentChecklistItem(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false,
    val isUserCreated: Boolean = false
)

data class DocumentQA(
    val id: String,
    val question: String,
    val answer: String,
    val timestamp: String
)

data class ExplainedDocument(
    val id: String,
    val title: String,
    val category: NoticeCategory,
    val originalExtractedText: String,
    val simpleExplanation: String,
    val translatedExplanation: String? = null,
    val activeLanguage: AppLanguage = AppLanguage.ENGLISH,
    val numberedSteps: List<String> = emptyList(),
    val requiredDocuments: List<String> = emptyList(),
    val importantDates: List<String> = emptyList(),
    val eligibilityConditions: List<String> = emptyList(),
    val directionsAndRooms: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val contactInfo: String? = null,
    val ambiguousOrReviewItems: List<String> = emptyList(),
    val personalChecklist: List<DocumentChecklistItem> = emptyList(),
    val imageUri: String? = null,
    val imageFileName: String? = null,
    val isAiAnalyzed: Boolean = true,
    val aiProviderNote: String? = null,
    val followUpQa: List<DocumentQA> = emptyList(),
    val isSaved: Boolean = false,
    val dateAdded: String
)
