package com.example.uomsmart.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.example.uomsmart.ui.theme.SplashBlueAccent
import com.example.uomsmart.ui.theme.SplashButtonBlue
import com.example.uomsmart.ui.theme.UOMGold
import com.example.uomsmart.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
        onAiScoutClick: () -> Unit = {},
        onSettingsClick: () -> Unit = {},
        onNotificationClick: () -> Unit = {},
        viewModel: HomeViewModel = viewModel()
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
                                                                                        Color.LightGray
                                                                        ),
                                                                contentPadding =
                                                                        PaddingValues(
                                                                                horizontal = 8.dp
                                                                        )
                                                        ) { Text(amount, color = Color.Black) }
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
                                containerColor = SplashButtonBlue,
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
                                        Box(
                                                modifier =
                                                        Modifier.size(44.dp)
                                                                .background(
                                                                        SplashBlueAccent,
                                                                        RoundedCornerShape(12.dp)
                                                                ),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        painter =
                                                                painterResource(
                                                                        id =
                                                                                R.drawable
                                                                                        .ic_graduation_cap
                                                                ),
                                                        contentDescription = "Logo",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(28.dp)
                                                )
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
                                                                tint = Color.Gray
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
                                                                tint = Color.Gray
                                                        )
                                                }
                                        }
                                }
                        }

                        // Campus Wallet Section (Moved to Top)
                        item {
                                Column(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(horizontal = 16.dp)
                                                        .padding(bottom = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Text(
                                                text = "Campus Wallet",
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier =
                                                        Modifier.clickable {
                                                                showTopUpDialog = true
                                                        }
                                        ) {
                                                Icon(
                                                        painter =
                                                                painterResource(
                                                                        id = R.drawable.ic_coin
                                                                ),
                                                        contentDescription = "Coins",
                                                        tint = UOMGold,
                                                        modifier = Modifier.size(32.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        text = String.format("%.2f", walletBalance),
                                                        fontSize = 32.sp,
                                                        fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        text = "UOM Coins",
                                                        color = SplashButtonBlue,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium
                                                )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                                onClick = { showTopUpDialog = true },
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = SplashButtonBlue
                                                        ),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(36.dp)
                                        ) { Text("Top Up", fontSize = 12.sp) }
                                }
                        }

                        // Upcoming Events Section
                        item {
                                Text(
                                        text = "Upcoming Events",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 16.dp,
                                                        vertical = 8.dp
                                                )
                                )
                        }

                        item {
                                LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) { items(events) { event -> EventCard(event = event) } }
                        }

                        // Live Study Spot Occupancy
                        item {
                                Text(
                                        text = "Live Study Spot Occupancy",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier =
                                                Modifier.padding(
                                                        start = 16.dp,
                                                        end = 16.dp,
                                                        top = 24.dp,
                                                        bottom = 8.dp
                                                )
                                )
                        }

                        items(occupancies) { occupancy -> OccupancyCard(occupancy = occupancy) }
                }
        }
}

@Composable
fun EventCard(event: Event) {
        Card(
                modifier = Modifier.width(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Column {
                        // Placeholder image
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(100.dp)
                                                .background(Color.LightGray)
                        )

                        Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                        text = event.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "📅", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = event.date,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                        )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "📍", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = event.location,
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                maxLines = 1
                                        )
                                }
                        }
                }
        }
}

@Composable
fun OccupancyCard(occupancy: Occupancy) {
        Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = occupancy.location,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = "${occupancy.currentPercentage}% Occupied",
                                        color =
                                                when {
                                                        occupancy.currentPercentage >= 80 ->
                                                                Color.Red
                                                        occupancy.currentPercentage >= 50 ->
                                                                SplashButtonBlue
                                                        else -> Color(0xFF4CAF50)
                                                },
                                        fontSize = 14.sp
                                )
                        }

                        // Vertical progress bar
                        Box(
                                modifier =
                                        Modifier.width(12.dp)
                                                .height(48.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFE0E0E0)),
                                contentAlignment = Alignment.BottomCenter
                        ) {
                                Box(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .fillMaxHeight(
                                                                occupancy.currentPercentage / 100f
                                                        )
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(SplashButtonBlue)
                                )
                        }
                }
        }
}
