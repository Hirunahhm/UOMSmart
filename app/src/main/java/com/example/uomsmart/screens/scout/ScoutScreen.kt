package com.example.uomsmart.screens.scout

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uomsmart.R
import com.example.uomsmart.ui.theme.SplashButtonBlue
import com.example.uomsmart.viewmodel.ScoutUiState
import com.example.uomsmart.viewmodel.SustainabilityScoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoutScreen(
        onBackClick: () -> Unit = {},
        viewModel: SustainabilityScoutViewModel = viewModel()
) {
        val uiState = viewModel.uiState
        val capturedImage = viewModel.capturedImage

        val cameraLauncher =
                rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.TakePicturePreview()
                ) { bitmap -> bitmap?.let { viewModel.analyzeImage(it) } }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Sustainability Scout", color = Color.White) },
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
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(paddingValues)
                                        .background(Color.White)
                ) {
                        when (uiState) {
                                is ScoutUiState.Idle -> {
                                        IdleView(onCaptureClick = { cameraLauncher.launch(null) })
                                }
                                is ScoutUiState.Thinking -> {
                                        ThinkingView()
                                }
                                is ScoutUiState.Review -> {
                                        ReviewContent(
                                                viewModel = viewModel,
                                                capturedImage = capturedImage
                                        )
                                }
                                is ScoutUiState.Success -> {
                                        SuccessView(onScanAgain = { viewModel.resetState() })
                                }
                                is ScoutUiState.Error -> {
                                        ErrorView(
                                                message = uiState.message,
                                                onRetry = { viewModel.resetState() }
                                        )
                                }
                        }
                }
        }
}

@Composable
fun IdleView(onCaptureClick: () -> Unit) {
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Camera",
                        modifier = Modifier.size(100.dp),
                        tint = SplashButtonBlue
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                        text = "Spot a Sustainability Issue?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                )
                Text(
                        text = "Take a photo and let AI analyze it.",
                        fontSize = 16.sp,
                        color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                        onClick = onCaptureClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SplashButtonBlue),
                        modifier = Modifier.height(50.dp)
                ) { Text("Capture Issue") }
        }
}

@Composable
fun ThinkingView() {
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                CircularProgressIndicator(color = SplashButtonBlue)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                        text = "Scout is Thinking...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                )
                Text(
                        text = "Analyzing resource waste & opportunities",
                        fontSize = 14.sp,
                        color = Color.Gray
                )
        }
}

@Composable
fun SuccessView(onScanAgain: () -> Unit) {
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Complaint Submitted!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                        text = "Thank you for making UOM greener.",
                        fontSize = 16.sp,
                        color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                        onClick = onScanAgain,
                        colors = ButtonDefaults.buttonColors(containerColor = SplashButtonBlue),
                        modifier = Modifier.height(50.dp)
                ) { Text("Scan Another Issue") }
        }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
        Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
                Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Analysis Failed", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(
                        text = message,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = SplashButtonBlue)
                ) { Text("Try Again") }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewContent(viewModel: SustainabilityScoutViewModel, capturedImage: Bitmap?) {
        // Explicitly typed mutableStateOf to avoid inference issues
        var category by remember { mutableStateOf<String>(viewModel.analyzedCategory) }
        var description by remember { mutableStateOf<String>(viewModel.analyzedDescription) }
        var urgency by remember {
                androidx.compose.runtime.mutableFloatStateOf(viewModel.analyzedUrgency.toFloat())
        }

        Column(
                modifier =
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
                capturedImage?.let {
                        Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = "Captured Issue",
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                        text = "AI Analysis Result",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SplashButtonBlue
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Issue Category") },
                        modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description & Recommendation") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        minLines = 3,
                        maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Urgency Level: ${urgency.toInt()}/10", fontWeight = FontWeight.Medium)
                Slider(
                        value = urgency,
                        onValueChange = { urgency = it },
                        valueRange = 1f..10f,
                        steps = 8
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                        onClick = {
                                viewModel.submitComplaint(category, description, urgency.toInt())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SplashButtonBlue),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                ) { Text("Submit Complaint", fontSize = 16.sp) }
        }
}
