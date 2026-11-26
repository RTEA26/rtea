package com.example.ui.screens

import android.net.Uri
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Toys
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ImportedLocalFile
import com.example.data.LocalDocumentManager
import com.example.data.ResourceFolder
import com.example.data.ResourceItem
import com.example.data.Student
import com.example.ui.components.DocumentPreviewDialog
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.CardSurface
import com.example.ui.theme.ErrorRed
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
import java.io.File

@Composable
fun RecursosScreen(
    folders: List<ResourceFolder>,
    resources: List<ResourceItem>,
    students: List<Student>,
    onAddFolder: (name: String, subtitle: String, accentKey: String) -> Unit,
    onImportDeviceFile: (Uri, (ImportedLocalFile) -> Unit) -> Unit,
    onDiscardTempFile: (String) -> Unit,
    onAddResource: (
        folderId: String,
        title: String,
        description: String,
        fileType: String,
        localUri: String,
        sizeLabel: String,
        mimeType: String
    ) -> Unit,
    onUpdateAssignments: (ResourceItem, List<String>) -> Unit,
    onDeleteResource: (String) -> Unit,
    onOpenBiblioteca: () -> Unit,
    onEmitToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var formatFilter by remember { mutableStateOf("ALL") } // ALL, PDF, PNG, MP3, DOC
    var activeFolderId by remember(folders) {
        mutableStateOf(folders.firstOrNull()?.id ?: "folder_autonomia")
    }

    var showNewFolderDialog by remember { mutableStateOf(false) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var draftUploadUri by remember { mutableStateOf("") }
    var draftUploadType by remember { mutableStateOf("PDF") }
    var draftUploadTitle by remember { mutableStateOf("") }
    var draftUploadSize by remember { mutableStateOf("1.1 MB") }
    var draftUploadMime by remember { mutableStateOf("") }

    var assignTargetResource by remember { mutableStateOf<ResourceItem?>(null) }
    var previewTargetResource by remember { mutableStateOf<ResourceItem?>(null) }
    var deleteTargetResource by remember { mutableStateOf<ResourceItem?>(null) }

    val handleImportedFile: (ImportedLocalFile) -> Unit = { imported ->
        if (draftUploadUri.isNotBlank() && draftUploadUri != imported.localPath) {
            onDiscardTempFile(draftUploadUri)
        }
        draftUploadUri = imported.localPath
        draftUploadType = imported.fileType
        draftUploadTitle = imported.title
        draftUploadSize = imported.sizeLabel
        draftUploadMime = imported.mimeType
        showUploadDialog = true
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onImportDeviceFile(uri, handleImportedFile)
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            onImportDeviceFile(uri, handleImportedFile)
        }
    }

    val activeFolder = folders.firstOrNull { it.id == activeFolderId } ?: folders.firstOrNull()
    val filteredResources = remember(resources, searchQuery, formatFilter, activeFolderId) {
        val q = searchQuery.trim().lowercase()
        resources.filter { res ->
            val matchesFormat = formatFilter == "ALL" || res.fileType.equals(formatFilter, ignoreCase = true)
            val matchesSearch = q.isEmpty() ||
                res.title.lowercase().contains(q) ||
                res.description.lowercase().contains(q)
            val matchesFolder = if (q.isNotEmpty() || formatFilter != "ALL") true
            else activeFolderId.isBlank() || res.folderId == activeFolderId || res.folderId.isBlank()
            matchesFormat && matchesSearch && matchesFolder
        }
    }

    val pdfCount = remember(resources) { resources.count { it.fileType == "PDF" } }
    val imgCount = remember(resources) { resources.count { it.fileType == "PNG" } }
    val audioCount = remember(resources) { resources.count { it.fileType == "MP3" } }
    val docCount = remember(resources) { resources.count { it.fileType == "DOC" } }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header + Active Materials Pill + Link to Biblioteca R-TEA
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(PastelBlueBg)
                            .border(1.dp, PastelBlueBorder, RoundedCornerShape(999.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.FolderSpecial, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Text("Biblioteca Terapéutica", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TertiaryMint))
                        Text(
                            text = "${resources.size} materiales activos",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = TextMuted
                        )
                    }
                }

                Text(
                    text = "Apoyos visuales, pictogramas estructurados y fichas paso a paso para el aula y el hogar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                // Quick bridge to Biblioteca R-TEA screen
                Surface(
                    onClick = onOpenBiblioteca,
                    shape = RoundedCornerShape(18.dp),
                    color = PastelLavenderBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PastelLavenderChip, RoundedCornerShape(18.dp))
                        .testTag("open_biblioteca_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(PastelLavenderChip),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.MenuBook, contentDescription = null, tint = PastelLavenderText, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text("Biblioteca R-TEA (Cuentos y Papers)", style = MaterialTheme.typography.labelLarge, color = PastelLavenderText)
                                Text("Explora literatura adaptada y búsqueda IA", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = PastelLavenderText)
                    }
                }
            }
        }

        // Search + Format Filter Pills + "+ Subir Nuevo Material" CTA
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar pictograma, ficha, rutina...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = OutlineLabel) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormatChip("Todos (${resources.size})", formatFilter == "ALL", PrimaryBlue) { formatFilter = "ALL" }
                    FormatChip("PDFs ($pdfCount)", formatFilter == "PDF", SecondaryTerracotta) { formatFilter = "PDF" }
                    FormatChip("Pictos & Fotos ($imgCount)", formatFilter == "PNG", PrimaryBlue) { formatFilter = "PNG" }
                    FormatChip("Audio Guías ($audioCount)", formatFilter == "MP3", TertiaryMint) { formatFilter = "MP3" }
                    FormatChip("Archivos ($docCount)", formatFilter == "DOC", PastelLavenderText) { formatFilter = "DOC" }
                }

                Button(
                    onClick = {
                        draftUploadUri = ""
                        draftUploadType = "PDF"
                        draftUploadTitle = ""
                        draftUploadSize = "1.1 MB"
                        draftUploadMime = ""
                        showUploadDialog = true
                    },
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryTerracotta, contentColor = CardSurface),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("upload_new_material_button")
                ) {
                    Icon(Icons.Filled.CloudUpload, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("+ Subir Nuevo Material", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        // Carpetas Temáticas Carousel
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Folder, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        Text("Carpetas Temáticas", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = { showNewFolderDialog = true }) {
                            Icon(Icons.Filled.CreateNewFolder, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Nueva", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                        }
                        TextButton(onClick = { activeFolderId = "" }) {
                            Text("Ver todas", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    folders.forEach { folder ->
                        val count = resources.count { it.folderId == folder.id }
                        FolderTileCard(
                            folder = folder,
                            count = count,
                            active = activeFolderId == folder.id,
                            onClick = { activeFolderId = folder.id }
                        )
                    }
                }
            }
        }

        // Active Folder Banner + Dashed Upload Zone
        if (activeFolder != null) {
            item {
                val activeCount = resources.count { it.folderId == activeFolder.id }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(PastelPeachBg)
                            .border(1.dp, PastelPeachBorder, RoundedCornerShape(20.dp))
                            .padding(14.dp),
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
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PastelPeachChip),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.SelfImprovement, contentDescription = null, tint = SecondaryTerracotta)
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Carpeta: ${activeFolder.name}",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(SecondaryCoralVibrant)
                                            .padding(horizontal = 7.dp, vertical = 1.dp)
                                    ) {
                                        Text("$activeCount", style = MaterialTheme.typography.labelSmall, color = CardSurface)
                                    }
                                }
                                Text(activeFolder.subtitle, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                    }

                    // Dashed Quick Upload Zone
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(PastelBlueBg.copy(alpha = 0.65f))
                            .border(1.5.dp, PastelBlueBorder, RoundedCornerShape(22.dp))
                            .clickable { showUploadDialog = true }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(PastelBlueChip),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, tint = PrimaryBlue)
                        }
                        Text(
                            text = "Pulsa para añadir a ${activeFolder.name}",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Text(
                            text = "Sube PDF, pictogramas, imágenes, audio o archivos desde tu dispositivo (Máx. 25MB)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    imagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Pictos / Fotos", style = MaterialTheme.typography.labelSmall)
                            }
                            OutlinedButton(
                                onClick = {
                                    documentPickerLauncher.launch(arrayOf("application/pdf", "image/*", "audio/*", "*/*"))
                                },
                                shape = RoundedCornerShape(999.dp)
                            ) {
                                Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("PDF / Archivos", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        // Materials List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Materiales disponibles (${filteredResources.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text("Recientes", style = MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }

        items(filteredResources, key = { it.id }) { res ->
            ResourceCard(
                item = res,
                students = students,
                onAssignStudents = { assignTargetResource = res },
                onPreview = { previewTargetResource = res },
                onSharePrint = {
                    LocalDocumentManager.shareFileExternally(
                        context = context,
                        localPath = res.localUri,
                        mimeType = res.mimeType,
                        title = res.title,
                        fallbackText = "Material R-TEA: ${res.title}\n${res.description}\n\n${res.previewSummary}"
                    )
                    onEmitToast("Preparando ${res.title} para compartir/imprimir.")
                },
                onDelete = { deleteTargetResource = res }
            )
        }

        item {
            Spacer(Modifier.height(14.dp))
        }
    }

    // Create Folder Dialog
    if (showNewFolderDialog) {
        var name by remember { mutableStateOf("") }
        var subtitle by remember { mutableStateOf("") }
        var accent by remember { mutableStateOf("peach") }
        AlertDialog(
            onDismissRequest = { showNewFolderDialog = false },
            containerColor = CardSurface,
            title = { Text("Nueva Carpeta Temática", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre de la carpeta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Descripción corta") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("peach" to "Melocotón", "mint" to "Menta", "sky" to "Cielo").forEach { (key, label) ->
                            val isSel = accent == key
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSel) PrimaryBlue else SurfaceContainer)
                                    .clickable { accent = key }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(label, style = MaterialTheme.typography.labelSmall, color = if (isSel) CardSurface else TextPrimary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onAddFolder(name, subtitle, accent)
                            showNewFolderDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Crear") }
            },
            dismissButton = {
                TextButton(onClick = { showNewFolderDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // Upload Resource Dialog
    if (showUploadDialog) {
        var title by remember(draftUploadTitle) { mutableStateOf(draftUploadTitle) }
        var description by remember { mutableStateOf("") }
        var fileType by remember(draftUploadType) { mutableStateOf(draftUploadType) }
        val closeAndDiscard = {
            if (draftUploadUri.isNotBlank()) {
                onDiscardTempFile(draftUploadUri)
            }
            draftUploadUri = ""
            draftUploadMime = ""
            draftUploadSize = "1.1 MB"
            showUploadDialog = false
        }
        AlertDialog(
            onDismissRequest = closeAndDiscard,
            containerColor = CardSurface,
            title = { Text("Subir Material desde tu Dispositivo", style = MaterialTheme.typography.titleMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                documentPickerLauncher.launch(arrayOf("application/pdf", "image/*", "audio/*", "*/*"))
                            },
                            shape = RoundedCornerShape(999.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("PDF / Archivo", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                        OutlinedButton(
                            onClick = {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(999.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Pictograma", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                    }

                    if (draftUploadUri.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PastelMintBg)
                                .border(1.dp, PastelMintBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = null, tint = PastelMintText, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Archivo adjunto ($draftUploadSize)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PastelMintText
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("PDF" to "PDF", "PNG" to "Picto/Img", "MP3" to "Audio", "DOC" to "Archivo").forEach { (fmt, label) ->
                            val isSel = fileType == fmt
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(if (isSel) PrimaryBlue else SurfaceContainer)
                                    .clickable { fileType = fmt }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = if (isSel) CardSurface else TextPrimary)
                            }
                        }
                    }
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Nombre del archivo / material") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción terapéutica") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAddResource(
                                activeFolder?.id ?: "folder_autonomia",
                                title,
                                description,
                                fileType,
                                draftUploadUri,
                                draftUploadSize,
                                draftUploadMime
                            )
                            draftUploadUri = ""
                            draftUploadMime = ""
                            showUploadDialog = false
                        }
                    },
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryTerracotta)
                ) { Text("Subir y Sincronizar") }
            },
            dismissButton = {
                TextButton(onClick = closeAndDiscard) { Text("Cancelar") }
            }
        )
    }

    // Assign Students Multi-Select Dialog
    if (assignTargetResource != null) {
        val target = assignTargetResource!!
        var selectedIds by remember(target.id) { mutableStateOf(target.assignedStudentIds.toSet()) }
        AlertDialog(
            onDismissRequest = { assignTargetResource = null },
            containerColor = CardSurface,
            title = { Text("Asignar «${target.title}»", style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Selecciona los alumnos que recibirán este apoyo en su tablet:", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    students.forEach { st ->
                        val isSelected = st.id in selectedIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) PastelMintBg else SurfaceLow)
                                .border(1.dp, if (isSelected) PastelMintBorder else OutlineSubtle, RoundedCornerShape(14.dp))
                                .clickable {
                                    selectedIds = if (isSelected) selectedIds - st.id else selectedIds + st.id
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StudentAvatar(st.avatarIndex, st.customAvatarUri, st.fullName, size = 36.dp)
                                Column {
                                    Text(st.fullName, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
                                    Text("Nivel ${st.supportLevel}", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                            }
                            if (isSelected) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = TertiaryMint)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateAssignments(target, selectedIds.toList())
                        assignTargetResource = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) { Text("Guardar Asignación") }
            },
            dismissButton = {
                TextButton(onClick = { assignTargetResource = null }) { Text("Cancelar") }
            }
        )
    }

    // Preview Resource Dialog
    if (previewTargetResource != null) {
        val target = previewTargetResource!!
        DocumentPreviewDialog(
            title = target.title,
            subtitle = "${target.fileType} · ${target.sizeLabel} · ${target.locationBadge}",
            fileType = target.fileType,
            localPath = target.localUri,
            mimeType = target.mimeType,
            fallbackSummary = target.previewSummary.ifBlank { target.description },
            onDismiss = { previewTargetResource = null },
            onOpenFailed = {
                onEmitToast("No se encontró ninguna app para abrir este formato en el dispositivo.")
            }
        )
    }

    // Delete Confirmation Dialog
    if (deleteTargetResource != null) {
        val target = deleteTargetResource!!
        AlertDialog(
            onDismissRequest = { deleteTargetResource = null },
            containerColor = CardSurface,
            title = { Text("¿Eliminar «${target.title}»?", style = MaterialTheme.typography.titleMedium) },
            text = { Text("El material dejará de estar disponible en las tablets asignadas.", style = MaterialTheme.typography.bodySmall, color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteResource(target.id)
                        deleteTargetResource = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTargetResource = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FormatChip(label: String, selected: Boolean, accent: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) accent else CardSurface)
            .border(1.dp, if (selected) accent else OutlineSubtle, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (selected) CardSurface else TextPrimary
        )
    }
}

@Composable
private fun FolderTileCard(
    folder: ResourceFolder,
    count: Int,
    active: Boolean,
    onClick: () -> Unit
) {
    val (bg, chipBg, accent) = when (folder.accentKey) {
        "mint" -> Triple(PastelMintBg, PastelMintChip, TertiaryMint)
        "sky" -> Triple(PastelBlueBg, PastelBlueChip, PrimaryBlue)
        "lavender" -> Triple(PastelLavenderBg, PastelLavenderChip, PastelLavenderText)
        else -> Triple(PastelPeachBg, PastelPeachChip, SecondaryTerracotta)
    }
    val icon: ImageVector = when (folder.iconKey) {
        "spa" -> Icons.Filled.Spa
        "view_timeline" -> Icons.Filled.ViewTimeline
        "forum" -> Icons.Filled.Forum
        "toys" -> Icons.Filled.Toys
        else -> Icons.Filled.SelfImprovement
    }

    Column(
        modifier = Modifier
            .width(142.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(CardSurface)
            .border(if (active) 2.dp else 1.dp, if (active) accent else OutlineSubtle, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(chipBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(18.dp))
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (active) bg else SurfaceContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (active) "Activa" else "$count docs",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (active) accent else TextMuted
                )
            }
        }
        Text(
            text = folder.name,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (active) "$count materiales" else folder.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        LinearProgressIndicator(
            progress = { if (active) 1f else 0.6f },
            color = accent,
            trackColor = SurfaceContainer,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun ResourceCard(
    item: ResourceItem,
    students: List<Student>,
    onAssignStudents: () -> Unit,
    onPreview: () -> Unit,
    onSharePrint: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val (badgeBg, badgeColor, icon) = when (item.fileType) {
        "PNG" -> Triple(PastelBlueChip, PrimaryBlue, Icons.Filled.Image)
        "MP3" -> Triple(PastelMintChip, TertiaryMint, Icons.Filled.GraphicEq)
        "DOC" -> Triple(PastelLavenderChip, PastelLavenderText, Icons.Filled.Description)
        else -> Triple(PastelPeachChip, SecondaryTerracotta, Icons.Filled.PictureAsPdf)
    }
    val hasLocalImage = item.fileType == "PNG" && LocalDocumentManager.fileExists(item.localUri)
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
            .testTag("resource_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(badgeBg)
                ) {
                    if (hasLocalImage) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(File(item.localUri))
                                .crossfade(true)
                                .build(),
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clip(RoundedCornerShape(topStart = 8.dp))
                            .background(badgeColor)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(item.fileType, style = MaterialTheme.typography.labelSmall, color = CardSurface)
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("${item.sizeLabel} · ${item.dateLabel}", style = MaterialTheme.typography.labelSmall, color = OutlineLabel)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(PastelMintChip)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(item.statusBadge, style = MaterialTheme.typography.labelSmall, color = PastelMintText)
                        }
                    }
                }
            }

            // Assigned Students Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceLow)
                    .clickable(onClick = onAssignStudents)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Alumnos:", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    if (assignedStudents.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(CardSurface)
                                .border(1.dp, OutlineSubtle, RoundedCornerShape(999.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Sin asignar aún", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            assignedStudents.take(3).forEach { st ->
                                StudentAvatar(st.avatarIndex, st.customAvatarUri, st.fullName, size = 24.dp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(PastelBlueChip)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${assignedStudents.size} alumno${if (assignedStudents.size > 1) "s" else ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(14.dp))
                    Text("Editar", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryBlue)
                }
            }

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAssignStudents,
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (item.fileType == "MP3") TertiaryMint else SecondaryTerracotta,
                        contentColor = CardSurface
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Icon(Icons.Filled.PersonAddAlt1, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Asignar Alumnos", style = MaterialTheme.typography.labelSmall)
                }

                OutlinedButton(
                    onClick = onPreview,
                    shape = RoundedCornerShape(999.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Icon(
                        imageVector = if (item.fileType == "MP3") Icons.Filled.PlayArrow else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (item.fileType == "MP3") "Escuchar" else "Previsualizar",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBlue,
                        maxLines = 1
                    )
                }

                if (item.fileType == "PDF" || item.fileType == "DOC" || item.localUri.isNotBlank()) {
                    IconButton(
                        onClick = onSharePrint,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer)
                    ) {
                        Icon(Icons.Filled.Print, contentDescription = "Compartir/Imprimir", tint = TextPrimary, modifier = Modifier.size(18.dp))
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(PastelPeachBg)
                ) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = "Eliminar", tint = ErrorRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
