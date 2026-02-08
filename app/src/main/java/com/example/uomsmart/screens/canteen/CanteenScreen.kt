package com.example.uomsmart.screens.canteen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uomsmart.R
import com.example.uomsmart.data.models.Meal
import com.example.uomsmart.data.models.Order
import com.example.uomsmart.ui.theme.SplashButtonBlue
import com.example.uomsmart.viewmodel.CanteenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenScreen(
        onBackClick: () -> Unit = {},
        onTransactionHistoryClick: () -> Unit = {},
        viewModel: CanteenViewModel = viewModel()
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Menu", "My Orders")

    // Observe ViewModel state
    val meals = viewModel.meals
    val myOrders = viewModel.myOrders
    val walletBalance = viewModel.walletBalance
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    var showConfirmDialog by remember { mutableStateOf<Meal?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showFailureDialog by remember { mutableStateOf<String?>(null) }

    if (showConfirmDialog != null) {
        AlertDialog(
                onDismissRequest = { showConfirmDialog = null },
                title = { Text("Confirm Booking") },
                text = {
                    Text(
                            "Do you want to buy ${showConfirmDialog?.name} for ${showConfirmDialog?.price} coins?"
                    )
                },
                confirmButton = {
                    Button(
                            onClick = {
                                val meal = showConfirmDialog
                                showConfirmDialog = null
                                meal?.let {
                                    viewModel.bookMeal(it) { success, error ->
                                        if (success) {
                                            showSuccessDialog = true
                                        } else {
                                            showFailureDialog = error
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SplashButtonBlue)
                    ) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = null }) { Text("Cancel") }
                }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Success") },
                text = { Text("Meal booked successfully! Check 'My Orders' for details.") },
                confirmButton = {
                    Button(
                            onClick = { showSuccessDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = SplashButtonBlue)
                    ) { Text("OK") }
                }
        )
    }

    if (showFailureDialog != null) {
        AlertDialog(
                onDismissRequest = { showFailureDialog = null },
                title = { Text("Error") },
                text = { Text(showFailureDialog ?: "Unknown error") },
                confirmButton = {
                    Button(
                            onClick = { showFailureDialog = null },
                            colors = ButtonDefaults.buttonColors(containerColor = SplashButtonBlue)
                    ) { Text("OK") }
                }
        )
    }

    Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                            title = { Text("Smart Canteen", fontWeight = FontWeight.SemiBold) },
                            navigationIcon = {
                                IconButton(onClick = onBackClick) {
                                    Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "Back"
                                    )
                                }
                            },
                            colors =
                                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                                            containerColor = Color.White
                                    )
                    )

                    TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = Color.White,
                            contentColor = SplashButtonBlue
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(title) }
                            )
                        }
                    }
                }
            }
    ) { paddingValues ->
        LazyColumn(
                modifier =
                        Modifier.fillMaxSize().background(Color(0xFFF8F7FC)).padding(paddingValues)
        ) {
            // Loading State
            if (isLoading) {
                item {
                    Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = SplashButtonBlue) }
                }
            }

            // Wallet Section (Always Visible)
            item {
                Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        painter = painterResource(id = R.drawable.ic_wallet),
                                        contentDescription = "Wallet",
                                        tint = SplashButtonBlue,
                                        modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Your Balance", color = Color.Gray, fontSize = 14.sp)
                            }

                            TextButton(onClick = onTransactionHistoryClick) {
                                Text(
                                        text = "History >",
                                        color = SplashButtonBlue,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                    text = String.format("%.2f", walletBalance),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SplashButtonBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                    text = "UOM Coins",
                                    fontSize = 14.sp,
                                    color = SplashButtonBlue,
                                    modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }
                }
            }

            if (selectedTabIndex == 0) {
                // MENU TAB
                if (meals.isEmpty() && !isLoading) {
                    item {
                        Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                        ) { Text("No meals available", color = Color.Gray) }
                    }
                }

                items(meals) { meal ->
                    MealCard(meal = meal, onBookClick = { showConfirmDialog = meal })
                }
            } else {
                // MY ORDERS TAB
                if (myOrders.isEmpty() && !isLoading) {
                    item {
                        Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                        ) { Text("No active orders", color = Color.Gray) }
                    }
                }

                items(myOrders) { order -> OrderCard(order = order) }
            }
        }
    }
}

@Composable
fun MealCard(meal: Meal, onBookClick: () -> Unit) {
    // Map meal name to local drawable resource
    val imageRes =
            when {
                meal.name.contains("Burger", ignoreCase = true) -> R.drawable.chicken_burger
                meal.name.contains("Fried Rice", ignoreCase = true) -> R.drawable.fried_rice
                meal.name.contains("Kottu", ignoreCase = true) -> R.drawable.veg_kottu
                meal.name.contains("Coffee", ignoreCase = true) -> R.drawable.ice_coffee
                meal.name.contains("Rice", ignoreCase = true) &&
                        meal.name.contains("Fish", ignoreCase = true) ->
                        R.drawable.rice_and_curry_fish
                else -> null
            }

    Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image
            if (imageRes != null) {
                Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = meal.name,
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder image
                Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Color(0xFFE8E8E8)))
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = meal.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = meal.description, color = Color.Gray, fontSize = 14.sp, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                                text = String.format("%.2f", meal.price),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = SplashButtonBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Coins", fontSize = 12.sp, color = SplashButtonBlue)
                    }

                    Button(
                            onClick = onBookClick,
                            colors = ButtonDefaults.buttonColors(containerColor = SplashButtonBlue),
                            shape = RoundedCornerShape(8.dp)
                    ) { Text("Book Now") }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Indicator
            Box(
                    modifier =
                            Modifier.size(12.dp)
                                    .background(
                                            when (order.status) {
                                                "PENDING" -> Color(0xFFFFA000) // Amber
                                                "READY" -> Color(0xFF4CAF50) // Green
                                                "COMPLETED" -> Color.Gray
                                                else -> Color.Red
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = order.mealName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                        text = "Token: ${order.token}",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = "-${order.price}", color = Color.Red, fontWeight = FontWeight.Bold)
                Text(text = order.status, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
