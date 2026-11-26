package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PsychologyAlt
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.LibraryItem
import com.example.data.QuickNote
import com.example.data.Student
import com.example.data.formatTodaySpanishHeader
import com.example.ui.theme.CardSurface
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PastelBlueBorder
import com.example.ui.theme.PastelBlueChip
import com.example.ui.theme.PastelBlueText
import com.example.ui.theme.PastelButterBg
import com.example.ui.theme.PastelButterBorder
import com.example.ui.theme.PastelButterChip
import com.example.ui.theme.PastelButterText
import com.example.ui.theme.PastelLavenderBg
import com.example.ui.theme.PastelLavenderBorder
import com.example.ui.theme.PastelLavenderBright
import com.example.ui.theme.PastelLavenderChip
import com.example.ui.theme.PastelLavenderText
import com.example.ui.theme.PastelMintChip
import com.example.ui.theme.PastelMintText
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryCoral
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceLow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun InicioScreen(
    teacherName: String,
    students: List<Student>,
    notes: List<QuickNote>,
    libraryItems: List<LibraryItem>,
    chatMessagesCount: Int,
    onOpenChatbot: () -> Unit,
    onNavigateToClase: () -> Unit,
    onNavigateToDiario: () -> Unit,
    onNavigateToRecursos: () -> Unit,
    onNavigateToBiblioteca: () -> Unit,
    onAddNote: (category: String, content: String, studentName: String, timeLabel: String) -> Unit,
    onToggleNoteCompleted: (QuickNote) -> Unit,
    onReplyToNote: (QuickNote, String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onSaveQuickLog: (Student, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var noteFilter by remember { mutableStateOf("TODAS") }
    var showNewNoteDialog by remember { mutableStateOf(false) }
    var showQuickLogDialog by remember { mutableStateOf(false) }
    var replyTargetNote by remember { mutableStateOf<QuickNote?>(null) }

    val filteredNotes = remember(notes, noteFilter) {
        when (noteFilter) {
            "ANTICIPACION" -> notes.filter { it.category == "ANTICIPACION" }
            "MATERIALES" -> notes.filter { it.category == "MATERIALES" }
            "SENSORIAL" -> notes.filter { it.category == "SENSORIAL" }
            else -> notes
        }
    }

    val firstName = teacherName.trim()
        .removePrefix("Profe ")
        .removePrefix("profe ")
        .trim()
        .split(" ")
        .firstOrNull()
        .orEmpty()
        .ifBlank { "Carmen" }
    val recommendedBook = libraryItems.firstOrNull { it.id == "lib_viaje_lucas" } ?: libraryItems.firstOrNull()

    val lifecycleOwner = LocalLifecycleOwner.current
    var todayDateLabel by remember { mutableStateOf(formatTodaySpanishHeader()) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
                todayDateLabel = formatTodaySpanishHeader()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Greeting Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFF7F0), Color(0xFFFDFAF6), Color(0xFFF2F7FC))
                        )
                    )
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(28.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(PastelButterChip.copy(alpha = 0.75f))
                                .border(1.dp, PastelButterBorder, RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                Icons.Filled.CalendarToday,
                                contentDescription = null,
                                tint = PastelButterText,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = todayDateLabel,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PastelButterText
                            )
                        }

                        Text(
                            text = "¡Hola $firstName! 👋",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = TextPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Filled.Spa,
                                contentDescription = null,
                                tint = PastelMintText,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Cada pequeño paso transforma un mundo. ¡Gran jornada hoy!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        }
                    }

                    Spacer(Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PastelLavenderBg)
                            .border(1.dp, PastelLavenderBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AllInclusive,
                            contentDescription = "Neurodiversidad",
                            tint = PastelLavenderBright,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }

        // 2. Exploración Rápida 2x2 Bento Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Exploración Rápida",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        text = "4 Áreas",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = PastelBlueText
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val studentCountLabel = if (students.size == 1) "1 Alumno" else "${students.size} Alumnos"
                    BentoTile(
                        title = "Clase",
                        subtitle = "Expedientes y progreso",
                        badge = studentCountLabel,
                        icon = Icons.Filled.School,
                        gradient = listOf(Color(0xFFFFF5EF), Color(0xFFFFFAF7)),
                        border = Color(0xFFFCDCC9),
                        iconBg = Color(0xFFFFE4D5),
                        iconTint = Color(0xFFE55B1B),
                        badgeBg = Color(0xFFFFEADA),
                        badgeText = Color(0xFF933D0D),
                        testTag = "bento_clase",
                        onClick = onNavigateToClase,
                        modifier = Modifier.weight(1f)
                    )
                    BentoTile(
                        title = "Diario",
                        subtitle = "Seguimiento por sesión",
                        badge = "Registro",
                        icon = Icons.Filled.EditCalendar,
                        gradient = listOf(Color(0xFFF0F7FD), Color(0xFFF8FCFF)),
                        border = Color(0xFFCFE4F8),
                        iconBg = Color(0xFFD7ECFC),
                        iconTint = Color(0xFF1E74B8),
                        badgeBg = Color(0xFFE4F2FE),
                        badgeText = Color(0xFF13578E),
                        testTag = "bento_diario",
                        onClick = onNavigateToDiario,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BentoTile(
                        title = "Recursos",
                        subtitle = "Fichas descargables",
                        badge = "PDFs",
                        icon = Icons.Filled.PictureAsPdf,
                        gradient = listOf(Color(0xFFF0FAF5), Color(0xFFF8FCFA)),
                        border = Color(0xFFCAEADB),
                        iconBg = Color(0xFFD6F3E6),
                        iconTint = Color(0xFF227D60),
                        badgeBg = Color(0xFFE2F7EE),
                        badgeText = Color(0xFF165A44),
                        testTag = "bento_recursos",
                        onClick = onNavigateToRecursos,
                        modifier = Modifier.weight(1f)
                    )
                    BentoTile(
                        title = "Biblioteca",
                        subtitle = "Cuentos e historias",
                        badge = "Libros",
                        icon = Icons.Filled.AutoStories,
                        gradient = listOf(Color(0xFFF6F3FB), Color(0xFFFAF8FD)),
                        border = Color(0xFFDDD4F7),
                        iconBg = Color(0xFFE8E1F8),
                        iconTint = Color(0xFF694EC2),
                        badgeBg = Color(0xFFEFEAFB),
                        badgeText = Color(0xFF553CA8),
                        testTag = "bento_biblioteca",
                        onClick = onNavigateToBiblioteca,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Registro Rápido Diario Coral Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFFB183), Color(0xFFFFA473), Color(0xFFFF8E58))
                        )
                    )
                    .border(1.dp, Color(0xFFF89766).copy(alpha = 0.5f), RoundedCornerShape(28.dp))
                    .clickable { showQuickLogDialog = true }
                    .padding(16.dp)
                    .testTag("quick_log_banner")
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
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CardSurface.copy(alpha = 0.45f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.AddCircle,
                                contentDescription = null,
                                tint = Color(0xFF622302),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Registro Rápido Diario",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color(0xFF4A1D05)
                            )
                            Text(
                                text = "Anota avances o incidencias en segundos",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = Color(0xFF5D280B)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(CardSurface.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Abrir registro rápido",
                            tint = Color(0xFF4A1D05),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 4. Nuevo Chatbot Gemini Multi-Turno
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFEEF4FE), Color(0xFFF5EEFD)))
                    )
                    .border(1.dp, PastelBlueBorder, RoundedCornerShape(26.dp))
                    .clickable(onClick = onOpenChatbot)
                    .padding(16.dp)
                    .testTag("open_chatbot_button"),
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
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PastelBlueChip, PastelLavenderChip)))
                            .border(1.dp, PastelLavenderBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = PastelLavenderText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Chatbot Gemini R-TEA",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = TextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(CardSurface)
                                    .border(1.dp, PastelLavenderBorder, RoundedCornerShape(999.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (chatMessagesCount > 0) "$chatMessagesCount msgs" else "Roles IA",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PastelLavenderText
                                )
                            }
                        }
                        Text(
                            text = "Conversa por roles con el contexto de tus alumnos",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Abrir Chatbot Gemini",
                        tint = CardSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // 4. Tablón de Notas Rápidas
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.PushPin, contentDescription = null, tint = Color(0xFFE55B1B))
                        Column {
                            Text(
                                text = "Tablón de Notas Rápidas",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "${notes.size} notas fijadas para hoy",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }

                    Surface(
                        onClick = { showNewNoteDialog = true },
                        shape = RoundedCornerShape(999.dp),
                        color = PastelButterBg,
                        modifier = Modifier
                            .border(1.dp, PastelButterBorder, RoundedCornerShape(999.dp))
                            .testTag("add_note_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = PastelButterText, modifier = Modifier.size(16.dp))
                            Text("Nueva Nota", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PastelButterText)
                        }
                    }
                }

                // Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NoteFilterChip("Todas (${notes.size})", noteFilter == "TODAS") { noteFilter = "TODAS" }
                    NoteFilterChip("Urgentes", noteFilter == "ANTICIPACION") { noteFilter = "ANTICIPACION" }
                    NoteFilterChip("Materiales", noteFilter == "MATERIALES") { noteFilter = "MATERIALES" }
                    NoteFilterChip("Sensorial", noteFilter == "SENSORIAL") { noteFilter = "SENSORIAL" }
                }

                // Sticky Notes Cards
                filteredNotes.forEach { note ->
                    StickyNoteCard(
                        note = note,
                        onToggleComplete = { onToggleNoteCompleted(note) },
                        onOpenReply = { replyTargetNote = note },
                        onDelete = { onDeleteNote(note.id) }
                    )
                }
            }
        }

        // 6. Lectura Recomendada
        if (recommendedBook != null) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFF7F4FC), Color(0xFFFCFBFD)))
                        )
                        .border(1.dp, PastelLavenderBorder, RoundedCornerShape(24.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.LocalLibrary, contentDescription = null, tint = PastelLavenderBright, modifier = Modifier.size(20.dp))
                            Text("Lectura Recomendada", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(PastelLavenderBg)
                                .border(1.dp, PastelLavenderBorder, RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text("TEA & Empatía", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PastelLavenderText)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardSurface)
                            .border(1.dp, OutlineSubtle, RoundedCornerShape(16.dp))
                            .clickable(onClick = onNavigateToBiblioteca)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 62.dp, height = 78.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(listOf(PastelBlueChip, PastelLavenderChip))
                                )
                                .border(1.dp, PastelBlueBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.AutoStories,
                                contentDescription = recommendedBook.title,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = recommendedBook.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = recommendedBook.reviewNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Filled.Visibility, contentDescription = null, tint = PastelLavenderText, modifier = Modifier.size(15.dp))
                                Text("Explorar ficha", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PastelLavenderText)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(12.dp)) }
    }

    // Dialogs
    if (showNewNoteDialog) {
        NewNoteDialog(
            students = students,
            onDismiss = { showNewNoteDialog = false },
            onSave = { cat, content, studentName, timeLabel ->
                onAddNote(cat, content, studentName, timeLabel)
                showNewNoteDialog = false
            }
        )
    }

    if (showQuickLogDialog && students.isNotEmpty()) {
        QuickLogDialog(
            students = students,
            onDismiss = { showQuickLogDialog = false },
            onSave = { st, text, state ->
                onSaveQuickLog(st, text, state)
                showQuickLogDialog = false
            },
            onOpenFullDiary = {
                showQuickLogDialog = false
                onNavigateToDiario()
            }
        )
    }

    if (replyTargetNote != null) {
        val target = replyTargetNote!!
        var replyText by remember(target.id) { mutableStateOf(target.replyText) }
        AlertDialog(
            onDismissRequest = { replyTargetNote = null },
            containerColor = CardSurface,
            title = { Text("Responder nota de ${target.studentName.ifBlank { "aula" }}", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(target.content, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        label = { Text("Mensaje para el equipo o familia") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (replyText.isNotBlank()) onReplyToNote(target, replyText)
                        replyTargetNote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { replyTargetNote = null }) { Text("Cancelar") }
            }
        )
    }

}

@Composable
private fun BentoTile(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    gradient: List<Color>,
    border: Color,
    iconBg: Color,
    iconTint: Color,
    badgeBg: Color,
    badgeText: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(142.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(gradient))
            .border(1.dp, border, RoundedCornerShape(26.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(23.dp))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(badgeBg)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(badge, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = badgeText)
            }
        }
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun NoteFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) PastelButterChip else CardSurface)
            .border(1.dp, if (selected) PastelButterBorder else OutlineSubtle, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold),
            color = if (selected) PastelButterText else TextMuted
        )
    }
}

