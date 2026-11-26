package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AiChatMessage
import com.example.data.ChatbotRole
import com.example.data.Student
import com.example.ui.theme.CanvasIvory
import com.example.ui.theme.CardSurface
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.OutlineSubtle
import com.example.ui.theme.PastelBlueBg
import com.example.ui.theme.PastelBlueBorder
import com.example.ui.theme.PastelBlueChip
import com.example.ui.theme.PastelBlueText
import com.example.ui.theme.PastelLavenderBg
import com.example.ui.theme.PastelLavenderBorder
import com.example.ui.theme.PastelLavenderChip
import com.example.ui.theme.PastelLavenderText
import com.example.ui.theme.PastelMintBg
import com.example.ui.theme.PastelMintBorder
import com.example.ui.theme.PastelMintText
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceLow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatbotScreen(
    students: List<Student>,
    messages: List<AiChatMessage>,
    isResponding: Boolean,
    activeRole: ChatbotRole,
    useFastModel: Boolean,
    focusedStudentId: String?,
    onSelectRole: (ChatbotRole) -> Unit,
    onToggleFastModel: () -> Unit,
    onSelectStudent: (String?) -> Unit,
    onSendMessage: (String) -> Unit,
    onRetryMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val focusedStudent = remember(students, focusedStudentId) {
        students.firstOrNull { it.id == focusedStudentId }
    }

    LaunchedEffect(messages.size, messages.lastOrNull()?.text?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasIvory)
    ) {
        // Dedicated Chatbot Header Bar
        Surface(
            color = CardSurface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("chatbot_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
                                tint = TextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PastelBlueChip, PastelLavenderChip))
                                )
                                .border(1.dp, PastelLavenderBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = PastelLavenderText,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(start = 6.dp)) {
                            Text(
                                text = "Chatbot Gemini",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = activeRole.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = PastelLavenderText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (useFastModel) PastelMintBg else PastelBlueBg)
                                .border(
                                    1.dp,
                                    if (useFastModel) PastelMintBorder else PastelBlueBorder,
                                    RoundedCornerShape(999.dp)
                                )
                                .clickable(enabled = !isResponding, onClick = onToggleFastModel)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("chatbot_model_toggle"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (useFastModel) Icons.Filled.Bolt else Icons.Filled.Psychology,
                                contentDescription = "Cambiar modo Gemini",
                                tint = if (useFastModel) PastelMintText else PastelBlueText,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (useFastModel) "Flash" else "3.8 Pro",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (useFastModel) PastelMintText else PastelBlueText
                            )
                        }

                        if (messages.isNotEmpty()) {
                            IconButton(
                                onClick = onClearChat,
                                modifier = Modifier
                                    .size(48.dp)
                                    .testTag("chatbot_clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DeleteSweep,
                                    contentDescription = "Nueva conversación",
                                    tint = TextMuted
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = OutlineSubtle)
            }
        }

        // Role + Student Context Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceLow)
                .padding(top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Specialized Roles (systemInstruction)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChatbotRole.entries.forEach { role ->
                    val selected = role == activeRole
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (selected) PastelLavenderChip else CardSurface)
                            .border(
                                1.dp,
                                if (selected) PastelLavenderText.copy(alpha = 0.45f) else OutlineSubtle,
                                RoundedCornerShape(999.dp)
                            )
                            .clickable(enabled = !isResponding) { onSelectRole(role) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("chatbot_role_${role.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = roleIcon(role),
                            contentDescription = null,
                            tint = if (selected) PastelLavenderText else TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = role.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                            ),
                            color = if (selected) PastelLavenderText else TextPrimary
                        )
                    }
                }
            }

            // Student Context Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ChatbotContextChip(
                    label = "Todo el aula",
                    selected = focusedStudentId == null,
                    onClick = { onSelectStudent(null) }
                )
                students.forEach { st ->
                    ChatbotContextChip(
                        label = st.shortDisplayName,
                        selected = focusedStudentId == st.id,
                        onClick = { onSelectStudent(st.id) }
                    )
                }
            }
        }
        HorizontalDivider(color = OutlineSubtle)

        // Scrollable Multi-Turn Conversation Thread
        if (messages.isEmpty()) {
            val targetName = focusedStudent?.shortDisplayName ?: "el aula"
            val starterPrompts = remember(activeRole, targetName) {
                when (activeRole) {
                    ChatbotRole.GEMINI_GENERAL -> listOf(
                        "Consulta libre" to "Hola Gemini, necesito ideas prácticas para organizar la jornada escolar de hoy.",
                        "Resumir o redactar" to "Ayúdame a redactar un aviso breve, amable y claro para las familias de mi clase.",
                        "Actividad creativa" to "Propón una dinámica de grupo inclusiva y tranquila de 15 minutos para el aula.",
                        "Apoyo con alumno" to "Dame ideas adaptadas al perfil de $targetName para motivarle hoy."
                    )
                    ChatbotRole.CONSULTOR_TEA -> listOf(
                        "Anticipar un cambio" to "¿Cómo anticipar un cambio de actividad o profesor hoy con $targetName?",
                        "Adaptar una tarea" to "Dame 3 ideas para adaptar una ficha escolar usando los intereses de $targetName.",
                        "Transiciones suaves" to "¿Qué rutina visual breve recomiendas para pasar del recreo al aula con $targetName?",
                        "Refuerzo positivo" to "¿Qué refuerzo positivo funciona mejor con el perfil de $targetName?"
                    )
                    ChatbotRole.SENSORIAL -> listOf(
                        "Calma tras ruido" to "$targetName muestra sobrecarga auditiva, ¿qué pasos inmediatos puedo aplicar?",
                        "Rincón de calma" to "¿Qué 4 elementos esenciales debería tener el rincón de calma para $targetName?",
                        "Señales tempranas" to "¿Qué señales previas de saturación sensorial conviene observar en $targetName?",
                        "Pausa activa" to "Sugiéreme una pausa sensorial de 3 minutos antes de empezar trabajo en mesa con $targetName."
                    )
                    ChatbotRole.COMUNICACION -> listOf(
                        "Tablero PECS" to "¿Cómo estructuro un tablero PECS inicial de peticiones para $targetName?",
                        "Guion social" to "Redáctame un guion social corto con frases claras para $targetName.",
                        "Pedir ayuda" to "¿Qué apoyo visual puedo usar para que $targetName pida ayuda o descanso sin frustrarse?",
                        "Agenda visual" to "Propón una secuencia visual de 5 pictogramas para la mañana de $targetName."
                    )
                    ChatbotRole.FAMILIA -> listOf(
                        "Resumen del día" to "Redacta un mensaje cercano para la familia contando un avance de $targetName hoy.",
                        "Coordinar rutina" to "¿Qué rutina sencilla podemos coordinar entre aula y casa para $targetName?",
                        "Comunicar dificultad" to "Ayúdame a explicar con tacto y apoyo positivo un momento difícil de $targetName hoy.",
                        "Celebrar logro" to "Escribe una nota breve para felicitar a la familia por el progreso de $targetName."
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(listOf(PastelBlueBg, PastelLavenderBg))
                            )
                            .border(1.dp, PastelLavenderBorder, RoundedCornerShape(24.dp))
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(CardSurface)
                                    .border(1.dp, PastelLavenderBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Rol activo: ${activeRole.label}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = activeRole.shortDescription,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                        Text(
                            text = if (focusedStudent != null) {
                                "Contexto conectado a ${focusedStudent.fullName} (${focusedStudent.classroom}, Nivel ${focusedStudent.supportLevel}). Haz una pregunta y sigue profundizando en el mismo hilo."
                            } else {
                                "Conversación multi-turno conectada a tus ${students.size} alumnos. Selecciona un rol arriba o empieza con una sugerencia."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }

                item {
                    Text(
                        text = "Sugerencias para empezar",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                }

                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        starterPrompts.forEach { (title, prompt) ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(CardSurface)
                                    .border(1.dp, OutlineSubtle, RoundedCornerShape(18.dp))
                                    .clickable(enabled = !isResponding) { onSendMessage(prompt) }
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryBlue
                                )
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("chatbot_messages_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatbotMessageBubble(
                        message = msg,
                        onRetry = {
                            if (msg.retryPrompt.isNotBlank()) {
                                onRetryMessage(msg.retryPrompt)
                            }
                        }
                    )
                }
            }
        }

        // Bottom Composer Bar
        Surface(
            color = CardSurface,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                HorizontalDivider(color = OutlineSubtle)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = {
                            Text(
                                text = if (focusedStudent != null) {
                                    "Pregunta sobre ${focusedStudent.shortDisplayName} (${activeRole.label})..."
                                } else {
                                    "Escribe tu consulta para ${activeRole.label}..."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        shape = RoundedCornerShape(22.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = OutlineSubtle,
                            focusedContainerColor = SurfaceLow,
                            unfocusedContainerColor = SurfaceLow
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (input.isNotBlank() && !isResponding) {
                                    onSendMessage(input)
                                    input = ""
                                }
                            }
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chatbot_input")
                    )

                    val canSend = input.isNotBlank() && !isResponding
                    IconButton(
                        onClick = {
                            if (canSend) {
                                onSendMessage(input)
                                input = ""
                            }
                        },
                        enabled = canSend,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (canSend) PrimaryBlue else SurfaceContainer)
                            .testTag("chatbot_send_button")
                    ) {
                        if (isResponding) {
                            CircularProgressIndicator(
                                color = PrimaryBlue,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar mensaje",
                                tint = if (canSend) CardSurface else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun roleIcon(role: ChatbotRole): ImageVector = when (role) {
    ChatbotRole.GEMINI_GENERAL -> Icons.Filled.AutoAwesome
    ChatbotRole.CONSULTOR_TEA -> Icons.Filled.School
    ChatbotRole.SENSORIAL -> Icons.Filled.Spa
    ChatbotRole.COMUNICACION -> Icons.Filled.RecordVoiceOver
    ChatbotRole.FAMILIA -> Icons.Filled.FamilyRestroom
}

@Composable
private fun ChatbotContextChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) PrimaryBlue else CardSurface)
            .border(1.dp, if (selected) PrimaryBlue else OutlineSubtle, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (selected) CardSurface else TextPrimary,
            maxLines = 1
        )
    }
}

@Composable
private fun ChatbotMessageBubble(
    message: AiChatMessage,
    onRetry: () -> Unit
) {
    val isUser = message.isUser
    val bubbleBg = when {
        isUser -> PrimaryBlue
        message.isError -> ErrorContainer
        else -> CardSurface
    }
    val bubbleBorder = when {
        isUser -> PrimaryBlue
        message.isError -> ErrorRed.copy(alpha = 0.35f)
        else -> PastelLavenderBorder
    }
    val textColor = when {
        isUser -> CardSurface
        message.isError -> ErrorRed
        else -> TextPrimary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(bubbleBg)
                .border(1.dp, bubbleBorder, RoundedCornerShape(18.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (!isUser) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = if (message.isError) ErrorRed else PastelLavenderText,
                        modifier = Modifier.size(13.dp)
                    )
                }
                val metaText = buildString {
                    append(if (isUser) "Tú" else "Gemini")
                    if (message.roleLabel.isNotBlank() && !isUser) {
                        append(" · ${message.roleLabel}")
                    }
                    if (message.studentLabel.isNotBlank()) {
                        append(" · ${message.studentLabel}")
                    }
                }
                Text(
                    text = metaText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isUser) CardSurface.copy(alpha = 0.85f)
                    else if (message.isError) ErrorRed
                    else PastelLavenderText
                )
            }

            if (message.isStreaming && message.text.isBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = PastelLavenderText,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Escribiendo respuesta...",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            } else {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }

            if (message.isError && message.retryPrompt.isNotBlank()) {
                OutlinedButton(
                    onClick = onRetry,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(
                        text = "Reintentar",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ErrorRed
                    )
                }
            }
        }
    }
}
