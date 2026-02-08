package com.example.uomsmart.screens.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uomsmart.data.models.Transaction
import com.example.uomsmart.ui.theme.SplashButtonBlue
import com.example.uomsmart.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
        onBackClick: () -> Unit = {},
        viewModel: TransactionViewModel = viewModel()
) {
    val transactions = viewModel.transactions
    val isLoading = viewModel.isLoading

    Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                            title = {
                                Text("Transaction History", fontWeight = FontWeight.SemiBold)
                            },
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
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
    ) { paddingValues ->
        LazyColumn(
                modifier =
                        Modifier.fillMaxSize().background(Color(0xFFF8F7FC)).padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
        ) {
            if (isLoading) {
                item {
                    Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = SplashButtonBlue) }
                }
            } else if (transactions.isEmpty()) {
                item {
                    Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                    ) { Text("No transactions found", color = Color.Gray) }
                }
            } else {
                items(transactions) { transaction -> TransactionCard(transaction = transaction) }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    val isDebit = transaction.type == "DEBIT"
    val color = if (isDebit) Color.Red else Color(0xFF4CAF50)
    val sign = if (isDebit) "-" else "+"

    val date = Date(transaction.timestamp)
    val formattedDate = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date)

    Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = transaction.description,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                )
                Text(text = formattedDate, color = Color.Gray, fontSize = 12.sp)
            }

            Text(
                    text = "$sign${String.format("%.2f", transaction.amount)}",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
            )
        }
    }
}
