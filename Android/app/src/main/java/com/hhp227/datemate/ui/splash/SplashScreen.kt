package com.hhp227.datemate.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hhp227.datemate.R
import com.kortek.myapplication.ui.theme.PINK200
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(lightTheme: Boolean = MaterialTheme.colors.isLight) {
    var startAnimation by remember { mutableStateOf(false) }
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f, // 0.8배에서 1배로 커짐
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "scaleAnim"
    )
    val gradientTop = Color.White
    val gradientBottom = PINK200
    val assetId = if (lightTheme) {
        R.drawable.ic_logo_light_eng
    } else {
        R.drawable.ic_logo_dark_eng
    }

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1500)
        //onSplashFinished()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientTop, gradientBottom),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = assetId),
            contentDescription = "DateMate Logo",
            modifier = Modifier
                .width(280.dp)
                .scale(scaleAnim.value)
        )
    }
}