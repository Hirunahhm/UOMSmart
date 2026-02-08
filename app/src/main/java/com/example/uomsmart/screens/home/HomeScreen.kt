package com.example.uomsmart.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uomsmart.R
import com.example.uomsmart.data.models.Event
import com.example.uomsmart.data.models.Occupancy
import com.example.uomsmart.ui.theme.SplashButtonBlue
import com.example.uomsmart.viewmodel.AuthViewModel
import com.example.uomsmart.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
        onAiScoutClick: () -> Unit = {},
        onSettingsClick: () -> Unit = {},
        onNotificationClick: () -> Unit = {},
        viewModel: HomeViewModel = viewModel(),
        authViewModel: AuthViewModel = viewModel()
) {
        // Observe ViewModel state
        val events = viewModel.events
        val occupancies = viewModel.occupancies
        val isLoading = viewModel.isLoading
        val walletBalance = viewModel.walletBalance

        var showTopUpDialog by remember { mutableStateOf(false) }
        var topUpAmount by remember { mutableStateOf("") }

        if (showTopUpDialog) {
                AlertDialog(
                        onDismissRequest = { showTopUpDialog = false },
                        title = { Text("Top Up Wallet") },
                        text = {
                                Column {
                                        Text("Enter amount to add:")
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedTextField(
                                                value = topUpAmount,
                                                onValueChange = {
                                                        if (it.all { char -> char.isDigit() })
                                                                topUpAmount = it
                                                },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                singleLine = true,
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                                listOf("100", "500", "1000").forEach { amount ->
                                                        Button(
                                                                onClick = { topUpAmount = amount },
                                                                colors =
                                                                        ButtonDefaults.buttonColors(
                                                                                containerColor =
                                                                                        if (topUpAmount ==
                                                                                                        amount
                                                                                        )
                                                                                                SplashButtonBlue
                                                                                        else
                                                                                                Color.Gray
                                                                        )
                                                        ) { Text(amount) }
                                                }
                                        }
                                }
                        },
                        confirmButton = {
                                Button(
                                        onClick = {
                                                val amount = topUpAmount.toDoubleOrNull()
                                                if (amount != null && amount > 0) {
                                                        viewModel.topUpWallet(amount)
                                                        showTopUpDialog = false
                                                        topUpAmount = ""
                                                }
                                        },
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = SplashButtonBlue
                                                )
                                ) { Text("Top Up") }
                        },
                        dismissButton = {
                                TextButton(onClick = { showTopUpDialog = false }) { Text("Cancel") }
                        }
                )
        }

        Scaffold(
                floatingActionButton = {
                        FloatingActionButton(
                                onClick = onAiScoutClick,
                                containerColor = com.example.uomsmart.ui.theme.MintGreen,
                                shape = CircleShape
                        ) {
                                Icon(
                                        painter = painterResource(id = R.drawable.ic_camera),
                                        contentDescription = "AI Scout",
                                        tint = Color.White
                                )
                        }
                }
        ) { paddingValues ->
                LazyColumn(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(Color.White)
                                        .padding(paddingValues),
                        contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                        // Header
                        item {
                                if (isLoading) {
                                        LinearProgressIndicator(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(bottom = 8.dp),
                                                color = SplashButtonBlue
                                        )
                                }

                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(48.dp)
                                                                        .clip(CircleShape)
                                                                        .background(
                                                                                Color.LightGray
                                                                        ),
                                                        contentAlignment = Alignment.Center
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Person,
                                                                contentDescription = "Profile",
                                                                tint = Color.White
                                                        )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                        Text(
                                                                text =
                                                                        "Hello, ${authViewModel.currentUser?.displayName?.split(" ")?.firstOrNull() ?: "Student"}!",
                                                                fontSize = 20.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        com.example.uomsmart.ui
                                                                                .theme.TextPrimary
                                                        )
                                                }
                                        }

                                        Row {
                                                IconButton(onClick = onNotificationClick) {
                                                        Icon(
                                                                painter =
                                                                        painterResource(
                                                                                id =
                                                                                        R.drawable
                                                                                                .ic_notification
                                                                        ),
                                                                contentDescription =
                                                                        "Notifications",
                                                                tint =
                                                                        com.example.uomsmart.ui
                                                                                .theme.TextPrimary
                                                        )
                                                }
                                                IconButton(onClick = onSettingsClick) {
                                                        Icon(
                                                                painter =
                                                                        painterResource(
                                                                                id =
                                                                                        R.drawable
                                                                                                .ic_settings
                                                                        ),
                                                                contentDescription = "Settings",
                                                                tint =
                                                                        com.example.uomsmart.ui
                                                                                .theme.TextPrimary
                                                        )
                                                }
                                        }
                                }
                        }

                        // Campus Wallet Card
                        item {
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                        )
                                                        .height(180.dp)
                                                        .clip(RoundedCornerShape(20.dp))
                                                        .background(
                                                                brush =
                                                                        androidx.compose.ui.graphics
                                                                                .Brush
                                                                                .horizontalGradient(
                                                                                        colors =
                                                                                                listOf(
                                                                                                        com.example
                                                                                                                .uomsmart
                                                                                                                .ui
                                                                                                                .theme
                                                                                                                .WalletGradientStart,
                                                                                                        com.example
                                                                                                                .uomsmart
                                                                                                                .ui
                                                                                                                .theme
                                                                                                                .WalletGradientEnd
                                                                                                )
                                                                                )
                                                        )
                                ) {
                                        // Background Wave Decoration (Simplified as a
                                        // circle for
                                        // now)
                                        Box(
                                                modifier =
                                                        Modifier.align(Alignment.BottomStart)
                                                                .size(200.dp)
                                                                .padding(
                                                                        start = (-50).dp,
                                                                        bottom = (-100).dp
                                                                )
                                                                .clip(CircleShape)
                                                                .background(
                                                                        Color.White.copy(
                                                                                alpha = 0.1f
                                                                        )
                                                                )
                                        )

                                        Column(
                                                modifier = Modifier.fillMaxSize().padding(20.dp),
                                                verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.Top
                                                ) {
                                                        Column {
                                                                Text(
                                                                        text = "Campus Wallet",
                                                                        color =
                                                                                Color.White.copy(
                                                                                        alpha = 0.8f
                                                                                ),
                                                                        fontSize = 14.sp
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        4.dp
                                                                                )
                                                                )
                                                                Text(
                                                                        text =
                                                                                "${String.format("%,.0f", walletBalance)} UOM Coins",
                                                                        color = Color.White,
                                                                        fontSize = 28.sp,
                                                                        fontWeight = FontWeight.Bold
                                                                )
                                                        }

                                                        // Reload Button
                                                        TextButton(
                                                                onClick = { showTopUpDialog = true }
                                                        ) {
                                                                Text(
                                                                        text = "Reload",
                                                                        color = Color.White,
                                                                        fontWeight =
                                                                                FontWeight.SemiBold
                                                                )
                                                        }
                                                }
                                        }

                                        // Bottom Decoration Line
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .height(40.dp)
                                                                .background(
                                                                        brush =
                                                                                Brush.verticalGradient(
                                                                                        colors =
                                                                                                listOf(
                                                                                                        Color.Transparent,
                                                                                                        Color.White
                                                                                                                .copy(
                                                                                                                        alpha =
                                                                                                                                0.2f
                                                                                                                )
                                                                                                )
                                                                                )
                                                                )
                                                                .align(Alignment.BottomCenter)
                                        )
                                }
                        }

                        // Upcoming Events Section
                        item {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 16.dp,
                                                                vertical = 8.dp
                                                        ),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = "Upcoming Events",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = com.example.uomsmart.ui.theme.TextPrimary
                                        )
                                        Text(
                                                text = "See All",
                                                fontSize = 14.sp,
                                                color = SplashButtonBlue,
                                                fontWeight = FontWeight.Medium,
                                                modifier =
                                                        Modifier
                                                                .clickable { /* TODO: Navigate to events */
                                                                }
                                        )
                                }
                        }

                        item {
                                LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) { items(events) { event -> EventCard(event = event) } }
                        }

                        // Live Study Spot Occupancy
                        item {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                start = 16.dp,
                                                                end = 16.dp,
                                                                top = 24.dp,
                                                                bottom = 8.dp
                                                        ),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = "Live Study Spot Occupancy",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = com.example.uomsmart.ui.theme.TextPrimary
                                        )
                                        Text(
                                                text = "View Map",
                                                fontSize = 14.sp,
                                                color = SplashButtonBlue,
                                                fontWeight = FontWeight.Medium,
                                                modifier =
                                                        Modifier
                                                                .clickable { /* TODO: Navigate to map */
                                                                }
                                        )
                                }
                        }

                        items(occupancies) { occupancy -> OccupancyCard(occupancy = occupancy) }
                }
        }
}

