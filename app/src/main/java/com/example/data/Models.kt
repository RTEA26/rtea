package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatTodaySpanishHeader(now: Date = Date()): String {
    val locale = Locale.forLanguageTag("es-ES")
    val day = SimpleDateFormat("d", locale).format(now)
    val monthRaw = SimpleDateFormat("MMMM", locale).format(now)
    val month = monthRaw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    return "Hoy, $day de $month"
}

fun formatTodaySpanishWithShift(now: Date = Date()): String {
    val cal = Calendar.getInstance().apply { time = now }
    val shift = if (cal.get(Calendar.HOUR_OF_DAY) < 14) "Turno Mañana" else "Turno Tarde"
    return "${formatTodaySpanishHeader(now)} • $shift"
}

@Keep
@IgnoreExtraProperties
data class ProgressArea(
    val id: String = "",
    val title: String = "",
    val completedSteps: Int = 0,
    val totalSteps: Int = 10,
    val colorHex: String = "#2C628E"
) {
    val percentage: Int
        get() = if (totalSteps <= 0) 0 else ((completedSteps.toFloat() / totalSteps) * 100f).toInt().coerceIn(0, 100)
}

@Keep
@IgnoreExtraProperties
data class StudentDocument(
    val id: String = "",
    val title: String = "",
    val localPath: String = "",
    val sizeLabel: String = "",
    val dateLabel: String = "",
    val mimeType: String = "application/pdf"
)

@Keep
@IgnoreExtraProperties
data class Student(
    @DocumentId val id: String = "",
    val fullName: String = "",
    val preferredName: String = "",
    val classroom: String = "Aula 3-B · Primaria Integrada",
    val avatarIndex: Int = 1,
    val customAvatarUri: String = "",
    val supportLevel: Int = 1,
    val interests: List<String> = emptyList(),
    val commSystem: String = "PECS / Cuaderno de Pictogramas Digital",
    val sensoryNotes: String = "",
    val lastSessionLabel: String = "Hoy, 10:30 h",
    val progressAreas: List<ProgressArea> = emptyList(),
    val mainReportDocsCount: Int = 0,
    val programDocsCount: Int = 0,
    val mainReportDocs: List<StudentDocument> = emptyList(),
    val programDocs: List<StudentDocument> = emptyList(),
    val aiReportSummary: String = "",
    val activeResourceIds: List<String> = emptyList(),
    val evalTemplate: String = "Sensorial",
    val evalReinforcement: String = "",
    val evalMedal: String = "GOLD",
    val evalScore: Int = 4,
    val evalDimensions: List<EvaluationDimension> = emptyList(),
    val evalNotes: String = "",
    val evalShareFamily: Boolean = true,
    val evalRecordHistory: Boolean = true,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val globalPercentage: Int
        get() = if (progressAreas.isEmpty()) 0 else progressAreas.sumOf { it.percentage } / progressAreas.size

    val shortDisplayName: String
        get() = preferredName.ifBlank { fullName.trim().split(" ").firstOrNull().orEmpty().ifBlank { "Alumno" } }

    val effectiveEvalDimensions: List<EvaluationDimension>
        get() = evalDimensions.ifEmpty { defaultEvaluationDimensions(evalTemplate.ifBlank { "Sensorial" }) }

    val effectiveReinforcement: String
        get() = evalReinforcement.ifBlank {
            "¡$shortDisplayName logró la transición de aula sin angustia y con gran calma!"
        }

    val initials: String
        get() {
            val parts = fullName.trim().split(" ").filter { it.isNotBlank() }
            return when {
                parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
                parts.size == 1 -> parts[0].take(2).uppercase()
                else -> "AL"
            }
        }
}

fun defaultEvaluationDimensions(category: String): List<EvaluationDimension> = when (category) {
    "Social / Juego" -> listOf(
        EvaluationDimension("d_turn", "Interacción y Turno", "Compartió piezas y respetó espera", 4, "#FF8A5C"),
        EvaluationDimension("d_eye", "Atención Conjunta", "Respondió a gestos e indicaciones", 4, "#3F82B8"),
        EvaluationDimension("d_flex", "Flexibilidad en Juego", "Aceptó variar las reglas con calma", 4, "#7C5CC7")
    )
    "Académico" -> listOf(
        EvaluationDimension("d_focus", "Comprensión de Consigna", "Siguió apoyo visual paso a paso", 4, "#3F82B8"),
        EvaluationDimension("d_auton", "Autonomía en Mesa", "Completó la ficha con mínima guía", 5, "#2FA37A"),
        EvaluationDimension("d_pace", "Ritmo y Persistencia", "Mantuvo motivación hasta el cierre", 4, "#FF8A5C")
    )
    "Conductual" -> listOf(
        EvaluationDimension("d_trans", "Transición entre Tareas", "Usó panel de anticipación sin tensión", 4, "#7C5CC7"),
        EvaluationDimension("d_ask", "Petición de Pausa", "Comunicó necesidad con pictograma", 5, "#2FA37A"),
        EvaluationDimension("d_calm", "Autorregulación", "Retomó actividad tras descanso táctil", 4, "#3F82B8")
    )
    else -> listOf(
        EvaluationDimension("d_att", "Atención y Enfoque", "Sostuvo 18 minutos en puzzle", 4, "#3F82B8"),
        EvaluationDimension("d_sen", "Regulación Sensorial", "Autorregulación óptima con cascos", 5, "#2FA37A"),
        EvaluationDimension("d_int", "Interacción y Turno", "Compartió piezas amablemente", 4, "#FF8A5C"),
        EvaluationDimension("d_flx", "Flexibilidad ante Cambios", "Aceptó el cambio con apoyo visual", 3, "#7C5CC7")
    )
}

