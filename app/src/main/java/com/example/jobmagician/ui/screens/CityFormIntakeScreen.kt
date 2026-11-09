package com.example.jobmagician.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobmagician.data.db.MunicipalCityFormRecord
import com.example.jobmagician.data.db.NormalizedMunicipalFilingRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityFormIntakeScreen(
    municipalForms: List<MunicipalCityFormRecord>,
    municipalFilings: List<NormalizedMunicipalFilingRecord>,
    isSyncing: Boolean,
    syncLogs: List<String>,
    lastSyncTime: String,
    onTriggerAudit: () -> Unit,
    onRegisterCityForm: (String, String, String, String?, String) -> Unit,
    onDeleteCityForm: (String) -> Unit,
    onCreateFiling: (MunicipalCityFormRecord, String, String, String, String) -> Unit,
    onDeleteFiling: (String) -> Unit,
    onExportFilingToDoc: (NormalizedMunicipalFilingRecord) -> Unit,
    onExportFilingToEmail: (NormalizedMunicipalFilingRecord, String) -> Unit,
    onBuildPrefillUrl: (MunicipalCityFormRecord, String, String) -> String,
    onNavigateToAdaStudio: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = City Registry, 1 = Harmonized Filings, 2 = Background Telemetry
    var selectedFilterPlatform by remember { mutableStateOf("All Platforms") }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog state for adding a new custom city form URL
    var showAddFormDialog by remember { mutableStateOf(false) }
    var inputCityName by remember { mutableStateOf("") }
    var inputAgencyName by remember { mutableStateOf("") }
    var inputFormUrl by remember { mutableStateOf("") }
    var inputPlatformType by remember { mutableStateOf("Auto-Detect") }
    var inputNotes by remember { mutableStateOf("") }

    // Dialog state for harmonizing and generating a filing for a specific city
    var filingTargetForm by remember { mutableStateOf<MunicipalCityFormRecord?>(null) }
    var inputEmployer by remember { mutableStateOf("Metro Logistics & Tech Fort Worth") }
    var inputRole by remember { mutableStateOf("Systems Software Analyst") }
    var inputAccommodation by remember { mutableStateOf("Certified Deaf Interpreter (CDI) & CART captioning for tech interview panels") }
    var inputIncident by remember { mutableStateOf("Candidate requested qualified ASL interpreter via interview invite email; employer stated they do not provide accommodations.") }

    // Inspection dialog for raw payload or prefill URL
    var viewingFilingPayload by remember { mutableStateOf<NormalizedMunicipalFilingRecord?>(null) }
    var showEmailDispatchDialog by remember { mutableStateOf<NormalizedMunicipalFilingRecord?>(null) }
    var emailRecipientInput by remember { mutableStateOf("civilrights-intake@fortworthtexas.gov") }

    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    // Rotation animation for sync button
    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Background Consistency Status Header
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isSyncing) Color(0xFFF59E0B) else Color(0xFF10B981))
                            )
                            Column {
                                Text(
                                    text = if (isSyncing) "BACKGROUND CONSISTENCY ENGINE: SYNCING..." else "BACKGROUND CONSISTENCY ENGINE: ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Auto-Normalizing Unformatted City Forms • Last Audit: $lastSyncTime",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        FilledTonalButton(
                            onClick = onTriggerAudit,
                            enabled = !isSyncing,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_trigger_consistency_audit")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Run Audit",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Audit Now", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                // Subtitle bar & Add Form Action
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Municipal Form Intake",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Fort Worth & All Cities",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Harmonizes unstandardized Wufoo, CivicPlus & portal forms into canonical schema",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showAddFormDialog = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_open_add_city_form")
                    ) {
                        Icon(Icons.Default.AddLink, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add City Form", style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Tabs: City Registry, Harmonized Filings, Background Telemetry
                PrimaryTabRow(
                    selectedTabIndex = activeTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("City Registry (${municipalForms.size})") },
                        icon = { Icon(Icons.Default.LocationCity, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_city_registry")
                    )
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Filings (${municipalFilings.size})") },
                        icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_harmonized_filings")
                    )
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("Telemetry & Logs") },
                        icon = { Icon(Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.testTag("tab_sync_telemetry")
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
                    // CITY REGISTRY & NORMALIZER
                    val filtered = remember(selectedFilterPlatform, searchQuery, municipalForms) {
                        municipalForms.filter { form ->
                            val matchesPlatform = selectedFilterPlatform == "All Platforms" || form.platformType.contains(selectedFilterPlatform, ignoreCase = true)
                            val matchesSearch = searchQuery.isBlank() || form.cityName.contains(searchQuery, ignoreCase = true) || form.agencyName.contains(searchQuery, ignoreCase = true) || form.formUrl.contains(searchQuery, ignoreCase = true)
                            matchesPlatform && matchesSearch
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Showcase Hero Card: City of Fort Worth FWHRC Wufoo Form
                        item {
                            val fortWorthForm = municipalForms.firstOrNull { it.id == "MUNI-FTW-01" }
                            if (fortWorthForm != null) {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)),
                                    modifier = Modifier.fillMaxWidth().testTag("hero_fort_worth_card")
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.VerifiedUser,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.padding(6.dp).size(20.dp)
                                                    )
                                                }
                                                Column {
                                                    Text(
                                                        text = "City of Fort Worth (Primary Demo)",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "Fort Worth Human Relations Commission (FWHRC)",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "Wufoo Form Normalized",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF10B981),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Source URL: ${fortWorthForm.formUrl}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                            text = "FWHRC receives discrimination, ADA reasonable accommodation, and municipal civil rights inquiries via their Wufoo engine. VR4DEAF continuously tracks this form in the background, maps unstandardized fields (Field1, Field3, Field112, Field206), and pre-fills them automatically.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { filingTargetForm = fortWorthForm },
                                                modifier = Modifier.weight(1.3f).testTag("btn_harmonize_fort_worth")
                                            ) {
                                                Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Harmonize & File")
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    val prefilled = onBuildPrefillUrl(fortWorthForm, inputEmployer, inputAccommodation)
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prefilled))
                                                    context.startActivity(intent)
                                                },
                                                modifier = Modifier.weight(1f).testTag("btn_launch_fort_worth_wufoo")
                                            ) {
                                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Open Wufoo")
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Search and Filter Bar
                        item {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search city, commission, or URL...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        item {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listOf("All Platforms", "Wufoo", "CivicPlus", "Laserfiche", "Formstack", "Web Portal")) { platform ->
                                    val isSelected = selectedFilterPlatform == platform
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { selectedFilterPlatform = platform },
                                        label = { Text(platform) },
                                        leadingIcon = if (isSelected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                        } else null
                                    )
                                }
                            }
                        }

                        // List of Registered City Forms
                        items(filtered) { form ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                modifier = Modifier.fillMaxWidth().testTag("city_card_${form.id}")
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(form.cityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Text(form.agencyName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = form.platformType,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = form.formUrl,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF10B981))
                                            )
                                            Text(
                                                text = "${form.schemaStatus} • Polled: ${form.lastPolledDate}",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Text(
                                            text = "Hash: ${form.consistencyHash.take(16)}...",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Action buttons for this city form
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            FilledTonalButton(
                                                onClick = { filingTargetForm = form },
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.testTag("btn_harmonize_${form.id}")
                                            ) {
                                                Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Harmonize Filing", style = MaterialTheme.typography.labelSmall)
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    val prefilled = onBuildPrefillUrl(form, inputEmployer, inputAccommodation)
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prefilled))
                                                    context.startActivity(intent)
                                                },
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Open Form", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }

                                        IconButton(
                                            onClick = { onDeleteCityForm(form.id) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Form", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // HARMONIZED FILINGS VAULT
                    if (municipalFilings.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.AssignmentLate, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("No municipal filings generated yet.", style = MaterialTheme.typography.titleMedium)
                                Text("Select any city from the registry (e.g. Fort Worth FWHRC) and tap 'Harmonize Filing'.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(municipalFilings) { filing ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("filing_card_${filing.id}")
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(filing.cityName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                                Text(filing.agencyName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = filing.status,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF10B981),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "Candidate: ${filing.candidateDeafAuthId} • ${filing.formattedDate}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = filing.canonicalPayloadJson.take(240) + "...",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontFamily = FontFamily.Monospace,
                                                maxLines = 4,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                FilledTonalButton(
                                                    onClick = { onExportFilingToDoc(filing) },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("btn_export_filing_doc_${filing.id}")
                                                ) {
                                                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Suite Doc", style = MaterialTheme.typography.labelSmall)
                                                }

                                                OutlinedButton(
                                                    onClick = { showEmailDispatchDialog = filing },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                    modifier = Modifier.testTag("btn_export_filing_email_${filing.id}")
                                                ) {
                                                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Suite Email", style = MaterialTheme.typography.labelSmall)
                                                }

                                                OutlinedButton(
                                                    onClick = { viewingFilingPayload = filing },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Payload", style = MaterialTheme.typography.labelSmall)
                                                }
                                            }

                                            IconButton(
                                                onClick = { onDeleteFiling(filing.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Filing", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // LIVE BACKGROUND SYNC TELEMETRY
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                    Icon(Icons.Default.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Column {
                                        Text("Background Consistency Worker Engine", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Text("Continuously polls municipal endpoints, validates field signatures, eliminates schema drift, and generates cryptographic DeafAuth digests.", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        items(syncLogs) { log ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = log,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Dialog: Add Custom City Form URL
            if (showAddFormDialog) {
                AlertDialog(
                    onDismissRequest = { showAddFormDialog = false },
                    icon = { Icon(Icons.Default.AddLink, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = { Text("Register Municipal City Form") },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Enter any city's unformatted intake form URL (e.g. Wufoo, Formstack, Google Form, or civic portal) to normalize and include in background consistency.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = inputCityName,
                                onValueChange = { inputCityName = it },
                                label = { Text("City & State (e.g. Arlington, TX)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = inputAgencyName,
                                onValueChange = { inputAgencyName = it },
                                label = { Text("Commission / Department Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = inputFormUrl,
                                onValueChange = { inputFormUrl = it },
                                label = { Text("Form URL (e.g. https://...wufoo.com/forms/...)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = inputNotes,
                                onValueChange = { inputNotes = it },
                                label = { Text("Optional Notes") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (inputCityName.isNotBlank() && inputFormUrl.isNotBlank()) {
                                    onRegisterCityForm(
                                        inputCityName,
                                        inputAgencyName.ifBlank { "$inputCityName Human Relations Commission" },
                                        inputFormUrl,
                                        null,
                                        inputNotes
                                    )
                                    inputCityName = ""
                                    inputAgencyName = ""
                                    inputFormUrl = ""
                                    inputNotes = ""
                                    showAddFormDialog = false
                                }
                            },
                            modifier = Modifier.testTag("btn_submit_add_city_form")
                        ) {
                            Text("Register & Normalize")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddFormDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Dialog: Harmonize & File for a specific City Form
            if (filingTargetForm != null) {
                val target = filingTargetForm!!
                AlertDialog(
                    onDismissRequest = { filingTargetForm = null },
                    icon = { Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = { Text("Harmonize Filing: ${target.cityName}") },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(target.agencyName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Text("Platform: ${target.platformType} • Normalized via VR4DEAF Schema", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            OutlinedTextField(
                                value = inputEmployer,
                                onValueChange = { inputEmployer = it },
                                label = { Text("Employer / Respondent Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = inputRole,
                                onValueChange = { inputRole = it },
                                label = { Text("Position / Job Role Applied") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = inputAccommodation,
                                onValueChange = { inputAccommodation = it },
                                label = { Text("Accommodation Requested") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = inputIncident,
                                onValueChange = { inputIncident = it },
                                label = { Text("Incident / Narrative Summary") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onCreateFiling(
                                    target,
                                    inputEmployer,
                                    inputRole,
                                    inputAccommodation,
                                    inputIncident
                                )
                                filingTargetForm = null
                                activeTab = 1 // jump to filings tab
                            },
                            modifier = Modifier.testTag("btn_confirm_create_harmonized_filing")
                        ) {
                            Text("Generate Harmonized Filing")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { filingTargetForm = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Dialog: View Raw Canonical JSON Payload
            if (viewingFilingPayload != null) {
                val filing = viewingFilingPayload!!
                AlertDialog(
                    onDismissRequest = { viewingFilingPayload = null },
                    icon = { Icon(Icons.Default.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = { Text("Canonical Payload: ${filing.cityName}") },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 260.dp)
                            ) {
                                LazyColumn(modifier = Modifier.padding(10.dp)) {
                                    item {
                                        Text(
                                            text = filing.canonicalPayloadJson,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(filing.canonicalPayloadJson))
                                viewingFilingPayload = null
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy JSON")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewingFilingPayload = null }) {
                            Text("Close")
                        }
                    }
                )
            }

            // Dialog: Dispatch via Suite Email
            if (showEmailDispatchDialog != null) {
                val filing = showEmailDispatchDialog!!
                AlertDialog(
                    onDismissRequest = { showEmailDispatchDialog = null },
                    icon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = { Text("Dispatch via Suite Email") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Dispatch normalized intake filing directly to ${filing.cityName} Human Relations Commission with DeafAuth verification hash:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = emailRecipientInput,
                                onValueChange = { emailRecipientInput = it },
                                label = { Text("Municipal Civil Rights Intake Email") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                onExportFilingToEmail(filing, emailRecipientInput)
                                showEmailDispatchDialog = null
                            }
                        ) {
                            Text("Dispatch Email")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEmailDispatchDialog = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
