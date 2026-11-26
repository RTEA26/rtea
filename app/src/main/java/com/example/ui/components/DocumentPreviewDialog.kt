package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.LocalDocumentManager
import com.example.ui.theme.CardSurface
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PastelBlueBg
import com.example.ui.theme.PastelBlueBorder
import com.example.ui.theme.PastelMintBg
import com.example.ui.theme.PastelMintBorder
import com.example.ui.theme.PastelMintText
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceLow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import java.io.File

@Composable
fun DocumentPreviewDialog(
    title: String,
    subtitle: String,
    fileType: String,
    localPath: String,
    mimeType: String,
    fallbackSummary: String,
    onDismiss: () -> Unit,
    onOpenFailed: () -> Unit = {}
) {
    val context = LocalContext.current
    val hasLocalFile = remember(localPath) { LocalDocumentManager.fileExists(localPath) }
    var pdfPages by remember(localPath) { mutableStateOf<List<Bitmap>>(emptyList()) }
    var loadingPdf by remember(localPath) { mutableStateOf(false) }

    LaunchedEffect(localPath, fileType, hasLocalFile) {
        if (hasLocalFile && fileType == "PDF") {
            loadingPdf = true
            pdfPages = LocalDocumentManager.renderPdfPreviewPages(localPath, maxPages = 3)
            loadingPdf = false
        } else {
            pdfPages = emptyList()
            loadingPdf = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(PastelBlueBg)
                        .border(1.dp, PastelBlueBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = PrimaryBlue
                    )
                }

                if (hasLocalFile) {
                    when (fileType) {
                        "PNG" -> {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(File(localPath))
                                    .crossfade(true)
                                    .build(),
                                contentDescription = title,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 140.dp, max = 260.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceLow)
                                    .border(1.dp, OutlineSubtle, RoundedCornerShape(16.dp))
                            )
                        }
                        "PDF" -> {
                            if (loadingPdf) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(color = PrimaryBlue, modifier = Modifier.size(28.dp))
                                        Text("Renderizando vista previa PDF...", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                    }
                                }
                            } else if (pdfPages.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    pdfPages.forEachIndexed { idx, bmp ->
                                        val ratio = if (bmp.height > 0) bmp.width.toFloat() / bmp.height.toFloat() else 0.71f
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                "Página ${idx + 1}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted
                                            )
                                            Image(
                                                bitmap = bmp.asImageBitmap(),
                                                contentDescription = "$title - Página ${idx + 1}",
                                                contentScale = ContentScale.FillWidth,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(ratio)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                                            )
                                        }
                                    }
                                }
                            } else {
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
                                    Icon(Icons.Filled.PictureAsPdf, contentDescription = null, tint = PastelMintText)
                                    Text(
                                        "PDF listo en el dispositivo. Pulsa «Abrir» para visualizarlo completo.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary
                                    )
                                }
                            }
                        }
                        else -> {
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
                                Icon(
                                    imageVector = if (fileType == "MP3") Icons.Filled.AudioFile else Icons.Filled.Description,
                                    contentDescription = null,
                                    tint = PastelMintText
                                )
                                Text(
                                    "Archivo guardado en tu dispositivo. Puedes abrirlo o compartirlo directamente.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    if (fallbackSummary.isNotBlank()) {
                        Text(
                            text = fallbackSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val ok = LocalDocumentManager.openFileExternally(context, localPath, mimeType)
                                if (!ok) onOpenFailed()
                            },
                            shape = RoundedCornerShape(999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Abrir", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                LocalDocumentManager.shareFileExternally(
                                    context = context,
                                    localPath = localPath,
                                    mimeType = mimeType,
                                    title = title,
                                    fallbackText = "$title\n$fallbackSummary"
                                )
                            },
                            shape = RoundedCornerShape(999.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Compartir", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                } else {
                    Text(
                        text = fallbackSummary.ifBlank { "Material de referencia sin archivo adjunto todavía." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
