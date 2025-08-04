package com.msg91.hellochatwidgetsdkapp.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // File upload is now completely automatic - no test buttons needed
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsSection("Appearance") {
                SettingsSwitchItem(
                    icon = Icons.Default.Create,
                    title = "Dark Mode",
                    subtitle = "Switch between light and dark theme",
                    checked = isDarkMode,
                    onCheckedChange = { isDarkMode = it }
                )
            }

            SettingsSection("Notifications") {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    subtitle = "Receive notifications about new messages",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                
                SettingsSwitchItem(
                    icon = Icons.Default.LocationOn,
                    title = "Sound",
                    subtitle = "Play sound for notifications",
                    checked = soundEnabled,
                    onCheckedChange = { soundEnabled = it }
                )
            }

            SettingsSection("Privacy") {
                SettingsSwitchItem(
                    icon = Icons.Default.LocationOn,
                    title = "Location Services",
                    subtitle = "Allow app to access your location",
                    checked = locationEnabled,
                    onCheckedChange = { locationEnabled = it }
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
