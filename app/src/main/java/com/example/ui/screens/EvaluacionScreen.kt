package com.example.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Evaluation
import com.example.data.EvaluationDimension
import com.example.data.Student
import com.example.data.defaultEvaluationDimensions
import com.example.data.formatTodaySpanishWithShift
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.StudentAvatar
import com.example.ui.components.parseHexColor
import com.example.ui.theme.BronzeAccent
import com.example.ui.theme.CardSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.OutlineLabel
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PastelBlueBg
import com.example.ui.theme.PastelBlueBorder
import com.example.ui.theme.PastelBlueChip
import com.example.ui.theme.PastelBlueText
import com.example.ui.theme.PastelButterBg
import com.example.ui.theme.PastelButterBorder
import com.example.ui.theme.PastelButterChip
import com.example.ui.theme.PastelButterText
import com.example.ui.theme.PastelLavenderBg
import com.example.ui.theme.PastelLavenderChip
import com.example.ui.theme.PastelLavenderText
import com.example.ui.theme.PastelMintBg
import com.example.ui.theme.PastelMintBorder
import com.example.ui.theme.PastelMintChip
import com.example.ui.theme.PastelMintText
import com.example.ui.theme.PastelPeachBg
import com.example.ui.theme.PastelPeachBorder
import com.example.ui.theme.PastelPeachChip
import com.example.ui.theme.PastelPeachText
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryCoralVibrant
import com.example.ui.theme.SecondaryTerracotta
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceLow
import com.example.ui.theme.TertiaryMint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.UUID

