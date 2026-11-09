package com.example.jobmagician.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobmagician.data.*
import com.example.jobmagician.ui.theme.BlueTws
import com.example.jobmagician.ui.theme.EmeraldSuccess
import com.example.jobmagician.ui.theme.PurpleDark
import com.example.jobmagician.ui.theme.PurplePrimary

enum class SuiteMainMode(val label: String, val icon: @Composable () -> Unit) {
    PRODUCTIVITY_SUITE("Suite Tools (Email/Doc/Chat/File)", { Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp)) }),
    CORE_ENGINES("Magician Engines (Job/Biz/Dev)", { Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(16.dp)) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MagiciansSuiteScreen(
    activeSuite: MagicianSubSuite,
    onSelectSuite: (MagicianSubSuite) -> Unit,
    activeSuiteTab: SuiteTab = SuiteTab.DOCS,
    onSelectSuiteTab: (SuiteTab) -> Unit = {},
    emails: List<SuiteEmail> = emptyList(),
    documents: List<SuiteDocument> = emptyList(),
    chatChannels: List<SuiteChatChannel> = emptyList(),
    channelMessages: Map<String, List<ChatMessage>> = emptyMap(),
    files: List<SuiteFileAttachment> = emptyList(),
    onSendEmail: (String, String, String, EmailCategory, List<String>) -> Unit = { _, _, _, _, _ -> },
    onCreateDocument: (String, SuiteDocType, String, List<String>) -> Unit = { _, _, _, _ -> },
    onUpdateDocument: (String, String, SuiteDocStatus) -> Unit = { _, _, _ -> },
    onSignDocument: (String) -> Unit = {},
    onDeleteDocument: (String) -> Unit = {},
    onSendChannelMessage: (String, String, String?) -> Unit = { _, _, _ -> },
    onUploadFile: (String, SuiteFileType, String, String) -> Unit = { _, _, _, _ -> },
    onDeleteFile: (String) -> Unit = {},
    cycleSteps: List<CycleStepData>,
    activeStep: Int,
    onSelectStep: (Int) -> Unit,
    jobListings: List<JobListingData>,
    onNavigate: (NavDestination) -> Unit
) {
    var mainMode by remember { mutableStateOf(SuiteMainMode.PRODUCTIVITY_SUITE) }

    // Dialog & Interaction States
    var showComposeEmailDialog by remember { mutableStateOf(false) }
    var showCreateDocDialog by remember { mutableStateOf(false) }
    var selectedDocForView by remember { mutableStateOf<SuiteDocument?>(null) }
    var selectedEmailForView by remember { mutableStateOf<SuiteEmail?>(null) }
    var showUploadFileDialog by remember { mutableStateOf(false) }
    var selectedFileForPreview by remember { mutableStateOf<SuiteFileAttachment?>(null) }
    var selectedChatChannelId by remember { mutableStateOf(chatChannels.firstOrNull()?.id ?: "ch-01") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // --- Header Banner ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PurpleDark, PurplePrimary, BlueTws)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoFixHigh,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "360 MAGICIANS SUITE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "pinkycollie/360magicians",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Productivity & Collaboration Hub",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Full integration with Email dispatch, Doc engine, multi-channel Chat, and encrypted File Vault for Deaf professionals & VR teams.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // --- Main Mode Selector Switcher ---
        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SuiteMainMode.entries.forEachIndexed { index, mode ->
                    SegmentedButton(
                        selected = mainMode == mode,
                        onClick = { mainMode = mode },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = SuiteMainMode.entries.size),
                        modifier = Modifier.testTag("mode_${mode.name.lowercase()}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            mode.icon()
                            Text(
                                text = if (mode == SuiteMainMode.PRODUCTIVITY_SUITE) "Suite Tools" else "Magician Engines",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (mainMode == mode) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // --- View branch based on mainMode ---
        if (mainMode == SuiteMainMode.PRODUCTIVITY_SUITE) {
            // --- Productivity Suite Sub-Tabs (Email, Doc, Chat, Files) ---
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuiteTab.entries.forEach { tab ->
                            val isSelected = tab == activeSuiteTab
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectSuiteTab(tab) }
                                    .testTag("suite_tab_${tab.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (tab) {
                                            SuiteTab.EMAIL -> Icons.Default.Email
                                            SuiteTab.DOCS -> Icons.Default.Description
                                            SuiteTab.CHAT -> Icons.Default.Chat
                                            SuiteTab.FILES -> Icons.Default.Folder
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = tab.label.split(" ").first(),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Render Active Suite Tab Content
            when (activeSuiteTab) {
                SuiteTab.EMAIL -> {
                    // EMAIL SUITE
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DeafAuth Email & Counselor Mailer",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${emails.size} messages synced over PinkSync Kernel",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { showComposeEmailDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("btn_compose_email")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Compose")
                            }
                        }
                    }

                    // Quick Action Template Chips
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Quick Dispatch Templates:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SuggestionChip(
                                        onClick = {
                                            onSendEmail(
                                                "marcus.vance@tws.texas.gov",
                                                "IPE Progress & Assistive Tech Voucher Request",
                                                "Hello Marcus,\n\nI have completed the technical milestone and am requesting authorization for the assistive tech voucher.\n\nBest,\nPinky Collie",
                                                EmailCategory.COUNSELOR_UPDATE,
                                                listOf("Signed_IPE_Plan_MarcusVance.pdf")
                                            )
                                        },
                                        label = { Text("⚡ 1-Click Counselor Update", fontSize = 11.sp) }
                                    )
                                    SuggestionChip(
                                        onClick = {
                                            onSendEmail(
                                                "hr@employer.com",
                                                "ADA Title I Accommodation Notice: ASL Interpreter",
                                                "Dear Hiring Team,\n\nI am requesting a certified ASL interpreter for our upcoming interview pursuant to ADA Title I.\n\nThank you,\nPinky Collie",
                                                EmailCategory.ACCOMMODATION_NOTICE,
                                                listOf("ADA_Title_I_Accommodation_Notice.docx")
                                            )
                                        },
                                        label = { Text("📋 ADA Notice to Employer", fontSize = 11.sp) }
                                    )
                                    SuggestionChip(
                                        onClick = {
                                            onSendEmail(
                                                "support@vr4deaf.org",
                                                "PinkSync State Sync & Health Check",
                                                "Ping kernel telemetry check across all 5 active nodes.",
                                                EmailCategory.SYSTEM_NOTIFY,
                                                emptyList()
                                            )
                                        },
                                        label = { Text("🔄 PinkSync Kernel Ping", fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }

                    items(emails) { email ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedEmailForView = email }
                                .testTag("email_item_${email.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (email.isUnread) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(email.category.badgeColorHex).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = email.category.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(email.category.badgeColorHex),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = email.timestamp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = email.subject,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "To: ${email.recipient}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = email.preview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )

                                if (email.attachedFiles.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.AttachFile,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        email.attachedFiles.forEach { fileName ->
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = fileName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                SuiteTab.DOCS -> {
                    // DOC ENGINE
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Suite Document Generator & Vault",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${documents.size} official VR & ADA documents on file",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { showCreateDocDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("btn_create_doc")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("New Doc")
                            }
                        }
                    }

                    items(documents) { doc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDocForView = doc }
                                .testTag("doc_card_${doc.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(doc.docType.badgeColorHex).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = doc.docType.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(doc.docType.badgeColorHex),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(doc.status.badgeColorHex).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = doc.status.label,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(doc.status.badgeColorHex),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = doc.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Author: ${doc.author}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = doc.fileSize,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (doc.isDeafAuthVerified) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.VerifiedUser,
                                                contentDescription = null,
                                                tint = EmeraldSuccess,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = "DeafAuth Verified",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EmeraldSuccess,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "Draft Mode",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = "Modified ${doc.lastModified}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                SuiteTab.CHAT -> {
                    // MULTI-CHAT
                    item {
                        Column {
                            Text(
                                text = "VR4DEAF Multi-Channel Live Chat",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Real-time communication with Counselors, CDIs, and Peers",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Channels Selector Row
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(chatChannels) { channel ->
                                    val isSelected = channel.id == selectedChatChannelId
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.clickable { selectedChatChannelId = channel.id }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = channel.avatarInitials,
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Column {
                                                Text(
                                                    text = channel.name,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                                Text(
                                                    text = channel.channelType.label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 9.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Active Channel Messages Stream
                    item {
                        val activeChannel = chatChannels.find { it.id == selectedChatChannelId }
                        val activeMessages = channelMessages[selectedChatChannelId] ?: emptyList()
                        var chatInputText by remember { mutableStateOf("") }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Channel Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldSuccess)
                                        )
                                        Text(
                                            text = activeChannel?.name ?: "Chat Stream",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EmeraldSuccess.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Encrypted WSS",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = EmeraldSuccess,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                                // Messages List
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 160.dp, max = 280.dp)
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (activeMessages.isEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No messages yet in this channel. Send the first greeting!",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    } else {
                                        activeMessages.forEach { msg ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(
                                                        topStart = 14.dp,
                                                        topEnd = 14.dp,
                                                        bottomStart = if (msg.isUser) 14.dp else 2.dp,
                                                        bottomEnd = if (msg.isUser) 2.dp else 14.dp
                                                    ),
                                                    color = if (msg.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                                    modifier = Modifier.widthIn(max = 280.dp)
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Text(
                                                            text = msg.text,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = if (msg.isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = msg.timestamp,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontSize = 9.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                                            modifier = Modifier.align(Alignment.End)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Chat Composer Bar
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { showUploadFileDialog = true },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(Icons.Default.AttachFile, contentDescription = "Attach File", tint = MaterialTheme.colorScheme.primary)
                                    }

                                    OutlinedTextField(
                                        value = chatInputText,
                                        onValueChange = { chatInputText = it },
                                        placeholder = { Text("Type message or dispatch ASL request...", fontSize = 12.sp) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("input_channel_chat"),
                                        shape = RoundedCornerShape(20.dp),
                                        singleLine = true
                                    )

                                    FilledIconButton(
                                        onClick = {
                                            if (chatInputText.isNotBlank()) {
                                                onSendChannelMessage(selectedChatChannelId, chatInputText, null)
                                                chatInputText = ""
                                            }
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("btn_send_channel_chat")
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                SuiteTab.FILES -> {
                    // FILE VAULT
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "DeafAuth Encrypted File Vault",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${files.size} verified documents & media attachments",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { showUploadFileDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("btn_upload_file")
                            ) {
                                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload")
                            }
                        }
                    }

                    items(files) { file ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedFileForPreview = file }
                                .testTag("file_item_${file.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(file.fileType.badgeColorHex).copy(alpha = 0.15f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when (file.fileType) {
                                                SuiteFileType.PDF -> Icons.Default.PictureAsPdf
                                                SuiteFileType.DOCX -> Icons.Default.Article
                                                SuiteFileType.AUDIOGRAM_IMG -> Icons.Default.Hearing
                                                SuiteFileType.SIGNED_FORM -> Icons.Default.Verified
                                                SuiteFileType.CERTIFICATE -> Icons.Default.CardMembership
                                                SuiteFileType.SPREADSHEET -> Icons.Default.TableChart
                                            },
                                            contentDescription = null,
                                            tint = Color(file.fileType.badgeColorHex),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = file.fileName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = file.category,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(
                                            text = file.sizeDisplay,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = file.verifiedHash,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { onDeleteFile(file.id) }
                                ) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = "Delete File",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // --- CORE ENGINES (Job-Magician, Business-Magician, Developer-Mag) ---
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MagicianSubSuite.entries.forEach { suite ->
                            val isSelected = suite == activeSuite
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectSuite(suite) }
                                    .testTag("magician_suite_${suite.slug}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = suite.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Render Sub-Suite Content
            when (activeSuite) {
                MagicianSubSuite.JOB_MAGICIAN -> {
                    item {
                        JobMagicianSuiteView(
                            cycleSteps = cycleSteps,
                            activeStep = activeStep,
                            onSelectStep = onSelectStep,
                            onNavigate = onNavigate,
                            onNewDoc = { showCreateDocDialog = true }
                        )
                    }
                }
                MagicianSubSuite.BUSINESS_MAGICIAN -> {
                    item {
                        BusinessMagicianSuiteView(
                            onNavigate = onNavigate,
                            onNewDoc = { showCreateDocDialog = true },
                            onNewEmail = { showComposeEmailDialog = true }
                        )
                    }
                }
                MagicianSubSuite.DEVELOPER_MAGICIAN -> {
                    item {
                        DeveloperMagicianSuiteView(
                            onNavigate = onNavigate
                        )
                    }
                }
            }
        }
    }

    // ----------------------------------------------------
    // Modals & Interactive Dialogs
    // ----------------------------------------------------

    // 1. Compose Email Dialog
    if (showComposeEmailDialog) {
        var recipientInput by remember { mutableStateOf("marcus.vance@tws.texas.gov") }
        var subjectInput by remember { mutableStateOf("VR4DEAF Protocol Update & Accommodation Verification") }
        var bodyInput by remember { mutableStateOf("Hello Marcus,\n\nI am submitting the requested documentation for my IPE milestone review.\n\nBest,\nPinky Collie") }
        var selectedCategory by remember { mutableStateOf(EmailCategory.COUNSELOR_UPDATE) }

        AlertDialog(
            onDismissRequest = { showComposeEmailDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Compose Suite Email", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = recipientInput,
                        onValueChange = { recipientInput = it },
                        label = { Text("To (Recipient)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = subjectInput,
                        onValueChange = { subjectInput = it },
                        label = { Text("Subject") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Email Category:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        EmailCategory.entries.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.label.split(" ").first(), fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = bodyInput,
                        onValueChange = { bodyInput = it },
                        label = { Text("Message Body") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        maxLines = 8
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSendEmail(recipientInput, subjectInput, bodyInput, selectedCategory, listOf("DeafAuth_Verified_Badge.pdf"))
                        showComposeEmailDialog = false
                    }
                ) {
                    Text("Send Dispatch")
                }
            },
            dismissButton = {
                TextButton(onClick = { showComposeEmailDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Create Document Dialog
    if (showCreateDocDialog) {
        var docTitleInput by remember { mutableStateOf("") }
        var selectedDocType by remember { mutableStateOf(SuiteDocType.ADA_ACCOMMODATION_LETTER) }
        var docContentInput by remember { mutableStateOf("### Certified Deaf Workplace Accommodation\n\nPursuant to ADA Title I (42 U.S.C. § 12112), this document confirms verified accommodation requirements for Pinky Collie (@pinkycollie).") }

        AlertDialog(
            onDismissRequest = { showCreateDocDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Create New Suite Document", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = docTitleInput,
                        onValueChange = { docTitleInput = it },
                        label = { Text("Document Title") },
                        placeholder = { Text("e.g. ADA Request Letter 2026") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Document Type:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuiteDocType.entries.forEach { dt ->
                            FilterChip(
                                selected = selectedDocType == dt,
                                onClick = { selectedDocType = dt },
                                label = { Text(dt.label.split(" ").take(2).joinToString(" "), fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = docContentInput,
                        onValueChange = { docContentInput = it },
                        label = { Text("Document Body & Sections") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        maxLines = 10
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = if (docTitleInput.isNotBlank()) docTitleInput else selectedDocType.label
                        onCreateDocument(title, selectedDocType, docContentInput, listOf("DeafTech", selectedDocType.name))
                        showCreateDocDialog = false
                    }
                ) {
                    Text("Save to Vault")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDocDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. View / Edit Document Dialog
    selectedDocForView?.let { doc ->
        var editedContent by remember { mutableStateOf(doc.content) }

        AlertDialog(
            onDismissRequest = { selectedDocForView = null },
            title = {
                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(doc.docType.badgeColorHex).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = doc.docType.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(doc.docType.badgeColorHex),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(doc.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Author: ${doc.author}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Status: ${doc.status.label}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(doc.status.badgeColorHex))
                    }

                    OutlinedTextField(
                        value = editedContent,
                        onValueChange = { editedContent = it },
                        label = { Text("Document Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                onSignDocument(doc.id)
                                selectedDocForView = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Draw, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("DeafAuth Sign", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                onUpdateDocument(doc.id, editedContent, SuiteDocStatus.FINALIZED)
                                selectedDocForView = null
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Changes", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDocForView = null }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDeleteDocument(doc.id)
                        selectedDocForView = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        )
    }

    // 4. View Email Detail Dialog
    selectedEmailForView?.let { email ->
        AlertDialog(
            onDismissRequest = { selectedEmailForView = null },
            title = {
                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(email.category.badgeColorHex).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = email.category.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(email.category.badgeColorHex),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(email.subject, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("From: ${email.sender}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("To: ${email.recipient}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    Text("Timestamp: ${email.timestamp}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    HorizontalDivider()

                    Text(
                        text = email.body,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (email.attachedFiles.isNotEmpty()) {
                        HorizontalDivider()
                        Text("Attached Files:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        email.attachedFiles.forEach { file ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Text(file, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedEmailForView = null }) {
                    Text("Close")
                }
            }
        )
    }

    // 5. Upload File Dialog
    if (showUploadFileDialog) {
        var uploadNameInput by remember { mutableStateOf("") }
        var selectedFileType by remember { mutableStateOf(SuiteFileType.PDF) }
        var selectedCategoryInput by remember { mutableStateOf("Vocational Rehabilitation") }

        AlertDialog(
            onDismissRequest = { showUploadFileDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Upload Attachment to Vault", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = uploadNameInput,
                        onValueChange = { uploadNameInput = it },
                        label = { Text("File Name") },
                        placeholder = { Text("e.g. Audiogram_Intake_2026.png") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("File Type Format:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SuiteFileType.entries.forEach { ft ->
                            FilterChip(
                                selected = selectedFileType == ft,
                                onClick = { selectedFileType = ft },
                                label = { Text(".${ft.extension}", fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = selectedCategoryInput,
                        onValueChange = { selectedCategoryInput = it },
                        label = { Text("Vault Category") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val name = if (uploadNameInput.isNotBlank()) uploadNameInput else "attachment_${System.currentTimeMillis()}.${selectedFileType.extension}"
                        onUploadFile(name, selectedFileType, "${(120..950).random()} KB", selectedCategoryInput)
                        showUploadFileDialog = false
                    }
                ) {
                    Text("Upload & Encrypt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadFileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. File Preview Dialog
    selectedFileForPreview?.let { file ->
        AlertDialog(
            onDismissRequest = { selectedFileForPreview = null },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = Color(file.fileType.badgeColorHex))
                    Text(file.fileName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Category: ${file.category}", style = MaterialTheme.typography.bodySmall)
                    Text("Size: ${file.sizeDisplay}", style = MaterialTheme.typography.bodySmall)
                    Text("Uploaded: ${file.uploadedDate}", style = MaterialTheme.typography.bodySmall)
                    Text("Verified Hash: ${file.verifiedHash}", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace, fontSize = 10.sp)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                            Text("AES-256 Encrypted & Verified via DeafAuth Key Vault", style = MaterialTheme.typography.labelSmall, color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedFileForPreview = null }) {
                    Text("Close")
                }
            }
        )
    }
}

// ----------------------------------------------------
// Engine Sub-Views
// ----------------------------------------------------

@Composable
fun JobMagicianSuiteView(
    cycleSteps: List<CycleStepData>,
    activeStep: Int,
    onSelectStep: (Int) -> Unit,
    onNavigate: (NavDestination) -> Unit,
    onNewDoc: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Job-Magician Portal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "VR Client Journey & Career Placement Engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Client Mode",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // VR 7-Step Cycle Progress Mini Bar
            Text(
                text = "7-Step Vocational Rehabilitation Roadmap",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                cycleSteps.forEach { step ->
                    val isActive = step.id == activeStep
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { onSelectStep(step.id) }
                    ) {
                        Text(
                            text = "${step.id}. ${step.title.take(14)}...",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(NavDestination.DASHBOARD) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Open VR Plan")
                }
                Button(
                    onClick = onNewDoc,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Draft IPE / ADA")
                }
            }
        }
    }
}

@Composable
fun BusinessMagicianSuiteView(
    onNavigate: (NavDestination) -> Unit,
    onNewDoc: () -> Unit,
    onNewEmail: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Business-Magician Suite",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Employer Inclusion Portal & WOTC Tax Credits",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSuccess.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "WOTC Active",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Employer Tax Advantage:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Text("Claim up to $9,600 in federal Work Opportunity Tax Credits (26 U.S. Code § 51) for each certified VR candidate onboarded.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNewEmail,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Email Employer")
                }
                Button(
                    onClick = onNewDoc,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Generate 8850")
                }
            }
        }
    }
}

@Composable
fun DeveloperMagicianSuiteView(
    onNavigate: (NavDestination) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Developer-Mag SDKs & Telemetry",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "PinkSync Kernel APIs & DeafAuth SDK",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BlueTws.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "SDK v2.6",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BlueTws,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "// Kotlin Multiplatform Kernel Connection",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF6C7086)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = """val client = PinkSyncKernelClient.connect(
    deafAuthToken = "DA-7928-PINK",
    kernelEndpoint = "wss://pinksync.vr4deaf.org/v1/telemetry"
)""",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFA5B4FC),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(NavDestination.OPS_WORKBOOK) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ops Workbook")
                }
                Button(
                    onClick = { onNavigate(NavDestination.DEAF_AUTH) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("DeafAuth & Mesh")
                }
            }
        }
    }
}
