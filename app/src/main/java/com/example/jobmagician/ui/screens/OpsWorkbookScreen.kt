package com.example.jobmagician.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobmagician.data.*

@Composable
fun OpsWorkbookScreen(
    activeTab: Int,
    serviceLifecycles: List<ServiceLifecycleItem>,
    techDebtItems: List<TechDebtItem>,
    serviceEquityScores: Map<String, Double>,
    contributorStakes: List<ContributorStake>,
    onTabSelected: (Int) -> Unit,
    onAddTechDebt: (serviceKey: String, description: String, severity: DebtSeverity, estHours: Double, impact: String) -> Unit,
    onToggleResolveDebt: (debtId: String) -> Unit,
    onUpdateEquityScore: (serviceKey: String, newScore: Double) -> Unit
) {
    var showAddDebtDialog by remember { mutableStateOf(false) }

    val tabTitles = listOf(
        "1. Lifecycle Overview",
        "2. Tech Debt Register",
        "3. Debt vs. Equity",
        "4. Contributors & Stake"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Workbook Header Banner ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "VR4DEAF — Lifecycle & Tech Debt-to-Equity",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Automated Ops Tracker & Governance Workbook",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Standard Hourly Rate: $75/hr • Compares cost-to-fix against value delivered across 5 core ecosystem services.",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // --- 4 Tabs Selector ---
        item {
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                edgePadding = 0.dp
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { onTabSelected(index) },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // --- Tab 1: Lifecycle Overview ---
        if (activeTab == 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Service Lifecycle Matrix",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "5 Registered Services",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(serviceLifecycles) { item ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.serviceName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = item.githubRepo,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "• ${item.serviceKey}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            val phaseColor = when (item.currentPhase) {
                                ServicePhase.IDEA -> Color(0xFFF59E0B) // Amber
                                ServicePhase.BUILD -> Color(0xFF3B82F6) // Blue
                                ServicePhase.GROW -> Color(0xFF8B5CF6) // Purple
                                ServicePhase.MANAGED -> Color(0xFF10B981) // Green
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = phaseColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Phase: ${item.currentPhase.label}",
                                    color = phaseColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Status: ${item.status}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Last verified: ${item.lastUpdated}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // --- Tab 2: Tech Debt Register ---
        if (activeTab == 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tech Debt Register",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        val totalOpenCost = techDebtItems.filter { !it.isResolved }.sumOf { it.estimatedCost }
                        Text(
                            text = "Open Debt: $${totalOpenCost.toInt()} (${techDebtItems.count { !it.isResolved }} items)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { showAddDebtDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Debt", fontSize = 12.sp)
                    }
                }
            }

            items(techDebtItems) { item ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isResolved) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(
                            checked = item.isResolved,
                            onCheckedChange = { onToggleResolveDebt(item.id) }
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.id,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = item.serviceName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                val severityColor = when (item.severity) {
                                    DebtSeverity.LOW -> Color(0xFF10B981)
                                    DebtSeverity.MEDIUM -> Color(0xFFF59E0B)
                                    DebtSeverity.HIGH -> Color(0xFFEF4444)
                                    DebtSeverity.CRITICAL -> Color(0xFF991B1B)
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = severityColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = item.severity.label,
                                        color = severityColor,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (item.isResolved) FontWeight.Normal else FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Est. ${item.estHoursToFix} hrs ➔ $${item.estimatedCost.toInt()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (item.isResolved) {
                                    Text(
                                        text = "RESOLVED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF10B981)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Impact: ${item.impactIfUnresolved}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- Tab 3: Debt vs Equity ---
        if (activeTab == 2) {
            item {
                Column {
                    Text(
                        text = "Debt vs Equity Score (0 - 100)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Adjust the yellow equity value slider for each service to calculate real-time Debt-to-Equity Ratio & automated health flags.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(serviceLifecycles) { service ->
                val openDebtCost = techDebtItems
                    .filter { it.serviceKey == service.serviceKey && !it.isResolved }
                    .sumOf { it.estimatedCost }
                val currentEquity = serviceEquityScores[service.serviceKey] ?: 0.0

                val summary = ServiceEquityDebtSummary(
                    serviceKey = service.serviceKey,
                    serviceName = service.serviceName,
                    totalDebtCost = openDebtCost,
                    equityScore = currentEquity
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = service.serviceName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            val healthBadgeColor = when {
                                summary.equityScore <= 0 -> Color(0xFF6B7280)
                                summary.debtToEquityRatio < 0.15 -> Color(0xFF10B981)
                                summary.debtToEquityRatio < 0.40 -> Color(0xFFF59E0B)
                                else -> Color(0xFFEF4444)
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = healthBadgeColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = summary.healthFlag,
                                    color = healthBadgeColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Open Debt Cost", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${summary.totalDebtCost.toInt()}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Equity Value (0-100)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${summary.equityScore.toInt()}/100", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Column {
                                Text("Debt-to-Equity", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(String.format("%.2f", summary.debtToEquityRatio), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Tune Equity Value (Hours saved / Business confidence):",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Slider(
                            value = currentEquity.toFloat(),
                            onValueChange = { onUpdateEquityScore(service.serviceKey, it.toDouble()) },
                            valueRange = 0f..100f,
                            steps = 19
                        )
                    }
                }
            }
        }

        // --- Tab 4: Contributors & Stake ---
        if (activeTab == 3) {
            item {
                Column {
                    Text(
                        text = "VR4DEAF Contributor & Stake Register",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Governance placeholder for founder equity, deaf community advisory board, and ecosystem partners.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(contributorStakes) { stake ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stake.nameRole,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${stake.equityPercentage.toInt()}% Equity",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Contribution Area: ${stake.contributionArea}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stake.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // Add Tech Debt Dialog
    if (showAddDebtDialog) {
        var selectedKey by remember { mutableStateOf("deafauth") }
        var description by remember { mutableStateOf("") }
        var hoursStr by remember { mutableStateOf("3.0") }
        var impact by remember { mutableStateOf("") }
        var severity by remember { mutableStateOf(DebtSeverity.MEDIUM) }

        AlertDialog(
            onDismissRequest = { showAddDebtDialog = false },
            title = { Text("Log Tech Debt Item") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Service:", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("deafauth", "fibonrose", "pinksync", "magicians", "gateway").forEach { key ->
                            FilterChip(
                                selected = selectedKey == key,
                                onClick = { selectedKey = key },
                                label = { Text(key, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Debt Description") },
                        placeholder = { Text("e.g. Missing retry logic...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = hoursStr,
                            onValueChange = { hoursStr = it },
                            label = { Text("Est. Hours") },
                            modifier = Modifier.weight(1f)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Severity:", style = MaterialTheme.typography.labelSmall)
                            Row {
                                DebtSeverity.entries.forEach { sev ->
                                    RadioButton(
                                        selected = severity == sev,
                                        onClick = { severity = sev }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = impact,
                        onValueChange = { impact = it },
                        label = { Text("Impact if Unresolved") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hours = hoursStr.toDoubleOrNull() ?: 2.0
                        if (description.isNotBlank()) {
                            onAddTechDebt(selectedKey, description, severity, hours, impact)
                            showAddDebtDialog = false
                        }
                    }
                ) {
                    Text("Save to Register")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDebtDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
