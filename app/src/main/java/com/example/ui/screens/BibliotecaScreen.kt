package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GroundedSearchResult
import com.example.data.LibraryItem
import com.example.data.Student
import com.example.data.UiState
import com.example.ui.components.StudentAvatar
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BibliotecaScreen(
    libraryItems: List<LibraryItem>,
    students: List<Student>,
    groundedSearchState: UiState<GroundedSearchResult>?,
    onBack: () -> Unit,
    onAddLibraryItem: (title: String, author: String, materialType: String, reviewNote: String) -> Unit,
    onUpdateAssignments: (LibraryItem, List<String>, List<String>) -> Unit,
    onDeleteLibraryItem: (String) -> Unit,
    onSearchEvidence: (String) -> Unit,
    onClearSearch: () -> Unit,
    onEmitToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    var searchQuery by remember { mutableStateOf("") }
    var categoryFilter by remember { mutableStateOf("ALL") } // ALL, cuento, libro, paper
    var showCreatePanel by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("") }
    var newAuthor by remember { mutableStateOf("") }
    var newType by remember { mutableStateOf("cuento") }
    var newReview by remember { mutableStateOf("") }

    var previewItem by remember { mutableStateOf<LibraryItem?>(null) }
    var assignItem by remember { mutableStateOf<LibraryItem?>(null) }
    var deleteItem by remember { mutableStateOf<LibraryItem?>(null) }

    val filteredItems = remember(libraryItems, searchQuery, categoryFilter) {
        val q = searchQuery.trim().lowercase()
        libraryItems.filter { item ->
            val matchesCat = categoryFilter == "ALL" || item.materialType == categoryFilter
            val matchesSearch = q.isEmpty() ||
                item.title.lowercase().contains(q) ||
                item.author.lowercase().contains(q) ||
                item.reviewNote.lowercase().contains(q)
            matchesCat && matchesSearch
        }
    }

    val cuentosCount = remember(libraryItems) { libraryItems.count { it.materialType == "cuento" } }
    val librosCount = remember(libraryItems) { libraryItems.count { it.materialType == "libro" } }
    val papersCount = remember(libraryItems) { libraryItems.count { it.materialType == "paper" } }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Row + "+ Nuevo Título" CTA
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Text(
                                text = "REPOSITORIO PEDAGÓGICO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp),
                                color = PrimaryBlue
                            )
                        }
                        Text(
                            text = "Biblioteca R-TEA",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = { showCreatePanel = !showCreatePanel },
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = CardSurface),
                        modifier = Modifier.testTag("new_library_title_button")
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Nuevo Título", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Text(
                    text = "Cuentos adaptados, literatura especializada y artículos científicos seleccionados para el aula y terapia neuroinclusiva con espectro armónico y calmado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Volver a Recursos / Inicio", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // Expandable "+ Nuevo Título" Creation Panel
        item {
            AnimatedVisibility(visible = showCreatePanel) {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = PastelBlueBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PastelBlueBorder, RoundedCornerShape(22.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Añadir Nuevo Título a la Biblioteca", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("cuento" to "Cuento TEA", "libro" to "Manual/Libro", "paper" to "Artículo").forEach { (typeKey, label) ->
                                val sel = newType == typeKey
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(if (sel) PrimaryBlue else CardSurface)
                                        .clickable { newType = typeKey }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(label, style = MaterialTheme.typography.labelSmall, color = if (sel) CardSurface else TextPrimary)
                                }
                            }
                        }
                        OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Título del libro, cuento o estudio") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newAuthor, onValueChange = { newAuthor = it }, label = { Text("Autor / Revista") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newReview, onValueChange = { newReview = it }, label = { Text("Uso pedagógico o resumen breve") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { showCreatePanel = false }, modifier = Modifier.weight(1f)) {
                                Text("Cancelar")
                            }
                            Button(
                                onClick = {
                                    if (newTitle.isNotBlank()) {
                                        onAddLibraryItem(
                                            newTitle,
                                            newAuthor.ifBlank { "Equipo R-TEA" },
                                            newType,
                                            newReview
                                        )
                                        newTitle = ""
                                        newAuthor = ""
                                        newReview = ""
                                        showCreatePanel = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Guardar Título")
                            }
                        }
                    }
                }
            }
        }

        // Search Bar + AI Evidence Search Trigger + Category Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por título, autor o tema pedagógico...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = OutlineLabel) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                onClearSearch()
                            }) {
                                Icon(Icons.Filled.Close, contentDescription = "Limpiar")
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

                // Grounded Google Search Pedagogical AI Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PastelMintBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PastelMintBorder, RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(16.dp))
                                Text("Buscador Pedagógico IA (Evidencia TEA)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = PastelMintText)
                            }
                            Button(
                                onClick = {
                                    val q = searchQuery.ifBlank { "estrategias visuales para transiciones TEA en primaria" }
                                    onSearchEvidence(q)
                                },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TertiaryMint),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp).testTag("biblioteca_ai_search_button")
                            ) {
                                Text("Consultar IA", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        when (groundedSearchState) {
                            is UiState.Loading -> {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CircularProgressIndicator(strokeWidth = 2.dp, color = TertiaryMint, modifier = Modifier.size(16.dp))
                                    Text("Buscando evidencia educativa actualizada...", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                            }
                            is UiState.Success -> {
                                val res = groundedSearchState.data
                                Text(res.summary, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                                if (res.sources.isNotEmpty()) {
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        res.sources.forEach { (title, url) ->
                                            Row(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(999.dp))
                                                    .background(CardSurface)
                                                    .border(1.dp, PastelMintBorder, RoundedCornerShape(999.dp))
                                                    .clickable { uriHandler.openUri(url) }
                                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = TertiaryMint, modifier = Modifier.size(12.dp))
                                                Text(title, style = MaterialTheme.typography.labelSmall, color = TertiaryMint, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            }
                                        }
                                    }
                                }
                            }
                            is UiState.Error -> {
                                Text(groundedSearchState.message, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                            }
                            null -> {
                                Text(
                                    "Escribe un tema arriba o pulsa «Consultar IA» para buscar literatura y guías basadas en evidencia.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryPill("Todos (${libraryItems.size})", categoryFilter == "ALL", Icons.Filled.Bookmark) { categoryFilter = "ALL" }
                    CategoryPill("Cuentos TEA ($cuentosCount)", categoryFilter == "cuento", Icons.Filled.MenuBook) { categoryFilter = "cuento" }
                    CategoryPill("Libros Teóricos ($librosCount)", categoryFilter == "libro", Icons.Filled.Psychology) { categoryFilter = "libro" }
                    CategoryPill("Artículos ($papersCount)", categoryFilter == "paper", Icons.Filled.Science) { categoryFilter = "paper" }
                }
            }
        }

        // Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Materiales Disponibles", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PastelBlueBg)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("${filteredItems.size} ítems", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                    }
                }
                Text("Recientes", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }

        // Library Cards
        items(filteredItems, key = { it.id }) { item ->
            LibraryItemCard(
                item = item,
                students = students,
                onPreview = { previewItem = item },
                onAssign = { assignItem = item },
                onShareOrDownload = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, item.title)
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${item.title} — ${item.author}\n${item.pedagogicalTitle}: ${item.reviewNote}"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Compartir material de Biblioteca R-TEA"))
                    onEmitToast("Compartiendo ficha de «${item.title}».")
                },
                onDelete = { deleteItem = item }
            )
        }

        // Consejo de Mediación Lectora TEA
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(PastelPeachBg)
                    .border(1.dp, PastelPeachBorder, RoundedCornerShape(22.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PastelPeachChip),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = SecondaryTerracotta, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text("Consejo de Mediación Lectora TEA", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = PastelPeachText)
                    Text(
                        "Usa anticipadores de lectura y permite tiempos de pausa sensorial antes de cada página con ilustraciones complejas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
        }
    }

    // Preview Modal
    if (previewItem != null) {
        val target = previewItem!!
        AlertDialog(
            onDismissRequest = { previewItem = null },
            containerColor = CardSurface,
            title = { Text(target.title, style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${target.author} · ${target.metaInfo}", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceLow)
                            .padding(12.dp)
                    ) {
                        Text("${target.pedagogicalTitle}: ${target.reviewNote}", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    }
                    if (target.usageMoments.isNotEmpty()) {
                        Text("Momentos sugeridos: ${target.usageMoments.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { previewItem = null }) { Text("Cerrar") }
            }
        )
    }

    // Assign Student & Usage Moments Modal
    if (assignItem != null) {
        val target = assignItem!!
        var selectedIds by remember(target.id) { mutableStateOf(target.assignedStudentIds.toSet()) }
        var selectedMoments by remember(target.id) { mutableStateOf(target.usageMoments.toSet()) }
        val momentOptions = listOf("Asamblea matutina", "Rincón de la calma", "Anticipación de recreo", "Lectura compartida")

        AlertDialog(
            onDismissRequest = { assignItem = null },
            containerColor = CardSurface,
            title = { Text("Asignar «${target.title}»", style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Alumnos destinatarios:", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    students.forEach { st ->
                        val sel = st.id in selectedIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (sel) PastelBlueBg else SurfaceLow)
                                .clickable { selectedIds = if (sel) selectedIds - st.id else selectedIds + st.id }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StudentAvatar(st.avatarIndex, st.customAvatarUri, st.fullName, size = 32.dp)
                                Text(st.fullName, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                            }
                            if (sel) Icon(Icons.Filled.Check, contentDescription = null, tint = PrimaryBlue)
                        }
                    }

                    Text("Momentos de uso recomendados:", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        momentOptions.forEach { m ->
                            val sel = m in selectedMoments
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (sel) PrimaryBlue else SurfaceContainer)
                                    .clickable { selectedMoments = if (sel) selectedMoments - m else selectedMoments + m }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(m, style = MaterialTheme.typography.labelSmall, color = if (sel) CardSurface else TextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateAssignments(target, selectedIds.toList(), selectedMoments.toList())
                        assignItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Sincronizar") }
            },
            dismissButton = {
                TextButton(onClick = { assignItem = null }) { Text("Cancelar") }
            }
        )
    }

    // Delete Confirmation Dialog
    if (deleteItem != null) {
        val target = deleteItem!!
        AlertDialog(
            onDismissRequest = { deleteItem = null },
            containerColor = CardSurface,
            title = { Text("¿Eliminar «${target.title}»?", style = MaterialTheme.typography.titleMedium) },
            text = { Text("Se retirará este título del repositorio pedagógico.", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteLibraryItem(target.id)
                        deleteItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { deleteItem = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun CategoryPill(
    label: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) PrimaryBlue else CardSurface)
            .border(1.dp, if (selected) PrimaryBlue else OutlineSubtle, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) CardSurface else TextMuted, modifier = Modifier.size(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (selected) CardSurface else TextPrimary
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LibraryItemCard(
    item: LibraryItem,
    students: List<Student>,
    onPreview: () -> Unit,
    onAssign: () -> Unit,
    onShareOrDownload: () -> Unit,
    onDelete: () -> Unit
) {
    val (coverBg, badgeBg, accent, typeLabel, icon) = when (item.materialType) {
        "libro" -> LibraryPalette(PastelPeachBg, PastelPeachChip, SecondaryTerracotta, "Manual", Icons.Filled.MenuBook)
        "paper" -> LibraryPalette(PastelMintBg, PastelMintChip, TertiaryMint, "Paper", Icons.Filled.Science)
        else -> LibraryPalette(PastelBlueBg, PastelBlueChip, PrimaryBlue, "Cuento", Icons.Filled.AutoAwesome)
    }

    val assignedStudents = remember(item.assignedStudentIds, students) {
        students.filter { it.id in item.assignedStudentIds }
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = CardSurface,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, OutlineSubtle, RoundedCornerShape(24.dp))
            .testTag("library_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 76.dp, height = 104.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(coverBg)
                        .border(1.dp, badgeBg, RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(accent)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(typeLabel, style = MaterialTheme.typography.labelSmall, color = CardSurface, fontSize = 10.sp)
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.author,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = accent
                            )
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Filled.DeleteOutline, contentDescription = "Eliminar", tint = OutlineLabel, modifier = Modifier.size(18.dp))
                        }
                    }

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        item.tags.forEachIndexed { idx, tag ->
                            val chipColor = if (idx == item.tags.lastIndex && item.materialType == "cuento") PastelMintChip else SurfaceContainer
                            val textColor = if (idx == item.tags.lastIndex && item.materialType == "cuento") PastelMintText else TextPrimary
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(chipColor)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(tag, style = MaterialTheme.typography.labelSmall, color = textColor)
                            }
                        }
                    }

                    if (item.metaInfo.isNotBlank()) {
                        Text(item.metaInfo, style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }
            }

            // Pedagogical Review Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceLow)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = accent, modifier = Modifier.size(15.dp))
                    Text("${item.pedagogicalTitle}:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                }
                Text(item.reviewNote, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }

            // Assigned Students Bar (when assigned)
            if (assignedStudents.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PastelBlueBg)
                        .border(1.dp, PastelBlueBorder, RoundedCornerShape(16.dp))
                        .clickable(onClick = onAssign)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            assignedStudents.take(3).forEach { st ->
                                StudentAvatar(st.avatarIndex, st.customAvatarUri, st.fullName, size = 28.dp)
                            }
                        }
                        Column {
                            Text("${assignedStudents.size} alumnos asignados", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            Text(
                                text = assignedStudents.joinToString(" · ") { it.fullName } + " (Sincronizado)",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryBlue,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(CardSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(12.dp))
                        Text("Gestionar", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    }
                }
            }

            // Card Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.materialType == "cuento") {
                    OutlinedButton(
                        onClick = onPreview,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Filled.Visibility, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ver Ficha", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    }
                    Button(
                        onClick = onAssign,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PastelBlueChip, contentColor = PrimaryBlue),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Filled.PersonAddAlt1, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Asignar Alumno", style = MaterialTheme.typography.labelSmall)
                    }
                } else {
                    OutlinedButton(
                        onClick = onPreview,
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(
                            imageVector = if (item.materialType == "paper") Icons.Filled.Share else Icons.Filled.Description,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(if (item.materialType == "paper") "Ver Resumen" else "Notas Internas", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    }
                    Button(
                        onClick = onShareOrDownload,
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.materialType == "paper") PastelMintChip else PastelPeachChip,
                            contentColor = if (item.materialType == "paper") PastelMintText else PastelPeachText
                        ),
                        modifier = Modifier.weight(1f).height(42.dp)
                    ) {
                        Icon(Icons.Filled.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (item.materialType == "paper") "Compartir Paper" else "Compartir Guía", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

private data class LibraryPalette(
    val coverBg: Color,
    val badgeBg: Color,
    val accent: Color,
    val typeLabel: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
