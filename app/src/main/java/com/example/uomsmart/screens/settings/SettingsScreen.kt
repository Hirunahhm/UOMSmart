package com.example.uomsmart.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uomsmart.ui.theme.SplashButtonBlue
import com.example.uomsmart.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit = {}, authViewModel: AuthViewModel = viewModel()) {
    val user = authViewModel.currentUser
    var isDarkMode by remember { mutableStateOf(false) }
    var canteenAlerts by remember { mutableStateOf(true) }
    var eventReminders by remember { mutableStateOf(true) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Settings", color = Color.White) },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = SplashButtonBlue
                                ),
                        navigationIcon = {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                )
                            }
                        }
                )
            }
    ) { paddingValues ->
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(paddingValues)
                                .background(Color(0xFFF5F5F5))
                                .verticalScroll(rememberScrollState())
        ) {
            // Profile Section
            Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                            imageVector =
                                    Icons.Default.Person, // Ideally replace with Profile Picture if
                            // available
                            contentDescription = "Profile",
                            modifier =
                                    Modifier.size(80.dp)
                                            .background(Color.LightGray, CircleShape)
                                            .padding(16.dp),
                            tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                            text = user?.displayName ?: "Student Name",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                    )
                    Text(
                            text = user?.email
                                            ?: "student@uom.lk", // Mock email usually matches auth
                            color = Color.Gray,
                            fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = "Department: Computer Science & Engineering", // Mock Data
                            color = SplashButtonBlue,
                            fontWeight = FontWeight.Medium
                    )
                }
            }

            // Preferences
            SectionHeader(title = "Preferences")
            SettingsToggleItem(
                    title = "Dark Mode",
                    subtitle = "Switch to dark theme",
                    checked = isDarkMode,
                    onCheckedChange = { isDarkMode = it }
            )
            SettingsToggleItem(
                    title = "Canteen Alerts",
                    subtitle = "Get notified about pre-orders",
                    checked = canteenAlerts,
                    onCheckedChange = { canteenAlerts = it }
            )
            SettingsToggleItem(
                    title = "Event Reminders",
                    subtitle = "Upcoming events alerts",
                    checked = eventReminders,
                    onCheckedChange = { eventReminders = it }
            )

            // Emergency Contacts
            SectionHeader(title = "Emergency Contacts")
            EmergencyContactItem(title = "Campus Security", number = "+94 11 265 0301")
            EmergencyContactItem(title = "University Medical Center", number = "+94 11 265 0302")

            Spacer(modifier = Modifier.height(24.dp))

            // Logout (Moved here or just redundant as it is in About too)
            // Keeping it simple as requested in plan
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(
        title: String,
        subtitle: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = SplashButtonBlue)
            )
        }
    }
}

@Composable
fun EmergencyContactItem(title: String, number: String) {
    Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(text = number, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}
