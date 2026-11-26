package com.example.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.InlineDataPart
import com.google.firebase.ai.type.Part
import com.google.firebase.ai.type.QuotaExceededException
import com.google.firebase.ai.type.RequestTimeoutException
import com.google.firebase.ai.type.ResponseStoppedException
import com.google.firebase.ai.type.SerializationException
import com.google.firebase.ai.type.ServerException
import com.google.firebase.ai.type.TextPart
import com.google.firebase.ai.type.ThinkingLevel
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.UnknownException
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.google.firebase.ai.type.thinkingConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "AiRepository"
private const val MAX_TOTAL_PDF_BYTES = 15 * 1024 * 1024 // ~15 MB safe inline payload budget
private const val MODEL_FLASH_LITE = "gemini-3.5-flash-lite"
private const val MODEL_FLASH_38 = "gemini-3.8-flash"

data class GroundedSearchResult(
    val summary: String,
    val sources: List<Pair<String, String>>
)

enum class ChatbotRole(
    val id: String,
    val label: String,
    val shortDescription: String,
    val systemPrompt: String
) {
    GEMINI_GENERAL(
        id = "gemini_general",
        label = "Gemini",
        shortDescription = "Asistente abierto para cualquier pregunta, idea o tarea",
        systemPrompt = "Eres Gemini, un asistente de inteligencia artificial amable, claro, útil y versátil. Responde en español natural y directo a cualquier pregunta, duda o tarea que te plantee el usuario, sin restricciones innecesarias. Si el mensaje incluye contexto sobre un alumno o el aula R-TEA, aprovéchalo para personalizar tu respuesta."
    ),
    CONSULTOR_TEA(
        id = "consultor_tea",
        label = "Consultor TEA",
        shortDescription = "Adaptaciones de aula, rutinas y pedagogía terapéutica",
        systemPrompt = "Eres Consultor TEA de R-TEA, especialista en pedagogía terapéutica y educación inclusiva. Ayudas al docente a adaptar actividades, estructurar rutinas visuales, anticipar cambios y resolver situaciones de aula con pasos claros y realistas."
    ),
    SENSORIAL(
        id = "sensorial",
        label = "Apoyo Sensorial",
        shortDescription = "Regulación sensorial, calma y prevención de sobrecarga",
        systemPrompt = "Eres Especialista en Regulación Sensorial TEA de R-TEA. Ofreces pautas prácticas para detectar señales tempranas de sobrecarga, diseñar rincones de calma, modular estímulos auditivos/visuales y acompañar momentos de bloqueo o nerviosismo con respeto."
    ),
    COMUNICACION(
        id = "comunicacion",
        label = "Comunicación SAAC",
        shortDescription = "PECS, pictogramas, agendas visuales y lenguaje funcional",
        systemPrompt = "Eres Especialista en Comunicación Aumentativa y Alternativa (SAAC y PECS) de R-TEA. Ayudas a diseñar apoyos visuales, tableros de elección, guiones sociales breves e interacciones comunicativas accesibles para cada alumno."
    ),
    FAMILIA(
        id = "familia",
        label = "Puente Familia",
        shortDescription = "Mensajes empáticos y pautas coordinadas entre aula y casa",
        systemPrompt = "Eres Orientador Sociofamiliar de R-TEA. Redactas mensajes claros, cercanos y constructivos para las familias, propones pequeñas rutinas compartidas entre escuela y hogar y cuidas siempre un tono positivo basado en fortalezas."
    )
}

data class AiChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val studentLabel: String = "",
    val roleLabel: String = "",
    val retryPrompt: String = "",
    val isStreaming: Boolean = false,
    val isError: Boolean = false
)