@Composable
private fun StickyNoteCard(
    note: QuickNote,
    onToggleComplete: () -> Unit,
    onOpenReply: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val style = when (note.category) {
        "MATERIALES" -> StickyPalette(
            Color(0xFFF2F8FE), Color(0xFFD3E7FA), Color(0xFF4BA2E8),
            Color(0xFFE1EFFC), Color(0xFF13578E), Icons.Filled.ContentCut
        )
        "SENSORIAL" -> StickyPalette(
            Color(0xFFF0FAF5), Color(0xFFCCE8DA), Color(0xFF4DB896),
            Color(0xFFDCF3E8), Color(0xFF155D46), Icons.Filled.PsychologyAlt
        )
        else -> StickyPalette(
            Color(0xFFFFF6F0), Color(0xFFFBD9C5), Color(0xFFFF9D6C),
            Color(0xFFFFE9DC), Color(0xFF933D0D), Icons.Filled.NotificationImportant
        )
    }
    val bgColor = style.bg
    val borderColor = style.border
    val stripeColor = style.stripe
    val pillBg = style.badgeBg
    val pillText = style.badgeText
    val catIcon = style.icon

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(22.dp))
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(if (note.replyText.isBlank()) 136.dp else 164.dp)
                .background(stripeColor)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(catIcon, contentDescription = null, tint = pillText, modifier = Modifier.size(18.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(pillBg)
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(note.tagLabel, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = pillText)
                    }
                    if (note.timeLabel.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(CardSurface.copy(alpha = 0.85f))
                                .border(1.dp, borderColor, RoundedCornerShape(999.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(note.timeLabel, style = MaterialTheme.typography.labelSmall, color = pillText)
                        }
                    }
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.MoreHoriz, contentDescription = "Opciones de nota", tint = TextMuted)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(if (note.completed) "Marcar pendiente" else "Marcar completada") },
                            leadingIcon = { Icon(Icons.Filled.CheckCircle, contentDescription = null) },
                            onClick = { menuExpanded = false; onToggleComplete() }
                        )
                        DropdownMenuItem(
                            text = { Text("Responder") },
                            leadingIcon = { Icon(Icons.Filled.Forum, contentDescription = null) },
                            onClick = { menuExpanded = false; onOpenReply() }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar nota") },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (note.completed) TextDecoration.LineThrough else TextDecoration.None
                ),
                color = if (note.completed) TextMuted else TextPrimary
            )

            if (note.replyText.isNotBlank()) {
                Text(
                    text = "Respuesta: ${note.replyText}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = pillText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardSurface.copy(alpha = 0.7f))
                        .padding(8.dp)
                )
            }

            HorizontalDivider(color = borderColor.copy(alpha = 0.7f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = when {
                            note.contextLabel.contains("familia", true) -> Icons.Filled.FamilyRestroom
                            note.contextLabel.isNotBlank() -> Icons.Filled.Folder
                            else -> Icons.Filled.Person
                        },
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = note.contextLabel.ifBlank { "Alumno: ${note.studentName.ifBlank { "Aula" }}" },
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable {
                            if (note.category == "SENSORIAL") onOpenReply() else onToggleComplete()
                        }
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when {
                            note.category == "SENSORIAL" -> Icons.Filled.Forum
                            note.completed -> Icons.Filled.TaskAlt
                            else -> Icons.Filled.CheckCircle
                        },
                        contentDescription = null,
                        tint = pillText,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = when {
                            note.category == "SENSORIAL" -> "Responder"
                            note.completed -> "Completada"
                            else -> "Marcar lista"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = pillText
                    )
                }
            }
        }
    }
}



