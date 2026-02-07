package com.example.uomsmart.screens.canteen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uomsmart.R
import com.example.uomsmart.data.models.Meal
import com.example.uomsmart.ui.theme.SplashButtonBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenScreen(
    onBackClick: () -> Unit = {},
    onMealBooked: (Meal, Double) -> Unit = { _, _ -> }
) {
    var walletBalance by remember { mutableStateOf(950.75) }
    
    val meals = listOf(
        Meal("1", "Rice & Curry", "A traditional Sri Lankan meal featuring fragrant rice served with a variety of flavorful curries.", 250.00),
        Meal("2", "Pasta Carbonara", "Classic Italian pasta dish made with eggs, hard cheese (Pecorino Romano or Parmesan), cured", 320.00),
        Meal("3", "Vegan Buddha Bowl", "A wholesome and nutritious bowl filled with quinoa, roasted sweet potatoes, fresh avocado,", 280.00)
    )
    
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Meal Booking",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F7FC))
                .padding(paddingValues)
        ) {
            // Wallet Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                        Text(
                            text = "Wallet",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View Transactions",
                            color = SplashButtonBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = ">",
                            color = SplashButtonBlue,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // Balance
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
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
                        color = SplashButtonBlue
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Meal Cards
            items(meals) { meal ->
                MealCard(
                    meal = meal,
                    onBookClick = {
                        if (walletBalance >= meal.price) {
                            walletBalance -= meal.price
                            onMealBooked(meal, walletBalance)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun MealCard(
    meal: Meal,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFE8E8E8))
            )
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = meal.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.2f", meal.price),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SplashButtonBlue
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "UOM Coins",
                        fontSize = 12.sp,
                        color = SplashButtonBlue
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}
