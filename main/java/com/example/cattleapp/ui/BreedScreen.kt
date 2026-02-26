package com.example.cattleapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.jeziellago.compose.markdowntext.MarkdownText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.decode.GifDecoder

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BreedScreen(onBack: () -> Unit, vm: CattleViewModel) {
    var breed by remember { mutableStateOf("") }
    val answer by vm.answer.collectAsState()

    val pageScroll = rememberScrollState()
    val answerScroll = rememberScrollState()

    val gradient = Brush.verticalGradient(
        listOf(
            Color(0xFFB2DFDB),
            Color(0xFFA5D6A7),
            Color(0xFF81C784)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .verticalScroll(pageScroll)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔙 Back button (centered)
        TextButton(onClick = onBack) {
            Text("← Back", color = Color(0xFF1B5E20))
        }

        // 🐄 Title + Subtitle (center aligned)
        Text(
            text = "Cattle Advisor 🐄",
            color = Color(0xFF1B5E20),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Ask about any cattle breed to get instant insights",
            color = Color(0xFF33691E),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))
        val imageLoader = ImageLoader.Builder(LocalContext.current)
            .components {
                add(GifDecoder.Factory())
            }
            .build()
        // 🐮 Cute GIF instead of Lottie
        AsyncImage(
            model = "https://i.pinimg.com/originals/b7/e3/ca/b7e3ca50f1e31f8e6939f10d04e5c191.gif",
            contentDescription = "Cute gif",
            imageLoader = imageLoader,
            modifier = Modifier
                .height(150.dp)
                .width(150.dp)
        )

        Spacer(Modifier.height(16.dp))

        // 🔍 Input Card
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("🔍 Enter breed name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (breed.isNotBlank()) vm.askByBreed(breed)
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        focusedLabelColor = Color(0xFF2E7D32)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = { vm.askByBreed(breed) },
                    enabled = breed.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ask Advisor")
                }

                Spacer(Modifier.height(12.dp))

                // 🧩 Breed suggestion chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Holstein", "Jersey", "Sahiwal", "Gir", "Red Sindhi", "Kankrej").forEach { breedName ->
                        AssistChip(
                            onClick = { breed = breedName },
                            label = { Text(breedName) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFF81C784),
                                labelColor = Color(0xFF1B5E20)
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 💬 Advisor Response Section (fixed but scrollable inside)
        if (answer.isNotBlank()) {
            Text(
                "🐮 Advisor says:",
                color = Color(0xFF1B5E20),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Column(
                    Modifier
                        .padding(16.dp)
                        .verticalScroll(answerScroll)
                ) {
                    MarkdownText(
                        markdown = answer,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
