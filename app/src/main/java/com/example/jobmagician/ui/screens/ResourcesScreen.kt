package com.example.jobmagician.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.jobmagician.data.ResourceData
import com.example.jobmagician.ui.theme.BlueTws
import com.example.jobmagician.ui.theme.PurplePrimary

@Composable
fun ResourcesScreen(
    resources: List<ResourceData>,
    onOpenResource: (ResourceData) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Resources & ASL Guides",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Vocational rehabilitation rights, WIOA rules, and ASL training media",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(resources) { item ->
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
                        Text(item.category, style = MaterialTheme.typography.labelSmall, color = BlueTws)

                        if (item.isAslAvailable) {
                            AssistChip(
                                onClick = {},
                                label = { Text("ASL Video Available", fontSize = 10.sp) },
                                leadingIcon = { Icon(Icons.Default.SignLanguage, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = PurplePrimary.copy(alpha = 0.15f), labelColor = PurplePrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(item.description, style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { onOpenResource(item) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Guide / Play ASL Video")
                    }
                }
            }
        }
    }
}