@Keep
@IgnoreExtraProperties
data class QuickNote(
    @DocumentId val id: String = "",
    val category: String = "ANTICIPACION", // ANTICIPACION, MATERIALES, SENSORIAL
    val tagLabel: String = "ANTICIPACIÓN",
    val timeLabel: String = "",
    val content: String = "",
    val studentName: String = "",
    val contextLabel: String = "",
    val completed: Boolean = false,
    val replyText: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class EvaluationDimension(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val score: Int = 4,
    val colorHex: String = "#3F82B8"
)

@Keep
@IgnoreExtraProperties
data class Evaluation(
    @DocumentId val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val medalType: String = "GOLD", // BRONZE, SILVER, GOLD
    val overallScore: Int = 4,
    val dimensionScores: List<EvaluationDimension> = emptyList(),
    val templateCategory: String = "Sensorial",
    val notes: String = "",
    val shareWithFamily: Boolean = true,
    val recordInHistory: Boolean = true,
    val draft: Boolean = false,
    val dateLabel: String = formatTodaySpanishHeader(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class DiaryAreaItem(
    val id: String = "",
    val title: String = "",
    val badgeLabel: String = "4/5 Autónomo",
    val objective: String = "",
    val observation: String = "",
    val nextSession: String = "",
    val accentKey: String = "peach" // peach, sky, mint, lavender
)

@Keep
@IgnoreExtraProperties
data class InternalNoteItem(
    val id: String = "",
    val authorName: String = "",
    val authorRole: String = "",
    val timeLabel: String = "",
    val text: String = "",
    val tag: String = "",
    val accentKey: String = "peach"
)

@Keep
@IgnoreExtraProperties
data class RoutineStep(
    val id: String = "",
    val time: String = "",
    val title: String = "",
    val subtitle: String = "",
    val status: String = "pending", // done, active, pending
    val accentKey: String = "peach"
)

@Keep
@IgnoreExtraProperties
data class DiaryEntry(
    @DocumentId val id: String = "",
    val studentId: String = "",
    val studentName: String = "",
    val dateKey: String = "2024-10-23",
    val dayLabel: String = "Miércoles 23",
    val emotionalState: String = "Radiante y Tranquilo",
    val emotionalSubtitle: String = "Conectado con sus compañeros y flexible.",
    val medalLabel: String = "¡Misión Hecha!",
    val dayIndicator: String = "calma", // calma, sensorial, evento
    val anticipationTitle: String = "Anticipación para mañana (Jueves)",
    val anticipationTime: String = "11:00 AM",
    val anticipationText: String = "Cambio de aula: La clase de Música se realizará en el salón de psicomotricidad.",
    val areas: List<DiaryAreaItem> = emptyList(),
    val internalNotes: List<InternalNoteItem> = emptyList(),
    val afternoonRoutine: List<RoutineStep> = emptyList(),
    val syncedLabel: String = "14:15 Sincronizado",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ResourceFolder(
    @DocumentId val id: String = "",
    val name: String = "",
    val subtitle: String = "",
    val accentKey: String = "peach",
    val iconKey: String = "self_improvement",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ResourceItem(
    @DocumentId val id: String = "",
    val folderId: String = "",
    val title: String = "",
    val description: String = "",
    val fileType: String = "PDF", // PDF, PNG, MP3, DOC
    val sizeLabel: String = "1.2 MB",
    val dateLabel: String = "Hoy",
    val statusBadge: String = "Listo imprimir",
    val locationBadge: String = "Activo en Tablet",
    val assignedStudentIds: List<String> = emptyList(),
    val localUri: String = "",
    val mimeType: String = "",
    val previewSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LibraryItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val author: String = "",
    val materialType: String = "cuento", // cuento, libro, paper
    val tags: List<String> = emptyList(),
    val metaInfo: String = "",
    val pedagogicalTitle: String = "Uso pedagógico",
    val reviewNote: String = "",
    val assignedStudentIds: List<String> = emptyList(),
    val usageMoments: List<String> = emptyList(),
    val accentKey: String = "blue",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
