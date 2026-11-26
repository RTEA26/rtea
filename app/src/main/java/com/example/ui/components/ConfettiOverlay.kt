package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PastelLavenderBright
import com.example.ui.theme.PrimarySky
import com.example.ui.theme.SecondaryCoral
import com.example.ui.theme.TertiaryMintBright
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiParticle(
    val xFraction: Float,
    val speed: Float,
    val sizeDp: Float,
    val phase: Float,
    val color: Color,
    val isCircle: Boolean
)

@Composable
fun ConfettiOverlay(
    visible: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val palette = remember {
        listOf(PrimarySky, SecondaryCoral, TertiaryMintBright, GoldAccent, PastelLavenderBright)
    }
    val particles = remember {
        val rng = Random(42)
        List(44) {
            ConfettiParticle(
                xFraction = rng.nextFloat(),
                speed = 0.7f + rng.nextFloat() * 0.8f,
                sizeDp = 5f + rng.nextFloat() * 5f,
                phase = rng.nextFloat() * 6.28f,
                color = palette[it % palette.size],
                isCircle = it % 3 == 0
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        progress.snapTo(0f)
        progress.animateTo(1f, animationSpec = tween(durationMillis = 2200, easing = LinearEasing))
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val t = progress.value
        val alpha = (1f - (t * 0.9f)).coerceIn(0f, 1f)
        particles.forEach { p ->
            val y = -30f + (size.height + 60f) * ((t * p.speed).coerceAtMost(1.2f))
            val sway = sin((t * 10f) + p.phase) * 18f
            val x = (p.xFraction * size.width) + sway
            val pxSize = p.sizeDp * density
            if (p.isCircle) {
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = pxSize * 0.55f,
                    center = Offset(x, y)
                )
            } else {
                rotate(degrees = t * 360f * p.speed, pivot = Offset(x, y)) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(x - pxSize / 2f, y - pxSize / 3f),
                        size = Size(pxSize, pxSize * 0.65f)
                    )
                }
            }
        }
    }
}
