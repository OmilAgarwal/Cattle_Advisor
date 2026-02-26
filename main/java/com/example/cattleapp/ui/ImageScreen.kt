package com.example.cattleapp.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.GifDecoder

@Composable
fun ImageScreen(onBack: () -> Unit, vm: CattleViewModel) {
    val answer by vm.answer.collectAsState()
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedUri = uri
        if (uri != null) {
            vm.identifyByImage(context.contentResolver, uri)
        }
    }

    // 🌿 Background gradient
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFB6E0A3), Color(0xFFE8F5E9), Color.White)
    )

    // ✨ Button glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔙 Back button
            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
                Text("← Back", color = Color(0xFF1B5E20))
            }

            Spacer(Modifier.height(12.dp))

            // 🐄 Title and subtitle
            Text(
                text = "Cattle Breed Detector 📷",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )
            Text(
                text = "Upload an image to let AI identify your cattle breed",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF33691E)
            )

            Spacer(Modifier.height(24.dp))

            // 🐮 Friendly GIF illustration
            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .components { add(GifDecoder.Factory()) }
                .build()

            AsyncImage(
                model = "https://media3.giphy.com/media/3o7btNhMBytxAM6YBa/giphy.gif",
                contentDescription = "Cow animation",
                imageLoader = imageLoader,
                modifier = Modifier.size(160.dp)
            )

            Spacer(Modifier.height(28.dp))

            // 🧩 Upload card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 📤 Glowing Upload Button
                    Button(
                        onClick = {
                            picker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale
                            )
                            .height(55.dp)
                            .fillMaxWidth(0.8f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("📷 Upload Image", fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Tip: Use a clear front-side image for best results.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(Modifier.height(16.dp))

                    // 🖼️ Selected Image Preview
                    selectedUri?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(2.dp, Color(0xFF81C784), RoundedCornerShape(12.dp))
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 💬 AI Response Area
            if (answer.isNotBlank()) {
                Text(
                    text = "🤖 AI Advisor says:",
                    color = Color(0xFF1B5E20),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    tonalElevation = 6.dp
                ) {
                    Text(
                        text = answer,
                        modifier = Modifier.padding(16.dp),
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
