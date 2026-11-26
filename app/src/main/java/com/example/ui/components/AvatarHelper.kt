package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.ui.theme.CardSurface
import com.example.ui.theme.PrimarySky
import com.example.ui.theme.SecondaryCoral
import com.example.ui.theme.SurfaceLow
import com.example.ui.theme.TertiaryMintBright

@DrawableRes
fun avatarDrawableForIndex(index: Int): Int = when (index.coerceIn(1, 14)) {
    1 -> R.drawable.avatar_1
    2 -> R.drawable.avatar_2
    3 -> R.drawable.avatar_3
    4 -> R.drawable.avatar_4
    5 -> R.drawable.avatar_5
    6 -> R.drawable.avatar_6
    7 -> R.drawable.avatar_7
    8 -> R.drawable.avatar_8
    9 -> R.drawable.avatar_9
    10 -> R.drawable.avatar_10
    11 -> R.drawable.avatar_11
    12 -> R.drawable.avatar_12
    13 -> R.drawable.avatar_13
    else -> R.drawable.avatar_14
}

@Composable
fun StudentAvatar(
    avatarIndex: Int,
    customUri: String = "",
    contentDescription: String,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier,
    haloBrush: Brush = Brush.linearGradient(listOf(SecondaryCoral, PrimarySky, TertiaryMintBright)),
    badgeIcon: ImageVector? = null,
    badgeBgColor: Color = TertiaryMintBright,
    badgeTint: Color = CardSurface
) {
    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(haloBrush)
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(SurfaceLow)
            ) {
                val context = LocalContext.current
                val pxSize = with(LocalDensity.current) { size.roundToPx().coerceIn(96, 320) }
                val requestModel = remember(avatarIndex, customUri, pxSize) {
                    ImageRequest.Builder(context)
                        .data(if (customUri.isNotBlank()) customUri else avatarDrawableForIndex(avatarIndex))
                        .size(pxSize, pxSize)
                        .crossfade(true)
                        .build()
                }
                AsyncImage(
                    model = requestModel,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        if (badgeIcon != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size((size.value * 0.34f).dp.coerceAtLeast(18.dp))
                    .clip(CircleShape)
                    .background(badgeBgColor)
                    .border(1.5.dp, CardSurface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = null,
                    tint = badgeTint,
                    modifier = Modifier.size((size.value * 0.2f).dp.coerceAtLeast(11.dp))
                )
            }
        }
    }
}
