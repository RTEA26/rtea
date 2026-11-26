package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProgressArea
import com.example.data.ResourceItem
import com.example.data.Student
import com.example.data.StudentDocument
import com.example.ui.components.ConcentricProgressRings
import com.example.ui.components.DocumentPreviewDialog
import com.example.ui.components.StudentAvatar
import com.example.ui.components.parseHexColor
import com.example.ui.theme.CardSurface
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OutlineLabel
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PastelBlueBg
import com.example.ui.theme.PastelBlueBorder
import com.example.ui.theme.PastelBlueChip
import com.example.ui.theme.PastelBlueText
import com.example.ui.theme.PastelButterBg
import com.example.ui.theme.PastelButterBorder
import com.example.ui.theme.PastelMintBg
import com.example.ui.theme.PastelMintBorder
import com.example.ui.theme.PastelMintChip
import com.example.ui.theme.PastelMintText
import com.example.ui.theme.PastelPeachBg
import com.example.ui.theme.PastelPeachBorder
import com.example.ui.theme.PastelPeachChip
import com.example.ui.theme.PastelPeachText
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryTerracotta
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceLow
import com.example.ui.theme.TertiaryMint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun ClaseScreen(
    students: List<Student>,
    resources: List<ResourceItem>,
    generatingReportForId: String?,
    onRegisterStudentClick: () -> Unit,
    onEditStudentClick: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit,
    onOpenEvaluation: (Student) -> Unit,
    onOpenDiario: (Student) -> Unit,
    onAdjustArea: (Student, String, Int) -> Unit,
    onAddArea: (Student, String, Int, String) -> Unit,
    onRemoveArea: (Student, String) -> Unit,
    onUpdateFolderCount: (Student, Int, Int) -> Unit,
    onUploadStudentPdf: (Student, Int, Uri) -> Unit,
    onDeleteStudentPdf: (Student, Int, StudentDocument) -> Unit,
    onOpenAiViewer: (Student) -> Unit,
    onGenerateAiReport: (Student) -> Unit,
    onAssignResourceToStudent: (Student, ResourceItem) -> Unit,
    onEmitToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var levelFilter by remember { mutableIntStateOf(0) } // 0=All, 1, 2, 3
    var selectedDetailStudentId by rememberSaveable { mutableStateOf<String?>(null) }
    var addAreaTargetStudent by remember { mutableStateOf<Student?>(null) }
    var deleteConfirmStudent by remember { mutableStateOf<Student?>(null) }
    var assignResourceStudent by remember { mutableStateOf<Student?>(null) }
    var pendingPdfTarget by remember { mutableStateOf<Pair<Student, Int>?>(null) }
    var previewDocState by remember { mutableStateOf<Pair<String, StudentDocument>?>(null) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        val target = pendingPdfTarget
        pendingPdfTarget = null
        if (uri != null && target != null) {
            onUploadStudentPdf(target.first, target.second, uri)
        }
    }

    val filteredStudents = remember(students, searchQuery, levelFilter) {
        students.filter { st ->
            val matchesLevel = levelFilter == 0 || st.supportLevel == levelFilter
            val q = searchQuery.trim().lowercase()
            val matchesSearch = q.isEmpty() ||
                st.fullName.lowercase().contains(q) ||
                st.preferredName.lowercase().contains(q) ||
                st.sensoryNotes.lowercase().contains(q)
            matchesLevel && matchesSearch
        }
    }

    val activeDetailStudent = remember(students, selectedDetailStudentId) {
        selectedDetailStudentId?.let { id -> students.firstOrNull { it.id == id } }
    }

    BackHandler(enabled = activeDetailStudent != null) {
        selectedDetailStudentId = null
    }

    if (activeDetailStudent != null) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = { selectedDetailStudentId = null },
                        shape = RoundedCornerShape(999.dp),
                        color = CardSurface,
                        modifier = Modifier
                            .border(1.dp, OutlineSubtle, RoundedCornerShape(999.dp))
                            .testTag("back_to_clase_grid")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver a Clase",
                                tint = PrimaryBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Volver a alumnos",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBlue
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PastelBlueChip)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = activeDetailStudent.classroom.ifBlank { "Aula TEA" },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PastelBlueText
                        )
                    }
                }
            }

            item(key = activeDetailStudent.id) {
                StudentProgressCard(
                    student = activeDetailStudent,
                    allResources = resources,
                    isGeneratingAi = generatingReportForId == activeDetailStudent.id,
                    onEdit = { onEditStudentClick(activeDetailStudent) },
                    onOpenHistory = { onOpenDiario(activeDetailStudent) },
                    onDelete = { deleteConfirmStudent = activeDetailStudent },
                    onAdjustArea = { areaId, delta -> onAdjustArea(activeDetailStudent, areaId, delta) },
                    onRemoveArea = { areaId -> onRemoveArea(activeDetailStudent, areaId) },
                    onAddAreaClick = { addAreaTargetStudent = activeDetailStudent },
                    onUploadPdf = { folderType ->
                        pendingPdfTarget = activeDetailStudent to folderType
                        pdfPickerLauncher.launch(arrayOf("application/pdf"))
                    },
                    onPreviewPdf = { folderLabel, doc ->
                        previewDocState = folderLabel to doc
                    },
                    onDeletePdf = { folderType, doc ->
                        onDeleteStudentPdf(activeDetailStudent, folderType, doc)
                    },
                    onClearLegacyFolderCount = { folderType ->
                        onUpdateFolderCount(activeDetailStudent, folderType, -1)
                    },
                    onOpenAiViewer = { onOpenAiViewer(activeDetailStudent) },
                    onGenerateAiReport = { onGenerateAiReport(activeDetailStudent) },
                    onAssignMoreResources = { assignResourceStudent = activeDetailStudent },
                    onEvaluateClick = { onOpenEvaluation(activeDetailStudent) }
                )
            }

            item { Spacer(Modifier.height(14.dp)) }
        }
    } else {
        // Avatar Grid Items: filtered students + permanent "Crear alumno" circle
        val gridEntries: List<Student?> = remember(filteredStudents) {
            filteredStudents.map<Student, Student?> { it } + listOf<Student?>(null)
        }
        val gridRows = remember(gridEntries) { gridEntries.chunked(3) }

        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Modo Tranquilidad Activo + Classroom Badge
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PastelMintBg)
                            .border(1.dp, PastelMintBorder, RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.Spa, contentDescription = null, tint = PastelMintText, modifier = Modifier.size(16.dp))
                        Text("Modo Tranquilidad Activo", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PastelMintText)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PastelBlueChip)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (students.size == 1) "1 Alumno" else "${students.size} Alumnos",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PastelBlueText
                        )
                    }
                }
            }

            // Search Bar + Level Filter Pills
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar alumno por nombre o apoyo...", style = MaterialTheme.typography.bodyMedium) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = OutlineLabel) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Filled.Close, contentDescription = "Limpiar búsqueda")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardSurface,
                            unfocusedContainerColor = CardSurface,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = OutlineSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LevelFilterPill("Todos (${students.size})", levelFilter == 0) { levelFilter = 0 }
                        LevelFilterPill("Nivel de apoyo 1", levelFilter == 1) { levelFilter = 1 }
                        LevelFilterPill("Nivel de apoyo 2", levelFilter == 2) { levelFilter = 2 }
                        LevelFilterPill("Nivel de apoyo 3", levelFilter == 3) { levelFilter = 3 }
                    }
                }
            }

            // Section Title
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Toca un alumno para abrir su expediente",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextMuted
                    )
                }
            }

            // Circular Avatars + Create Student Circle Grid
            items(gridRows) { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    rowItems.forEach { entry ->
                        if (entry != null) {
                            StudentAvatarCircleTile(
                                student = entry,
                                onClick = { selectedDetailStudentId = entry.id },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            CreateStudentCircleTile(
                                onClick = onRegisterStudentClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }

            if (filteredStudents.isEmpty() && students.isNotEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = CardSurface,
                        modifier = Modifier.fillMaxWidth().border(1.dp, OutlineSubtle, RoundedCornerShape(18.dp))
                    ) {
                        Text(
                            text = "No hay alumnos que coincidan con ese filtro. Puedes limpiar la búsqueda o crear uno nuevo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(14.dp)) }
        }
    }

    // Add Work Area Modal
    if (addAreaTargetStudent != null) {
        val target = addAreaTargetStudent!!
        AddWorkAreaDialog(
            studentName = target.fullName,
            onDismiss = { addAreaTargetStudent = null },
            onSave = { title, totalSteps, colorHex ->
                onAddArea(target, title, totalSteps, colorHex)
                addAreaTargetStudent = null
            }
        )
    }

    // Delete Student Confirmation Dialog
    if (deleteConfirmStudent != null) {
        val target = deleteConfirmStudent!!
        AlertDialog(
            onDismissRequest = { deleteConfirmStudent = null },
            containerColor = CardSurface,
            title = { Text("¿Eliminar expediente de ${target.fullName}?", style = MaterialTheme.typography.titleMedium) },
            text = {
                Text(
                    "Se eliminarán su ficha inclusiva y sus anillos de progreso de tu aula sincronizada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteStudent(target)
                        if (selectedDetailStudentId == target.id) {
                            selectedDetailStudentId = null
                        }
                        deleteConfirmStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmStudent = null }) { Text("Cancelar") }
            }
        )
    }

    // Assign More Resources Dialog
    if (assignResourceStudent != null) {
        val target = assignResourceStudent!!
        AlertDialog(
            onDismissRequest = { assignResourceStudent = null },
            containerColor = CardSurface,
            title = { Text("Asignar apoyos a ${target.fullName}", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    resources.take(6).forEach { res ->
                        val assigned = target.activeResourceIds.contains(res.id) || res.assignedStudentIds.contains(target.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (assigned) PastelMintBg else SurfaceLow)
                                .border(1.dp, if (assigned) PastelMintBorder else OutlineSubtle, RoundedCornerShape(12.dp))
                                .clickable { onAssignResourceToStudent(target, res) }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(res.title, style = MaterialTheme.typography.labelMedium, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${res.fileType} · ${res.statusBadge}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                            Text(
                                text = if (assigned) "Activo" else "+ Asignar",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (assigned) PastelMintText else PrimaryBlue
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { assignResourceStudent = null }) { Text("Listo") }
            }
        )
    }

    if (previewDocState != null) {
        val (folderLabel, doc) = previewDocState!!
        DocumentPreviewDialog(
            title = doc.title,
            subtitle = "$folderLabel · ${doc.sizeLabel} · ${doc.dateLabel}",
            fileType = "PDF",
            localPath = doc.localPath,
            mimeType = doc.mimeType,
            fallbackSummary = "Documento PDF adjunto en $folderLabel.",
            onDismiss = { previewDocState = null },
            onOpenFailed = {
                onEmitToast("No se encontró un visor de PDF instalado en el dispositivo.")
            }
        )
    }
}

@Composable
private fun StudentAvatarCircleTile(
    student: Student,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haloBrush = when (student.supportLevel) {
        2 -> Brush.linearGradient(listOf(SecondaryTerracotta, Color(0xFFFFCA99)))
        3 -> Brush.linearGradient(listOf(PrimaryBlue, Color(0xFF8ECAFA)))
        else -> Brush.linearGradient(listOf(TertiaryMint, Color(0xFF9BE3C9)))
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag("student_avatar_circle_${student.id}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(contentAlignment = Alignment.BottomCenter) {
            StudentAvatar(
                avatarIndex = student.avatarIndex,
                customUri = student.customAvatarUri,
                contentDescription = student.fullName,
                size = 86.dp,
                haloBrush = haloBrush
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(CardSurface)
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Nivel ${student.supportLevel}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = when (student.supportLevel) {
                        2 -> PastelPeachText
                        3 -> PastelBlueText
                        else -> PastelMintText
                    }
                )
            }
        }
        Text(
            text = student.fullName,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CreateStudentCircleTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .testTag("register_student_circle"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(PastelPeachBg)
                .border(2.dp, SecondaryTerracotta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(PastelPeachChip),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PersonAdd,
                    contentDescription = "Crear alumno",
                    tint = SecondaryTerracotta,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Text(
            text = "Crear alumno",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = SecondaryTerracotta,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun LevelFilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) PrimaryBlue else CardSurface)
            .border(1.dp, if (selected) PrimaryBlue else OutlineSubtle, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) CardSurface else TextMuted
        )
    }
}

@Composable
private fun StudentProgressCard(
    student: Student,
    allResources: List<ResourceItem>,
    isGeneratingAi: Boolean,
    onEdit: () -> Unit,
    onOpenHistory: () -> Unit,
    onDelete: () -> Unit,
    onAdjustArea: (String, Int) -> Unit,
    onRemoveArea: (String) -> Unit,
    onAddAreaClick: () -> Unit,
    onUploadPdf: (Int) -> Unit,
    onPreviewPdf: (String, StudentDocument) -> Unit,
    onDeletePdf: (Int, StudentDocument) -> Unit,
    onClearLegacyFolderCount: (Int) -> Unit,
    onOpenAiViewer: () -> Unit,
    onGenerateAiReport: () -> Unit,
    onAssignMoreResources: () -> Unit,
    onEvaluateClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val levelBadge = when (student.supportLevel) {
        2 -> Triple("Nivel de apoyo 2", PastelPeachBg, PastelPeachText)
        3 -> Triple("Nivel de apoyo 3", PastelBlueBg, PastelBlueText)
        else -> Triple("Nivel de apoyo 1", PastelMintBg, PastelMintText)
    }

    val studentResources = remember(student, allResources) {
        allResources.filter { it.id in student.activeResourceIds || student.id in it.assignedStudentIds }
    }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = CardSurface,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineSubtle, RoundedCornerShape(28.dp))
            .testTag("student_card_${student.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    StudentAvatar(
                        avatarIndex = student.avatarIndex,
                        customUri = student.customAvatarUri,
                        contentDescription = student.fullName,
                        size = 62.dp,
                        badgeIcon = Icons.Filled.Verified,
                        badgeBgColor = TertiaryMint
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = student.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(levelBadge.second)
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = levelBadge.first,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = levelBadge.third
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Filled.Event, contentDescription = null, tint = OutlineLabel, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Última sesión: ${student.lastSessionLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OutlineLabel
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opciones de alumno", tint = OutlineLabel)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Editar Ficha") },
                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            onClick = { menuExpanded = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("Ver Historial / Diario") },
                            leadingIcon = { Icon(Icons.Filled.History, contentDescription = null) },
                            onClick = { menuExpanded = false; onOpenHistory() }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Eliminar Alumno", color = ErrorRed) },
                            leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = ErrorRed) },
                            onClick = { menuExpanded = false; onDelete() }
                        )
                    }
                }
            }

            // Concentric Progress Rings Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceLow)
                    .border(1.dp, OutlineSubtle, RoundedCornerShape(20.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.DonutLarge, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        Text("Anillos de Progreso por Áreas", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PastelBlueBg)
                            .border(1.dp, PastelBlueBorder, RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("${student.globalPercentage}% Global", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ConcentricProgressRings(
                        areas = student.progressAreas,
                        globalPercentage = student.globalPercentage,
                        size = 114.dp
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    student.progressAreas.forEach { area ->
                        ProgressAreaRow(
                            area = area,
                            onDecrement = { onAdjustArea(area.id, -1) },
                            onIncrement = { onAdjustArea(area.id, +1) },
                            onRemove = { onRemoveArea(area.id) }
                        )
                    }
                }

                OutlinedButton(
                    onClick = onAddAreaClick,
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.AddCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("+ Añadir Área de Trabajo", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                }
            }

            // 3 Document Folders
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val mainCount = if (student.mainReportDocs.isNotEmpty()) student.mainReportDocs.size else student.mainReportDocsCount
                val progCount = if (student.programDocs.isNotEmpty()) student.programDocs.size else student.programDocsCount

                FolderRow(
                    title = "1. Informe Principal",
                    subtitle = "Informe inicial y diagnóstico (PDF)",
                    badgeText = "$mainCount ${if (mainCount == 1) "PDF" else "PDFs"}",
                    docs = student.mainReportDocs,
                    legacyCount = student.mainReportDocsCount,
                    bg = PastelBlueBg,
                    border = PastelBlueBorder,
                    accent = PrimaryBlue,
                    icon = Icons.Filled.Folder,
                    onUpload = { onUploadPdf(1) },
                    onPreviewDoc = { doc -> onPreviewPdf("1. Informe Principal", doc) },
                    onDeleteDoc = { doc -> onDeletePdf(1, doc) },
                    onClearLegacy = { onClearLegacyFolderCount(1) }
                )
                FolderRow(
                    title = "2. Programación",
                    subtitle = "Plan individualizado TEA (PDF)",
                    badgeText = "$progCount ${if (progCount == 1) "PDF" else "PDFs"}",
                    docs = student.programDocs,
                    legacyCount = student.programDocsCount,
                    bg = PastelPeachBg,
                    border = PastelPeachBorder,
                    accent = SecondaryTerracotta,
                    icon = Icons.Filled.Assignment,
                    onUpload = { onUploadPdf(2) },
                    onPreviewDoc = { doc -> onPreviewPdf("2. Programación", doc) },
                    onDeleteDoc = { doc -> onDeletePdf(2, doc) },
                    onClearLegacy = { onClearLegacyFolderCount(2) }
                )

                // 3. Informe Final IA
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PastelMintBg)
                        .border(1.dp, PastelMintBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp),
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(TertiaryMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.SmartToy, contentDescription = null, tint = CardSurface, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("3. Informe Final IA", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TertiaryMint)
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                "Analiza tus PDFs iniciales, programación y evaluación",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            onClick = onOpenAiViewer,
                            shape = RoundedCornerShape(999.dp),
                            color = TertiaryMint,
                            modifier = Modifier.testTag("ai_visor_${student.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Filled.Visibility, contentDescription = null, tint = CardSurface, modifier = Modifier.size(14.dp))
                                Text("Visor", style = MaterialTheme.typography.labelSmall, color = CardSurface)
                            }
                        }

                        Surface(
                            onClick = { if (!isGeneratingAi) onGenerateAiReport() },
                            shape = RoundedCornerShape(999.dp),
                            color = CardSurface,
                            modifier = Modifier
                                .border(1.dp, PastelMintBorder, RoundedCornerShape(999.dp))
                                .testTag("ai_generate_${student.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isGeneratingAi) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, color = TertiaryMint, modifier = Modifier.size(13.dp))
                                } else {
                                    Icon(Icons.Filled.Refresh, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(14.dp))
                                }
                                Text(if (isGeneratingAi) "..." else "Generar", style = MaterialTheme.typography.labelSmall, color = TertiaryMint)
                            }
                        }
                    }
                }
            }

            // Active Classroom Resources
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(PastelButterBg)
                    .border(1.dp, PastelButterBorder, RoundedCornerShape(18.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.Category, contentDescription = null, tint = SecondaryTerracotta, modifier = Modifier.size(17.dp))
                        Text("Recursos y Apoyos Activos", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(SecondaryTerracotta)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("${studentResources.size} activos hoy", style = MaterialTheme.typography.labelSmall, color = CardSurface)
                    }
                }

                studentResources.take(3).forEach { res ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CardSurface)
                            .border(1.dp, OutlineSubtle, RoundedCornerShape(14.dp))
                            .padding(8.dp),
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
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(if (res.fileType == "PNG") PastelBlueChip else PastelPeachChip),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (res.fileType == "PNG") Icons.Filled.Image else Icons.Filled.PictureAsPdf,
                                    contentDescription = null,
                                    tint = if (res.fileType == "PNG") PrimaryBlue else SecondaryTerracotta,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            Column {
                                Text(res.title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("${res.fileType} · ${res.locationBadge}", style = MaterialTheme.typography.labelSmall, color = OutlineLabel)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(PastelMintBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("En uso", style = MaterialTheme.typography.labelSmall, color = PastelMintText)
                        }
                    }
                }

                TextButton(onClick = onAssignMoreResources, modifier = Modifier.fillMaxWidth()) {
                    Text("+ Asignar más recursos", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = SecondaryTerracotta)
                }
            }

            // Realizar Evaluación Button
            Button(
                onClick = onEvaluateClick,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = CardSurface),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp)
                    .testTag("evaluate_${student.id}")
            ) {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    tint = Color(0xFFFDE8B3),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Realizar Evaluación",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProgressAreaRow(
    area: ProgressArea,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onRemove: () -> Unit
) {
    val dotColor = parseHexColor(area.colorHex)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .border(1.dp, OutlineSubtle, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(dotColor))
            Text(
                text = area.title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainer)
                    .clickable(onClick = onDecrement),
                contentAlignment = Alignment.Center
            ) {
                Text("-", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text(
                text = "${area.completedSteps}/${area.totalSteps}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SurfaceContainer)
                    .clickable(onClick = onIncrement),
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(dotColor.copy(alpha = 0.16f))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text("${area.percentage}%", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = dotColor)
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Quitar área", tint = OutlineLabel, modifier = Modifier.size(14.dp))
            }
        }
    }
}

