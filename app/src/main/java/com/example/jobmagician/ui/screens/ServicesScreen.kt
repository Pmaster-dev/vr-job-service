package com.example.jobmagician.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobmagician.data.IntegrationTab
import com.example.jobmagician.ui.theme.BlueTws
import com.example.jobmagician.ui.theme.EmeraldSuccess
import com.example.jobmagician.ui.theme.PurplePrimary

@Composable
fun ServicesScreen(
    activeTab: IntegrationTab,
    onTabSelected: (IntegrationTab) -> Unit,
    onStartJourney: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "VR4DEAF Ecosystem Services",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Coordinated services bridging Vocational Rehabilitation, DeafAuth identity, VURA Knowledge Engine, and Job-Magician 360.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Tab Selector Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(IntegrationTab.entries.toTypedArray()) { tab ->
                    FilterChip(
                        selected = activeTab == tab,
                        onClick = { onTabSelected(tab) },
                        label = { Text(tab.title, fontSize = 12.sp, fontWeight = if (activeTab == tab) FontWeight.Bold else FontWeight.Normal) },
                        leadingIcon = {
                            if (activeTab == tab) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    )
                }
            }
        }

        when (activeTab) {
            IntegrationTab.VR4DEAF_CORE -> {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Hub, contentDescription = null, tint = Color.White)
                                    }
                                }
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("VR4DEAF Universal Platform", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)) {
                                            Text("pinkycollie/vr4deaf", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text("Mission: Autonomous Deaf Empowerment", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "VR4DEAF.org is a holistic ecosystem unifying state vocational rehabilitation agencies, employers, and Deaf job seekers with verified legal rights, ASL training, and automated ops tracking.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                val coreFeatures = listOf(
                    "Universal DeafAuth Identity" to "Single verified sign-on tracking communication preferences, CDI requests, and VR case history.",
                    "VURA Knowledge Engine" to "Living legal & community RAG providing citation-backed answers for ADA Title I, WIOA, and Section 504.",
                    "Job-Magician 360" to "End-to-end vocational rehabilitation specialist cycle with ASL mock interviews and accessible job matching.",
                    "PinkSync Service Mesh" to "Real-time sync engine coordinating assistive device endpoints and counselor progress logs."
                )

                items(coreFeatures) { (title, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            IntegrationTab.TWS -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BlueTws.copy(alpha = 0.15f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = BlueTws, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Texas Workforce Solutions (VR/TWS)",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                val twsServices = listOf(
                    "Vocational Rehabilitation (VR)" to "Dedicated funding for training, tuition, assistive hardware, and certified ASL interpreters.",
                    "WIOA Title I Adult & Youth" to "Workforce innovation and opportunity grants for career upskilling.",
                    "Individualized Plan for Employment (IPE)" to "Formalized contractual plan between client and counselor established within 90 days.",
                    "Supportive Auxiliary Services" to "Transportation assistance, ergonomic workstation setups, and tactile assistive tools."
                )

                items(twsServices) { (title, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                            Column {
                                Text(title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            IntegrationTab.MAGICIAN -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PurplePrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = PurplePrimary, modifier = Modifier.size(32.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Job-Magician 360",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(6.dp), color = PurplePrimary.copy(alpha = 0.2f)) {
                                Text("pinkycollie/360magicians", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PurplePrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }
                }

                val magicianServices = listOf(
                    "Deaf Career Mentorship" to "1-on-1 guidance from successful Deaf professionals in STEM, Business, and Government.",
                    "Aptitude & Skills Inventory" to "ASL-guided assessments measuring digital fluency and transferable vocational competencies.",
                    "Deaf-Friendly Employer Placements" to "Vetted job matches with hiring managers trained in visual communication.",
                    "Post-Placement 90-Day Support" to "Proactive retention check-ins ensuring smooth accommodation delivery."
                )

                items(magicianServices) { (title, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldSuccess)
                            Column {
                                Text(title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            IntegrationTab.VURA_AI -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("VURA Knowledge Engine & RAG", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF3B82F6).copy(alpha = 0.2f)) {
                                    Text("pinkycollie/vura", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "PinkAI queries the authoritative Deaf Knowledge Registry, ranks source trust (Levels 1 to 5), and generates verifiable citations.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                val vuraPillars = listOf(
                    "Level 5 Trust: Law & Gov" to "ADA Title I & III, Rehabilitation Act § 504 & § 103, EEOC Enforcement Directives.",
                    "Level 4 Trust: Academic & Orgs" to "Gallaudet University research, NAD advocacy reports, DeafTech benchmarks.",
                    "Level 3 Trust: Reviewed SOPs" to "Internal VR4DEAF operational procedures, counselor workflows, accommodation standards.",
                    "Level 2 & 1 Trust: Lived Experience" to "Community submissions, employer accommodation reviews, regional sign variants."
                )

                items(vuraPillars) { (title, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF3B82F6))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            IntegrationTab.PINKSYNC -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("PinkSync & DeafAuth Security Mesh", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF8B5CF6).copy(alpha = 0.2f)) {
                                    Text("pinkycollie/pinksync", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D28D9), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Zero-trust accessible identity bridge. Protects sensitive counselor notes, synchronizes CART streams, and coordinates with state VR gateways.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                val syncItems = listOf(
                    "Identity Bridge (DeafAuth)" to "Cryptographically signs participant communication preferences for rapid employer accommodation setup.",
                    "Real-Time Stream Dispatcher" to "Routes CART captioning text streams via WebSocket to mobile and desktop client interfaces.",
                    "Offline-First Resiliency" to "Local SQLite/Room caching ensures client IPE checklists and video lessons remain accessible offline."
                )

                items(syncItems) { (title, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.SyncLock, contentDescription = null, tint = Color(0xFF8B5CF6))
                            Column {
                                Text(title, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(desc, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onStartJourney,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Access VR Client Portal")
            }
        }
    }
}
