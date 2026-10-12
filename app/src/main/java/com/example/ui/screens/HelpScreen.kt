package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AccessibleButton
import com.example.ui.components.AccessibleConfirmationDialog

data class HelplineContact(
    val name: String,
    val number: String,
    val purpose: String
)

data class FaqItem(
    val question: String,
    val answer: String
)

@Composable
fun HelpScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var helplineToCall by remember { mutableStateOf<HelplineContact?>(null) }

    val helplines = listOf(
        HelplineContact(
            name = "National Disability Helpline",
            number = "1800-11-2001",
            purpose = "Department of Empowerment of Persons with Disabilities (Toll Free)"
        ),
        HelplineContact(
            name = "National Emergency Number",
            number = "112",
            purpose = "Unified police, fire & medical emergency response"
        ),
        HelplineContact(
            name = "Rail Madad Divyangjan Assistance",
            number = "139",
            purpose = "Wheelchair, battery buggy & train lower-berth boarding"
        ),
        HelplineContact(
            name = "National Health Helpline",
            number = "1075",
            purpose = "Government hospital guidance & assistive device schemes"
        )
    )

    val faqs = listOf(
        FaqItem(
            question = "How do I request a wheelchair or battery cart at railway stations?",
            answer = "Dial 139 at least 2 hours before your train leaves, or visit the Divyangjan Help Booth at Platform 1. You can also book battery buggies on the Rail Madad mobile portal."
        ),
        FaqItem(
            question = "What extra time is granted to students in exams?",
            answer = "Under RPwD Act guidelines, candidates with benchmark disabilities receive a minimum of 20 minutes compensatory extra time per examination hour and the right to an approved scribe."
        ),
        FaqItem(
            question = "How does AccessBridge protect my personal information?",
            answer = "All your saved notices, barrier reports, and preferences are stored exclusively on your device. No cloud account or tracking is required."
        )
    )

    Column(
        modifier = modifier
            .testTag("help_screen")
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Help, Helplines & Rights",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Official emergency helplines in India and your rights under the RPwD Act 2016",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Helplines Section
        Text(
            text = "National Helplines (India)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        helplines.forEach { helpline ->
            HelplineCard(
                contact = helpline,
                onCall = { helplineToCall = helpline }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Rights Section
        Text(
            text = "Your Rights Under RPwD Act 2016",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                RightsBulletPoint(
                    title = "Mandatory Accessibility of Public Buildings (Section 40-44)",
                    description = "Government and private public establishments must provide ramps, lifts, accessible toilets, and tactile paths."
                )

                Spacer(modifier = Modifier.height(12.dp))

                RightsBulletPoint(
                    title = "Reasonable Accommodation & Non-Discrimination",
                    description = "No institution may deny service, employment, or education on grounds of disability. Necessary modifications must be provided."
                )

                Spacer(modifier = Modifier.height(12.dp))

                RightsBulletPoint(
                    title = "Accessible Public Transport & Information (Section 41-42)",
                    description = "Buses, trains, airports, and official websites must offer audio announcements, tactile guidance, and accessible formats."
                )

                Spacer(modifier = Modifier.height(12.dp))

                RightsBulletPoint(
                    title = "Compensatory Time in Examinations",
                    description = "Candidates with benchmark disabilities receive a minimum of 20 minutes extra time per hour and approved scribe support."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // FAQ Section
        Text(
            text = "Common Questions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        faqs.forEach { faq ->
            FaqCard(faq = faq)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About AccessBridge AI
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "About AccessBridge AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "AccessBridge AI is designed for universal inclusion, supporting individuals with visual, hearing, mobility, speech, and cognitive disabilities across India. Built for public institutions, colleges, hospitals, and transit hubs.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    // Call Confirmation Dialog to prevent accidental pocket calls
    if (helplineToCall != null) {
        AccessibleConfirmationDialog(
            title = "Call ${helplineToCall?.name}?",
            description = "You are about to dial ${helplineToCall?.number} (${helplineToCall?.purpose}).",
            confirmButtonText = "Dial Number",
            cancelButtonText = "Cancel",
            icon = Icons.Default.Phone,
            onConfirm = {
                helplineToCall?.let { h ->
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${h.number.replace("-", "")}"))
                    context.startActivity(intent)
                }
                helplineToCall = null
            },
            onDismiss = { helplineToCall = null }
        )
    }
}

@Composable
private fun HelplineCard(
    contact: HelplineContact,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = contact.purpose,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = contact.number,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            AccessibleButton(
                text = "Call",
                icon = Icons.Default.Call,
                onClick = onCall,
                isPrimary = true
            )
        }
    }
}

@Composable
private fun FaqCard(faq: FaqItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        onClick = { isExpanded = !isExpanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse question" else "Expand question",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RightsBulletPoint(
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