class AiRepository {
    private val reportModelPrimary by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_FLASH_38,
            generationConfig = generationConfig {
                thinkingConfig = thinkingConfig {
                    thinkingLevel = ThinkingLevel.LOW
                }
            }
        )
    }

    private val reportModelFallback by lazy {
        Firebase.ai.generativeModel(MODEL_FLASH_LITE)
    }

    private val groundedSearchModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_FLASH_38,
            tools = listOf(Tool.googleSearch())
        )
    }

    private val chatModelCache = ConcurrentHashMap<String, GenerativeModel>()

    private fun getOrCreateChatModel(modelName: String, role: ChatbotRole): GenerativeModel {
        val key = "$modelName:${role.id}"
        return chatModelCache.getOrPut(key) {
            val sysText = buildString {
                appendLine(role.systemPrompt)
                append("Mantén el hilo natural de la conversación multi-turno y responde en español claro y bien estructurado.")
            }
            if (modelName == MODEL_FLASH_38) {
                Firebase.ai.generativeModel(
                    modelName = modelName,
                    generationConfig = generationConfig {
                        thinkingConfig = thinkingConfig {
                            thinkingLevel = ThinkingLevel.LOW
                        }
                    },
                    systemInstruction = content("system") { text(sysText) }
                )
            } else {
                Firebase.ai.generativeModel(
                    modelName = modelName,
                    systemInstruction = content("system") { text(sysText) }
                )
            }
        }
    }

    suspend fun generateStudentFinalReport(
        student: Student,
        evaluations: List<Evaluation>,
        diaryEntries: List<DiaryEntry>
    ): String {
        val areasText = if (student.progressAreas.isEmpty()) {
            "Sin áreas registradas"
        } else {
            student.progressAreas.joinToString("\n") {
                "- ${it.title}: ${it.completedSteps}/${it.totalSteps} (${it.percentage}%)"
            }
        }

        val currentDimsText = student.effectiveEvalDimensions.joinToString("\n") {
            "  * ${it.title} (${it.score}/5): ${it.subtitle}"
        }
        val currentEvalSection = """
            Evaluación Actual en Curso:
            - Plantilla / Enfoque: ${student.evalTemplate.ifBlank { "Sensorial" }}
            - Medalla actual: ${student.evalMedal} | Nivel bienestar: ${student.evalScore}/5
            - Refuerzo positivo: ${student.effectiveReinforcement}
            - Notas de evaluación: ${student.evalNotes.ifBlank { "Sin notas adicionales" }}
            - Dimensiones evaluadas:
            $currentDimsText
        """.trimIndent()

        val evalsHistoryText = evaluations.filter { it.studentId == student.id }.take(6).joinToString("\n") {
            "- ${it.dateLabel} [${it.templateCategory}]: Medalla ${it.medalType}, Bienestar ${it.overallScore}/5. Notas: ${it.notes}"
        }.ifBlank { "Sin evaluaciones históricas adicionales." }

        val diaryText = diaryEntries.filter { it.studentId == student.id }.take(4).joinToString("\n") { d ->
            val obs = d.areas.joinToString("; ") { "${it.title}: ${it.observation}" }
            "- ${d.dayLabel}: Estado '${d.emotionalState}' (${d.emotionalSubtitle}). $obs"
        }.ifBlank { "Sin entradas recientes en el diario." }

        // Load real PDF bytes from "1. Informe Principal / Inicial" and "2. Programación"
        val attachedPdfs = mutableListOf<Triple<String, StudentDocument, ByteArray>>()
        val listedOnlyDocs = mutableListOf<String>()
        var accumulatedBytes = 0

        suspend fun collectDocs(folderLabel: String, docs: List<StudentDocument>) {
            for (doc in docs) {
                val bytes = LocalDocumentManager.readFileBytes(doc.localPath)
                if (bytes != null && accumulatedBytes + bytes.size <= MAX_TOTAL_PDF_BYTES) {
                    accumulatedBytes += bytes.size
                    attachedPdfs.add(Triple(folderLabel, doc, bytes))
                } else {
                    listedOnlyDocs.add("$folderLabel -> ${doc.title} (${doc.sizeLabel})")
                }
            }
        }

        collectDocs("1. Informe Inicial / Principal", student.mainReportDocs)
        collectDocs("2. Programación TEA", student.programDocs)

        val docsSummaryText = buildString {
            if (attachedPdfs.isEmpty() && listedOnlyDocs.isEmpty()) {
                append("No hay PDFs adjuntos todavía en las carpetas del expediente.")
            } else {
                if (attachedPdfs.isNotEmpty()) {
                    appendLine("Documentos PDF reales analizados directamente (${attachedPdfs.size}):")
                    attachedPdfs.forEach { (folder, doc, _) ->
                        appendLine("- [$folder] ${doc.title} (${doc.sizeLabel})")
                    }
                }
                if (listedOnlyDocs.isNotEmpty()) {
                    appendLine("Documentos referenciados sin archivo local en este dispositivo:")
                    listedOnlyDocs.forEach { appendLine("- $it") }
                }
            }
        }.trim()

        val prompt = """
            Eres un especialista en pedagogía terapéutica y neuroeducación TEA redactando el "Informe Final IA" para el expediente escolar de R-TEA en español.
            Usa un tono cálido, respetuoso, profesional y concreto (máximo 260 palabras).
            Integra obligatoriamente la información extraída de los PDFs adjuntos de "Informe Inicial / Principal" y "Programación TEA" junto con los datos de Evaluación y el Diario escolar.
            
            Alumno: ${student.fullName} (Apodo: ${student.preferredName.ifBlank { student.fullName }})
            Aula: ${student.classroom}
            Nivel de Apoyo TEA: Nivel ${student.supportLevel}
            Sistema de Comunicación: ${student.commSystem}
            Claves Sensoriales: ${student.sensoryNotes}
            Intereses motivadores: ${student.interests.joinToString(", ")}
            
            Progreso por Áreas (${student.globalPercentage}% global):
            $areasText
            
            $currentEvalSection
            
            Historial de Evaluaciones:
            $evalsHistoryText
            
            Observaciones del Diario:
            $diaryText
            
            Documentación del Expediente:
            $docsSummaryText
            
            Estructura tu respuesta en 4 apartados claros con viñetas breves:
            1. Síntesis de Informe Inicial y Programación (según los PDFs adjuntos)
            2. Logros y Evolución en Evaluación y Aula
            3. Puntos de Apoyo o Atascos Detectados
            4. Recomendaciones Prácticas para Aula y Familia
        """.trimIndent()

        val parts = buildList<Part> {
            add(TextPart(prompt))
            attachedPdfs.forEach { (folderLabel, doc, bytes) ->
                add(TextPart("Adjunto PDF [$folderLabel]: ${doc.title}"))
                add(
                    InlineDataPart(
                        bytes,
                        doc.mimeType.ifBlank { "application/pdf" },
                        doc.title
                    )
                )
            }
        }
        val multimodalContent = Content(parts = parts)

        return try {
            val response = try {
                withTimeout(90_000L) {
                    reportModelPrimary.generateContent(multimodalContent)
                }
            } catch (e: Exception) {
                if (isRetriableAiError(e)) {
                    Log.w(TAG, "Primary report model failed (${e::class.java.simpleName}), falling back to $MODEL_FLASH_LITE")
                    delay(1_000L)
                    withTimeout(90_000L) {
                        reportModelFallback.generateContent(multimodalContent)
                    }
                } else {
                    throw e
                }
            }
            val text = response.text?.trim()
            if (text.isNullOrBlank()) {
                throw IllegalStateException("El modelo devolvió una respuesta vacía.")
            }
            text
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Report generation timed out", e)
            throw IllegalStateException("La generación tardó más de 90 segundos. Inténtalo de nuevo.", e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Report generation failed", e)
            throw e
        }
    }

    suspend fun searchPedagogicalEvidence(query: String): GroundedSearchResult {
        val prompt = """
            Responde en español como especialista en educación inclusiva y autismo (TEA).
            Proporciona una síntesis práctica y basada en evidencia para docentes y familias sobre: "$query".
            Incluye 3 recomendaciones concretas de lectura o intervención aplicables en aula primaria.
        """.trimIndent()

        return try {
            val response = try {
                withTimeout(60_000L) {
                    groundedSearchModel.generateContent(prompt)
                }
            } catch (e: Exception) {
                if (isRetriableAiError(e)) {
                    Log.w(TAG, "Grounded search transient failure, retrying once", e)
                    delay(1_200L)
                    withTimeout(60_000L) {
                        groundedSearchModel.generateContent(prompt)
                    }
                } else {
                    throw e
                }
            }
            val text = response.text?.trim()
            if (text.isNullOrBlank()) {
                throw IllegalStateException("No se obtuvo respuesta del buscador pedagógico.")
            }
            val chunks = response.candidates.firstOrNull()
                ?.groundingMetadata
                ?.groundingChunks
                .orEmpty()
                .mapNotNull { chunk ->
                    val web = chunk.web
                    val title = web?.title
                    val uri = web?.uri
                    if (!title.isNullOrBlank() && !uri.isNullOrBlank()) title to uri else null
                }
                .distinctBy { it.second }
                .take(4)

            GroundedSearchResult(summary = text, sources = chunks)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Grounded search timed out", e)
            throw IllegalStateException("La búsqueda tardó más de 60 segundos.", e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Grounded search failed", e)
            throw e
        }
    }

    fun streamRoleChat(
        userMessage: String,
        role: ChatbotRole,
        useFastModel: Boolean,
        history: List<AiChatMessage>,
        focusedStudent: Student?,
        allStudents: List<Student>,
        evaluations: List<Evaluation>,
        diaryEntries: List<DiaryEntry>
    ): Flow<String> = flow {
        val contextualUserTurn = buildContextualUserTurn(
            userMessage = userMessage,
            focusedStudent = focusedStudent,
            allStudents = allStudents,
            evaluations = evaluations,
            diaryEntries = diaryEntries
        )

        val sanitizedHistory = buildSanitizedHistory(history)

        // Try preferred model first, then seamlessly fail over to the alternate model
        // and non-streaming fallback if quota/rate limits or transient stream errors occur.
        val candidateModels = if (useFastModel) {
            listOf(MODEL_FLASH_LITE, MODEL_FLASH_38, MODEL_FLASH_LITE)
        } else {
            listOf(MODEL_FLASH_38, MODEL_FLASH_LITE, MODEL_FLASH_LITE)
        }

        var lastException: Exception? = null
        var delivered = false

        for ((attemptIndex, modelName) in candidateModels.withIndex()) {
            val useNonStreamingFallback = attemptIndex == candidateModels.lastIndex && lastException != null
            if (attemptIndex > 0) {
                delay(if (attemptIndex == 1) 1_000L else 2_000L)
            }

            val buffer = StringBuilder()
            try {
                val chatModel = getOrCreateChatModel(modelName, role)
                val chat = chatModel.startChat(history = sanitizedHistory)

                if (useNonStreamingFallback) {
                    val response = withTimeout(45_000L) {
                        chat.sendMessage(contextualUserTurn)
                    }
                    val text = response.text?.trim().orEmpty()
                    if (text.isBlank()) {
                        throw IllegalStateException("Gemini no devolvió texto. Inténtalo de nuevo.")
                    }
                    emit(text)
                    delivered = true
                    break
                } else {
                    withTimeout(45_000L) {
                        chat.sendMessageStream(contextualUserTurn).collect { chunk ->
                            val part = chunk.text
                            if (!part.isNullOrEmpty()) {
                                buffer.append(part)
                                emit(buffer.toString())
                            }
                        }
                    }
                    if (buffer.isBlank()) {
                        throw IllegalStateException("Gemini no devolvió texto. Inténtalo de nuevo.")
                    }
                    delivered = true
                    break
                }
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    if (buffer.isNotBlank()) {
                        delivered = true
                        break
                    }
                    Log.w(TAG, "Chat stream timed out on $modelName (attempt ${attemptIndex + 1})")
                    lastException = e
                    continue
                }
                throw e
            } catch (e: ResponseStoppedException) {
                if (buffer.isNotBlank()) {
                    delivered = true
                    break
                }
                Log.w(TAG, "ResponseStoppedException on $modelName", e)
                lastException = e
                break
            } catch (e: Exception) {
                if (buffer.isNotBlank()) {
                    delivered = true
                    break
                }
                Log.w(TAG, "Chat attempt ${attemptIndex + 1} failed on $modelName: ${e::class.java.simpleName} - ${e.message}")
                lastException = e
                if (!isRetriableAiError(e)) {
                    throw e
                }
            }
        }

        if (!delivered) {
            throw (lastException ?: IllegalStateException("No se pudo obtener respuesta de Gemini."))
        }
    }

    private fun buildContextualUserTurn(
        userMessage: String,
        focusedStudent: Student?,
        allStudents: List<Student>,
        evaluations: List<Evaluation>,
        diaryEntries: List<DiaryEntry>
    ): String {
        if (focusedStudent == null) {
            return userMessage
        }
        val stEvals = evaluations.filter { it.studentId == focusedStudent.id }.take(2).joinToString("; ") {
            "${it.dateLabel}: ${it.templateCategory} (${it.overallScore}/5)"
        }.ifBlank { "Sin evaluaciones" }
        val stDiary = diaryEntries.firstOrNull { it.studentId == focusedStudent.id }?.let { d ->
            "Estado '${d.emotionalState}', Anticipación: ${d.anticipationText.take(80)}"
        } ?: "Sin notas"
        val stAreas = focusedStudent.progressAreas.take(4).joinToString(", ") {
            "${it.title} (${it.percentage}%)"
        }.ifBlank { "Sin áreas" }

        return """
            [Alumno seleccionado: ${focusedStudent.fullName} | ${focusedStudent.classroom} | Apoyo N${focusedStudent.supportLevel} | Com: ${focusedStudent.commSystem} | Sensorial: ${focusedStudent.sensoryNotes.take(80)} | Intereses: ${focusedStudent.interests.joinToString(", ").take(80)} | Áreas: $stAreas | Eval: $stEvals | Diario: $stDiary | Total aula: ${allStudents.size}]
            $userMessage
        """.trimIndent()
    }

    private fun buildSanitizedHistory(history: List<AiChatMessage>): List<Content> {
        val sanitized = mutableListOf<Content>()
        history.takeLast(12)
            .filter { it.text.isNotBlank() && !it.isError && !it.isStreaming }
            .forEach { msg ->
                val msgRole = if (msg.isUser) "user" else "model"
                if (sanitized.isEmpty() && msgRole != "user") {
                    return@forEach
                }
                val clipped = if (msg.text.length > 2_000) msg.text.take(2_000) else msg.text
                if (sanitized.lastOrNull()?.role == msgRole) {
                    sanitized[sanitized.lastIndex] = content(msgRole) { text(clipped) }
                } else {
                    sanitized.add(content(msgRole) { text(clipped) })
                }
            }
        if (sanitized.lastOrNull()?.role == "user") {
            sanitized.removeAt(sanitized.lastIndex)
        }
        return sanitized
    }

    private fun isRetriableAiError(e: Exception): Boolean {
        return e is QuotaExceededException ||
            e is ServerException ||
            e is RequestTimeoutException ||
            e is SerializationException ||
            e is UnknownException ||
            e is TimeoutCancellationException ||
            (e.message?.contains("429", ignoreCase = true) == true) ||
            (e.message?.contains("quota", ignoreCase = true) == true) ||
            (e.message?.contains("exhausted", ignoreCase = true) == true) ||
            (e.message?.contains("timeout", ignoreCase = true) == true)
    }
}