@Composable
fun EvaluacionScreen(
    students: List<Student>,
    selectedStudent: Student?,
    evaluations: List<Evaluation>,
    onSelectStudent: (String) -> Unit,
    onBack: () -> Unit,
    onSaveEvaluationConfig: (
        student: Student,
        medalType: String,
        overallScore: Int,
        dimensions: List<EvaluationDimension>,
        templateCategory: String,
        reinforcementText: String,
        notes: String,
        shareWithFamily: Boolean,
        recordInHistory: Boolean,
        showToast: Boolean
    ) -> Unit,
    onSubmitEvaluation: (
        student: Student,
        medalType: String,
        overallScore: Int,
        dimensions: List<EvaluationDimension>,
        templateCategory: String,
        reinforcementText: String,
        notes: String,
        shareWithFamily: Boolean,
        recordInHistory: Boolean,
        isDraft: Boolean
    ) -> Unit,
    onEmitToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val student = selectedStudent ?: students.firstOrNull() ?: return
    val shortName = student.shortDisplayName

    var activeTab by remember { mutableIntStateOf(0) } // 0=Sesión Actual, 1=Semanal, 2=Historial
    var selectedMedal by remember(student.id) { mutableStateOf(student.evalMedal.ifBlank { "GOLD" }) }
    var overallScore by remember(student.id) { mutableIntStateOf(student.evalScore.coerceIn(1, 5)) }
    var recordInHistory by remember(student.id) { mutableStateOf(student.evalRecordHistory) }
    var selectedTemplate by remember(student.id) { mutableStateOf(student.evalTemplate.ifBlank { "Sensorial" }) }
    var dimensions by remember(student.id) { mutableStateOf(student.effectiveEvalDimensions) }
    var reinforcementText by remember(student.id) { mutableStateOf(student.effectiveReinforcement) }
    var notes by remember(student.id) {
        mutableStateOf(
            student.evalNotes.ifBlank {
                "Usó plastilina sensorial a los 20 min y retomó la actividad con gran sonrisa y motivación."
            }
        )
    }
    var shareWithFamily by remember(student.id) { mutableStateOf(student.evalShareFamily) }

    var showStudentPicker by remember { mutableStateOf(false) }
    var showAddDimensionDialog by remember { mutableStateOf(false) }
    var editingDimension by remember { mutableStateOf<EvaluationDimension?>(null) }
    var showEditReinforcementDialog by remember { mutableStateOf(false) }
    var showCustomTemplateDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var showCelebrationBanner by remember { mutableStateOf(false) }

    LaunchedEffect(showCelebrationBanner) {
        if (showCelebrationBanner) {
            delay(2800)
            showCelebrationBanner = false
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                notes = if (notes.isBlank()) spoken else "${notes.trim()} $spoken"
            }
        }
    }

    fun persistConfig(
        nextMedal: String = selectedMedal,
        nextScore: Int = overallScore,
        nextDims: List<EvaluationDimension> = dimensions,
        nextTpl: String = selectedTemplate,
        nextReinf: String = reinforcementText,
        nextNotes: String = notes,
        nextShare: Boolean = shareWithFamily,
        nextRecord: Boolean = recordInHistory,
        showToast: Boolean = false
    ) {
        onSaveEvaluationConfig(
            student,
            nextMedal,
            nextScore,
            nextDims,
            nextTpl,
            nextReinf,
            nextNotes,
            nextShare,
            nextRecord,
            showToast
        )
    }

    val studentEvaluations = remember(evaluations, student.id) {
        evaluations.filter { it.studentId == student.id }
    }

    val classroomBadge = student.classroom.substringBefore(" ·").ifBlank { "Aula 3-B" }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Student Header Card + Quick Student Pills + Segmented Tabs
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                StudentAvatar(
                                    avatarIndex = student.avatarIndex,
                                    customUri = student.customAvatarUri,
                                    contentDescription = student.fullName,
                                    size = 56.dp,
                                    badgeIcon = Icons.Filled.Star,
                                    badgeBgColor = TertiaryMint
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = student.fullName,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(999.dp))
                                                .background(PastelBlueChip)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = classroomBadge,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = PastelBlueText
                                            )
                                        }
                                    }
                                    val evalLifecycleOwner = LocalLifecycleOwner.current
                                    var todayShiftLabel by remember { mutableStateOf(formatTodaySpanishWithShift()) }
                                    DisposableEffect(evalLifecycleOwner) {
                                        val observer = LifecycleEventObserver { _, event ->
                                            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
                                                todayShiftLabel = formatTodaySpanishWithShift()
                                            }
                                        }
                                        evalLifecycleOwner.lifecycle.addObserver(observer)
                                        onDispose { evalLifecycleOwner.lifecycle.removeObserver(observer) }
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.CalendarToday,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = todayShiftLabel,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { showStudentPicker = true },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceLow)
                                    .border(1.dp, OutlineSubtle, CircleShape)
                                    .testTag("eval_switch_student_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PersonSearch,
                                    contentDescription = "Cambiar alumno",
                                    tint = PrimaryBlue
                                )
                            }
                        }

                        // Quick switcher across every created student
                        if (students.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                students.forEach { st ->
                                    val isSel = st.id == student.id
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(if (isSel) PastelBlueBg else SurfaceLow)
                                            .border(1.dp, if (isSel) PrimaryBlue else OutlineSubtle, RoundedCornerShape(999.dp))
                                            .clickable { onSelectStudent(st.id) }
                                            .padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        StudentAvatar(st.avatarIndex, st.customAvatarUri, st.fullName, size = 22.dp)
                                        Text(
                                            text = st.shortDisplayName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                            ),
                                            color = if (isSel) PrimaryBlue else TextPrimary
                                        )
                                    }
                                }
                            }
                        }

                        // Segmented 3-Tab Switcher
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(999.dp))
                                .background(SurfaceContainer)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Sesión Actual", "Semanal", "Historial").forEachIndexed { idx, label ->
                                val selected = activeTab == idx
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(if (selected) CardSurface else Color.Transparent)
                                        .clickable { activeTab = idx }
                                        .testTag("eval_tab_$idx"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                                        ),
                                        color = if (selected) PastelBlueText else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (activeTab == 0) {
                // Card 1: Logro y Reconocimiento (3 Medals + Editable Reinforcement)
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
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(PastelPeachChip),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.MilitaryTech,
                                            contentDescription = null,
                                            tint = SecondaryTerracotta,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            "Logro y Reconocimiento",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            "Celebra el esfuerzo y la autorregulación de hoy",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(PastelBlueBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MedalOptionCard(
                                    title = "Buen Esfuerzo",
                                    subtitle = "Iniciativa",
                                    icon = Icons.Filled.EmojiEvents,
                                    bg = PastelPeachBg,
                                    chipBg = PastelPeachChip,
                                    accent = SecondaryTerracotta,
                                    selected = selectedMedal == "BRONZE",
                                    onClick = {
                                        selectedMedal = "BRONZE"
                                        persistConfig(nextMedal = "BRONZE")
                                    },
                                    modifier = Modifier.weight(1f).testTag("medal_bronze")
                                )
                                MedalOptionCard(
                                    title = "Gran Progreso",
                                    subtitle = "Autonomía",
                                    icon = Icons.Filled.WorkspacePremium,
                                    bg = PastelBlueBg,
                                    chipBg = PastelBlueChip,
                                    accent = PrimaryBlue,
                                    selected = selectedMedal == "SILVER",
                                    onClick = {
                                        selectedMedal = "SILVER"
                                        persistConfig(nextMedal = "SILVER")
                                    },
                                    modifier = Modifier.weight(1f).testTag("medal_silver")
                                )
                                MedalOptionCard(
                                    title = "¡Misión Hecha!",
                                    subtitle = "Calma total",
                                    icon = Icons.Filled.Star,
                                    bg = PastelButterBg,
                                    chipBg = PastelButterChip,
                                    accent = PastelButterText,
                                    selected = selectedMedal == "GOLD",
                                    onClick = {
                                        selectedMedal = "GOLD"
                                        persistConfig(nextMedal = "GOLD")
                                    },
                                    modifier = Modifier.weight(1f).testTag("medal_gold")
                                )
                            }

                            // Customizable Reinforcement Banner
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(PastelPeachBg)
                                    .border(1.dp, PastelPeachBorder, RoundedCornerShape(16.dp))
                                    .clickable { showEditReinforcementDialog = true }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Filled.Favorite, contentDescription = null, tint = SecondaryTerracotta, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "Refuerzo hoy: \"$reinforcementText\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PastelPeachText,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Filled.Edit, contentDescription = "Personalizar refuerzo", tint = SecondaryTerracotta, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Card 2: Nivel de Bienestar y Logro (1..5 Scale)
                item {
                    val levelDesc = when (overallScore) {
                        1 -> "Nivel 1: Necesitó pausa reparadora y acompañamiento cercano continuo."
                        2 -> "Nivel 2: Proceso realizado con apoyo guiado y modelado frecuente."
                        3 -> "Nivel 3: Progreso positivo y receptivo ante apoyos y pistas visuales."
                        4 -> "Nivel 4: Muy buena autonomía, iniciativa y participación positiva en las actividades."
                        else -> "Nivel 5: ¡Excelente desempeño, autorregulación y alegría plena durante la sesión!"
                    }

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
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Nivel de Bienestar y Logro",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        "Escala respetuosa y adaptada para la sesión",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(PastelMintChip),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.SentimentSatisfied, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(20.dp))
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                ScaleLevelCard(
                                    level = 1, label = "Pausa", icon = Icons.Filled.SelfImprovement,
                                    selectedBg = PastelLavenderBg, accent = PastelLavenderText,
                                    selected = overallScore == 1,
                                    onClick = { overallScore = 1; persistConfig(nextScore = 1) },
                                    modifier = Modifier.weight(1f).testTag("score_level_1")
                                )
                                ScaleLevelCard(
                                    level = 2, label = "Guiado", icon = Icons.Filled.SupportAgent,
                                    selectedBg = PastelPeachBg, accent = PastelPeachText,
                                    selected = overallScore == 2,
                                    onClick = { overallScore = 2; persistConfig(nextScore = 2) },
                                    modifier = Modifier.weight(1f).testTag("score_level_2")
                                )
                                ScaleLevelCard(
                                    level = 3, label = "Pistas", icon = Icons.Filled.Lightbulb,
                                    selectedBg = PastelButterBg, accent = PastelButterText,
                                    selected = overallScore == 3,
                                    onClick = { overallScore = 3; persistConfig(nextScore = 3) },
                                    modifier = Modifier.weight(1f).testTag("score_level_3")
                                )
                                ScaleLevelCard(
                                    level = 4, label = "Autónomo", icon = Icons.Filled.ThumbUp,
                                    selectedBg = PastelBlueBg, accent = PrimaryBlue,
                                    selected = overallScore == 4,
                                    onClick = { overallScore = 4; persistConfig(nextScore = 4) },
                                    modifier = Modifier.weight(1f).testTag("score_level_4")
                                )
                                ScaleLevelCard(
                                    level = 5, label = "Excelente", icon = Icons.Filled.Celebration,
                                    selectedBg = PastelMintBg, accent = TertiaryMint,
                                    selected = overallScore == 5,
                                    onClick = { overallScore = 5; persistConfig(nextScore = 5) },
                                    modifier = Modifier.weight(1f).testTag("score_level_5")
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceLow)
                                    .border(1.dp, OutlineSubtle, RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.Psychology, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                Text(levelDesc, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                            }
                        }
                    }
                }

                // Card 3: Dimensiones y Plantillas
                item {
                    val presetTemplates = listOf("Sensorial", "Social / Juego", "Académico", "Conductual")
                    val allTemplateChips = if (selectedTemplate in presetTemplates) presetTemplates else presetTemplates + selectedTemplate

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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(PastelBlueBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(
                                            "Registrar en Historial del Alumno",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            "Sincronizar con expediente escolar",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                }
                                Switch(
                                    checked = recordInHistory,
                                    onCheckedChange = {
                                        recordInHistory = it
                                        persistConfig(nextRecord = it)
                                    },
                                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryBlue)
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Plantillas:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                allTemplateChips.forEach { tpl ->
                                    val selected = selectedTemplate == tpl
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(999.dp))
                                            .background(if (selected) PrimaryBlue else SurfaceContainer)
                                            .clickable {
                                                selectedTemplate = tpl
                                                val newDims = if (tpl in presetTemplates) defaultEvaluationDimensions(tpl) else dimensions
                                                dimensions = newDims
                                                persistConfig(nextTpl = tpl, nextDims = newDims)
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = tpl,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (selected) CardSurface else TextPrimary
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(PastelMintBg)
                                        .border(1.dp, PastelMintBorder, RoundedCornerShape(999.dp))
                                        .clickable { showCustomTemplateDialog = true }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("+ Propia", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PastelMintText)
                                }
                            }

                            dimensions.forEach { dim ->
                                DimensionScoringCard(
                                    dim = dim,
                                    onEdit = { editingDimension = dim },
                                    onScoreChange = { newScore ->
                                        val nextDims = dimensions.map {
                                            if (it.id == dim.id) it.copy(score = newScore) else it
                                        }
                                        dimensions = nextDims
                                        persistConfig(nextDims = nextDims)
                                    },
                                    onRemove = {
                                        val nextDims = dimensions.filterNot { it.id == dim.id }
                                        dimensions = nextDims
                                        persistConfig(nextDims = nextDims)
                                    }
                                )
                            }

                            OutlinedButton(
                                onClick = { showAddDimensionDialog = true },
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.AddCircleOutline, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Añadir Nueva Dimensión / Objetivo", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                            }
                        }
                    }
                }

                // Card 4: Observaciones Terapéuticas + Dictado + Compartir con Familia
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
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(PastelBlueBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.EditNote, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                    }
                                    Text(
                                        "Observaciones Terapéuticas",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "ES"))
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Dicta la observación para $shortName")
                                        }
                                        try {
                                            speechLauncher.launch(intent)
                                        } catch (_: ActivityNotFoundException) {
                                            onEmitToast("Dictado por voz no disponible en este dispositivo; escribe directamente abajo.")
                                        }
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(SurfaceLow)
                                        .border(1.dp, OutlineSubtle, CircleShape)
                                        .testTag("eval_mic_button")
                                ) {
                                    Icon(Icons.Filled.Mic, contentDescription = "Dictado por voz", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                }
                            }

                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                placeholder = { Text("Añade notas breves sobre reguladores utilizados, apoyos o conductas observables...") },
                                minLines = 3,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceLow,
                                    unfocusedContainerColor = SurfaceLow,
                                    focusedBorderColor = PrimaryBlue,
                                    unfocusedBorderColor = OutlineSubtle
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceLow)
                                    .border(1.dp, OutlineSubtle, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(PastelMintChip),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.FamilyRestroom, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(18.dp))
                                    }
                                    Column {
                                        Text(
                                            "Compartir con Familia",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = TextPrimary
                                        )
                                        Text(
                                            "Se enviará al Diario de $shortName",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextMuted
                                        )
                                    }
                                }
                                Switch(
                                    checked = shareWithFamily,
                                    onCheckedChange = {
                                        shareWithFamily = it
                                        persistConfig(nextShare = it)
                                    },
                                    colors = SwitchDefaults.colors(checkedTrackColor = TertiaryMint)
                                )
                            }
                        }
                    }
                }

                // Bottom CTAs
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                if (overallScore >= 5 || selectedMedal == "GOLD") {
                                    showConfetti = true
                                }
                                showCelebrationBanner = true
                                onSubmitEvaluation(
                                    student,
                                    selectedMedal,
                                    overallScore,
                                    dimensions,
                                    selectedTemplate,
                                    reinforcementText,
                                    notes,
                                    shareWithFamily,
                                    recordInHistory,
                                    false
                                )
                            },
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SecondaryCoralVibrant,
                                contentColor = CardSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("submit_evaluation_button")
                        ) {
                            Icon(Icons.Filled.VerifiedUser, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Registrar y Entregar Medalla", style = MaterialTheme.typography.labelLarge)
                        }

                        OutlinedButton(
                            onClick = {
                                onSubmitEvaluation(
                                    student,
                                    selectedMedal,
                                    overallScore,
                                    dimensions,
                                    selectedTemplate,
                                    reinforcementText,
                                    notes,
                                    shareWithFamily,
                                    recordInHistory,
                                    true
                                )
                            },
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_draft_evaluation_button")
                        ) {
                            Icon(Icons.Filled.BookmarkBorder, contentDescription = null, tint = TextMuted)
                            Spacer(Modifier.width(8.dp))
                            Text("Guardar como borrador", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                        }
                    }
                }
            } else if (activeTab == 1) {
                // Semanal Summary Tab
                item {
                    val completedCount = studentEvaluations.count { !it.draft }
                    val avgScore = if (studentEvaluations.isEmpty()) overallScore.toFloat()
                    else studentEvaluations.map { it.overallScore }.average().toFloat()
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
                            Text(
                                "Resumen Semanal de $shortName",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SummaryMetricTile(
                                    label = "Media Bienestar",
                                    value = String.format("%.1f / 5", avgScore),
                                    bg = PastelMintBg,
                                    accent = PastelMintText,
                                    modifier = Modifier.weight(1f)
                                )
                                SummaryMetricTile(
                                    label = "Sesiones",
                                    value = "$completedCount reg.",
                                    bg = PastelBlueBg,
                                    accent = PastelBlueText,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Text(
                                "Plantilla activa: $selectedTemplate · ${dimensions.size} dimensiones activas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                // Historial Tab
                if (studentEvaluations.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            color = CardSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, OutlineSubtle, RoundedCornerShape(22.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.History, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                                Text("Sin evaluaciones previas", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                                Text("Registra la sesión actual para comenzar el historial de $shortName.", style = MaterialTheme.typography.bodySmall, color = TextMuted, textAlign = TextAlign.Center)
                            }
                        }
                    }
                } else {
                    items(studentEvaluations, key = { it.id }) { ev ->
                        EvaluationHistoryCard(ev)
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }

        // Floating Celebration Banner matching code_bf519a75.html
        AnimatedVisibility(
            visible = showCelebrationBanner,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(PastelMintBg)
                    .border(1.dp, PastelMintBorder, RoundedCornerShape(20.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(28.dp))
                    Column {
                        Text("¡Evaluación Guardada!", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PastelMintText)
                        Text("Medalla y reporte entregados a $shortName", style = MaterialTheme.typography.bodySmall, color = PastelMintText)
                    }
                }
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(20.dp))
            }
        }

        ConfettiOverlay(
            visible = showConfetti,
            onFinished = { showConfetti = false }
        )
    }

    if (showStudentPicker) {
        AlertDialog(
            onDismissRequest = { showStudentPicker = false },
            containerColor = CardSurface,
            title = { Text("Seleccionar alumno a evaluar", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    students.forEach { st ->
                        val isSel = st.id == student.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSel) PastelBlueBg else SurfaceLow)
                                .border(1.dp, if (isSel) PrimaryBlue else OutlineSubtle, RoundedCornerShape(14.dp))
                                .clickable {
                                    onSelectStudent(st.id)
                                    showStudentPicker = false
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StudentAvatar(
                                avatarIndex = st.avatarIndex,
                                customUri = st.customAvatarUri,
                                contentDescription = st.fullName,
                                size = 40.dp
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(st.fullName, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                                Text(
                                    "${st.evalTemplate.ifBlank { "Sensorial" }} · ${st.effectiveEvalDimensions.size} dimensiones",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                            if (isSel) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = PrimaryBlue)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStudentPicker = false }) { Text("Cerrar") }
            }
        )
    }

    if (showEditReinforcementDialog) {
        var draftReinf by remember(reinforcementText) { mutableStateOf(reinforcementText) }
        AlertDialog(
            onDismissRequest = { showEditReinforcementDialog = false },
            containerColor = CardSurface,
            title = { Text("Personalizar Refuerzo Hoy", style = MaterialTheme.typography.titleMedium) },
            text = {
                OutlinedTextField(
                    value = draftReinf,
                    onValueChange = { draftReinf = it },
                    label = { Text("Mensaje positivo para $shortName") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (draftReinf.isNotBlank()) {
                            reinforcementText = draftReinf.trim()
                            persistConfig(nextReinf = draftReinf.trim(), showToast = true)
                            showEditReinforcementDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryTerracotta)
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditReinforcementDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showCustomTemplateDialog) {
        var customName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCustomTemplateDialog = false },
            containerColor = CardSurface,
            title = { Text("Nueva Plantilla", style = MaterialTheme.typography.titleMedium) },
            text = {
                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("Nombre (ej. Rutina Comedor)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customName.isNotBlank()) {
                            val clean = customName.trim()
                            selectedTemplate = clean
                            persistConfig(nextTpl = clean, showToast = true)
                            showCustomTemplateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Aplicar") }
            },
            dismissButton = {
                TextButton(onClick = { showCustomTemplateDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showAddDimensionDialog || editingDimension != null) {
        val editing = editingDimension
        var newTitle by remember(editing) { mutableStateOf(editing?.title ?: "") }
        var newSubtitle by remember(editing) { mutableStateOf(editing?.subtitle ?: "") }
        var newColor by remember(editing) { mutableStateOf(editing?.colorHex ?: "#3F82B8") }
        val colorChoices = listOf(
            "#3F82B8" to "Azul",
            "#2FA37A" to "Menta",
            "#FF8A5C" to "Melocotón",
            "#7C5CC7" to "Lavanda"
        )
        AlertDialog(
            onDismissRequest = {
                showAddDimensionDialog = false
                editingDimension = null
            },
            containerColor = CardSurface,
            title = {
                Text(
                    if (editing == null) "Nueva Dimensión / Objetivo" else "Personalizar Dimensión",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Título (ej. Comunicación PECS)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSubtitle,
                        onValueChange = { newSubtitle = it },
                        label = { Text("Descripción breve de logro") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colorChoices.forEach { (hex, label) ->
                            val c = parseHexColor(hex)
                            val sel = newColor == hex
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (sel) c else c.copy(alpha = 0.16f))
                                    .clickable { newColor = hex }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(label, style = MaterialTheme.typography.labelSmall, color = if (sel) CardSurface else c)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            val nextDims = if (editing == null) {
                                dimensions + EvaluationDimension(
                                    id = "dim_${UUID.randomUUID().toString().take(6)}",
                                    title = newTitle.trim(),
                                    subtitle = newSubtitle.trim().ifBlank { "Observación en aula TEA" },
                                    score = 4,
                                    colorHex = newColor
                                )
                            } else {
                                dimensions.map {
                                    if (it.id == editing.id) {
                                        it.copy(
                                            title = newTitle.trim(),
                                            subtitle = newSubtitle.trim().ifBlank { it.subtitle },
                                            colorHex = newColor
                                        )
                                    } else it
                                }
                            }
                            dimensions = nextDims
                            persistConfig(nextDims = nextDims, showToast = true)
                            showAddDimensionDialog = false
                            editingDimension = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(if (editing == null) "Añadir" else "Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDimensionDialog = false
                    editingDimension = null
                }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun MedalOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bg: Color,
    chipBg: Color,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) PastelButterBg else SurfaceLow)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) GoldAccent else OutlineSubtle,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Brush.verticalGradient(listOf(chipBg, bg))),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accent, modifier = Modifier.size(23.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(SecondaryCoralVibrant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = CardSurface, modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
private fun ScaleLevelCard(
    level: Int,
    label: String,
    icon: ImageVector,
    selectedBg: Color,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) selectedBg else SurfaceLow)
            .border(if (selected) 2.dp else 1.dp, if (selected) accent else OutlineSubtle, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = level.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = if (selected) accent else TextPrimary
        )
        Icon(icon, contentDescription = label, tint = accent, modifier = Modifier.size(20.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 10.sp
            ),
            color = if (selected) accent else TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DimensionScoringCard(
    dim: EvaluationDimension,
    onEdit: () -> Unit,
    onScoreChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val accent = parseHexColor(dim.colorHex)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceLow)
            .border(1.dp, OutlineSubtle, RoundedCornerShape(18.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f).clickable(onClick = onEdit)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CenterFocusStrong, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                }
                Column {
                    Text(dim.title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Text(dim.subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar dimensión", tint = PrimaryBlue, modifier = Modifier.size(15.dp))
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Eliminar dimensión", tint = OutlineLabel, modifier = Modifier.size(16.dp))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Puntaje observado:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (1..5).forEach { value ->
                    val isSel = dim.score == value
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSel) accent else CardSurface)
                            .border(1.dp, if (isSel) accent else OutlineSubtle, CircleShape)
                            .clickable { onScoreChange(value) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSel) CardSurface else TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricTile(
    label: String,
    value: String,
    bg: Color,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = accent)
        Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = accent)
    }
}

@Composable
private fun EvaluationHistoryCard(ev: Evaluation) {
    val medalColor = when (ev.medalType) {
        "BRONZE" -> BronzeAccent
        "SILVER" -> PastelBlueChip
        else -> GoldAccent
    }
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CardSurface,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineSubtle, RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(medalColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                    Column {
                        Text(
                            text = "${ev.templateCategory} • Nivel ${ev.overallScore}/5",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(ev.dateLabel, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
                if (ev.draft) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PastelButterChip)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("Borrador", style = MaterialTheme.typography.labelSmall, color = PastelButterText)
                    }
                }
            }
            if (ev.notes.isNotBlank()) {
                Text(ev.notes, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            }
        }
    }
}
