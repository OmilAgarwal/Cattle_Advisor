package com.example.cattleapp
import android.os.Bundle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.decode.GifDecoder
import com.example.cattleapp.ui.BreedScreen
import com.example.cattleapp.ui.CattleViewModel
import com.example.cattleapp.ui.ImageScreen
import com.example.cattleapp.ui.theme.AppTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import com.airbnb.lottie.compose.*



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot()
                }
            }
        }
    }
}

@Composable
fun AppRoot(vm: CattleViewModel = viewModel()) {
    var screen by remember { mutableStateOf("splash") }

    if (screen == "splash") {
        SplashScreen(onDone = { screen = "home" })
        return
    }

    when (screen) {
        "home" -> HomeScreen(
            onBreedClick = { screen = "breed" },
            onImageClick = { screen = "image" }
        )
        "breed" -> BreedScreen(
            onBack = { screen = "home" },
            vm = vm
        )
        "image" -> ImageScreen(
            onBack = { screen = "home" },
            vm = vm
        )
    }
}

@Composable
fun HomeScreen(onBreedClick: () -> Unit, onImageClick: () -> Unit) {
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFFA5D394), Color(0xFFE8F5E9), Color.White)
    )

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components { add(GifDecoder.Factory()) }
        .build()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        AsyncImage(
            model = "https://media1.tenor.com/m/vbu8DLN_NRcAAAAC/cowanimated-cow.gif",
            contentDescription = "Cattle Animation",
            imageLoader = imageLoader,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(Modifier.height(12.dp))

        // Header Text
        Text(
            text = "Welcome to Cattle Advisor 🐮",
            color = Color(0xFF1B5E20),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your intelligent cattle companion",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        // Mode Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ModeCard(
                title = "Breed by Name",
                subtitle = "Know details instantly",
                icon = "https://cdn-icons-png.flaticon.com/512/616/616408.png",
                onClick = onBreedClick
            )

            ModeCard(
                title = "Detect by Image",
                subtitle = "Upload and identify",
                icon = "https://cdn-icons-png.flaticon.com/512/685/685655.png",
                onClick = onImageClick
            )
        }

        Spacer(Modifier.height(20.dp))

        // 🐄 Multiple Facts Section
        val facts = listOf(
            "Did you know? Cows have best friends and become stressed when they are separated!",
            "Cows can recognize over 50 different faces — both bovine and human!",
            "A cow produces around 6-7 gallons of milk every day on average!",
            "Cows have an almost 360° panoramic vision — they can see all around them!",
            "They spend up to 10 hours a day lying down, chewing cud and relaxing.",
            "Cows are naturally curious and enjoy exploring their surroundings."
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            facts.forEachIndexed { index, fact ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF2E7D32).copy(alpha = 0.1f + (index * 0.02f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "🐄 Cattle Fact #${index + 1}",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            fact,
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeCard(title: String, subtitle: String, icon: String, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .height(160.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32),
                style = MaterialTheme.typography.titleMedium
            )
            Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Composable
fun SplashScreen(onDone: () -> Unit) {
    // Trigger navigation after animation duration
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        onDone()
    }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.8f) }

    LaunchedEffect(true) {
        alphaAnim.animateTo(1f, animationSpec = tween(1000))
        scaleAnim.animateTo(1f, animationSpec = tween(1000))
    }

    // Background gradient animation
    val bgTransition = rememberInfiniteTransition(label = "")
    val color1 by bgTransition.animateColor(
        initialValue = Color(0xFFD0F8CE),
        targetValue = Color(0xFFE8F5E9),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color1"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(color1, Color.White)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated cattle GIF logo

            val imageLoader = ImageLoader.Builder(LocalContext.current)
                .components {
                    add(GifDecoder.Factory())
                }
                .build()

            AsyncImage(
                model = "https://media4.giphy.com/media/jSECOkSDmDnaJ6rh09/giphy.gif",
                contentDescription = "Animated cattle logo",
                imageLoader = imageLoader,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scaleAnim.value,
                        scaleY = scaleAnim.value,
                        alpha = alphaAnim.value
                    )
                    .size(180.dp)
            )


            Spacer(Modifier.height(20.dp))

            // Title
            Text(
                text = "CATTLE ADVISOR",
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.graphicsLayer(alpha = alphaAnim.value)
            )

            // Tagline
            Text(
                text = "Your AI-Powered Cattle Companion",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.graphicsLayer(alpha = alphaAnim.value)
            )

            Spacer(Modifier.height(40.dp))

            // Rotating loader
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .graphicsLayer(rotationZ = rotation)
                    .border(3.dp, Color(0xFF2E7D32), shape = CircleShape)
            )
        }
    }
}
