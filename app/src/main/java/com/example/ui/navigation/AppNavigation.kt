package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.SeedData
import com.example.data.UiState
import com.example.ui.components.AiReportViewerDialog
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.ProfileAccountDialog
import com.example.ui.components.RteaBottomBar
import com.example.ui.components.RteaNavigationRail
import com.example.ui.components.RteaTab
import com.example.ui.components.RteaTopBar
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BibliotecaScreen
import com.example.ui.screens.ChatbotScreen
import com.example.ui.screens.ClaseScreen
import com.example.ui.screens.DiarioScreen
import com.example.ui.screens.EvaluacionScreen
import com.example.ui.screens.InicioScreen
import com.example.ui.screens.RecursosScreen
import com.example.ui.screens.RegistroAlumnoScreen
import com.example.ui.theme.CanvasIvory
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.TextMuted
import com.example.viewmodel.RteaViewModel

object RteaRoutes {
    const val INICIO = "inicio"
    const val CLASE = "clase"
    const val DIARIO = "diario"
    const val RECURSOS = "recursos"
    const val REGISTRO_ALUMNO = "registro_alumno"
    const val EVALUACION = "evaluacion"
    const val BIBLIOTECA = "biblioteca"
    const val CHATBOT = "chatbot"
}

@Composable
fun AppNavigation(
    viewModel: RteaViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    if (currentUser == null) {
        AuthScreen(
            isLoading = authLoading,
            errorMessage = authError,
            onGoogleSignInClick = { viewModel.signInWithGoogle(context) },
            onAttemptSilentSignIn = viewModel::trySilentSignInIfNeeded
        )
        return
    }

    val errorBanner by viewModel.errorBanner.collectAsStateWithLifecycle()
    val selectedStudentId by viewModel.selectedStudentId.collectAsStateWithLifecycle()
    val generatingReportForId by viewModel.generatingReportForId.collectAsStateWithLifecycle()
    val aiReportViewerState by viewModel.aiReportViewerState.collectAsStateWithLifecycle()
    val groundedSearchState by viewModel.groundedSearchState.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatResponding by viewModel.isChatResponding.collectAsStateWithLifecycle()
    val chatFocusStudentId by viewModel.chatFocusStudentId.collectAsStateWithLifecycle()
    val chatRole by viewModel.chatRole.collectAsStateWithLifecycle()
    val useFastChatModel by viewModel.useFastChatModel.collectAsStateWithLifecycle()

    val studentsState by viewModel.studentsState.collectAsStateWithLifecycle()
    val notesState by viewModel.notesState.collectAsStateWithLifecycle()
    val evaluationsState by viewModel.evaluationsState.collectAsStateWithLifecycle()
    val diaryEntriesState by viewModel.diaryEntriesState.collectAsStateWithLifecycle()
    val foldersState by viewModel.foldersState.collectAsStateWithLifecycle()
    val resourcesState by viewModel.resourcesState.collectAsStateWithLifecycle()
    val libraryState by viewModel.libraryState.collectAsStateWithLifecycle()

    val students = (studentsState as? UiState.Success)?.data ?: emptyList()
    val notes = (notesState as? UiState.Success)?.data ?: emptyList()
    val evaluations = (evaluationsState as? UiState.Success)?.data ?: emptyList()
    val diaryEntries = (diaryEntriesState as? UiState.Success)?.data ?: emptyList()
    val folders = (foldersState as? UiState.Success)?.data ?: SeedData.defaultFolders
    val resources = (resourcesState as? UiState.Success)?.data ?: emptyList()
    val libraryItems = (libraryState as? UiState.Success)?.data ?: emptyList()

    val selectedStudent = students.firstOrNull { it.id == selectedStudentId } ?: students.firstOrNull()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: RteaRoutes.INICIO

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val activeTab = when {
        currentRoute.startsWith(RteaRoutes.CLASE) ||
            currentRoute.startsWith(RteaRoutes.REGISTRO_ALUMNO) ||
            currentRoute.startsWith(RteaRoutes.EVALUACION) -> RteaTab.CLASE
        currentRoute.startsWith(RteaRoutes.DIARIO) -> RteaTab.DIARIO
        currentRoute.startsWith(RteaRoutes.RECURSOS) ||
            currentRoute.startsWith(RteaRoutes.BIBLIOTECA) -> RteaTab.RECURSOS
        else -> RteaTab.INICIO
    }

    val isChatbotScreen = currentRoute.startsWith(RteaRoutes.CHATBOT)

    val screenTitle = when {
        currentRoute.startsWith(RteaRoutes.CLASE) -> "Clase"
        currentRoute.startsWith(RteaRoutes.REGISTRO_ALUMNO) -> "Clase"
        currentRoute.startsWith(RteaRoutes.EVALUACION) -> "Evaluación"
        currentRoute.startsWith(RteaRoutes.DIARIO) -> "Diario Escolar"
        currentRoute.startsWith(RteaRoutes.RECURSOS) -> "Recursos R-TEA"
        currentRoute.startsWith(RteaRoutes.BIBLIOTECA) -> "Biblioteca R-TEA"
        currentRoute.startsWith(RteaRoutes.CHATBOT) -> "Chatbot Gemini"
        else -> "Inicio"
    }

    val navigateToTopTab: (RteaTab) -> Unit = { tab ->
        navController.navigate(tab.route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val density = LocalDensity.current
    var isWide by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasIvory)
            .onSizeChanged { size ->
                val widthDp = with(density) { size.width.toDp() }
                if ((widthDp >= 600.dp) != isWide) {
                    isWide = widthDp >= 600.dp
                }
            }
    ) {

        Scaffold(
            containerColor = CanvasIvory,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (!isChatbotScreen) {
                    RteaTopBar(
                        screenTitle = screenTitle,
                        user = currentUser,
                        pendingNotificationsCount = notes.count { !it.completed },
                        onNotificationsClick = { showNotificationsDialog = true },
                        onProfileClick = { showProfileDialog = true }
                    )
                }
            },
            bottomBar = {
                if (!isWide && !isChatbotScreen) {
                    RteaBottomBar(
                        activeTab = activeTab,
                        onSelectTab = navigateToTopTab
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isWide && !isChatbotScreen) {
                    RteaNavigationRail(
                        activeTab = activeTab,
                        onSelectTab = navigateToTopTab
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (errorBanner != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 720.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ErrorContainer)
                                .border(1.dp, ErrorRed.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                                Text(errorBanner.orEmpty(), style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                            }
                            IconButton(onClick = { viewModel.dismissErrorBanner() }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.Close, contentDescription = "Cerrar aviso", tint = ErrorRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    if (studentsState is UiState.Loading && students.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(color = PrimaryBlue)
                                Text(
                                    "Sincronizando tu aula R-TEA en tiempo real...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextMuted
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 720.dp)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = RteaRoutes.INICIO,
                                enterTransition = {
                                    fadeIn(tween(240)) + slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Start,
                                        tween(260)
                                    )
                                },
                                exitTransition = {
                                    fadeOut(tween(180)) + slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Start,
                                        tween(260)
                                    )
                                },
                                popEnterTransition = {
                                    fadeIn(tween(240)) + slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.End,
                                        tween(260)
                                    )
                                },
                                popExitTransition = {
                                    fadeOut(tween(180)) + slideOutOfContainer(
                                        AnimatedContentTransitionScope.SlideDirection.End,
                                        tween(260)
                                    )
                                }
                            ) {
                                composable(RteaRoutes.INICIO) {
                                    InicioScreen(
                                        teacherName = currentUser?.displayName ?: "Carmen",
                                        students = students,
                                        notes = notes,
                                        libraryItems = libraryItems,
                                        chatMessagesCount = chatMessages.size,
                                        onOpenChatbot = { navController.navigate(RteaRoutes.CHATBOT) },
                                        onNavigateToClase = { navigateToTopTab(RteaTab.CLASE) },
                                        onNavigateToDiario = { navigateToTopTab(RteaTab.DIARIO) },
                                        onNavigateToRecursos = { navigateToTopTab(RteaTab.RECURSOS) },
                                        onNavigateToBiblioteca = { navController.navigate(RteaRoutes.BIBLIOTECA) },
                                        onAddNote = viewModel::addQuickNote,
                                        onToggleNoteCompleted = viewModel::toggleNoteCompleted,
                                        onReplyToNote = viewModel::replyToNote,
                                        onDeleteNote = viewModel::deleteNote,
                                        onSaveQuickLog = viewModel::saveQuickLogToDiary
                                    )
                                }

                                composable(RteaRoutes.CLASE) {
                                    ClaseScreen(
                                        students = students,
                                        resources = resources,
                                        generatingReportForId = generatingReportForId,
                                        onRegisterStudentClick = {
                                            navController.navigate(RteaRoutes.REGISTRO_ALUMNO)
                                        },
                                        onEditStudentClick = { st ->
                                            navController.navigate("${RteaRoutes.REGISTRO_ALUMNO}?studentId=${st.id}")
                                        },
                                        onDeleteStudent = viewModel::deleteStudent,
                                        onOpenEvaluation = { st ->
                                            viewModel.selectStudent(st.id)
                                            navController.navigate("${RteaRoutes.EVALUACION}?studentId=${st.id}")
                                        },
                                        onOpenDiario = { st ->
                                            viewModel.selectStudent(st.id)
                                            navigateToTopTab(RteaTab.DIARIO)
                                        },
                                        onAdjustArea = viewModel::adjustProgressArea,
                                        onAddArea = viewModel::addProgressArea,
                                        onRemoveArea = viewModel::removeProgressArea,
                                        onUpdateFolderCount = viewModel::updateStudentFolderCount,
                                        onUploadStudentPdf = viewModel::uploadStudentPdf,
                                        onDeleteStudentPdf = viewModel::deleteStudentPdf,
                                        onOpenAiViewer = viewModel::openAiViewer,
                                        onGenerateAiReport = viewModel::generateAiReport,
                                        onAssignResourceToStudent = viewModel::assignResourceToStudent,
                                        onEmitToast = viewModel::emitToast
                                    )
                                }

                                composable(RteaRoutes.DIARIO) {
                                    DiarioScreen(
                                        students = students,
                                        selectedStudent = selectedStudent,
                                        diaryEntries = diaryEntries,
                                        onSelectStudent = viewModel::selectStudent,
                                        onUpdateEmotionalState = viewModel::updateDiaryEmotionalState,
                                        onUpdateAnticipation = viewModel::updateDiaryAnticipation,
                                        onAddDiaryArea = viewModel::addDiaryArea,
                                        onRemoveDiaryArea = viewModel::removeDiaryArea,
                                        onAddInternalNote = viewModel::addDiaryInternalNote,
                                        onUpdateInternalNote = viewModel::updateDiaryInternalNote,
                                        onDeleteInternalNote = viewModel::deleteDiaryInternalNote,
                                        onToggleRoutineStep = viewModel::toggleRoutineStep
                                    )
                                }

                                composable(RteaRoutes.RECURSOS) {
                                    RecursosScreen(
                                        folders = folders,
                                        resources = resources,
                                        students = students,
                                        onAddFolder = viewModel::addResourceFolder,
                                        onImportDeviceFile = viewModel::importDeviceFileForResource,
                                        onDiscardTempFile = viewModel::discardTempLocalFile,
                                        onAddResource = viewModel::addResource,
                                        onUpdateAssignments = viewModel::updateResourceAssignments,
                                        onDeleteResource = viewModel::deleteResource,
                                        onOpenBiblioteca = { navController.navigate(RteaRoutes.BIBLIOTECA) },
                                        onEmitToast = viewModel::emitToast
                                    )
                                }

                                composable(
                                    route = "${RteaRoutes.REGISTRO_ALUMNO}?studentId={studentId}",
                                    arguments = listOf(
                                        navArgument("studentId") {
                                            type = NavType.StringType
                                            nullable = true
                                            defaultValue = null
                                        }
                                    )
                                ) { backStack ->
                                    val argId = backStack.arguments?.getString("studentId")
                                    val existing = students.firstOrNull { it.id == argId }
                                    RegistroAlumnoScreen(
                                        existingStudent = existing,
                                        onBack = { navController.popBackStack() },
                                        onSaveStudent = { st ->
                                            viewModel.saveStudentProfile(st) {
                                                navController.popBackStack()
                                            }
                                        }
                                    )
                                }

                                composable(
                                    route = "${RteaRoutes.EVALUACION}?studentId={studentId}",
                                    arguments = listOf(
                                        navArgument("studentId") {
                                            type = NavType.StringType
                                            nullable = true
                                            defaultValue = null
                                        }
                                    )
                                ) { backStack ->
                                    val argId = backStack.arguments?.getString("studentId")
                                    val targetStudent = students.firstOrNull { it.id == argId } ?: selectedStudent
                                    EvaluacionScreen(
                                        students = students,
                                        selectedStudent = targetStudent,
                                        evaluations = evaluations,
                                        onSelectStudent = viewModel::selectStudent,
                                        onBack = { navController.popBackStack() },
                                        onSaveEvaluationConfig = viewModel::updateStudentEvaluationConfig,
                                        onSubmitEvaluation = viewModel::submitEvaluation,
                                        onEmitToast = viewModel::emitToast
                                    )
                                }

                                composable(RteaRoutes.BIBLIOTECA) {
                                    BibliotecaScreen(
                                        libraryItems = libraryItems,
                                        students = students,
                                        groundedSearchState = groundedSearchState,
                                        onBack = { navController.popBackStack() },
                                        onAddLibraryItem = viewModel::addLibraryItem,
                                        onUpdateAssignments = viewModel::updateLibraryAssignments,
                                        onDeleteLibraryItem = viewModel::deleteLibraryItem,
                                        onSearchEvidence = viewModel::searchEvidenceWithGoogle,
                                        onClearSearch = viewModel::clearGroundedSearch,
                                        onEmitToast = viewModel::emitToast
                                    )
                                }

                                composable(RteaRoutes.CHATBOT) {
                                    ChatbotScreen(
                                        students = students,
                                        messages = chatMessages,
                                        isResponding = isChatResponding,
                                        activeRole = chatRole,
                                        useFastModel = useFastChatModel,
                                        focusedStudentId = chatFocusStudentId,
                                        onSelectRole = viewModel::setChatRole,
                                        onToggleFastModel = viewModel::toggleFastChatModel,
                                        onSelectStudent = viewModel::setChatFocusStudent,
                                        onSendMessage = viewModel::sendChatMessage,
                                        onRetryMessage = viewModel::retryChatMessage,
                                        onClearChat = viewModel::clearChatHistory,
                                        onBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNotificationsDialog) {
        NotificationsDialog(
            notes = notes,
            onDismiss = { showNotificationsDialog = false },
            onMarkDone = viewModel::toggleNoteCompleted
        )
    }

    if (showProfileDialog) {
        ProfileAccountDialog(
            user = currentUser,
            studentCount = students.size,
            onDismiss = { showProfileDialog = false },
            onSignOut = viewModel::signOut
        )
    }

    if (aiReportViewerState != null) {
        val (name, report) = aiReportViewerState!!
        AiReportViewerDialog(
            studentName = name,
            reportText = report,
            onDismiss = { viewModel.dismissAiViewer() }
        )
    }
}
