package com.example.jobmagician.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jobmagician.data.*
import com.example.jobmagician.ui.theme.AmberWarning
import com.example.jobmagician.ui.theme.BlueTws
import com.example.jobmagician.ui.theme.EmeraldSuccess
import com.example.jobmagician.ui.theme.PurplePrimary

@Composable
fun DashboardScreen(
    activeTab: String,
    uiState: AppUiState,
    onTabSelected: (String) -> Unit,
    onEnrollCourse: (String) -> Unit,
    onAddApplication: (title: String, company: String, note: String) -> Unit
) {
    var showAddAppDialog by remember { mutableStateOf(false) }
    var newAppTitle by remember { mutableStateOf("") }
    var newAppCompany by remember { mutableStateOf("") }
    var newAppNote by remember { mutableStateOf("") }
    var newAppAccommodation by remember { mutableStateOf("Certified ASL Interpreter & CART Requested") }

    var showReportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Client VR Portal & IPE Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Personalized Vocational Rehabilitation, IPE milestones, training modules, and job placements.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Dashboard Navigation Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = when (activeTab) {
                    "overview" -> 0
                    "ipe" -> 1
                    "assessment" -> 2
                    "training" -> 3
                    "placement" -> 4
                    else -> 0
                },
                edgePadding = 0.dp
            ) {
                Tab(
                    selected = activeTab == "overview",
                    onClick = { onTabSelected("overview") },
                    text = { Text("Overview", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == "ipe",
                    onClick = { onTabSelected("ipe") },
                    text = { Text("IPE Plan", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == "assessment",
                    onClick = { onTabSelected("assessment") },
                    text = { Text("Assessment", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == "training",
                    onClick = { onTabSelected("training") },
                    text = { Text("Training", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeTab == "placement",
                    onClick = { onTabSelected("placement") },
                    text = { Text("Placement", fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        // TAB CONTENT
        when (activeTab) {
            "overview" -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Client Overall VR Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val progressItems = listOf(
                                "Intake & DeafAuth Verified" to 1.0f,
                                "Skills & Aptitude Assessment" to 1.0f,
                                "IPE Training Plan" to 0.70f,
                                "Job Placement & Employer Outreach" to 0.45f
                            )

                            progressItems.forEach { (label, value) ->
                                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { value },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        color = if (value == 1.0f) EmeraldSuccess else PurplePrimary,
                                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Upcoming VR Appointments",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            uiState.appointments.forEach { appt ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = PurplePrimary.copy(alpha = 0.1f),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Event,
                                                contentDescription = null,
                                                tint = PurplePrimary
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(appt.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text(appt.dateTime, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(appt.location, style = MaterialTheme.typography.labelSmall, color = BlueTws)
                                    }

                                    Badge(containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                                        Text(appt.tag, fontSize = 10.sp)
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Program Eligibility & Funding Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            uiState.eligibilities.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (item.isEligible == true) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (item.isEligible == true) EmeraldSuccess else AmberWarning
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.program, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            item.status,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (item.isEligible == true) EmeraldSuccess else AmberWarning
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "ipe" -> {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column {
                                    Text("Individualized Plan for Employment (IPE)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text("Mandated under Rehabilitation Act § 102", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                "Target Vocational Goal: Senior Systems Administrator / IT Support Lead\nEstimated Completion Date: October 2026\nFunding Partner: Texas Workforce Solutions (TWS-VR)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                val ipeMilestones = listOf(
                    "1. Complete CompTIA Security+ & Cloud Training" to "Status: In-Progress (65%) • Funded by VR Grant",
                    "2. ASL Technical Mock Interview Series (3 sessions)" to "Status: Completed with CDI Specialist",
                    "3. ADA Workplace Accommodation Packet Prepared" to "Status: Approved & Attached to DeafAuth Profile",
                    "4. Competitive Integrated Job Placement" to "Status: Active Applications in Review"
                )

                items(ipeMilestones) { (milestone, detail) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color(0xFF10B981))
                            Column {
                                Text(milestone, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            "assessment" -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Skills & Aptitude Inventory",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Administered via ASL-guided evaluation • Verified by CRC",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Skill Proficiency", fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))

                            uiState.skillProficiencies.forEach { skill ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(skill.skill, style = MaterialTheme.typography.bodyMedium)
                                        Text("${skill.percentage}%", fontWeight = FontWeight.Bold)
                                    }
                                    LinearProgressIndicator(
                                        progress = { skill.percentage / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp),
                                        color = PurplePrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Recommended Career Matches", fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))

                            uiState.careerRecommendations.forEach { rec ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(rec.title, fontWeight = FontWeight.Bold)
                                            Text(rec.demandOutlook, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Badge(containerColor = EmeraldSuccess, contentColor = Color.White) {
                                            Text("Match: ${rec.matchScore}%", modifier = Modifier.padding(4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { showReportDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download Full Assessment Report")
                    }
                }
            }

            "training" -> {
                item {
                    Text(
                        text = "Personalized Training Plan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.trainingCourses) { course ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(course.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                val badgeColor = when (course.status) {
                                    CourseStatus.IN_PROGRESS -> PurplePrimary
                                    CourseStatus.SCHEDULED -> BlueTws
                                    CourseStatus.RECOMMENDED -> AmberWarning
                                    CourseStatus.COMPLETED -> EmeraldSuccess
                                }
                                Badge(containerColor = badgeColor, contentColor = Color.White) {
                                    Text(course.status.name.replace("_", " "), fontSize = 10.sp, modifier = Modifier.padding(4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(course.description, style = MaterialTheme.typography.bodySmall)

                            if (course.status == CourseStatus.IN_PROGRESS) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { course.progressPercent / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp),
                                    color = PurplePrimary
                                )
                                Text("Progress: ${course.progressPercent}%", style = MaterialTheme.typography.labelSmall)
                            } else if (course.dateText.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(course.dateText, style = MaterialTheme.typography.labelSmall, color = BlueTws)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (course.status == CourseStatus.RECOMMENDED) {
                                    Button(
                                        onClick = { onEnrollCourse(course.id) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Enroll Now")
                                    }
                                } else if (course.status == CourseStatus.IN_PROGRESS) {
                                    Button(
                                        onClick = {},
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Continue Training")
                                    }
                                }
                                OutlinedButton(
                                    onClick = {},
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("View Details")
                                }
                            }
                        }
                    }
                }
            }

            "placement" -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Job Application Tracker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Button(
                            onClick = { showAddAppDialog = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Application")
                        }
                    }
                }

                items(uiState.jobApplications) { app ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(app.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(app.company, style = MaterialTheme.typography.bodyMedium, color = BlueTws)
                                }
                                val statusColor = when (app.status) {
                                    ApplicationStatus.INTERVIEW_SCHEDULED -> EmeraldSuccess
                                    ApplicationStatus.SUBMITTED -> PurplePrimary
                                    ApplicationStatus.DRAFT -> AmberWarning
                                    ApplicationStatus.OFFER_RECEIVED -> Color(0xFF10B981)
                                }
                                Badge(containerColor = statusColor, contentColor = Color.White) {
                                    Text(app.status.name.replace("_", " "), fontSize = 10.sp, modifier = Modifier.padding(4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Applied: ${app.appliedDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (app.note.isNotEmpty()) {
                                Text("Note: ${app.note}", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Accommodation: ${app.accommodationRequested}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Application Dialog
    if (showAddAppDialog) {
        AlertDialog(
            onDismissRequest = { showAddAppDialog = false },
            title = { Text("Track New Job Application") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newAppTitle,
                        onValueChange = { newAppTitle = it },
                        label = { Text("Job Position Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAppCompany,
                        onValueChange = { newAppCompany = it },
                        label = { Text("Company Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAppAccommodation,
                        onValueChange = { newAppAccommodation = it },
                        label = { Text("Accommodation Requested") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAppNote,
                        onValueChange = { newAppNote = it },
                        label = { Text("Notes / Counselor Updates") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAppTitle.isNotBlank() && newAppCompany.isNotBlank()) {
                            onAddApplication(newAppTitle, newAppCompany, newAppNote)
                            newAppTitle = ""
                            newAppCompany = ""
                            newAppNote = ""
                            showAddAppDialog = false
                        }
                    }
                ) {
                    Text("Save Application")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAppDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Report Download Dialog
    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Vocational Assessment Report") },
            text = {
                Text("Your official VR Skills Assessment & IPE Milestone Report (WIOA/TWS format) has been compiled. Includes aptitude scores, recommended job codes, and ASL accessibility preferences.")
            },
            confirmButton = {
                Button(onClick = { showReportDialog = false }) {
                    Text("Download PDF")
                }
            }
        )
    }
}
