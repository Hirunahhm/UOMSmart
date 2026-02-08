package com.example.uomsmart.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uomsmart.ui.theme.SplashButtonBlue

data class NotificationItem(
        val id: Int,
        val title: String,
        val message: String,
        val time: String,
        val type: NotificationType
)

enum class NotificationType {
    CANTEEN,
    SECURITY,
    PULSE,
    SCOUT,
    SPOTFINDER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBackClick: () -> Unit = {}) {
    val notifications =
            listOf(
                    NotificationItem(
                            1,
                            "Canteen Booking Confirmed",
                            "Your Lunch booking is confirmed for 11:30 AM.",
                            "10:00 AM",
                            NotificationType.CANTEEN
                    ),
                    NotificationItem(
                            2,
                            "Digital Gate Pass Active",
                            "Your pass is valid for the next 2 hours.",
                            "09:30 AM",
                            NotificationType.SECURITY
                    ),
                    NotificationItem(
                            3,
                            "Green Report Update",
                            "Your report for 'Leaking Tap' has been received by maintenance.",
                            "Yesterday",
                            NotificationType.SCOUT
                    ),
                    NotificationItem(
                            4,
                            "Library Crowded",
                            "The Library is currently 90% full. Consider the Study Hall.",
                            "Yesterday",
                            NotificationType.SPOTFINDER
                    ),
                    NotificationItem(
                            5,
                            "Flash Event Alert",
                            "Workshop starting in 15 mins at Computer Lab.",
                            "Yesterday",
                            NotificationType.PULSE
                    )
            )

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Notifications", color = Color.White) },
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
        LazyColumn(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(paddingValues)
                                .background(Color(0xFFF5F5F5))
                                .padding(16.dp)
        ) { items(notifications) { notification -> NotificationCard(notification) } }
    }
}

@Composable
fun NotificationCard(notification: NotificationItem) {
    Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(
                    imageVector = Icons.Default.Notifications, // Could be dynamic based on type
                    contentDescription = "Notification",
                    tint = getTypeColor(notification.type),
                    modifier = Modifier.padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                                androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text(
                            text = notification.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                    )
                    Text(text = notification.time, fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = notification.message, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}

fun getTypeColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.CANTEEN -> Color(0xFFFFA000) // Amber
        NotificationType.SECURITY -> Color(0xFFD32F2F) // Red
        NotificationType.PULSE -> Color(0xFF1976D2) // Blue
        NotificationType.SCOUT -> Color(0xFF388E3C) // Green
        NotificationType.SPOTFINDER -> Color(0xFF7B1FA2) // Purple
    }
}