@Composable
private fun NewNoteDialog(
    students: List<Student>,
    onDismiss: () -> Unit,
    onSave: (category: String, content: String, studentName: String, timeLabel: String) -> Unit
) {
    var category by remember { mutableStateOf("ANTICIPACION") }
    var content by remember { mutableStateOf("") }
    var studentName by remember { mutableStateOf(students.firstOrNull()?.fullName ?: "Mateo García") }
    var timeLabel by remember { mutableStateOf("Hoy") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = { Text("Nueva Nota Rápida", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("ANTICIPACION" to "Urgente", "MATERIALES" to "Materiales", "SENSORIAL" to "Sensorial").forEach { (key, label) ->
                        val active = category == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (active) PrimaryBlue else SurfaceContainer)
                                .clickable { category = key }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(label, style = MaterialTheme.typography.labelSmall, color = if (active) CardSurface else TextPrimary)
                        }
                    }
                }
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Alumno / Contexto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = timeLabel,
                    onValueChange = { timeLabel = it },
                    label = { Text("Hora o estado (ej. 11:30 Recreo)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Recordatorio o nota de aula") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (content.isNotBlank()) onSave(category, content, studentName, timeLabel) },
                enabled = content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Fijar Nota")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun QuickLogDialog(
    students: List<Student>,
    onDismiss: () -> Unit,
    onSave: (Student, String, String) -> Unit,
    onOpenFullDiary: () -> Unit
) {
    var selectedStudent by remember { mutableStateOf(students.first()) }
    var text by remember { mutableStateOf("") }
    var emotionalState by remember { mutableStateOf("Radiante y Tranquilo") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = { Text("Registro Rápido Diario", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Selecciona alumno:", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    students.forEach { st ->
                        val active = st.id == selectedStudent.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (active) PrimaryBlue else SurfaceContainer)
                                .clickable { selectedStudent = st }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(st.fullName, style = MaterialTheme.typography.labelSmall, color = if (active) CardSurface else TextPrimary)
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Radiante y Tranquilo", "Con Apoyo Sereno", "Sensibilidad Alta").forEach { state ->
                        val active = emotionalState == state
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (active) PastelMintChip else SurfaceLow)
                                .clickable { emotionalState = state }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(state, style = MaterialTheme.typography.labelSmall, color = if (active) PastelMintText else TextMuted)
                        }
                    }
                }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Avance o incidencia rápida") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(onClick = onOpenFullDiary, modifier = Modifier.fillMaxWidth()) {
                    Text("Abrir Diario Escolar completo")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onSave(selectedStudent, text, emotionalState) },
                enabled = text.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryCoral)
            ) {
                Text("Guardar en Diario", color = Color(0xFF4A1D05))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

private data class StickyPalette(
    val bg: Color,
    val border: Color,
    val stripe: Color,
    val badgeBg: Color,
    val badgeText: Color,
    val icon: ImageVector
)

