package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FolderCopy
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.QuickNote
import com.example.ui.theme.CanvasIvory
import com.example.ui.theme.CardSurface
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PastelBlueBg
import com.example.ui.theme.PastelBlueBorder
import com.example.ui.theme.PastelBlueChip
import com.example.ui.theme.PastelButterBg
import com.example.ui.theme.PastelButterBorder
import com.example.ui.theme.PastelButterChip
import com.example.ui.theme.PastelMintBg
import com.example.ui.theme.PastelMintBorder
import com.example.ui.theme.PastelMintChip
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryCoralVibrant
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceLow
import com.example.ui.theme.TertiaryMint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.google.firebase.auth.FirebaseUser

enum class RteaTab(
    val route: String,
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val testTag: String
) {
    INICIO("inicio", "Inicio", Icons.Filled.Home, Icons.Outlined.Home, "nav_inicio"),
    CLASE("clase", "Clase", Icons.Filled.Group, Icons.Outlined.Group, "nav_clase"),
    DIARIO("diario", "Diario", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, "nav_diario"),
    RECURSOS("recursos", "Recursos", Icons.Filled.FolderCopy, Icons.Outlined.FolderCopy, "nav_recursos")
}

@Composable
fun RteaTopBar(
    screenTitle: String,
    user: FirebaseUser?,
    pendingNotificationsCount: Int,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardSurface.copy(alpha = 0.94f),
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PastelButterBg)
                            .border(1.dp, PastelButterBorder, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.rtea_logo),
                            contentDescription = stringResource(R.string.cd_rtea_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Column {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = SecondaryCoralVibrant, fontWeight = FontWeight.ExtraBold)) {
                                    append("R-")
                                }
                                withStyle(SpanStyle(color = Color(0xFF2B8CE0), fontWeight = FontWeight.ExtraBold)) {
                                    append("TEA")
                                }
                            },
                            fontSize = 12.sp,
                            lineHeight = 14.sp
                        )
                        Text(
                            text = screenTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("top_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (pendingNotificationsCount > 0) {
                                    Badge(containerColor = SecondaryCoralVibrant) {
                                        Text(
                                            text = pendingNotificationsCount.coerceAtMost(9).toString(),
                                            color = CardSurface,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = stringResource(R.string.cd_notifications),
                                tint = TextPrimary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onProfileClick)
                            .testTag("top_profile_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(PastelButterChip)
                                .padding(2.dp)
                        ) {
                            val photo = user?.photoUrl
                            if (photo != null) {
                                AsyncImage(
                                    model = photo,
                                    contentDescription = stringResource(R.string.cd_user_profile),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                val initial = (user?.displayName?.firstOrNull() ?: 'C').uppercaseChar().toString()
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PastelBlueChip),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initial,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PrimaryBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 1.dp, color = OutlineSubtle)
        }
    }
}

@Composable
fun RteaBottomBar(
    activeTab: RteaTab,
    onSelectTab: (RteaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = CardSurface.copy(alpha = 0.96f),
        shadowElevation = 6.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            HorizontalDivider(thickness = 1.dp, color = OutlineSubtle)
            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RteaTab.entries.forEach { tab ->
                    val selected = tab == activeTab
                    val tint = if (selected) PrimaryBlue else TextMuted
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onSelectTab(tab) }
                            .testTag(tab.testTag),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (selected) PastelBlueBg else Color.Transparent)
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selected) tab.filledIcon else tab.outlinedIcon,
                                contentDescription = tab.label,
                                tint = tint,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                            ),
                            color = tint,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RteaNavigationRail(
    activeTab: RteaTab,
    onSelectTab: (RteaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        containerColor = CanvasIvory,
        modifier = modifier.padding(top = 12.dp)
    ) {
        RteaTab.entries.forEach { tab ->
            val selected = tab == activeTab
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.filledIcon else tab.outlinedIcon,
                        contentDescription = tab.label
                    )
                },
                label = { Text(tab.label) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = PastelBlueChip,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("rail_${tab.route}")
            )
        }
    }
}

@Composable
fun NotificationsDialog(
    notes: List<QuickNote>,
    onDismiss: () -> Unit,
    onMarkDone: (QuickNote) -> Unit
) {
    val pending = notes.filter { !it.completed }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Notifications, contentDescription = null, tint = PrimaryBlue)
                Text("Avisos y Anticipación", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (pending.isEmpty()) {
                    Text(
                        "Todo al día. No hay avisos urgentes pendientes en este momento.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                } else {
                    pending.take(4).forEach { note ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceLow)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${note.tagLabel} · ${note.timeLabel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = note.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(onClick = { onMarkDone(note) }) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Completar", tint = TertiaryMint)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ProfileAccountDialog(
    user: FirebaseUser?,
    studentCount: Int,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (user?.photoUrl != null) {
                    AsyncImage(
                        model = user.photoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PastelBlueChip),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.displayName?.firstOrNull() ?: 'P').toString(),
                            style = MaterialTheme.typography.titleMedium,
                            color = PrimaryBlue
                        )
                    }
                }
                Column {
                    Text(
                        text = user?.displayName ?: "Profe R-TEA",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = user?.email ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PastelMintBg)
                        .border(1.dp, PastelMintBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = TertiaryMint)
                    Column {
                        Text("Sincronización en Tiempo Real Activa", style = MaterialTheme.typography.labelMedium, color = TertiaryMint)
                        Text(
                            "$studentCount expedientes conectados en la nube de R-TEA.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    onDismiss()
                    onSignOut()
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Cerrar sesión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continuar")
            }
        }
    )
}

@Composable
fun AiReportViewerDialog(
    studentName: String,
    reportText: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(PastelMintChip),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text("Informe Final IA", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Text(studentName, style = MaterialTheme.typography.bodySmall, color = TertiaryMint)
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                }
            }
        },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer)
                    .border(1.dp, PastelBlueBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = reportText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TertiaryMint)
            ) {
                Text("Entendido")
            }
        }
    )
}
