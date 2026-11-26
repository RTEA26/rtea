package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiChatMessage
import com.example.data.AiRepository
import com.example.data.AuthManager
import com.example.data.AuthResult
import com.example.data.ChatbotRole
import com.example.data.DiaryAreaItem
import com.example.data.DiaryEntry
import com.example.data.Evaluation
import com.example.data.EvaluationDimension
import com.example.data.GroundedSearchResult
import com.example.data.ImportedLocalFile
import com.example.data.InternalNoteItem
import com.example.data.LibraryItem
import com.example.data.LocalDocumentManager
import com.example.data.ProgressArea
import com.example.data.QuickNote
import com.example.data.ResourceFolder
import com.example.data.ResourceItem
import com.example.data.RoutineStep
import com.example.data.RteaRepository
import com.example.data.SeedData
import com.example.data.Student
import com.example.data.StudentDocument
import com.example.data.UiState
import com.example.data.formatTodaySpanishHeader
import com.google.firebase.ai.type.ContentBlockedException
import com.google.firebase.ai.type.PermissionMissingException
import com.google.firebase.ai.type.PromptBlockedException
import com.google.firebase.ai.type.QuotaExceededException
import com.google.firebase.ai.type.ServiceDisabledException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "RteaViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
class RteaViewModel(application: Application) : AndroidViewModel(application) {
    private val authManager = AuthManager(application)
    private val repository = RteaRepository(application)
    private val aiRepository = AiRepository()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(authManager.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _errorBanner = MutableStateFlow<String?>(null)
    val errorBanner: StateFlow<String?> = _errorBanner.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private val _selectedStudentId = MutableStateFlow("mateo")
    val selectedStudentId: StateFlow<String> = _selectedStudentId.asStateFlow()

    private val _generatingReportForId = MutableStateFlow<String?>(null)
    val generatingReportForId: StateFlow<String?> = _generatingReportForId.asStateFlow()

    private val _aiReportViewerState = MutableStateFlow<Pair<String, String>?>(null)
    val aiReportViewerState: StateFlow<Pair<String, String>?> = _aiReportViewerState.asStateFlow()

    private val _groundedSearchState = MutableStateFlow<UiState<GroundedSearchResult>?>(null)
    val groundedSearchState: StateFlow<UiState<GroundedSearchResult>?> = _groundedSearchState.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<AiChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<AiChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatResponding = MutableStateFlow(false)
    val isChatResponding: StateFlow<Boolean> = _isChatResponding.asStateFlow()

    private val _chatFocusStudentId = MutableStateFlow<String?>(null)
    val chatFocusStudentId: StateFlow<String?> = _chatFocusStudentId.asStateFlow()

    private val _chatRole = MutableStateFlow(ChatbotRole.GEMINI_GENERAL)
    val chatRole: StateFlow<ChatbotRole> = _chatRole.asStateFlow()

    private val _useFastChatModel = MutableStateFlow(true)
    val useFastChatModel: StateFlow<Boolean> = _useFastChatModel.asStateFlow()

    private var chatJob: Job? = null
    private var seededUid: String? = null
    private var hasAttemptedSilentSignIn = false

    init {
        viewModelScope.launch {
            authManager.authStateFlow.collect { user ->
                _currentUser.value = user
                if (user != null && seededUid != user.uid) {
                    seededUid = user.uid
                    _errorBanner.value = null
                    launch {
                        try {
                            repository.ensureSeeded(user.uid)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            Log.e(TAG, "Error seeding user workspace", e)
                            _errorBanner.value = "Error al inicializar datos en la nube: ${e.localizedMessage}"
                        }
                    }
                }
            }
        }
    }

    fun trySilentSignInIfNeeded(activityContext: Context) {
        if (hasAttemptedSilentSignIn || _currentUser.value != null) return
        hasAttemptedSilentSignIn = true
        viewModelScope.launch {
            authManager.trySilentGoogleSignIn(activityContext)
        }
    }

    private fun <T> userScopedFlow(
        streamProvider: (String) -> kotlinx.coroutines.flow.Flow<List<T>>,
        label: String
    ): StateFlow<UiState<List<T>>> =
        _currentUser.flatMapLatest { user ->
            if (user == null) {
                flowOf<UiState<List<T>>>(UiState.Loading)
            } else {
                streamProvider(user.uid)
                    .map<List<T>, UiState<List<T>>> { UiState.Success(it) }
                    .catch { e ->
                        val fsEx = e as? FirebaseFirestoreException ?: e.cause as? FirebaseFirestoreException
                        if (fsEx?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                            Log.e(TAG, "PERMISSION_DENIED loading $label", e)
                            emit(UiState.Error("Sin permisos para leer $label. Verifica tu sesión."))
                        } else {
                            Log.e(TAG, "Error loading $label", e)
                            emit(UiState.Error(e.localizedMessage ?: "Error al cargar $label"))
                        }
                    }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), UiState.Loading)

    val studentsState: StateFlow<UiState<List<Student>>> =
        userScopedFlow(repository::observeStudents, "alumnos")

    val notesState: StateFlow<UiState<List<QuickNote>>> =
        userScopedFlow(repository::observeNotes, "notas")

    val evaluationsState: StateFlow<UiState<List<Evaluation>>> =
        userScopedFlow(repository::observeEvaluations, "evaluaciones")

    val diaryEntriesState: StateFlow<UiState<List<DiaryEntry>>> =
        userScopedFlow(repository::observeDiaryEntries, "diario")

    val foldersState: StateFlow<UiState<List<ResourceFolder>>> =
        userScopedFlow(repository::observeResourceFolders, "carpetas")

    val resourcesState: StateFlow<UiState<List<ResourceItem>>> =
        userScopedFlow(repository::observeResources, "recursos")

    val libraryState: StateFlow<UiState<List<LibraryItem>>> =
        userScopedFlow(repository::observeLibraryItems, "biblioteca")

    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            when (val res = authManager.signInWithGoogleOneClick(activityContext)) {
                is AuthResult.Success -> {
                    _authError.value = null
                    _toastMessage.tryEmit("¡Sesión iniciada con Google! Sincronizando R-TEA...")
                }
                is AuthResult.Cancelled -> {
                    // User cancelled sheet
                }
                is AuthResult.Error -> {
                    _authError.value = res.message
                }
            }
            _authLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            seededUid = null
            _errorBanner.value = null
            authManager.signOut()
            _toastMessage.tryEmit("Sesión cerrada correctamente.")
        }
    }

    fun selectStudent(studentId: String) {
        _selectedStudentId.value = studentId
    }

    fun dismissErrorBanner() {
        _errorBanner.value = null
    }

    fun dismissAiViewer() {
        _aiReportViewerState.value = null
    }

    fun openAiViewer(student: Student) {
        val text = student.aiReportSummary.ifBlank {
            "Aún no se ha generado un informe IA para ${student.fullName}. Pulsa «Generar» para crear una síntesis personalizada."
        }
        _aiReportViewerState.value = student.fullName to text
    }

    fun emitToast(message: String) {
        _toastMessage.tryEmit(message)
    }

    private fun uidOrWarn(): String? {
        val uid = _currentUser.value?.uid
        if (uid == null) {
            _toastMessage.tryEmit("Inicia sesión con Google para guardar cambios.")
        }
        return uid
    }

    // --- Quick Notes ---
    fun addQuickNote(category: String, content: String, studentName: String, timeLabel: String) {
        val uid = uidOrWarn() ?: return
        val tagLabel = when (category) {
            "MATERIALES" -> "MATERIALES"
            "SENSORIAL" -> "SENSORIAL / BIENESTAR"
            else -> "ANTICIPACIÓN"
        }
        viewModelScope.launch {
            runSafe("guardar nota") {
                repository.saveNote(
                    uid,
                    QuickNote(
                        category = category,
                        tagLabel = tagLabel,
                        timeLabel = timeLabel.ifBlank { "Hoy" },
                        content = content.trim(),
                        studentName = studentName.trim(),
                        contextLabel = if (category == "SENSORIAL") "Nota de aula" else "",
                        completed = false
                    )
                )
                _toastMessage.tryEmit("Nota fijada y sincronizada.")
            }
        }
    }

    fun toggleNoteCompleted(note: QuickNote) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("actualizar nota") {
                repository.saveNote(uid, note.copy(completed = !note.completed))
            }
        }
    }

    fun replyToNote(note: QuickNote, reply: String) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("responder nota") {
                repository.saveNote(uid, note.copy(replyText = reply.trim()))
                _toastMessage.tryEmit("Respuesta guardada en el tablón.")
            }
        }
    }

    fun deleteNote(noteId: String) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("eliminar nota") {
                repository.deleteNote(uid, noteId)
                _toastMessage.tryEmit("Nota eliminada.")
            }
        }
    }

    // --- Students & Progress Areas ---
    fun saveStudentProfile(student: Student, onComplete: () -> Unit = {}) {
        val uid = uidOrWarn() ?: return
        val isNewStudent = student.id.isBlank()
        viewModelScope.launch {
            runSafe("guardar alumno") {
                val prepared = if (isNewStudent) {
                    val defaultAreas = if (student.progressAreas.isEmpty()) {
                        listOf(
                            ProgressArea("pa_1", "Autonomía & Vida Diaria", 4, 10, "#4BA2E8"),
                            ProgressArea("pa_2", "Regulación Sensorial", 5, 10, "#4DB896"),
                            ProgressArea("pa_3", "Comunicación Funcional", 6, 10, "#FF9D6C")
                        )
                    } else student.progressAreas
                    val defaultDims = if (student.evalDimensions.isEmpty()) {
                        com.example.data.defaultEvaluationDimensions(student.evalTemplate.ifBlank { "Sensorial" })
                    } else student.evalDimensions
                    student.copy(
                        progressAreas = defaultAreas,
                        evalDimensions = defaultDims,
                        lastSessionLabel = student.lastSessionLabel.ifBlank { "Hoy" }
                    )
                } else {
                    student
                }
                val newId = repository.saveStudent(uid, prepared)
                if (isNewStudent) {
                    val baseDiary = SeedData.defaultDiaryEntries.first()
                    repository.saveDiaryEntry(
                        uid,
                        baseDiary.copy(
                            id = "diary_${newId}_2024_10_23",
                            studentId = newId,
                            studentName = prepared.fullName,
                            syncedLabel = "Sincronizado"
                        )
                    )
                }
                _selectedStudentId.value = newId
                _toastMessage.tryEmit("Perfil de ${student.fullName} sincronizado.")
                onComplete()
            }
        }
    }

    fun deleteStudent(student: Student) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("eliminar alumno") {
                repository.deleteStudent(uid, student.id)
                _toastMessage.tryEmit("Expediente de ${student.fullName} eliminado.")
            }
        }
    }

    fun adjustProgressArea(student: Student, areaId: String, delta: Int) {
        val uid = uidOrWarn() ?: return
        val updated = student.progressAreas.map { area ->
            if (area.id == areaId) {
                val next = (area.completedSteps + delta).coerceIn(0, area.totalSteps)
                area.copy(completedSteps = next)
            } else area
        }
        viewModelScope.launch {
            runSafe("actualizar progreso") {
                repository.updateStudentProgressAreas(uid, student.id, updated)
            }
        }
    }

    fun addProgressArea(student: Student, title: String, totalSteps: Int, colorHex: String) {
        val uid = uidOrWarn() ?: return
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val newArea = ProgressArea(
            id = "pa_${UUID.randomUUID().toString().take(6)}",
            title = cleanTitle,
            completedSteps = 1,
            totalSteps = totalSteps.coerceIn(1, 50),
            colorHex = colorHex
        )
        viewModelScope.launch {
            runSafe("añadir área") {
                repository.updateStudentProgressAreas(uid, student.id, student.progressAreas + newArea)
                _toastMessage.tryEmit("Área «$cleanTitle» añadida a ${student.fullName}.")
            }
        }
    }

    fun removeProgressArea(student: Student, areaId: String) {
        val uid = uidOrWarn() ?: return
        val updated = student.progressAreas.filterNot { it.id == areaId }
        viewModelScope.launch {
            runSafe("quitar área") {
                repository.updateStudentProgressAreas(uid, student.id, updated)
            }
        }
    }

    fun updateStudentFolderCount(student: Student, folderType: Int, delta: Int) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("actualizar documentos") {
                if (folderType == 1) {
                    repository.updateStudentDocsCount(uid, student.id, mainDelta = delta)
                } else {
                    repository.updateStudentDocsCount(uid, student.id, programDelta = delta)
                }
                val actionWord = if (delta > 0) "añadido" else "retirado"
                _toastMessage.tryEmit("Documento $actionWord correctamente.")
            }
        }
    }

    fun uploadStudentPdf(student: Student, folderType: Int, uri: Uri) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("subir PDF del alumno") {
                val imported = LocalDocumentManager.importFromUri(
                    context = getApplication(),
                    uri = uri,
                    subfolder = "students/${student.id}/folder_$folderType",
                    fallbackPrefix = if (folderType == 1) "Informe_Inicial" else "Programacion",
                    fallbackExt = "pdf"
                )
                if (imported.fileType != "PDF") {
                    LocalDocumentManager.deleteLocalFile(getApplication(), imported.localPath)
                    _toastMessage.tryEmit("Selecciona un archivo en formato PDF.")
                    return@runSafe
                }
                val doc = StudentDocument(
                    id = "doc_${UUID.randomUUID().toString().take(8)}",
                    title = imported.title,
                    localPath = imported.localPath,
                    sizeLabel = imported.sizeLabel,
                    dateLabel = formatTodaySpanishHeader(),
                    mimeType = imported.mimeType.ifBlank { "application/pdf" }
                )
                repository.addStudentDocument(uid, student.id, folderType, doc)
                val folderTitle = if (folderType == 1) "Informe Inicial/Principal" else "Programación"
                _toastMessage.tryEmit("PDF «${doc.title}» guardado en $folderTitle.")
            }
        }
    }

    fun deleteStudentPdf(student: Student, folderType: Int, doc: StudentDocument) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("eliminar PDF del alumno") {
                repository.removeStudentDocument(uid, student.id, folderType, doc.id)
                LocalDocumentManager.deleteLocalFile(getApplication(), doc.localPath)
                _toastMessage.tryEmit("PDF «${doc.title}» eliminado.")
            }
        }
    }

    fun assignResourceToStudent(student: Student, resource: ResourceItem) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("asignar recurso al alumno") {
                val updatedStudentRes = (student.activeResourceIds + resource.id).distinct()
                repository.updateStudentActiveResources(uid, student.id, updatedStudentRes)
                if (!resource.assignedStudentIds.contains(student.id)) {
                    repository.saveResource(
                        uid,
                        resource.copy(assignedStudentIds = (resource.assignedStudentIds + student.id).distinct())
                    )
                }
                _toastMessage.tryEmit("${resource.title} asignado a ${student.fullName}.")
            }
        }
    }

    fun generateAiReport(student: Student) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            _generatingReportForId.value = student.id
            try {
                val evals = (evaluationsState.value as? UiState.Success)?.data.orEmpty()
                val diaries = (diaryEntriesState.value as? UiState.Success)?.data.orEmpty()
                val summary = aiRepository.generateStudentFinalReport(student, evals, diaries)
                repository.updateStudentAiReport(uid, student.id, summary)
                _aiReportViewerState.value = student.fullName to summary
                _toastMessage.tryEmit("Informe Final IA actualizado para ${student.fullName}.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Report generation failed", e)
                _errorBanner.value = "Error de IA al generar informe: ${e.localizedMessage ?: "Verifica la conexión y App Check."}"
            } finally {
                _generatingReportForId.value = null
            }
        }
    }

    // --- Evaluations ---
    fun updateStudentEvaluationConfig(
        student: Student,
        medalType: String,
        overallScore: Int,
        dimensions: List<EvaluationDimension>,
        templateCategory: String,
        reinforcementText: String,
        notes: String,
        shareWithFamily: Boolean,
        recordInHistory: Boolean,
        showToast: Boolean = false
    ) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("guardar evaluación") {
                repository.updateStudentEvaluationConfig(
                    userId = uid,
                    studentId = student.id,
                    evalTemplate = templateCategory,
                    evalReinforcement = reinforcementText,
                    evalMedal = medalType,
                    evalScore = overallScore,
                    evalDimensions = dimensions,
                    evalNotes = notes,
                    evalShareFamily = shareWithFamily,
                    evalRecordHistory = recordInHistory
                )
                if (showToast) {
                    _toastMessage.tryEmit("Evaluación guardada para ${student.shortDisplayName}.")
                }
            }
        }
    }

    fun submitEvaluation(
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
    ) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("guardar evaluación") {
                repository.updateStudentEvaluationConfig(
                    userId = uid,
                    studentId = student.id,
                    evalTemplate = templateCategory,
                    evalReinforcement = reinforcementText,
                    evalMedal = medalType,
                    evalScore = overallScore,
                    evalDimensions = dimensions,
                    evalNotes = notes,
                    evalShareFamily = shareWithFamily,
                    evalRecordHistory = recordInHistory
                )
                if (recordInHistory || isDraft) {
                    repository.saveEvaluation(
                        uid,
                        Evaluation(
                            studentId = student.id,
                            studentName = student.fullName,
                            medalType = medalType,
                            overallScore = overallScore,
                            dimensionScores = dimensions,
                            templateCategory = templateCategory,
                            notes = notes.trim(),
                            shareWithFamily = shareWithFamily,
                            recordInHistory = recordInHistory,
                            draft = isDraft,
                            dateLabel = formatTodaySpanishHeader()
                        )
                    )
                }
                if (!isDraft && shareWithFamily) {
                    val existingDiary = (diaryEntriesState.value as? UiState.Success)?.data
                        ?.firstOrNull { it.studentId == student.id }
                        ?: SeedData.defaultDiaryEntries.first().copy(
                            id = "diary_${student.id}_2024_10_23",
                            studentId = student.id,
                            studentName = student.fullName
                        )
                    val medalName = when (medalType) {
                        "BRONZE" -> "Buen Esfuerzo"
                        "SILVER" -> "Gran Progreso"
                        else -> "¡Misión Hecha!"
                    }
                    val evalNote = InternalNoteItem(
                        id = "in_${UUID.randomUUID().toString().take(6)}",
                        authorName = _currentUser.value?.displayName ?: "Profe Carmen",
                        authorRole = "Evaluación",
                        timeLabel = "Ahora",
                        text = notes.ifBlank { "Evaluación de sesión registrada ($overallScore/5)." },
                        tag = medalName,
                        accentKey = "sky"
                    )
                    repository.saveDiaryEntry(
                        uid,
                        existingDiary.copy(
                            medalLabel = medalName,
                            emotionalState = if (overallScore >= 4) "Radiante y Tranquilo" else "Con Apoyo Sereno",
                            internalNotes = listOf(evalNote) + existingDiary.internalNotes,
                            syncedLabel = "Ahora Sincronizado"
                        )
                    )
                }
                _toastMessage.tryEmit(
                    if (isDraft) "Borrador guardado para ${student.fullName}."
                    else "¡Medalla y evaluación registradas para ${student.fullName}!"
                )
            }
        }
    }

    // --- Diary ---
    fun addDiaryArea(entry: DiaryEntry, title: String, badgeLabel: String, objective: String, observation: String, nextSession: String, accentKey: String) {
        val uid = uidOrWarn() ?: return
        val newItem = DiaryAreaItem(
            id = "da_${UUID.randomUUID().toString().take(6)}",
            title = title.trim(),
            badgeLabel = badgeLabel,
            objective = objective.trim(),
            observation = observation.trim(),
            nextSession = nextSession.trim(),
            accentKey = accentKey
        )
        viewModelScope.launch {
            runSafe("añadir área al diario") {
                repository.saveDiaryEntry(uid, entry.copy(areas = entry.areas + newItem, syncedLabel = "Sincronizado"))
                _toastMessage.tryEmit("Área añadida al diario de ${entry.studentName}.")
            }
        }
    }

    fun removeDiaryArea(entry: DiaryEntry, areaId: String) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("eliminar área del diario") {
                repository.saveDiaryEntry(uid, entry.copy(areas = entry.areas.filterNot { it.id == areaId }))
            }
        }
    }

    fun updateDiaryEmotionalState(entry: DiaryEntry, emotionalState: String, emotionalSubtitle: String) {
        val uid = uidOrWarn() ?: return
        val cleanState = emotionalState.trim()
        if (cleanState.isEmpty()) return
        val cleanSubtitle = emotionalSubtitle.trim().ifBlank { "Estado registrado por el docente." }
        viewModelScope.launch {
            runSafe("actualizar estado emocional") {
                repository.saveDiaryEntry(
                    uid,
                    entry.copy(
                        emotionalState = cleanState,
                        emotionalSubtitle = cleanSubtitle,
                        syncedLabel = "Sincronizado"
                    )
                )
                _toastMessage.tryEmit("Estado emocional actualizado.")
            }
        }
    }

    fun updateDiaryAnticipation(entry: DiaryEntry, title: String, time: String, text: String) {
        val uid = uidOrWarn() ?: return
        val cleanTitle = title.trim().ifBlank { "Anticipación para mañana" }.take(120)
        val cleanTime = time.trim().ifBlank { "Mañana" }.take(40)
        val cleanText = text.trim().take(600)
        viewModelScope.launch {
            runSafe("guardar anticipación") {
                repository.saveDiaryEntry(
                    uid,
                    entry.copy(
                        anticipationTitle = cleanTitle,
                        anticipationTime = cleanTime,
                        anticipationText = cleanText,
                        syncedLabel = "Sincronizado"
                    )
                )
                _toastMessage.tryEmit("Anticipación actualizada.")
            }
        }
    }

    fun addDiaryInternalNote(entry: DiaryEntry, text: String, tag: String) {
        val uid = uidOrWarn() ?: return
        val note = InternalNoteItem(
            id = "in_${UUID.randomUUID().toString().take(6)}",
            authorName = "Nota personal",
            authorRole = "Registro propio",
            timeLabel = "Ahora",
            text = text.trim(),
            tag = tag,
            accentKey = "butter"
        )
        viewModelScope.launch {
            runSafe("guardar apunte en bloc de notas") {
                repository.saveDiaryEntry(
                    uid,
                    entry.copy(internalNotes = listOf(note) + entry.internalNotes, syncedLabel = "Sincronizado")
                )
                _toastMessage.tryEmit("Apunte guardado en tu bloc de notas.")
            }
        }
    }

    fun updateDiaryInternalNote(entry: DiaryEntry, noteId: String, newText: String, newTag: String) {
        val uid = uidOrWarn() ?: return
        val cleanText = newText.trim()
        if (cleanText.isEmpty()) return
        val updated = entry.internalNotes.map { item ->
            if (item.id == noteId) item.copy(text = cleanText, tag = newTag, timeLabel = "Editado") else item
        }
        viewModelScope.launch {
            runSafe("editar apunte del bloc de notas") {
                repository.saveDiaryEntry(uid, entry.copy(internalNotes = updated, syncedLabel = "Sincronizado"))
                _toastMessage.tryEmit("Apunte actualizado.")
            }
        }
    }

    fun deleteDiaryInternalNote(entry: DiaryEntry, noteId: String) {
        val uid = uidOrWarn() ?: return
        val updated = entry.internalNotes.filterNot { it.id == noteId }
        viewModelScope.launch {
            runSafe("eliminar apunte del bloc de notas") {
                repository.saveDiaryEntry(uid, entry.copy(internalNotes = updated, syncedLabel = "Sincronizado"))
                _toastMessage.tryEmit("Apunte eliminado del bloc de notas.")
            }
        }
    }

    fun toggleRoutineStep(entry: DiaryEntry, stepId: String) {
        val uid = uidOrWarn() ?: return
        val updated = entry.afternoonRoutine.map { step ->
            if (step.id == stepId) {
                val nextStatus = when (step.status) {
                    "pending" -> "active"
                    "active" -> "done"
                    else -> "pending"
                }
                step.copy(status = nextStatus)
            } else step
        }
        viewModelScope.launch {
            runSafe("actualizar rutina") {
                repository.saveDiaryEntry(uid, entry.copy(afternoonRoutine = updated))
            }
        }
    }

    fun saveQuickLogToDiary(student: Student, text: String, stateLabel: String) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("registro rápido diario") {
                val existing = (diaryEntriesState.value as? UiState.Success)?.data
                    ?.firstOrNull { it.studentId == student.id }
                    ?: SeedData.defaultDiaryEntries.first().copy(
                        id = "diary_${student.id}_2024_10_23",
                        studentId = student.id,
                        studentName = student.fullName
                    )
                val note = InternalNoteItem(
                    id = "in_${UUID.randomUUID().toString().take(6)}",
                    authorName = _currentUser.value?.displayName ?: "Profe Carmen",
                    authorRole = "Registro Rápido",
                    timeLabel = "Ahora",
                    text = text.trim(),
                    tag = stateLabel,
                    accentKey = "mint"
                )
                repository.saveDiaryEntry(
                    uid,
                    existing.copy(
                        emotionalState = stateLabel,
                        internalNotes = listOf(note) + existing.internalNotes,
                        syncedLabel = "Sincronizado"
                    )
                )
                _toastMessage.tryEmit("Registro rápido guardado en el Diario de ${student.fullName}.")
            }
        }
    }

    // --- Resources ---
    fun addResourceFolder(name: String, subtitle: String, accentKey: String) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("crear carpeta") {
                repository.saveResourceFolder(
                    uid,
                    ResourceFolder(
                        name = name.trim(),
                        subtitle = subtitle.trim().ifBlank { "Apoyos visuales" },
                        accentKey = accentKey,
                        iconKey = "folder"
                    )
                )
                _toastMessage.tryEmit("Carpeta «$name» creada.")
            }
        }
    }

    fun importDeviceFileForResource(uri: Uri, onImported: (ImportedLocalFile) -> Unit) {
        viewModelScope.launch {
            runSafe("cargar archivo desde tu dispositivo") {
                val imported = LocalDocumentManager.importFromUri(
                    context = getApplication(),
                    uri = uri,
                    subfolder = "resources",
                    fallbackPrefix = "Material_RTEA",
                    fallbackExt = "pdf"
                )
                onImported(imported)
                _toastMessage.tryEmit("Archivo «${imported.title}» (${imported.sizeLabel}) preparado para subir.")
            }
        }
    }

    fun discardTempLocalFile(localPath: String) {
        LocalDocumentManager.deleteLocalFile(getApplication(), localPath)
    }

    fun addResource(
        folderId: String,
        title: String,
        description: String,
        fileType: String,
        localUri: String,
        sizeLabel: String = "1.1 MB",
        mimeType: String = ""
    ) {
        val uid = uidOrWarn() ?: return
        val normalizedType = fileType.takeIf { it in setOf("PDF", "PNG", "MP3", "DOC") } ?: "DOC"
        viewModelScope.launch {
            runSafe("subir recurso") {
                repository.saveResource(
                    uid,
                    ResourceItem(
                        folderId = folderId,
                        title = title.trim(),
                        description = description.trim().ifBlank { "Material visual adaptado para el aula." },
                        fileType = normalizedType,
                        sizeLabel = sizeLabel.ifBlank { "1.1 MB" },
                        dateLabel = formatTodaySpanishHeader(),
                        statusBadge = if (localUri.isNotBlank()) "Archivo real" else "Sincronizado",
                        locationBadge = if (localUri.isNotBlank()) "Dispositivo" else "Tablet Aula",
                        assignedStudentIds = emptyList(),
                        localUri = localUri,
                        mimeType = mimeType,
                        previewSummary = description.trim().ifBlank { "Recurso visual listo para usar en sesión o tablet." }
                    )
                )
                _toastMessage.tryEmit("Material «$title» subido y sincronizado.")
            }
        }
    }

    fun updateResourceAssignments(resource: ResourceItem, selectedStudentIds: List<String>) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("asignar alumnos al recurso") {
                repository.saveResource(uid, resource.copy(assignedStudentIds = selectedStudentIds))
                val students = (studentsState.value as? UiState.Success)?.data.orEmpty()
                students.forEach { st ->
                    val shouldHave = selectedStudentIds.contains(st.id)
                    val currentlyHas = st.activeResourceIds.contains(resource.id)
                    if (shouldHave && !currentlyHas) {
                        repository.updateStudentActiveResources(uid, st.id, st.activeResourceIds + resource.id)
                    } else if (!shouldHave && currentlyHas) {
                        repository.updateStudentActiveResources(uid, st.id, st.activeResourceIds - resource.id)
                    }
                }
                _toastMessage.tryEmit("Asignación sincronizada con ${selectedStudentIds.size} alumnos.")
            }
        }
    }

    fun deleteResource(resourceId: String) {
        val uid = uidOrWarn() ?: return
        val target = (resourcesState.value as? UiState.Success)?.data?.firstOrNull { it.id == resourceId }
        viewModelScope.launch {
            runSafe("eliminar recurso") {
                repository.deleteResource(uid, resourceId)
                if (target != null && target.localUri.isNotBlank()) {
                    LocalDocumentManager.deleteLocalFile(getApplication(), target.localUri)
                }
                _toastMessage.tryEmit("Material eliminado.")
            }
        }
    }

    // --- Library ---
    fun addLibraryItem(title: String, author: String, materialType: String, reviewNote: String) {
        val uid = uidOrWarn() ?: return
        val accent = when (materialType) {
            "libro" -> "peach"
            "paper" -> "mint"
            else -> "blue"
        }
        viewModelScope.launch {
            runSafe("añadir lectura") {
                repository.saveLibraryItem(
                    uid,
                    LibraryItem(
                        title = title.trim(),
                        author = author.trim(),
                        materialType = materialType,
                        tags = listOf("Neurodiversidad", "Aula TEA"),
                        metaInfo = "Añadido hoy",
                        pedagogicalTitle = if (materialType == "paper") "Evidencia clínica" else "Uso pedagógico",
                        reviewNote = reviewNote.trim().ifBlank { "Material incorporado al inventario pedagógico." },
                        accentKey = accent
                    )
                )
                _toastMessage.tryEmit("«$title» añadido a Biblioteca R-TEA.")
            }
        }
    }

    fun updateLibraryAssignments(item: LibraryItem, studentIds: List<String>, moments: List<String>) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("asignar lectura") {
                repository.saveLibraryItem(
                    uid,
                    item.copy(assignedStudentIds = studentIds, usageMoments = moments)
                )
                _toastMessage.tryEmit("Lectura asignada a ${studentIds.size} alumnos.")
            }
        }
    }

    fun deleteLibraryItem(itemId: String) {
        val uid = uidOrWarn() ?: return
        viewModelScope.launch {
            runSafe("eliminar lectura") {
                repository.deleteLibraryItem(uid, itemId)
                _toastMessage.tryEmit("Material eliminado de la biblioteca.")
            }
        }
    }

    fun searchEvidenceWithGoogle(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _groundedSearchState.value = UiState.Loading
            try {
                val res = aiRepository.searchPedagogicalEvidence(query.trim())
                _groundedSearchState.value = UiState.Success(res)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Search grounding failed", e)
                _groundedSearchState.value = UiState.Error(
                    e.localizedMessage ?: "No pudimos completar la búsqueda pedagógica."
                )
            }
        }
    }

    fun clearGroundedSearch() {
        _groundedSearchState.value = null
    }

    fun setChatFocusStudent(studentId: String?) {
        _chatFocusStudentId.value = studentId
    }

    fun setChatRole(role: ChatbotRole) {
        _chatRole.value = role
    }

    fun toggleFastChatModel() {
        _useFastChatModel.value = !_useFastChatModel.value
    }

    fun clearChatHistory() {
        chatJob?.cancel()
        _isChatResponding.value = false
        _chatMessages.value = emptyList()
    }

    fun retryChatMessage(promptToRetry: String) {
        if (promptToRetry.isBlank() || _isChatResponding.value) return
        val current = _chatMessages.value
        val trimmed = if (current.lastOrNull()?.isError == true) {
            val withoutError = current.dropLast(1)
            if (withoutError.lastOrNull()?.isUser == true && withoutError.lastOrNull()?.text == promptToRetry) {
                withoutError.dropLast(1)
            } else {
                withoutError
            }
        } else {
            current
        }
        _chatMessages.value = trimmed
        sendChatMessage(promptToRetry)
    }

    fun sendChatMessage(rawPrompt: String) {
        val cleanPrompt = rawPrompt.trim()
        if (cleanPrompt.isEmpty() || _isChatResponding.value) return

        val students = (studentsState.value as? UiState.Success)?.data.orEmpty()
        val evals = (evaluationsState.value as? UiState.Success)?.data.orEmpty()
        val diaries = (diaryEntriesState.value as? UiState.Success)?.data.orEmpty()
        val focusId = _chatFocusStudentId.value
        val focusedStudent = if (focusId != null) students.firstOrNull { it.id == focusId } else null
        val focusLabel = focusedStudent?.shortDisplayName ?: "Todo el aula"
        val currentRole = _chatRole.value
        val fastMode = _useFastChatModel.value

        val previousHistory = _chatMessages.value
        val userMsg = AiChatMessage(
            id = "u_${UUID.randomUUID().toString().take(8)}",
            isUser = true,
            text = cleanPrompt,
            studentLabel = focusLabel,
            roleLabel = currentRole.label
        )
        val aiMsgId = "ai_${UUID.randomUUID().toString().take(8)}"
        val placeholderAi = AiChatMessage(
            id = aiMsgId,
            isUser = false,
            text = "",
            studentLabel = focusLabel,
            roleLabel = currentRole.label,
            isStreaming = true
        )

        _chatMessages.value = previousHistory + userMsg + placeholderAi
        _isChatResponding.value = true

        chatJob?.cancel()
        chatJob = viewModelScope.launch {
            try {
                aiRepository.streamRoleChat(
                    userMessage = cleanPrompt,
                    role = currentRole,
                    useFastModel = fastMode,
                    history = previousHistory,
                    focusedStudent = focusedStudent,
                    allStudents = students,
                    evaluations = evals,
                    diaryEntries = diaries
                ).collect { accumulatedText ->
                    _chatMessages.value = _chatMessages.value.map { item ->
                        if (item.id == aiMsgId) {
                            item.copy(text = accumulatedText, isStreaming = true)
                        } else item
                    }
                }
                _chatMessages.value = _chatMessages.value.map { item ->
                    if (item.id == aiMsgId) item.copy(isStreaming = false) else item
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Chat timed out", e)
                _chatMessages.value = _chatMessages.value.map { item ->
                    if (item.id == aiMsgId) {
                        item.copy(
                            text = "La respuesta tardó más de lo esperado. Pulsa reintentar.",
                            retryPrompt = cleanPrompt,
                            isStreaming = false,
                            isError = true
                        )
                    } else item
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Chat failed", e)
                val friendlyError = when {
                    e is QuotaExceededException ||
                        e.message?.contains("429", ignoreCase = true) == true ||
                        e.message?.contains("quota", ignoreCase = true) == true ||
                        e.message?.contains("exhausted", ignoreCase = true) == true ->
                        "Los servidores gratuitos de Gemini están temporalmente saturados en este instante. Espera unos segundos y pulsa Reintentar."
                    e is PromptBlockedException || e is ContentBlockedException ->
                        "Gemini no pudo responder a ese mensaje por las políticas de seguridad. Prueba formulándolo de otra manera."
                    e is PermissionMissingException || e is ServiceDisabledException ->
                        "Error de verificación de la app con Firebase AI (${e.localizedMessage ?: "revisa App Check o la sesión"})."
                    else -> {
                        val detail = e.localizedMessage?.takeIf { it.isNotBlank() } ?: e::class.java.simpleName
                        "No se pudo completar la consulta ($detail). Pulsa Reintentar."
                    }
                }
                _chatMessages.value = _chatMessages.value.map { item ->
                    if (item.id == aiMsgId) {
                        item.copy(
                            text = friendlyError,
                            retryPrompt = cleanPrompt,
                            isStreaming = false,
                            isError = true
                        )
                    } else item
                }
            } finally {
                _isChatResponding.value = false
            }
        }
    }

    private suspend fun runSafe(actionName: String, block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error during $actionName", e)
            _errorBanner.value = "No se pudo $actionName: ${e.localizedMessage ?: "Error inesperado"}"
        }
    }
}
