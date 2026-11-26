package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.EvaluationDimension
import com.example.data.Student
import com.example.data.defaultEvaluationDimensions
import com.example.ui.components.StudentAvatar
import com.example.ui.components.avatarDrawableForIndex
import com.example.ui.components.parseHexColor
import java.util.UUID
import com.example.ui.theme.CardSurface
import com.example.ui.theme.OutlineLabel
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PastelBlueBg
import com.example.ui.theme.PastelBlueChip
import com.example.ui.theme.PastelMintBg
import com.example.ui.theme.PastelMintBorder
import com.example.ui.theme.PastelMintChip
import com.example.ui.theme.PastelMintText
import com.example.ui.theme.PastelPeachBg
import com.example.ui.theme.PastelPeachBorder
import com.example.ui.theme.PastelPeachChip
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimarySky
import com.example.ui.theme.SecondaryCoral
import com.example.ui.theme.SecondaryTerracotta
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceHigh
import com.example.ui.theme.SurfaceLow
import com.example.ui.theme.TertiaryMint
import com.example.ui.theme.TertiaryMintBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistroAlumnoScreen(
    existingStudent: Student?,
    onBack: () -> Unit,
    onSaveStudent: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var avatarTab by remember { mutableIntStateOf(0) } // 0=3D(14), 1=Mis Diseños, 2=Pictos
    var avatarIndex by remember(existingStudent) { mutableIntStateOf(existingStudent?.avatarIndex ?: 1) }
    var customAvatarUri by remember(existingStudent) { mutableStateOf(existingStudent?.customAvatarUri ?: "") }
    var fullName by remember(existingStudent) { mutableStateOf(existingStudent?.fullName ?: "") }
    var preferredName by remember(existingStudent) { mutableStateOf(existingStudent?.preferredName ?: "") }
    var classroom by remember(existingStudent) { mutableStateOf(existingStudent?.classroom?.ifBlank { "1º de Primaria" } ?: "1º de Primaria") }
    var supportLevel by remember(existingStudent) { mutableIntStateOf(existingStudent?.supportLevel ?: 2) }
    var interests by remember(existingStudent) { mutableStateOf(existingStudent?.interests ?: listOf("Dinosaurios", "Puzzles")) }
    var commSystem by remember(existingStudent) { mutableStateOf(existingStudent?.commSystem ?: "PECS / Cuaderno de Pictogramas Digital") }
    var sensoryNotes by remember(existingStudent) { mutableStateOf(existingStudent?.sensoryNotes ?: "") }
    var evalTemplate by remember(existingStudent) { mutableStateOf(existingStudent?.evalTemplate?.ifBlank { "Sensorial" } ?: "Sensorial") }
    var evalDimensions by remember(existingStudent) {
        mutableStateOf(existingStudent?.effectiveEvalDimensions ?: defaultEvaluationDimensions("Sensorial"))
    }
    var evalReinforcement by remember(existingStudent) { mutableStateOf(existingStudent?.evalReinforcement ?: "") }

    var showClassroomMenu by remember { mutableStateOf(false) }
    var showCommMenu by remember { mutableStateOf(false) }
    var showAddInterestDialog by remember { mutableStateOf(false) }
    var showAddEvalDimDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            customAvatarUri = uri.toString()
            avatarTab = 1
        }
    }

    val classrooms = listOf(
        "Infantil",
        "1º de Primaria",
        "2º de Primaria",
        "3º de Primaria",
        "4º de Primaria",
        "5º de Primaria",
        "6º de Primaria",
        "1º E.S.O.",
        "2º E.S.O.",
        "3º E.S.O.",
        "4º E.S.O."
    )
    val commOptions = listOf(
        "PECS / Cuaderno de Pictogramas Digital",
        "Lenguaje Verbal con apoyo de pictogramas",
        "Comunicación Bimodal / Signos",
        "Dispositivo SAAC de alta tecnología",
        "Gestual natural y corporal"
    )
    val suggestedInterests = listOf("Dinosaurios", "Puzzles", "Música", "Astronomía", "Trenes", "Acuarela")

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Contextual Back Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onBack,
                    shape = RoundedCornerShape(999.dp),
                    color = SurfaceHigh,
                    modifier = Modifier.testTag("registro_back_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        Text("Volver a Clase", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(PastelMintChip)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TertiaryMint))
                    Text("Ficha Inclusiva", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PastelMintText)
                }
            }
        }

        // Title
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (existingStudent == null) "Nuevo Alumno" else "Editar Ficha",
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Configura su entorno visual, intereses y nivel de soporte para una experiencia acogedora.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Avatar Selector Card (14 Official Avatars)
        item {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = CardSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(28.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Avatar del Alumno", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            Text("Avatares Oficiales R-TEA (14 disponibles)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(PastelPeachChip)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Seleccionado", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SecondaryTerracotta)
                        }
                    }

                    Box(contentAlignment = Alignment.BottomEnd) {
                        StudentAvatar(
                            avatarIndex = avatarIndex,
                            customUri = customAvatarUri,
                            contentDescription = "Avatar seleccionado $avatarIndex",
                            size = 112.dp,
                            haloBrush = Brush.linearGradient(listOf(SecondaryCoral, PrimarySky, TertiaryMintBright))
                        )
                        Surface(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = CircleShape,
                            color = SecondaryTerracotta,
                            shadowElevation = 4.dp,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.PhotoCamera, contentDescription = "Subir foto propia", tint = CardSurface, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (customAvatarUri.isNotBlank()) "Foto personalizada" else "Avatar $avatarIndex · Estilo 3D Amigable",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Toca cualquier avatar para seleccionarlo para el alumno",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    // 3 Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(999.dp))
                            .background(SurfaceContainer)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Colección 3D (14)", "Mis Diseños", "Pictogramas").forEachIndexed { idx, label ->
                            val selected = avatarTab == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (selected) CardSurface else androidx.compose.ui.graphics.Color.Transparent)
                                    .clickable {
                                        avatarTab = idx
                                        if (idx == 1 && customAvatarUri.isBlank()) {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                                    color = if (selected) SecondaryTerracotta else TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Horizontal Carousel of all 14 official 3D avatars (lazy + async downsampled)
                    val avatarContext = LocalContext.current
                    val avatarPx = with(LocalDensity.current) { 64.dp.roundToPx().coerceIn(96, 256) }
                    val avatarIndices = remember { (1..14).toList() }
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(avatarIndices, key = { it }) { idx ->
                            val selected = customAvatarUri.isBlank() && avatarIndex == idx
                            val thumbRequest = remember(idx, avatarPx) {
                                ImageRequest.Builder(avatarContext)
                                    .data(avatarDrawableForIndex(idx))
                                    .size(avatarPx, avatarPx)
                                    .crossfade(true)
                                    .build()
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        customAvatarUri = ""
                                        avatarIndex = idx
                                        avatarTab = 0
                                    }
                                    .padding(4.dp)
                                    .testTag("avatar_option_$idx")
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) SecondaryTerracotta else SurfaceHigh)
                                        .padding(if (selected) 3.dp else 1.5.dp)
                                ) {
                                    AsyncImage(
                                        model = thumbRequest,
                                        contentDescription = "Avatar $idx",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Avatar $idx",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (selected) SecondaryTerracotta else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Datos Personales
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = CardSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(26.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier.size(34.dp).clip(CircleShape).background(PastelPeachChip),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Badge, contentDescription = null, tint = SecondaryTerracotta, modifier = Modifier.size(18.dp))
                        }
                        Text("Datos Personales", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nombre completo del alumno *") },
                        placeholder = { Text("Ej. Mateo García López") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardSurface,
                            unfocusedContainerColor = SurfaceLow
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("student_fullname_input")
                    )

                    OutlinedTextField(
                        value = preferredName,
                        onValueChange = { preferredName = it },
                        label = { Text("Nombre preferido / Apodo cariñoso") },
                        placeholder = { Text("Ej. Mate / Teo") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardSurface,
                            unfocusedContainerColor = SurfaceLow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box {
                        OutlinedTextField(
                            value = classroom,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Aula o Grupo de Referencia") },
                            trailingIcon = { Icon(Icons.Filled.ExpandMore, contentDescription = null) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = SurfaceLow),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClassroomMenu = true }
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { showClassroomMenu = true })
                        DropdownMenu(expanded = showClassroomMenu, onDismissRequest = { showClassroomMenu = false }) {
                            classrooms.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = { classroom = option; showClassroomMenu = false }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Nivel de Apoyo TEA
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = CardSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(26.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(34.dp).clip(CircleShape).background(PastelBlueChip),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.AllInclusive, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            }
                            Text("Nivel de Apoyo TEA", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        }
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(SurfaceContainer).padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Guía DSM-5", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }

                    Text(
                        "Define la densidad visual de las agendas y el acompañamiento en transiciones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    TeaLevelOption(
                        level = 1,
                        selected = supportLevel == 1,
                        title = "Nivel de apoyo 1",
                        desc = "Necesita ayuda puntual en organización y cambios de tarea.",
                        badge = "Independiente",
                        badgeBg = PastelMintChip,
                        badgeTint = PastelMintText,
                        onClick = { supportLevel = 1 }
                    )
                    TeaLevelOption(
                        level = 2,
                        selected = supportLevel == 2,
                        title = "Nivel de apoyo 2",
                        desc = "Requiere anticipación estructurada y pictogramas claros.",
                        badge = "Frecuente",
                        badgeBg = CardSurface,
                        badgeTint = SecondaryTerracotta,
                        onClick = { supportLevel = 2 }
                    )
                    TeaLevelOption(
                        level = 3,
                        selected = supportLevel == 3,
                        title = "Nivel de apoyo 3",
                        desc = "Acompañamiento 1 a 1 constante y comunicación aumentativa.",
                        badge = "Continuo",
                        badgeBg = PastelBlueChip,
                        badgeTint = PrimaryBlue,
                        onClick = { supportLevel = 3 }
                    )
                }
            }
        }

        // Intereses y Motivadores
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = CardSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(26.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier.size(34.dp).clip(CircleShape).background(PastelMintChip),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(18.dp))
                            }
                            Text("Intereses y Motivadores", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        }
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(PastelMintBg).padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Claves de Éxito", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PastelMintText)
                        }
                    }

                    Text("Se usarán como reforzadores positivos en sus rutinas y recompensas visuales.", style = MaterialTheme.typography.bodySmall, color = TextMuted)

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val allOptions = (suggestedInterests + interests).distinct()
                        allOptions.forEach { tag ->
                            val active = tag in interests
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (active) SecondaryTerracotta else SurfaceContainer)
                                    .clickable {
                                        interests = if (active) interests - tag else interests + tag
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(tag, style = MaterialTheme.typography.labelMedium, color = if (active) CardSurface else TextMuted)
                                Icon(
                                    if (active) Icons.Filled.Close else Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = if (active) CardSurface else PrimaryBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(SurfaceHigh)
                                .border(1.dp, PastelPeachBorder, RoundedCornerShape(999.dp))
                                .clickable { showAddInterestDialog = true }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = SecondaryTerracotta, modifier = Modifier.size(15.dp))
                            Text("Añadir interés", style = MaterialTheme.typography.labelMedium, color = SecondaryTerracotta)
                        }
                    }
                }
            }
        }

        // Comunicación y Sensorial
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = CardSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(26.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier.size(34.dp).clip(CircleShape).background(PastelBlueChip),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        }
                        Text("Comunicación y Sensorial", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }

                    Box {
                        OutlinedTextField(
                            value = commSystem,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sistema de comunicación preferido") },
                            trailingIcon = { Icon(Icons.Filled.ExpandMore, contentDescription = null) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = SurfaceLow),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { showCommMenu = true })
                        DropdownMenu(expanded = showCommMenu, onDismissRequest = { showCommMenu = false }) {
                            commOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = { commSystem = opt; showCommMenu = false }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = sensoryNotes,
                        onValueChange = { sensoryNotes = it },
                        label = { Text("Claves de regulación sensorial") },
                        placeholder = { Text("Ej. Sensibilidad a timbres fuertes (usa auriculares de cancelación). Prefiere descanso táctil con plastilina.") },
                        minLines = 3,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = SurfaceLow),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(PastelMintBg)
                            .border(1.dp, PastelMintBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Filled.SentimentSatisfied, contentDescription = null, tint = TertiaryMint)
                        Column {
                            Text("Acompañamiento Sin Juicios", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PastelMintText)
                            Text(
                                "Este perfil es flexible. Podrás adaptar los pasos, descansos y pictogramas en cualquier momento desde el aula.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Personalized Evaluation Setup Card (appears on every created student)
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = CardSurface,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(26.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PastelPeachChip),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = SecondaryTerracotta, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Evaluación", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                Text("Aparecerá lista en la ficha del alumno", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(PastelBlueChip)
                                .padding(horizontal = 9.dp, vertical = 3.dp)
                        ) {
                            Text("Adaptable", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                        }
                    }

                    Text("Plantilla inicial de dimensiones:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Sensorial", "Social / Juego", "Académico", "Conductual").forEach { tpl ->
                            val selected = evalTemplate == tpl
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (selected) PrimaryBlue else SurfaceContainer)
                                    .clickable {
                                        evalTemplate = tpl
                                        evalDimensions = defaultEvaluationDimensions(tpl)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = tpl,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (selected) CardSurface else TextPrimary
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        evalDimensions.forEach { dim ->
                            val dot = parseHexColor(dim.colorHex)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceLow)
                                    .border(1.dp, OutlineSubtle, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(dot))
                                    Column {
                                        Text(dim.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                        Text(dim.subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                }
                                IconButton(
                                    onClick = { evalDimensions = evalDimensions.filterNot { it.id == dim.id } },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = "Quitar objetivo", tint = OutlineLabel, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { showAddEvalDimDialog = true },
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Añadir Dimensión / Objetivo", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                    }

                    OutlinedTextField(
                        value = evalReinforcement,
                        onValueChange = { evalReinforcement = it },
                        label = { Text("Mensaje de refuerzo positivo (opcional)") },
                        placeholder = { Text("Ej. ¡Logró la transición de aula sin angustia y con gran calma!") },
                        minLines = 2,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = SurfaceLow),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val cleanName = fullName.trim()
                        if (cleanName.isNotEmpty()) {
                            onSaveStudent(
                                (existingStudent ?: Student()).copy(
                                    fullName = cleanName,
                                    preferredName = preferredName.trim().ifBlank { cleanName.split(" ").first() },
                                    classroom = classroom,
                                    avatarIndex = avatarIndex,
                                    customAvatarUri = customAvatarUri,
                                    supportLevel = supportLevel,
                                    interests = interests,
                                    commSystem = commSystem,
                                    sensoryNotes = sensoryNotes.trim(),
                                    evalTemplate = evalTemplate,
                                    evalDimensions = evalDimensions.ifEmpty { defaultEvaluationDimensions(evalTemplate) },
                                    evalReinforcement = evalReinforcement.trim()
                                )
                            )
                        }
                    },
                    enabled = fullName.isNotBlank(),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryTerracotta, contentColor = CardSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("save_student_button")
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (existingStudent == null) "Guardar y Crear Perfil" else "Guardar Cambios",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Cancelar y Descartar", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    if (showAddInterestDialog) {
        var customTag by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddInterestDialog = false },
            containerColor = CardSurface,
            title = { Text("Añadir interés motivador", style = MaterialTheme.typography.titleMedium) },
            text = {
                OutlinedTextField(
                    value = customTag,
                    onValueChange = { customTag = it },
                    label = { Text("Ej. Lego, Animales marinos, Pintura") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTag.isNotBlank()) {
                            interests = (interests + customTag.trim()).distinct()
                        }
                        showAddInterestDialog = false
                    }
                ) { Text("Añadir") }
            },
            dismissButton = {
                TextButton(onClick = { showAddInterestDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showAddEvalDimDialog) {
        var dimTitle by remember { mutableStateOf("") }
        var dimSub by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddEvalDimDialog = false },
            containerColor = CardSurface,
            title = { Text("Nueva Dimensión de Evaluación", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = dimTitle,
                        onValueChange = { dimTitle = it },
                        label = { Text("Objetivo (ej. Tolerancia auditiva)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = dimSub,
                        onValueChange = { dimSub = it },
                        label = { Text("Criterio observable") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dimTitle.isNotBlank()) {
                            evalDimensions = evalDimensions + EvaluationDimension(
                                id = "dim_${UUID.randomUUID().toString().take(6)}",
                                title = dimTitle.trim(),
                                subtitle = dimSub.trim().ifBlank { "Seguimiento en aula" },
                                score = 4,
                                colorHex = "#3F82B8"
                            )
                            showAddEvalDimDialog = false
                        }
                    }
                ) { Text("Añadir") }
            },
            dismissButton = {
                TextButton(onClick = { showAddEvalDimDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun TeaLevelOption(
    level: Int,
    selected: Boolean,
    title: String,
    desc: String,
    badge: String,
    badgeBg: androidx.compose.ui.graphics.Color,
    badgeTint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) PastelPeachBg else SurfaceLow)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) SecondaryTerracotta.copy(alpha = 0.65f) else OutlineSubtle,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag("tea_level_$level"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (selected) SecondaryTerracotta else CardSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = if (selected) CardSurface else OutlineLabel,
                    modifier = Modifier.size(15.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold),
                    color = if (selected) SecondaryTerracotta else TextPrimary
                )
                Text(desc, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(badgeBg)
                .padding(horizontal = 9.dp, vertical = 4.dp)
        ) {
            Text(badge, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = badgeTint)
        }
    }
}
