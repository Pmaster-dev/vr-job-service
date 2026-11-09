package com.example.jobmagician.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobmagician.data.db.SavedAccommodationRecord

enum class AccommodationTemplateType(val displayName: String, val badgeColorHex: Long) {
    CDI_ASL("Certified Deaf Interpreter (CDI)", 0xFF3B82F6),
    CART_CAPTIONS("Live CART Captioning", 0xFF10B981),
    VISUAL_ALERTS("Visual Strobes & Notification Alerts", 0xFF8B5CF6),
    ERGONOMICS("Sightlines & Lighting Ergonomics", 0xFFF59E0B),
    ASYNC_STANDUP("Async Agendas & Meeting Transcripts", 0xFFEC4899)
}

enum class DisclosureStage(val stageName: String, val timingRecommendation: String, val legalNote: String) {
    APPLICATION(
        "1. Application Stage",
        "Generally Not Recommended",
        "Disability is protected and employers cannot inquire about medical conditions before offer. Only disclose if the application itself contains an inaccessible assessment test."
    ),
    INTERVIEW_INVITATION(
        "2. Interview Invitation",
        "★ Highly Recommended",
        "The ideal window under EEOC guidance. Gives HR/recruiters 3-5 days to book qualified RID/CDI interpreters or CART providers without delaying your candidacy."
    ),
    POST_OFFER(
        "3. Post-Job Offer",
        "Protected & Ideal for Equipment",
        "Once an offer is extended, request workplace accommodations (monitors, strobe alarms, ongoing team interpreters) during the interactive dialogue."
    ),
    ON_THE_JOB(
        "4. During Employment",
        "Ongoing Interactive Process",
        "As projects evolve or offsite team retreats are planned, submit updated Title I accommodation addendums."
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaAccommodationStudioScreen(
    savedAccommodations: List<SavedAccommodationRecord>,
    onSaveAccommodation: (String, String, String, String, String, String) -> Unit,
    onDeleteAccommodation: (String) -> Unit,
    onExportToDoc: (SavedAccommodationRecord) -> Unit,
    onExportToEmail: (String, SavedAccommodationRecord) -> Unit,
    onNavigateToSimulator: () -> Unit,
    onNavigateToCityForms: (() -> Unit)? = null
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Letter Generator, 1 = Disclosure Timing Guide, 2 = Saved Vault

    var selectedTemplate by remember { mutableStateOf(AccommodationTemplateType.CDI_ASL) }
    var selectedStage by remember { mutableStateOf(DisclosureStage.INTERVIEW_INVITATION) }

    var companyName by remember { mutableStateOf("Stripe") }
    var roleTitle by remember { mutableStateOf("Senior Mobile Engineer") }
    var counselorRef by remember { mutableStateOf("TWS-VR-2026-8841 (Counselor: Sarah Jenkins, CRC)") }
    var customNotes by remember { mutableStateOf("Candidate prefers low-latency direct video pinning for interpreters during technical coding screens.") }

    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var showExportEmailDialog by remember { mutableStateOf<SavedAccommodationRecord?>(null) }
    var recipientEmailInput by remember { mutableStateOf("recruiting-accommodations@stripe.com") }

    // Dynamic Letter Builder
    val generatedLetter = remember(selectedTemplate, selectedStage, companyName, roleTitle, counselorRef, customNotes) {
        val specificAccommodations = when (selectedTemplate) {
            AccommodationTemplateType.CDI_ASL -> """
                1. Dual Certified ASL Interpreter Services (RID/CDI Certified) for all technical, architectural, and behavioral interview panels.
                2. Direct video pin and shared screen permissions for the interpreter stream.
                3. Pre-meeting technical vocabulary glossary sharing (5 minutes prior to session start).
            """.trimIndent()

            AccommodationTemplateType.CART_CAPTIONS -> """
                1. Live CART (Communication Access Realtime Translation) verbatim English captioning provided via high-speed API stream or embedded video window.
                2. Post-interview transcript provided to candidate for review of technical specifications.
                3. Minimum 220 WPM certified real-time stenographer standard.
            """.trimIndent()

            AccommodationTemplateType.VISUAL_ALERTS -> """
                1. Visual strobe alarms integrated with emergency building systems and office notification beacons.
                2. Optical doorbell / tactile vibration sensors for individual workstation or conference room entry.
                3. Visual screen banners for priority Slack/Teams audio ring alerts.
            """.trimIndent()

            AccommodationTemplateType.ERGONOMICS -> """
                1. U-shaped or direct circular seating layout for conference rooms ensuring clear line of sight to all speakers.
                2. Glare-free, natural color rendering lighting (minimum 500 lux) to prevent eye strain during prolonged visual signing.
                3. Dual 27-inch 4K monitors to allow parallel IDE viewing and video relay communication windows.
            """.trimIndent()

            AccommodationTemplateType.ASYNC_STANDUP -> """
                1. Mandatory written meeting agendas provided at least 1 hour prior to synchronous sprint syncs.
                2. Summary bullet points posted to team chat channels following voice discussions.
                3. Permission to participate asynchronously via text updates or recorded ASL video memos for daily standups.
            """.trimIndent()
        }

        """
To: Human Resources, Talent Acquisition & Disability Inclusion Office
Organization: $companyName
Regarding: Title I ADA Reasonable Accommodation Request
Position: $roleTitle
Timing: ${selectedStage.stageName}

Dear Hiring Team,

I am writing to formally request reasonable accommodations under Title I of the Americans with Disabilities Act (ADA), 42 U.S.C. § 12112, and Section 504 of the Rehabilitation Act of 1973.

As a qualified Deaf professional, these accommodations will enable equal opportunity to demonstrate my technical competencies and participate fully in the interview and work environment:

$specificAccommodations

Additional Setup Notes:
$customNotes

Vocational Rehabilitation Sponsorship & Case Coordination:
State Vocational Rehabilitation Reference: $counselorRef. State VR/TWS can coordinate qualified interpreter agencies and assist with technical equipment logistics if desired.

Furthermore, please note that employers providing accommodations may qualify for the Federal Disabled Access Tax Credit (IRS Code Section 44) and the Work Opportunity Tax Credit (WOTC).

Thank you for your partnership in fostering an accessible, inclusive hiring process.

Sincerely,
Jordan Vance
DeafAuth Decentralized ID: DA-7928-PINK
Email: jordan.vance@vr4deaf.org
        """.trimIndent()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "ADA Accommodation Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Title I Law",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Generate legally grounded disclosure letters & sync with Suite Docs",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (onNavigateToCityForms != null) {
                            OutlinedButton(
                                onClick = onNavigateToCityForms,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_to_city_forms")
                            ) {
                                Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("City Forms", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        FilledTonalButton(
                            onClick = onNavigateToSimulator,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_to_simulator")
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Practice Sim", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                PrimaryTabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Letter Generator") },
                        icon = { Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_ada_generator")
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Timing Strategy") },
                        icon = { Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_ada_timing")
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Saved Vault (${savedAccommodations.size})") },
                        icon = { Icon(Icons.Default.FolderSpecial, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_ada_vault")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                0 -> {
                    // LETTER GENERATOR
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Template Selector
                        item {
                            Text("SELECT ACCOMMODATION TYPE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(6.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(AccommodationTemplateType.entries) { template ->
                                    val isSelected = selectedTemplate == template
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedTemplate = template },
                                        label = { Text(template.displayName) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null
                                    )
                                }
                            }
                        }

                        // Form Inputs
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = companyName,
                                            onValueChange = { companyName = it },
                                            label = { Text("Target Employer") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = roleTitle,
                                            onValueChange = { roleTitle = it },
                                            label = { Text("Job Role") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    OutlinedTextField(
                                        value = counselorRef,
                                        onValueChange = { counselorRef = it },
                                        label = { Text("VR / TWS Case Reference") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = customNotes,
                                        onValueChange = { customNotes = it },
                                        label = { Text("Custom Setup & Specific Tools") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 2
                                    )
                                }
                            }
                        }

                        // Generated Letter Preview Card
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Icon(Icons.Default.Article, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                            Text("Generated Title I Letter Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "DeafAuth Ready",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF10B981),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = generatedLetter,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }

                                    // Action Buttons: Save to DB, Export to Suite Doc, Copy
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                onSaveAccommodation(
                                                    selectedTemplate.displayName,
                                                    "Title I Reasonable Accommodation",
                                                    companyName,
                                                    roleTitle,
                                                    selectedStage.stageName,
                                                    generatedLetter
                                                )
                                                activeTab = 2 // jump to vault
                                            },
                                            modifier = Modifier.weight(1f).testTag("btn_save_ada_to_db")
                                        ) {
                                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Save to DB")
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(generatedLetter))
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy Text")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // DISCLOSURE TIMING STRATEGY GUIDE
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Column {
                                        Text("EEOC & Title I Disclosure Strategy Guide", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("Know your legal rights under 42 U.S.C. § 12112: when to disclose, how to request accommodations without bias, and how to safeguard your candidacy.", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        items(DisclosureStage.entries) { stage ->
                            val isRecommended = stage == DisclosureStage.INTERVIEW_INVITATION
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isRecommended) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                border = if (isRecommended) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)) else null,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(stage.stageName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isRecommended) Color(0xFF10B981) else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = stage.timingRecommendation,
                                                color = if (isRecommended) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Text(stage.legalNote, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                    if (stage != selectedStage) {
                                        OutlinedButton(
                                            onClick = {
                                                selectedStage = stage
                                                activeTab = 0 // back to generator
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Set Letter to This Stage", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // SAVED VAULT (Local Room/SQLite Database)
                    if (savedAccommodations.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("No saved accommodations yet.", style = MaterialTheme.typography.titleMedium)
                                Text("Use the Letter Generator to compose and save your first Title I request.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(savedAccommodations) { record ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("saved_ada_${record.id}")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(record.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                                Text("${record.targetCompany} • ${record.targetRole}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "DeafAuth Signed",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF10B981),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Created ${record.formattedDate} • Timing: ${record.timingStage}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = record.fullLetterText.take(200) + "...",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontFamily = FontFamily.Monospace,
                                                maxLines = 4,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }

                                        // Actions: Export to Doc Engine, Email, Delete
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                FilledTonalButton(
                                                    onClick = { onExportToDoc(record) },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("btn_export_doc_${record.id}")
                                                ) {
                                                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Export Doc", style = MaterialTheme.typography.labelSmall)
                                                }

                                                OutlinedButton(
                                                    onClick = { showExportEmailDialog = record },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("btn_export_email_${record.id}")
                                                ) {
                                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Suite Email", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }

                                            IconButton(
                                                onClick = { onDeleteAccommodation(record.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Accommodation", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Export to Email Dialog
            if (showExportEmailDialog != null) {
                val record = showExportEmailDialog!!
                AlertDialog(
                    onDismissRequest = { showExportEmailDialog = null },
                    icon = { Icon(Icons.Default.MailOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = { Text("Send via Suite Email") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Dispatch this Title I ADA accommodation directly to the employer or recruiter with cryptographic DeafAuth proof:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = recipientEmailInput,
                                onValueChange = { recipientEmailInput = it },
                                label = { Text("Employer HR / Recruiter Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onExportToEmail(recipientEmailInput, record)
                                showExportEmailDialog = null
                            }
                        ) {
                            Text("Dispatch Email")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExportEmailDialog = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