@Composable
fun EventCard(event: Event) {
        Card(
                modifier = Modifier.width(280.dp), // Wider card
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column {
                        // Placeholder image
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(140.dp)
                                                .background(Color.LightGray)
                        ) {
                                // Overlay Text (Title)
                                Box(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .background(
                                                                brush =
                                                                        androidx.compose.ui.graphics
                                                                                .Brush
                                                                                .verticalGradient(
                                                                                        colors =
                                                                                                listOf(
                                                                                                        Color.Transparent,
                                                                                                        Color.Black
                                                                                                                .copy(
                                                                                                                        alpha =
                                                                                                                                0.7f
                                                                                                                )
                                                                                                )
                                                                                )
                                                        )
                                )

                                Text(
                                        text = event.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier =
                                                Modifier.align(Alignment.BottomStart).padding(12.dp)
                                )
                        }

                        Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                        text = "Future-Proof Your Skills", // Subtitle placeholder
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                imageVector =
                                                        Icons.Default.DateRange, // Use vector icon
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text =
                                                        "${event.date} • ${event.location}", // Combined
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                maxLines = 1,
                                                overflow =
                                                        androidx.compose.ui.text.style.TextOverflow
                                                                .Ellipsis
                                        )
                                }
                        }
                }
        }
}

@Composable
fun OccupancyCard(occupancy: Occupancy) {
        // Derive status from percentage
        val status =
                when {
                        occupancy.currentPercentage >= 80 -> "Busy"
                        occupancy.currentPercentage >= 50 -> "Moderate"
                        else -> "Quiet"
                }

        val statusColor =
                when (status) {
                        "Busy" -> com.example.uomsmart.ui.theme.StatusRed
                        "Quiet" -> com.example.uomsmart.ui.theme.StatusGreen
                        else -> com.example.uomsmart.ui.theme.StatusBlue
                }

        val progress =
                when (status) {
                        "Busy" -> 0.8f
                        "Quiet" -> 0.2f
                        else -> 0.5f
                }

        Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = occupancy.location,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = com.example.uomsmart.ui.theme.TextPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Status Pill
                                        Box(
                                                modifier =
                                                        Modifier.background(
                                                                        statusColor,
                                                                        RoundedCornerShape(50)
                                                                )
                                                                .padding(
                                                                        horizontal = 12.dp,
                                                                        vertical = 4.dp
                                                                )
                                        ) {
                                                Text(
                                                        text = status,
                                                        color = Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        // Progress Bar
                                        LinearProgressIndicator(
                                                progress = progress,
                                                modifier =
                                                        Modifier.width(100.dp)
                                                                .height(6.dp)
                                                                .clip(RoundedCornerShape(3.dp)),
                                                color =
                                                        androidx.compose.ui.graphics.Color(
                                                                0xFF3F51B5
                                                        ), // Dark Blue
                                                trackColor =
                                                        androidx.compose.ui.graphics.Color(
                                                                0xFFEEEEEE
                                                        )
                                        )
                                }
                        }

                        Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Details",
                                tint = Color.Gray
                        )
                }
        }
}
