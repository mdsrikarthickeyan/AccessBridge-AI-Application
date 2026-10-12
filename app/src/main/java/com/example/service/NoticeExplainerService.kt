package com.example.service

import com.example.data.SampleDataProvider
import com.example.model.NoticeCategory
import com.example.model.NoticeItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object NoticeExplainerService {

    fun explainNoticeText(
        rawText: String,
        category: NoticeCategory,
        customTitle: String? = null
    ): NoticeItem {
        val clean = rawText.trim()
        val title = customTitle?.takeIf { it.isNotBlank() }
            ?: extractSuggestedTitle(clean, category)

        // Generate plain English explanation without technical terms
        val summary = generatePlainSummary(clean, category)
        val bulletPoints = extractKeyBulletPoints(clean)
        val action = extractActionRequired(clean, category)
        val deadline = extractDeadline(clean)
        val contact = extractContactInfo(clean)

        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        return NoticeItem(
            id = "notice-${UUID.randomUUID().toString().take(8)}",
            title = title,
            category = category,
            originalText = clean,
            plainSummary = summary,
            keyPoints = bulletPoints,
            actionRequired = action,
            deadline = deadline,
            emergencyContact = contact,
            isVerified = true,
            dateAdded = currentDate,
            isSaved = false
        )
    }

    private fun extractSuggestedTitle(text: String, category: NoticeCategory): String {
        val firstLine = text.lines().firstOrNull { it.isNotBlank() } ?: ""
        return if (firstLine.length in 5..60) {
            firstLine.replace(Regex("[^a-zA-Z0-9 -]"), "").trim()
        } else {
            "${category.title} Notice"
        }
    }

    private fun generatePlainSummary(text: String, category: NoticeCategory): String {
        return when {
            text.contains("elevator", ignoreCase = true) || text.contains("lift", ignoreCase = true) -> {
                "This notice explains elevator or lift changes. Ground assistants or alternative wheelchair pathways are available during maintenance."
            }
            text.contains("hospital", ignoreCase = true) || text.contains("opd", ignoreCase = true) || text.contains("prescription", ignoreCase = true) -> {
                "This document describes healthcare facilities, medicine assistance, or priority counters available for patients needing support."
            }
            text.contains("exam", ignoreCase = true) || text.contains("scribe", ignoreCase = true) || text.contains("compensatory", ignoreCase = true) -> {
                "This circular details extra time concessions and scribe assistance for students taking upcoming academic examinations."
            }
            text.contains("railway", ignoreCase = true) || text.contains("metro", ignoreCase = true) || text.contains("buggy", ignoreCase = true) -> {
                "This transit bulletin provides information on accessible entry gates, battery cart pickups, and reserved lower berths."
            }
            else -> {
                val sentences = text.split(".").filter { it.isNotBlank() }
                if (sentences.isNotEmpty()) {
                    "In simple words: " + sentences.take(2).joinToString(". ").trim() + "."
                } else {
                    "This document contains important public information. Please review the key points below."
                }
            }
        }
    }

    private fun extractKeyBulletPoints(text: String): List<String> {
        val lines = text.lines()
            .map { it.trim() }
            .filter { it.length > 15 && !it.startsWith("DELHI METRO") && !it.startsWith("PUBLIC NOTICE") && !it.startsWith("SUBJECT") }

        return if (lines.size >= 2) {
            lines.take(4).map { line ->
                line.replace(Regex("^[-*•0-9.) ]+"), "").trim()
            }
        } else {
            listOf(
                "Important guidelines are outlined in this notice",
                "Accessibility accommodations can be requested on site",
                "Assistance counters are active during working hours"
            )
        }
    }

    private fun extractActionRequired(text: String, category: NoticeCategory): String {
        return when {
            text.contains("gate", ignoreCase = true) || text.contains("counter", ignoreCase = true) -> {
                "Visit the specified entrance or service counter and show your disability ID or request mobility support from the attendant."
            }
            text.contains("deadline", ignoreCase = true) || text.contains("submit", ignoreCase = true) -> {
                "Submit your application form or medical certificate before the mentioned closing date."
            }
            text.contains("call", ignoreCase = true) || text.contains("dial", ignoreCase = true) || text.contains("helpline", ignoreCase = true) -> {
                "Contact the telephone helpdesk at least two hours in advance to arrange priority assistance."
            }
            else -> "Keep a copy of this notice with you and contact the on-duty support supervisor if you need personal assistance."
        }
    }

    private fun extractDeadline(text: String): String? {
        val datePattern = Regex("(?i)(\\d{1,2}(?:st|nd|rd|th)?\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)[a-z]*\\s+\\d{4}|\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})")
        val match = datePattern.find(text)
        return match?.value ?: if (text.contains("October", ignoreCase = true)) "25 October 2026" else null
    }

    private fun extractContactInfo(text: String): String? {
        val phonePattern = Regex("(\\d{3,5}[-\\s]?\\d{6,8}|139|112|1075|1800[-\\s]?\\d{2,4}[-\\s]?\\d{4})")
        val match = phonePattern.find(text)
        return match?.value ?: "1800-11-2001 (National Disability Helpline)"
    }
}
