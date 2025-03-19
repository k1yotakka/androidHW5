package com.example.hw4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.airbnb.lottie.compose.*
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.time.format.TextStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "main") {
                    composable("main") { MainScreen(navController) }
                    composable("second") { SecondScreen() }
                }
            }
        }
        lifecycleScope.launch {
            MyWidget().updateAll(this@MainActivity)
        }
    }
}

// Главный экран
@Composable
fun MainScreen(navController: NavHostController) {
    var isTextVisible by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        isTextVisible = true
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Анимированная иконка
        Image(
            imageVector = ImageVector.vectorResource(id = R.drawable.animated_icon),
            contentDescription = "Animated Icon",
            modifier = Modifier.size(100.dp)
        )

        // Анимация появления текста
        AnimatedVisibility(visible = isTextVisible) {
            Text(text = "Добро пожаловать!", style = MaterialTheme.typography.headlineMedium)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lottie-анимация
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading))
        LottieAnimation(composition, iterations = LottieConstants.IterateForever)

        Spacer(modifier = Modifier.height(20.dp))

        // Анимированная кнопка
        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    scale.animateTo(1.1f, animationSpec = tween(200))
                    scale.animateTo(1f)
                }
                navController.navigate("second")
            },
            modifier = Modifier.scale(scale.value),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text("Перейти", color = Color.White)
        }
    }
}

// Второй экран
@Composable
fun SecondScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Второй экран", style = MaterialTheme.typography.headlineLarge)
    }
}

// Glance Widget
class MyWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = androidx.glance.layout.Alignment.CenterVertically,
                horizontalAlignment = androidx.glance.layout.Alignment.CenterHorizontally
            ) {
                Text(text = "Меня зовут Арман!",
                    style = androidx.glance.text.TextStyle(color = ColorProvider(Color.White))
                )
                Spacer(modifier = GlanceModifier.height(16.dp))
                Box(
                    modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>())
                ) {
                    Text(text = "Открыть приложение",
                        style = androidx.glance.text.TextStyle(color = ColorProvider(Color.White))
                    )

                }
            }
        }
    }
}

class MyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MyWidget()
}