@Composable
private fun FolderRow(
    title: String,
    subtitle: String,
    badgeText: String,
    docs: List<StudentDocument>,
    legacyCount: Int,
    bg: Color,
    border: Color,
    accent: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onUpload: () -> Unit,
    onPreviewDoc: (StudentDocument) -> Unit,
    onDeleteDoc: (StudentDocument) -> Unit,
    onClearLegacy: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onUpload)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CardSurface.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(accent)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(badgeText, style = MaterialTheme.typography.labelSmall, color = CardSurface)
                }
                IconButton(
                    onClick = onUpload,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(Icons.Filled.UploadFile, contentDescription = "Subir PDF", tint = accent, modifier = Modifier.size(16.dp))
                }
                if (docs.isEmpty() && legacyCount > 0) {
                    IconButton(
                        onClick = onClearLegacy,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CardSurface)
                    ) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Restar referencia", tint = ErrorRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        if (docs.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                docs.forEach { doc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardSurface)
                            .border(1.dp, border.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .clickable { onPreviewDoc(doc) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Filled.PictureAsPdf,
                                contentDescription = null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = doc.title,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${doc.sizeLabel} · ${doc.dateLabel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OutlineLabel
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onPreviewDoc(doc) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Filled.Visibility, contentDescription = "Ver PDF", tint = accent, modifier = Modifier.size(16.dp))
                            }
                            IconButton(
                                onClick = { onDeleteDoc(doc) },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Filled.DeleteOutline, contentDescription = "Eliminar PDF", tint = ErrorRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddWorkAreaDialog(
    studentName: String,
    onDismiss: () -> Unit,
    onSave: (title: String, totalSteps: Int, colorHex: String) -> Unit
) {
    val presets = listOf(
        "Autonomía en Comedor" to "#4BA2E8",
        "Regulación Auditiva" to "#4DB896",
        "Juego Compartido" to "#FF9D6C",
        "Lectoescritura Visual" to "#9581E6"
    )
    var title by remember { mutableStateOf("") }
    var stepsText by remember { mutableStateOf("10") }
    var selectedColor by remember { mutableStateOf("#4BA2E8") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = { Text("Nueva Área para $studentName", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Sugerencias TEA:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.forEach { (presetTitle, colorHex) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(SurfaceContainer)
                                .clickable {
                                    title = presetTitle
                                    selectedColor = colorHex
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(presetTitle, style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                        }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre del área de trabajo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stepsText,
                    onValueChange = { stepsText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Total de objetivos / pasos (1-50)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf("#4BA2E8", "#4DB896", "#FF9D6C", "#9581E6", "#F7CA55").forEach { hex ->
                        val active = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(parseHexColor(hex))
                                .border(if (active) 3.dp else 1.dp, if (active) TextPrimary else OutlineSubtle, CircleShape)
                                .clickable { selectedColor = hex }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val steps = stepsText.toIntOrNull()?.coerceIn(1, 50) ?: 10
                    if (title.isNotBlank()) onSave(title, steps, selectedColor)
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Añadir Área")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
