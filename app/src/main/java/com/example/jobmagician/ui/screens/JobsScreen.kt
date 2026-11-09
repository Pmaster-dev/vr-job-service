package com.example.jobmagician.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.jobmagician.data.JobListingData
import com.example.jobmagician.ui.theme.BlueTws
import com.example.jobmagician.ui.theme.EmeraldSuccess
import com.example.jobmagician.ui.theme.PurplePrimary

@Composable
fun JobsScreen(
    jobListings: List<JobListingData>,
    onApplyJob: (JobListingData) -> Unit
) {
    var filterQuery by remember { mutableStateOf("") }
    var selectedFilterTag by remember { mutableStateOf("All") }

    val filterTags = listOf("All", "WIOA Partner", "Deaf-Friendly", "Remote", "360 Employer")

    val filteredList = jobListings.filter { listing ->
        (selectedFilterTag == "All" || listing.tags.contains(selectedFilterTag)) &&
                (filterQuery.isEmpty() || listing.title.contains(filterQuery, ignoreCase = true) || listing.company.contains(filterQuery, ignoreCase = true))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Job Opportunities",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "WIOA and Texas Workforce Solutions matched positions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = filterQuery,
                onValueChange = { filterQuery = it },
                placeholder = { Text("Filter jobs by keyword or company...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterTags.forEach { tag ->
                    FilterChip(
                        selected = selectedFilterTag == tag,
                        onClick = { selectedFilterTag = tag },
                        label = { Text(tag, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filteredList) { job ->
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
                            Text(job.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(job.company, color = BlueTws, fontWeight = FontWeight.SemiBold)
                            Text(job.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Badge(containerColor = EmeraldSuccess, contentColor = Color.White) {
                            Text("${job.matchScore}% Match", fontSize = 11.sp, modifier = Modifier.padding(4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(job.description, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        job.tags.forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tag, fontSize = 10.sp) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Posted: ${job.postedDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Button(
                            onClick = { onApplyJob(job) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Apply & Track")
                        }
                    }
                }
            }
        }
    }
}
