package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProgressArea
import com.example.ui.theme.OutlineLabel
import com.example.ui.theme.SurfaceHigh
import com.example.ui.theme.TextPrimary

fun parseHexColor(hex: String, fallback: Color = Color(0xFF4BA2E8)): Color {
    return try {
        val cleaned = hex.removePrefix("#")
        val value = cleaned.toLong(16)
        when (cleaned.length) {
            6 -> Color(0xFF000000L or value)
            8 -> Color(value)
            else -> fallback
        }
    } catch (_: Exception) {
        fallback
    }
}

@Composable
fun ConcentricProgressRings(
    areas: List<ProgressArea>,
    globalPercentage: Int,
    size: Dp = 114.dp,
    modifier: Modifier = Modifier
) {
    val displayedAreas = areas.take(5)
    val animatedFractions = displayedAreas.map { area ->
        val target = if (area.totalSteps > 0) (area.completedSteps.toFloat() / area.totalSteps).coerceIn(0f, 1f) else 0f
        val anim by animateFloatAsState(
            targetValue = target,
            animationSpec = tween(durationMillis = 550),
            label = "ring_${area.id}"
        )
        anim
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minDim = this.size.minDimension
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val strokeWidth = (minDim * 0.068f).coerceIn(5.dp.toPx(), 9.dp.toPx())
            val gap = strokeWidth * 1.45f
            val outerRadius = (minDim / 2f) - strokeWidth

            displayedAreas.forEachIndexed { index, area ->
                val radius = outerRadius - (index * gap)
                if (radius > strokeWidth) {
                    val topLeft = Offset(center.x - radius, center.y - radius)
                    val arcSize = Size(radius * 2f, radius * 2f)
                    val color = parseHexColor(area.colorHex)

                    drawArc(
                        color = SurfaceHigh,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedFractions[index],
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$globalPercentage%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                ),
                color = TextPrimary
            )
            Text(
                text = "LOGRO",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    letterSpacing = 0.8.sp
                ),
                color = OutlineLabel
            )
        }
    }
}
