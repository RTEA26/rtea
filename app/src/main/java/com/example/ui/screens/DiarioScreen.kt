package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DiaryAreaItem
import com.example.data.DiaryEntry
import com.example.data.InternalNoteItem
import com.example.data.RoutineStep
import com.example.data.SeedData
import com.example.data.Student
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.CardSurface
import com.example.ui.theme.OutlineLabel
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PastelBlueBg
import com.example.ui.theme.PastelBlueBorder
import com.example.ui.theme.PastelBlueChip
import com.example.ui.theme.PastelBlueText
import com.example.ui.theme.PastelButterBg
import com.example.ui.theme.PastelButterChip
import com.example.ui.theme.PastelButterText
import com.example.ui.theme.PastelLavenderBg
import com.example.ui.theme.PastelLavenderBorder
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

private data class WeekDayCell(
    val shortDay: String,
    val dayNumber: String,
    val fullLabel: String,
    val dots: List<Color>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiarioScreen(
    students: List<Student>,
    selectedStudent: Student?,
    diaryEntries: List<DiaryEntry>,
    onSelectStudent: (String) -> Unit,
    onUpdateEmotionalState: (DiaryEntry, String, String) -> Unit,
    onUpdateAnticipation: (DiaryEntry, String, String, String) -> Unit,
    onAddDiaryArea: (DiaryEntry, title: String, badgeLabel: String, objective: String, observation: String, nextSession: String, accentKey: String) -> Unit,
    onRemoveDiaryArea: (DiaryEntry, areaId: String) -> Unit,
    onAddInternalNote: (DiaryEntry, text: String, tag: String) -> Unit,
    onUpdateInternalNote: (DiaryEntry, noteId: String, text: String, tag: String) -> Unit,
    onDeleteInternalNote: (DiaryEntry, noteId: String) -> Unit,
    onToggleRoutineStep: (DiaryEntry, stepId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val student = selectedStudent ?: students.firstOrNull() ?: return
    val shortName = student.preferredName.ifBlank { student.fullName.split(" ").firstOrNull().orEmpty() }

    val entry = remember(diaryEntries, student.id, student.fullName) {
        diaryEntries.firstOrNull { it.studentId == student.id }
            ?: SeedData.defaultDiaryEntries.first().copy(
                id = "diary_${student.id}_2024_10_23",
                studentId = student.id,
                studentName = student.fullName
            )
    }

    var activeTab by remember { mutableIntStateOf(1) } // 0=Mes/Agenda, 1=Diario(Hoy), 2=Bloc de Notas
    var selectedDayNumber by remember { mutableStateOf("23") }
    var showStudentDialog by remember { mutableStateOf(false) }
    var showAddAreaDialog by remember { mutableStateOf(false) }
    var showEditMoodDialog by remember { mutableStateOf(false) }
    var showEditAnticipationDialog by remember { mutableStateOf(false) }
    var showAddInternalNoteDialog by remember { mutableStateOf(false) }
    var editingInternalNote by remember { mutableStateOf<InternalNoteItem?>(null) }

    val weekDays = remember {
        listOf(
            WeekDayCell("L", "21", "Lunes 21", listOf(TertiaryMint)),
            WeekDayCell("M", "22", "Martes 22", listOf(TertiaryMint)),
            WeekDayCell("X", "23", "Miércoles 23", listOf(PrimaryBlue, TertiaryMint)),
            WeekDayCell("J", "24", "Jueves 24", listOf(SecondaryTerracotta)),
            WeekDayCell("V", "25", "Viernes 25", listOf(PrimaryBlue)),
            WeekDayCell("S", "26", "Sábado 26", emptyList()),
            WeekDayCell("D", "27", "Domingo 27", emptyList())
        )
    }

    val activeDayCell = weekDays.firstOrNull { it.dayNumber == selectedDayNumber } ?: weekDays[2]

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Student Selector + Synced Pill + Segmented Tabs
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
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(999.dp))
                                .background(SurfaceLow)
                                .border(1.dp, OutlineSubtle, RoundedCornerShape(999.dp))
                                .clickable { showStudentDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("studentSelectorBtn"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StudentAvatar(student.avatarIndex, student.customAvatarUri, student.fullName, size = 34.dp)
                            Text(
                                text = student.fullName,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Filled.ExpandMore, contentDescription = "Cambiar alumno", tint = OutlineLabel)
                        }

                        Spacer(Modifier.width(8.dp))

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(PastelMintBg)
                                .border(1.dp, PastelMintBorder, RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(14.dp))
                            Text(
                                text = entry.syncedLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PastelMintText
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(999.dp))
                            .background(SurfaceContainer)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Mes / Agenda", "Diario (Hoy)", "Bloc de Notas").forEachIndexed { idx, label ->
                            val selected = activeTab == idx
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (selected) CardSurface else Color.Transparent)
                                    .clickable { activeTab = idx }
                                    .testTag("diario_tab_$idx"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                                        ),
                                        color = if (selected) TextPrimary else TextMuted,
                                        maxLines = 1
                                    )
                                    if (idx == 2 && entry.internalNotes.isNotEmpty()) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SecondaryCoralVibrant))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Weekly Calendar & Anticipation Card (shown in Mes/Agenda and Diario)
        if (activeTab == 0 || activeTab == 1) {
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
                                Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                                Text("Octubre 2024", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.ChevronLeft, contentDescription = "Semana anterior", tint = OutlineLabel, modifier = Modifier.size(18.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(PastelBlueChip)
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                ) {
                                    Text("Semana 43", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                                }
                                Icon(Icons.Filled.ChevronRight, contentDescription = "Semana siguiente", tint = OutlineLabel, modifier = Modifier.size(18.dp))
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            weekDays.forEach { day ->
                                val selected = day.dayNumber == selectedDayNumber
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (selected) PrimaryBlue else SurfaceLow)
                                        .clickable { selectedDayNumber = day.dayNumber }
                                        .padding(vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = day.shortDay,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (selected) CardSurface.copy(alpha = 0.85f) else TextMuted
                                    )
                                    Text(
                                        text = day.dayNumber,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selected) CardSurface else TextPrimary
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.height(6.dp)) {
                                        day.dots.forEach { dotColor ->
                                            Box(
                                                modifier = Modifier
                                                    .size(5.dp)
                                                    .clip(CircleShape)
                                                    .background(if (selected) CardSurface else dotColor)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LegendDot("Calma", TertiaryMint)
                            LegendDot("Sensorial", PrimaryBlue)
                            LegendDot("Evento / Cambio", SecondaryTerracotta)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(PastelPeachBg)
                                .border(1.dp, PastelPeachBorder, RoundedCornerShape(18.dp))
                                .clickable { showEditAnticipationDialog = true }
                                .padding(12.dp)
                                .testTag("edit_anticipation_card"),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(PastelPeachChip),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = SecondaryTerracotta, modifier = Modifier.size(18.dp))
                            }
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = entry.anticipationTitle.ifBlank { "Anticipación para mañana" },
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = PastelPeachText,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        if (entry.anticipationTime.isNotBlank()) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(999.dp))
                                                    .background(CardSurface)
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(entry.anticipationTime, style = MaterialTheme.typography.labelSmall, color = SecondaryTerracotta)
                                            }
                                        }
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(999.dp))
                                                .background(CardSurface)
                                                .border(1.dp, PastelPeachBorder, RoundedCornerShape(999.dp))
                                                .padding(horizontal = 7.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Editar anticipación", tint = SecondaryTerracotta, modifier = Modifier.size(11.dp))
                                            Text("Editar", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = PastelPeachText)
                                        }
                                    }
                                }
                                Text(
                                    text = entry.anticipationText.ifBlank { "Toca aquí para escribir qué necesitas anticipar para mañana." },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (entry.anticipationText.isBlank()) TextMuted else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily School & Therapist Report Section
        if (activeTab == 1) {
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
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "REPORTE ESCOLAR & TERAPEUTA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.6.sp
                                ),
                                color = PrimaryBlue
                            )
                        }

                        Text(
                            text = "¿Cómo fue el día de $shortName?",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(PastelBlueChip)
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(activeDayCell.fullLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(SurfaceContainer)
                                    .clickable { showAddAreaDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                                    .testTag("diario_add_area_chip")
                            ) {
                                Text("+ Añadir Área", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            }
                        }

                        // Editable Emotional State Highlight Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(PastelMintBg)
                                .border(1.dp, PastelMintBorder, RoundedCornerShape(20.dp))
                                .clickable { showEditMoodDialog = true }
                                .padding(14.dp)
                                .testTag("edit_emotional_state_box"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(CardSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.WbSunny, contentDescription = null, tint = TertiaryMint)
                                }
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            "ESTADO EMOCIONAL",
                                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
                                            color = PastelMintText
                                        )
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(999.dp))
                                                .background(CardSurface)
                                                .border(1.dp, PastelMintBorder, RoundedCornerShape(999.dp))
                                                .padding(horizontal = 7.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(Icons.Filled.Edit, contentDescription = "Cambiar estado emocional", tint = TertiaryMint, modifier = Modifier.size(11.dp))
                                            Text("Cambiar", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = PastelMintText)
                                        }
                                    }
                                    Text(
                                        entry.emotionalState,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        entry.emotionalSubtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CardSurface)
                                    .border(1.dp, PastelPeachBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = SecondaryTerracotta, modifier = Modifier.size(18.dp))
                                Text(entry.medalLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                                Text("Logro del día", style = MaterialTheme.typography.labelSmall, color = TextMuted, fontSize = 10.sp)
                            }
                        }

                        // Customizable Diary Areas
                        entry.areas.forEach { area ->
                            DiaryAreaCard(
                                area = area,
                                onRemove = { onRemoveDiaryArea(entry, area.id) }
                            )
                        }

                        OutlinedButton(
                            onClick = { showAddAreaDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.AddCircleOutline, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("+ Añadir Área de Trabajo / Objetivo Funcional", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                        }
                    }
                }
            }
        }

        // Bloc de Notas Personal (solo registro propio, no compartido)
        if (activeTab == 1 || activeTab == 2) {
            item {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = Color(0xFFFFFDF4),
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFEBDCA8), RoundedCornerShape(26.dp))
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
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(PastelButterChip),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.EditNote, contentDescription = null, tint = PastelButterText, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text(
                                        "Bloc de Notas Personal",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Filled.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                                        Text(
                                            "Registro propio · No compartido",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(PastelButterBg)
                                    .border(1.dp, Color(0xFFEBDCA8), RoundedCornerShape(999.dp))
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (entry.internalNotes.size == 1) "1 apunte" else "${entry.internalNotes.size} apuntes",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PastelButterText
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF0E5BD))

                        if (entry.internalNotes.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CardSurface)
                                    .border(1.dp, Color(0xFFF0E5BD), RoundedCornerShape(16.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    "Tu bloc de notas para $shortName está vacío. Anota aquí tus observaciones privadas de aula.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(CardSurface)
                                    .border(1.dp, Color(0xFFF0E5BD), RoundedCornerShape(18.dp))
                            ) {
                                entry.internalNotes.forEachIndexed { index, note ->
                                    NotepadEntryRow(
                                        note = note,
                                        onEdit = { editingInternalNote = note },
                                        onDelete = { onDeleteInternalNote(entry, note.id) }
                                    )
                                    if (index < entry.internalNotes.lastIndex) {
                                        HorizontalDivider(color = Color(0xFFF3EBD0))
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { showAddInternalNoteDialog = true },
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PastelButterText, contentColor = CardSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("add_internal_note_button")
                        ) {
                            Icon(Icons.Filled.AddCircleOutline, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Escribir en Bloc de Notas", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // Rutina de Tarde (Secuencia Predecible)
        if (activeTab == 0 || activeTab == 1) {
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
                            Column {
                                Text(
                                    "SECUENCIA PREDECIBLE",
                                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.6.sp),
                                    color = TextMuted
                                )
                                Text(
                                    "Rutina de Tarde (Aula TEA & Talleres)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                            }
                            Icon(Icons.Filled.EventNote, contentDescription = null, tint = OutlineLabel)
                        }

                        val steps = entry.afternoonRoutine
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            steps.chunked(2).forEach { rowSteps ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowSteps.forEach { step ->
                                        RoutineStepTile(
                                            step = step,
                                            onClick = { onToggleRoutineStep(entry, step.id) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    if (rowSteps.size == 1) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }
        }
    }

    // Switch Student Dialog
    if (showStudentDialog) {
        AlertDialog(
            onDismissRequest = { showStudentDialog = false },
            containerColor = CardSurface,
            title = { Text("Seleccionar Diario de Alumno", style = MaterialTheme.typography.titleMedium) },
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
                                    showStudentDialog = false
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StudentAvatar(st.avatarIndex, st.customAvatarUri, st.fullName, size = 38.dp)
                                Text(st.fullName, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                            }
                            if (isSel) Icon(Icons.Filled.Check, contentDescription = null, tint = PrimaryBlue)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStudentDialog = false }) { Text("Cerrar") }
            }
        )
    }

    // Add Work Area Dialog
    if (showAddAreaDialog) {
        var title by remember { mutableStateOf("") }
        var objective by remember { mutableStateOf("") }
        var observation by remember { mutableStateOf("") }
        var nextSession by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddAreaDialog = false },
            containerColor = CardSurface,
            title = { Text("Añadir Área al Diario", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Área (ej. Lenguaje Funcional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = objective, onValueChange = { objective = it }, label = { Text("Objetivo trabajado") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = observation, onValueChange = { observation = it }, label = { Text("Observación de hoy") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = nextSession, onValueChange = { nextSession = it }, label = { Text("Próxima sesión") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAddDiaryArea(
                                entry,
                                title,
                                "4/5 Autónomo",
                                objective.ifBlank { "Reforzar autonomía con apoyo visual." },
                                observation.ifBlank { "Buena disposición y calma durante la actividad." },
                                nextSession.ifBlank { "Mantener apoyo visual de inicio." },
                                "sky"
                            )
                            showAddAreaDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Guardar Área") }
            },
            dismissButton = {
                TextButton(onClick = { showAddAreaDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Edit Emotional State Dialog
    if (showEditMoodDialog) {
        var moodTitle by remember(entry.id, entry.emotionalState) { mutableStateOf(entry.emotionalState) }
        var moodSubtitle by remember(entry.id, entry.emotionalSubtitle) { mutableStateOf(entry.emotionalSubtitle) }
        val quickMoods = listOf(
            "Radiante y Tranquilo" to "Conectado con sus compañeros y flexible.",
            "Calmado y Receptivo" to "Buena disposición en tareas y transiciones.",
            "Con Apoyo Sereno" to "Necesitó guía visual y ritmo pausado.",
            "Muy Motivado" to "Participación activa con sus intereses.",
            "Sensible / Sobrecarga" to "Requirió rincón de calma y menos estímulos.",
            "Cansado / Necesita Pausa" to "Se priorizaron descansos sensoriales breves."
        )
        AlertDialog(
            onDismissRequest = { showEditMoodDialog = false },
            containerColor = CardSurface,
            title = { Text("Estado Emocional de $shortName", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        quickMoods.forEach { (titleOpt, subOpt) ->
                            val isSel = moodTitle.equals(titleOpt, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSel) TertiaryMint else PastelMintBg)
                                    .border(1.dp, PastelMintBorder, RoundedCornerShape(999.dp))
                                    .clickable {
                                        moodTitle = titleOpt
                                        moodSubtitle = subOpt
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    titleOpt,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSel) CardSurface else PastelMintText
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = moodTitle,
                        onValueChange = { moodTitle = it },
                        label = { Text("¿Cómo estuvo hoy?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = moodSubtitle,
                        onValueChange = { moodSubtitle = it },
                        label = { Text("Detalle o matiz del día") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (moodTitle.isNotBlank()) {
                            onUpdateEmotionalState(entry, moodTitle, moodSubtitle)
                            showEditMoodDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TertiaryMint)
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditMoodDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Edit Anticipation Dialog
    if (showEditAnticipationDialog) {
        var title by remember(entry.id, entry.anticipationTitle) {
            mutableStateOf(entry.anticipationTitle.ifBlank { "Anticipación para mañana" })
        }
        var time by remember(entry.id, entry.anticipationTime) {
            mutableStateOf(entry.anticipationTime)
        }
        var text by remember(entry.id, entry.anticipationText) {
            mutableStateOf(entry.anticipationText)
        }
        val anticipationPresets = listOf(
            Triple("Cambio de aula", "10:30 AM", "Anticipar con pictograma el traslado al aula de música o psicomotricidad."),
            Triple("Excursión / Salida", "09:30 AM", "Mostrar secuencia visual de salida, autobús y normas de calma."),
            Triple("Taller / Visita", "11:00 AM", "Anticipar la visita o taller especial y ofrecer cascos si hay ruido."),
            Triple("Ruido / Simulacro", "12:00 PM", "Preparar aviso visual previo y rincón de regulación sensorial.")
        )
        AlertDialog(
            onDismissRequest = { showEditAnticipationDialog = false },
            containerColor = CardSurface,
            title = { Text("Anticipación para mañana ($shortName)", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        anticipationPresets.forEach { (label, presetTime, presetText) ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(PastelPeachBg)
                                    .border(1.dp, PastelPeachBorder, RoundedCornerShape(999.dp))
                                    .clickable {
                                        time = presetTime
                                        text = presetText
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold), color = PastelPeachText)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título (ej. Anticipación para mañana)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("anticipation_title_input")
                    )
                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Hora o momento (ej. 11:00 AM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("anticipation_time_input")
                    )
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("¿Qué necesitas anticipar?") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("anticipation_text_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateAnticipation(entry, title, time, text)
                        showEditAnticipationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryTerracotta),
                    modifier = Modifier.testTag("save_anticipation_button")
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditAnticipationDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Add / Edit Personal Notepad Entry Dialog
    if (showAddInternalNoteDialog || editingInternalNote != null) {
        val editingTarget = editingInternalNote
        var noteText by remember(editingTarget?.id) { mutableStateOf(editingTarget?.text.orEmpty()) }
        var selectedTag by remember(editingTarget?.id) { mutableStateOf(editingTarget?.tag?.ifBlank { "Nota personal" } ?: "Nota personal") }
        val quickTags = listOf("Nota personal", "Objetivo cumplido", "Apoyo visual", "Regulación", "Recordatorio")
        AlertDialog(
            onDismissRequest = {
                showAddInternalNoteDialog = false
                editingInternalNote = null
            },
            containerColor = CardSurface,
            title = {
                Text(
                    if (editingTarget == null) "Nuevo Apunte en Bloc de Notas" else "Editar Apunte Personal",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        quickTags.forEach { t ->
                            val isSel = selectedTag == t
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSel) PastelButterText else PastelButterBg)
                                    .clickable { selectedTag = t }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(t, style = MaterialTheme.typography.labelSmall, color = if (isSel) CardSurface else PastelButterText)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        label = { Text("Escribe tu apunte privado...") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            if (editingTarget == null) {
                                onAddInternalNote(entry, noteText, selectedTag)
                            } else {
                                onUpdateInternalNote(entry, editingTarget.id, noteText, selectedTag)
                            }
                            showAddInternalNoteDialog = false
                            editingInternalNote = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PastelButterText)
                ) { Text("Guardar Apunte") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddInternalNoteDialog = false
                    editingInternalNote = null
                }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun LegendDot(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }
}

@Composable
private fun DiaryAreaCard(
    area: DiaryAreaItem,
    onRemove: () -> Unit
) {
    val (bg, border, chipBg, accent, icon) = when (area.accentKey) {
        "sky" -> Quintuple(PastelBlueBg, PastelBlueBorder, PastelBlueChip, PrimaryBlue, Icons.Filled.Headphones)
        "mint" -> Quintuple(PastelMintBg, PastelMintBorder, PastelMintChip, TertiaryMint, Icons.Filled.Psychology)
        "lavender" -> Quintuple(PastelLavenderBg, PastelLavenderBorder, PastelLavenderChip, PastelLavenderText, Icons.Filled.Bedtime)
        else -> Quintuple(PastelPeachBg, PastelPeachBorder, PastelPeachChip, SecondaryTerracotta, Icons.Filled.Restaurant)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
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
                        .background(chipBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
                }
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(area.title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(chipBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(area.badgeLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = accent)
                    }
                }
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Eliminar área", tint = OutlineLabel, modifier = Modifier.size(16.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardSurface)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Objetivo:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SecondaryTerracotta)
            Text(area.objective, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }

        Text(area.observation, style = MaterialTheme.typography.bodySmall, color = TextPrimary)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceContainer)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Filled.PushPin, contentDescription = null, tint = SecondaryCoralVibrant, modifier = Modifier.size(14.dp))
            Column {
                Text("Próxima sesión:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                Text(area.nextSession, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
        }
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

@Composable
private fun NotepadEntryRow(
    note: InternalNoteItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical red/coral notepad margin rule
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(SecondaryCoralVibrant.copy(alpha = 0.65f))
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (note.tag.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PastelButterBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(note.tag, style = MaterialTheme.typography.labelSmall, color = PastelButterText)
                    }
                } else {
                    Spacer(Modifier.width(1.dp))
                }
                Text(note.timeLabel, style = MaterialTheme.typography.labelSmall, color = OutlineLabel)
            }
            Text(
                text = note.text,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                Icons.Filled.DeleteOutline,
                contentDescription = "Eliminar apunte",
                tint = OutlineLabel,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun RoutineStepTile(
    step: RoutineStep,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bg, border, chipBg, accent, icon) = when (step.accentKey) {
        "sky" -> Quintuple(PastelBlueBg, PastelBlueBorder, PastelBlueChip, PrimaryBlue, Icons.Filled.Spa)
        "lavender" -> Quintuple(PastelLavenderBg, PastelLavenderBorder, PastelLavenderChip, PastelLavenderText, Icons.Filled.DirectionsRun)
        "mint" -> Quintuple(PastelMintBg, PastelMintBorder, PastelMintChip, TertiaryMint, Icons.Filled.PlaylistAddCheck)
        else -> Quintuple(PastelPeachBg, PastelPeachBorder, PastelPeachChip, SecondaryTerracotta, Icons.Filled.Restaurant)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .border(if (step.status == "active") 2.dp else 1.dp, if (step.status == "active") accent else border, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(chipBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            }
            Text(step.time, style = MaterialTheme.typography.labelSmall, color = OutlineLabel)
            Text(step.title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(step.subtitle, style = MaterialTheme.typography.labelSmall, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (step.status == "active") {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(PrimaryBlue)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("AHORA", style = MaterialTheme.typography.labelSmall, color = CardSurface, fontSize = 10.sp)
                }
            }
        }
        if (step.status == "done") {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(TertiaryMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Check, contentDescription = "Completado", tint = CardSurface, modifier = Modifier.size(12.dp))
            }
        }
    }
}
