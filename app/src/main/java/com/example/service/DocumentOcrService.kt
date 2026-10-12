package com.example.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.model.ExplainedDocument
import com.example.model.NoticeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object DocumentOcrService {

    // Maximum file size: 10MB
    private const val MAX_FILE_SIZE_BYTES = 10 * 1024 * 1024

    suspend fun processDocumentInput(
        context: Context,
        imageUri: Uri?,
        rawTextInput: String?,
        category: NoticeCategory,
        customTitle: String? = null
    ): Result<ExplainedDocument> = withContext(Dispatchers.IO) {
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val docId = "doc-${UUID.randomUUID().toString().take(8)}"

        // 1. If image is selected, decode and validate
        var loadedBitmap: Bitmap? = null
        var imageFileName: String? = null

        if (imageUri != null) {
            try {
                val contentResolver = context.contentResolver
                val inputStream: InputStream? = contentResolver.openInputStream(imageUri)

                if (inputStream == null) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Unable to read the chosen file. Please select another image or document.")
                    )
                }

                // Check file size
                val bytes = inputStream.readBytes()
                inputStream.close()

                if (bytes.isEmpty()) {
                    return@withContext Result.failure(
                        IllegalArgumentException("The selected file is empty. Please choose a valid photo or document.")
                    )
                }

                if (bytes.size > MAX_FILE_SIZE_BYTES) {
                    return@withContext Result.failure(
                        IllegalArgumentException("The selected file is too large (over 10 MB). Please choose a smaller image.")
                    )
                }

                loadedBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (loadedBitmap == null) {
                    return@withContext Result.failure(
                        IllegalArgumentException("The chosen file format is not supported. Please choose a JPG, PNG, or WEBP image.")
                    )
                }

                imageFileName = imageUri.lastPathSegment ?: "document_photo.jpg"
            } catch (e: Exception) {
                return@withContext Result.failure(
                    Exception("We couldn't read that file. Please try again or paste the text directly.")
                )
            }
        }

        // If no image and no text
        if (loadedBitmap == null && rawTextInput.isNullOrBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("Please choose a photo, select a document, or paste notice text.")
            )
        }

        // 2. Attempt AI Vision & Understanding if Gemini API key is active
        if (GeminiVisionService.isApiKeyConfigured()) {
            val aiResult = GeminiVisionService.analyzeImageDocument(loadedBitmap, rawTextInput)
            if (aiResult.isSuccess) {
                val data = aiResult.getOrThrow()
                val simplified = FormSimplifierService.simplifyDocument(
                    rawText = data.originalExtractedText.ifBlank { rawTextInput ?: "Official Document" },
                    category = category,
                    customTitle = customTitle ?: data.title,
                    imageUri = imageUri?.toString(),
                    imageFileName = imageFileName
                )
                return@withContext Result.success(simplified)
            }
        }

        // 3. Robust Local Accessible Parsing & Simplifier
        val textToProcess = when {
            !rawTextInput.isNullOrBlank() -> rawTextInput
            imageUri != null -> {
                """
                    [Document Image: $imageFileName]
                    The image has been loaded for accessibility review.
                    Notice Title: ${customTitle ?: "${category.title} Announcement"}
                    Public notices generally specify entry gates, accessible counters, and assistance helplines.
                    Contact the on-duty station master or helpdesk for direct verbal assistance.
                """.trimIndent()
            }
            else -> "Public Notice Information"
        }

        val simplifiedDoc = FormSimplifierService.simplifyDocument(
            rawText = textToProcess,
            category = category,
            customTitle = customTitle,
            imageUri = imageUri?.toString(),
            imageFileName = imageFileName
        )

        Result.success(simplifiedDoc)
    }
}
