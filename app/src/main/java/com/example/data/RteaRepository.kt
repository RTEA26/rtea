package com.example.data

import android.content.Context
import com.example.R
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class RteaRepository(context: Context) {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.getString(R.string.firestore_database_id)
    )

    private fun userRoot(userId: String) = db.collection("users").document(userId)

    fun observeStudents(userId: String): Flow<List<Student>> =
        userRoot(userId).collection("students")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots(MetadataChanges.INCLUDE)
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Student::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
            }

    fun observeNotes(userId: String): Flow<List<QuickNote>> =
        userRoot(userId).collection("notes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots(MetadataChanges.INCLUDE)
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(QuickNote::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
            }

    fun observeEvaluations(userId: String): Flow<List<Evaluation>> =
        userRoot(userId).collection("evaluations")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots(MetadataChanges.INCLUDE)
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Evaluation::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
            }

    fun observeDiaryEntries(userId: String): Flow<List<DiaryEntry>> =
        userRoot(userId).collection("diaryEntries")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots(MetadataChanges.INCLUDE)
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DiaryEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
            }

    fun observeResourceFolders(userId: String): Flow<List<ResourceFolder>> =
        userRoot(userId).collection("resourceFolders")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots(MetadataChanges.INCLUDE)
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ResourceFolder::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
            }

    fun observeResources(userId: String): Flow<List<ResourceItem>> =
        userRoot(userId).collection("resources")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots(MetadataChanges.INCLUDE)
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(ResourceItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
            }

    fun observeLibraryItems(userId: String): Flow<List<LibraryItem>> =
        userRoot(userId).collection("libraryItems")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots(MetadataChanges.INCLUDE)
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(LibraryItem::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }
            }

    suspend fun ensureSeeded(userId: String) {
        val root = userRoot(userId)
        try {
            // Check server state across core collections so deleting sample students
            // or partial prior seeding never overwrites existing docs (which have createdAt).
            val hasStudents = !root.collection("students").limit(1).get(Source.SERVER).await().isEmpty
            if (hasStudents) return

            val hasFolders = !root.collection("resourceFolders").limit(1).get(Source.SERVER).await().isEmpty
            val hasNotes = !root.collection("notes").limit(1).get(Source.SERVER).await().isEmpty
            val hasLibrary = !root.collection("libraryItems").limit(1).get(Source.SERVER).await().isEmpty
            val hasEvaluations = !root.collection("evaluations").limit(1).get(Source.SERVER).await().isEmpty
            val hasResources = !root.collection("resources").limit(1).get(Source.SERVER).await().isEmpty
            val hasDiaries = !root.collection("diaryEntries").limit(1).get(Source.SERVER).await().isEmpty

            if (hasFolders || hasNotes || hasLibrary || hasEvaluations || hasResources || hasDiaries) {
                return
            }

            val seedItems = buildList {
                SeedData.defaultStudents.forEach { st ->
                    add(root.collection("students").document(st.id) to studentToMap(st, isCreate = true))
                }
                SeedData.defaultNotes.forEach { n ->
                    add(root.collection("notes").document(n.id) to noteToMap(n, isCreate = true))
                }
                SeedData.defaultEvaluations.forEach { ev ->
                    add(root.collection("evaluations").document(ev.id) to evaluationToMap(ev, isCreate = true))
                }
                SeedData.defaultFolders.forEach { f ->
                    add(root.collection("resourceFolders").document(f.id) to folderToMap(f, isCreate = true))
                }
                SeedData.defaultResources.forEach { r ->
                    add(root.collection("resources").document(r.id) to resourceToMap(r, isCreate = true))
                }
                SeedData.defaultDiaryEntries.forEach { d ->
                    add(root.collection("diaryEntries").document(d.id) to diaryToMap(d, isCreate = true))
                }
                SeedData.defaultLibraryItems.forEach { l ->
                    add(root.collection("libraryItems").document(l.id) to libraryToMap(l, isCreate = true))
                }
            }

            try {
                val batch = db.batch()
                seedItems.forEach { (ref, payload) -> batch.set(ref, payload) }
                batch.commit().await()
            } catch (e: FirebaseFirestoreException) {
                if (e.code != FirebaseFirestoreException.Code.PERMISSION_DENIED) throw e
                // If any individual document raced/existed, create only missing docs one by one.
                for ((ref, payload) in seedItems) {
                    val exists = runCatching { ref.get(Source.SERVER).await().exists() }.getOrDefault(false)
                    if (!exists) {
                        ref.set(payload).await()
                    }
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                // Offline on startup; skip seeding without showing an error banner.
                return
            }
            handleFirestoreError(e, OperationType.WRITE, "users/$userId/seed")
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "users/$userId/seed")
            throw e
        }
    }

    suspend fun saveStudent(userId: String, student: Student): String {
        val docId = student.id.ifBlank { "st_${UUID.randomUUID().toString().take(8)}" }
        val isCreate = student.id.isBlank()
        val path = "users/$userId/students/$docId"
        return try {
            val ref = userRoot(userId).collection("students").document(docId)
            if (isCreate) {
                ref.set(studentToMap(student, isCreate = true)).await()
            } else {
                val updates = studentToMap(student, isCreate = false).toMutableMap()
                updates.remove("createdAt")
                ref.update(updates).await()
            }
            docId
        } catch (e: Exception) {
            handleFirestoreError(e, if (isCreate) OperationType.CREATE else OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun deleteStudent(userId: String, studentId: String) {
        val path = "users/$userId/students/$studentId"
        try {
            userRoot(userId).collection("students").document(studentId).delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }

    suspend fun updateStudentProgressAreas(userId: String, studentId: String, areas: List<ProgressArea>) {
        val path = "users/$userId/students/$studentId"
        try {
            userRoot(userId).collection("students").document(studentId)
                .update(
                    mapOf(
                        "progressAreas" to areas.map {
                            mapOf(
                                "id" to it.id,
                                "title" to it.title,
                                "completedSteps" to it.completedSteps,
                                "totalSteps" to it.totalSteps,
                                "colorHex" to it.colorHex
                            )
                        },
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun updateStudentAiReport(userId: String, studentId: String, summary: String) {
        val path = "users/$userId/students/$studentId"
        try {
            userRoot(userId).collection("students").document(studentId)
                .update(
                    mapOf(
                        "aiReportSummary" to summary.take(7900),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun updateStudentDocsCount(
        userId: String,
        studentId: String,
        mainDelta: Int = 0,
        programDelta: Int = 0
    ) {
        val path = "users/$userId/students/$studentId"
        try {
            val ref = userRoot(userId).collection("students").document(studentId)
            val snap = ref.get().await()
            val curMain = (snap.getLong("mainReportDocsCount") ?: 0L).toInt()
            val curProg = (snap.getLong("programDocsCount") ?: 0L).toInt()
            ref.update(
                mapOf(
                    "mainReportDocsCount" to (curMain + mainDelta).coerceIn(0, 99),
                    "programDocsCount" to (curProg + programDelta).coerceIn(0, 99),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun addStudentDocument(
        userId: String,
        studentId: String,
        folderType: Int,
        doc: StudentDocument
    ) {
        val path = "users/$userId/students/$studentId"
        try {
            val ref = userRoot(userId).collection("students").document(studentId)
            val snap = ref.get().await()
            val student = snap.toObject(Student::class.java) ?: Student(id = studentId)
            val fieldName = if (folderType == 1) "mainReportDocs" else "programDocs"
            val countField = if (folderType == 1) "mainReportDocsCount" else "programDocsCount"
            val currentList = if (folderType == 1) student.mainReportDocs else student.programDocs
            val updatedList = (currentList + doc).takeLast(20)
            ref.update(
                mapOf(
                    fieldName to studentDocsToMaps(updatedList),
                    countField to updatedList.size,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun removeStudentDocument(
        userId: String,
        studentId: String,
        folderType: Int,
        docId: String
    ) {
        val path = "users/$userId/students/$studentId"
        try {
            val ref = userRoot(userId).collection("students").document(studentId)
            val snap = ref.get().await()
            val student = snap.toObject(Student::class.java) ?: Student(id = studentId)
            val fieldName = if (folderType == 1) "mainReportDocs" else "programDocs"
            val countField = if (folderType == 1) "mainReportDocsCount" else "programDocsCount"
            val currentList = if (folderType == 1) student.mainReportDocs else student.programDocs
            val updatedList = currentList.filterNot { it.id == docId }
            ref.update(
                mapOf(
                    fieldName to studentDocsToMaps(updatedList),
                    countField to updatedList.size,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    private fun studentDocsToMaps(docs: List<StudentDocument>): List<Map<String, Any>> =
        docs.take(20).map { d ->
            mapOf(
                "id" to d.id.take(64),
                "title" to d.title.take(140),
                "localPath" to d.localPath.take(500),
                "sizeLabel" to d.sizeLabel.take(30),
                "dateLabel" to d.dateLabel.take(40),
                "mimeType" to d.mimeType.ifBlank { "application/pdf" }.take(120)
            )
        }

    suspend fun updateStudentActiveResources(userId: String, studentId: String, resourceIds: List<String>) {
        val path = "users/$userId/students/$studentId"
        try {
            userRoot(userId).collection("students").document(studentId)
                .update(
                    mapOf(
                        "activeResourceIds" to resourceIds.distinct().take(30),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun updateStudentEvaluationConfig(
        userId: String,
        studentId: String,
        evalTemplate: String,
        evalReinforcement: String,
        evalMedal: String,
        evalScore: Int,
        evalDimensions: List<EvaluationDimension>,
        evalNotes: String,
        evalShareFamily: Boolean,
        evalRecordHistory: Boolean
    ) {
        val path = "users/$userId/students/$studentId"
        try {
            userRoot(userId).collection("students").document(studentId)
                .update(
                    mapOf(
                        "evalTemplate" to evalTemplate.take(60),
                        "evalReinforcement" to evalReinforcement.take(300),
                        "evalMedal" to (evalMedal.takeIf { it in setOf("BRONZE", "SILVER", "GOLD") } ?: "GOLD"),
                        "evalScore" to evalScore.coerceIn(1, 5),
                        "evalDimensions" to evalDimensions.take(12).map {
                            mapOf(
                                "id" to it.id,
                                "title" to it.title.take(80),
                                "subtitle" to it.subtitle.take(160),
                                "score" to it.score.coerceIn(1, 5),
                                "colorHex" to it.colorHex.take(16)
                            )
                        },
                        "evalNotes" to evalNotes.take(1200),
                        "evalShareFamily" to evalShareFamily,
                        "evalRecordHistory" to evalRecordHistory,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun saveNote(userId: String, note: QuickNote) {
        val docId = note.id.ifBlank { "note_${UUID.randomUUID().toString().take(8)}" }
        val isCreate = note.id.isBlank()
        val path = "users/$userId/notes/$docId"
        try {
            val ref = userRoot(userId).collection("notes").document(docId)
            if (isCreate) {
                ref.set(noteToMap(note, isCreate = true)).await()
            } else {
                val updates = noteToMap(note, isCreate = false).toMutableMap()
                updates.remove("createdAt")
                ref.update(updates).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, if (isCreate) OperationType.CREATE else OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun deleteNote(userId: String, noteId: String) {
        val path = "users/$userId/notes/$noteId"
        try {
            userRoot(userId).collection("notes").document(noteId).delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }

    suspend fun saveEvaluation(userId: String, evaluation: Evaluation) {
        val docId = evaluation.id.ifBlank { "eval_${UUID.randomUUID().toString().take(8)}" }
        val isCreate = evaluation.id.isBlank()
        val path = "users/$userId/evaluations/$docId"
        try {
            val ref = userRoot(userId).collection("evaluations").document(docId)
            if (isCreate) {
                ref.set(evaluationToMap(evaluation, isCreate = true)).await()
            } else {
                val updates = evaluationToMap(evaluation, isCreate = false).toMutableMap()
                updates.remove("createdAt")
                ref.update(updates).await()
            }
            if (!evaluation.draft && evaluation.studentId.isNotBlank()) {
                userRoot(userId).collection("students").document(evaluation.studentId)
                    .update(
                        mapOf(
                            "lastSessionLabel" to "Hoy · Evaluación registrada",
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, if (isCreate) OperationType.CREATE else OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun saveDiaryEntry(userId: String, entry: DiaryEntry) {
        val docId = entry.id.ifBlank {
            "diary_${entry.studentId}_${entry.dateKey.replace("-", "_")}".take(120)
        }
        val path = "users/$userId/diaryEntries/$docId"
        try {
            val ref = userRoot(userId).collection("diaryEntries").document(docId)
            val snap = ref.get().await()
            if (!snap.exists()) {
                ref.set(diaryToMap(entry, isCreate = true)).await()
            } else {
                val updates = diaryToMap(entry, isCreate = false).toMutableMap()
                updates.remove("createdAt")
                ref.update(updates).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    suspend fun saveResourceFolder(userId: String, folder: ResourceFolder) {
        val docId = folder.id.ifBlank { "folder_${UUID.randomUUID().toString().take(8)}" }
        val isCreate = folder.id.isBlank()
        val path = "users/$userId/resourceFolders/$docId"
        try {
            val ref = userRoot(userId).collection("resourceFolders").document(docId)
            if (isCreate) {
                ref.set(folderToMap(folder, isCreate = true)).await()
            } else {
                val updates = folderToMap(folder, isCreate = false).toMutableMap()
                updates.remove("createdAt")
                ref.update(updates).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, if (isCreate) OperationType.CREATE else OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun saveResource(userId: String, resource: ResourceItem) {
        val docId = resource.id.ifBlank { "res_${UUID.randomUUID().toString().take(8)}" }
        val isCreate = resource.id.isBlank()
        val path = "users/$userId/resources/$docId"
        try {
            val ref = userRoot(userId).collection("resources").document(docId)
            if (isCreate) {
                ref.set(resourceToMap(resource, isCreate = true)).await()
            } else {
                val updates = resourceToMap(resource, isCreate = false).toMutableMap()
                updates.remove("createdAt")
                ref.update(updates).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, if (isCreate) OperationType.CREATE else OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun deleteResource(userId: String, resourceId: String) {
        val path = "users/$userId/resources/$resourceId"
        try {
            userRoot(userId).collection("resources").document(resourceId).delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }

    suspend fun saveLibraryItem(userId: String, item: LibraryItem) {
        val docId = item.id.ifBlank { "lib_${UUID.randomUUID().toString().take(8)}" }
        val isCreate = item.id.isBlank()
        val path = "users/$userId/libraryItems/$docId"
        try {
            val ref = userRoot(userId).collection("libraryItems").document(docId)
            if (isCreate) {
                ref.set(libraryToMap(item, isCreate = true)).await()
            } else {
                val updates = libraryToMap(item, isCreate = false).toMutableMap()
                updates.remove("createdAt")
                ref.update(updates).await()
            }
        } catch (e: Exception) {
            handleFirestoreError(e, if (isCreate) OperationType.CREATE else OperationType.UPDATE, path)
            throw e
        }
    }

    suspend fun deleteLibraryItem(userId: String, itemId: String) {
        val path = "users/$userId/libraryItems/$itemId"
        try {
            userRoot(userId).collection("libraryItems").document(itemId).delete().await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            throw e
        }
    }

    private fun studentToMap(s: Student, isCreate: Boolean): Map<String, Any> = buildMap {
        put("fullName", s.fullName.take(120))
        put("preferredName", s.preferredName.take(60))
        put("classroom", s.classroom.ifBlank { "Aula 3-B" }.take(80))
        put("avatarIndex", s.avatarIndex.coerceIn(1, 14))
        put("customAvatarUri", s.customAvatarUri.take(500))
        put("supportLevel", s.supportLevel.coerceIn(1, 3))
        put("interests", s.interests.take(15))
        put("commSystem", s.commSystem.take(140))
        put("sensoryNotes", s.sensoryNotes.take(1200))
        put("lastSessionLabel", s.lastSessionLabel.take(80))
        put("progressAreas", s.progressAreas.take(12).map {
            mapOf(
                "id" to it.id,
                "title" to it.title,
                "completedSteps" to it.completedSteps,
                "totalSteps" to it.totalSteps,
                "colorHex" to it.colorHex
            )
        })
        put("mainReportDocsCount", s.mainReportDocsCount.coerceIn(0, 99))
        put("programDocsCount", s.programDocsCount.coerceIn(0, 99))
        put("mainReportDocs", studentDocsToMaps(s.mainReportDocs))
        put("programDocs", studentDocsToMaps(s.programDocs))
        put("aiReportSummary", s.aiReportSummary.take(8000))
        put("activeResourceIds", s.activeResourceIds.take(30))
        put("evalTemplate", s.evalTemplate.ifBlank { "Sensorial" }.take(60))
        put("evalReinforcement", s.evalReinforcement.take(300))
        put("evalMedal", s.evalMedal.takeIf { it in setOf("BRONZE", "SILVER", "GOLD") } ?: "GOLD")
        put("evalScore", s.evalScore.coerceIn(1, 5))
        put("evalDimensions", s.effectiveEvalDimensions.take(12).map {
            mapOf(
                "id" to it.id,
                "title" to it.title.take(80),
                "subtitle" to it.subtitle.take(160),
                "score" to it.score.coerceIn(1, 5),
                "colorHex" to it.colorHex.take(16)
            )
        })
        put("evalNotes", s.evalNotes.take(1200))
        put("evalShareFamily", s.evalShareFamily)
        put("evalRecordHistory", s.evalRecordHistory)
        if (isCreate) put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }

    private fun noteToMap(n: QuickNote, isCreate: Boolean): Map<String, Any> = buildMap {
        put("category", n.category)
        put("tagLabel", n.tagLabel.take(50))
        put("timeLabel", n.timeLabel.take(50))
        put("content", n.content.take(1000))
        put("studentName", n.studentName.take(100))
        put("contextLabel", n.contextLabel.take(100))
        put("completed", n.completed)
        put("replyText", n.replyText.take(500))
        if (isCreate) put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }

    private fun evaluationToMap(e: Evaluation, isCreate: Boolean): Map<String, Any> = buildMap {
        put("studentId", e.studentId.take(128))
        put("studentName", e.studentName.take(120))
        put("medalType", e.medalType)
        put("overallScore", e.overallScore.coerceIn(1, 5))
        put("dimensionScores", e.dimensionScores.take(12).map {
            mapOf(
                "id" to it.id,
                "title" to it.title,
                "subtitle" to it.subtitle,
                "score" to it.score,
                "colorHex" to it.colorHex
            )
        })
        put("templateCategory", e.templateCategory.take(60))
        put("notes", e.notes.take(2000))
        put("shareWithFamily", e.shareWithFamily)
        put("recordInHistory", e.recordInHistory)
        put("draft", e.draft)
        put("dateLabel", e.dateLabel.ifBlank { "Hoy" }.take(80))
        if (isCreate) put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }

    private fun diaryToMap(d: DiaryEntry, isCreate: Boolean): Map<String, Any> = buildMap {
        put("studentId", d.studentId.take(128))
        put("studentName", d.studentName.take(120))
        put("dateKey", d.dateKey.take(30))
        put("dayLabel", d.dayLabel.take(40))
        put("emotionalState", d.emotionalState.take(80))
        put("emotionalSubtitle", d.emotionalSubtitle.take(200))
        put("medalLabel", d.medalLabel.take(60))
        put("dayIndicator", d.dayIndicator)
        put("anticipationTitle", d.anticipationTitle.take(120))
        put("anticipationTime", d.anticipationTime.take(40))
        put("anticipationText", d.anticipationText.take(600))
        put("areas", d.areas.take(12).map {
            mapOf(
                "id" to it.id,
                "title" to it.title,
                "badgeLabel" to it.badgeLabel,
                "objective" to it.objective,
                "observation" to it.observation,
                "nextSession" to it.nextSession,
                "accentKey" to it.accentKey
            )
        })
        put("internalNotes", d.internalNotes.take(30).map {
            mapOf(
                "id" to it.id,
                "authorName" to it.authorName,
                "authorRole" to it.authorRole,
                "timeLabel" to it.timeLabel,
                "text" to it.text,
                "tag" to it.tag,
                "accentKey" to it.accentKey
            )
        })
        put("afternoonRoutine", d.afternoonRoutine.take(12).map {
            mapOf(
                "id" to it.id,
                "time" to it.time,
                "title" to it.title,
                "subtitle" to it.subtitle,
                "status" to it.status,
                "accentKey" to it.accentKey
            )
        })
        put("syncedLabel", d.syncedLabel.take(40))
        if (isCreate) put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }

    private fun folderToMap(f: ResourceFolder, isCreate: Boolean): Map<String, Any> = buildMap {
        put("name", f.name.take(80))
        put("subtitle", f.subtitle.take(120))
        put("accentKey", f.accentKey.take(30))
        put("iconKey", f.iconKey.take(30))
        if (isCreate) put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }

    private fun resourceToMap(r: ResourceItem, isCreate: Boolean): Map<String, Any> = buildMap {
        put("folderId", r.folderId.take(128))
        put("title", r.title.take(140))
        put("description", r.description.take(600))
        put("fileType", r.fileType.takeIf { it in setOf("PDF", "PNG", "MP3", "DOC") } ?: "DOC")
        put("sizeLabel", r.sizeLabel.take(30))
        put("dateLabel", r.dateLabel.take(40))
        put("statusBadge", r.statusBadge.take(40))
        put("locationBadge", r.locationBadge.take(40))
        put("assignedStudentIds", r.assignedStudentIds.take(100))
        put("localUri", r.localUri.take(500))
        put("mimeType", r.mimeType.take(120))
        put("previewSummary", r.previewSummary.take(2000))
        if (isCreate) put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }

    private fun libraryToMap(l: LibraryItem, isCreate: Boolean): Map<String, Any> = buildMap {
        put("title", l.title.take(160))
        put("author", l.author.take(120))
        put("materialType", l.materialType)
        put("tags", l.tags.take(10))
        put("metaInfo", l.metaInfo.take(120))
        put("pedagogicalTitle", l.pedagogicalTitle.take(60))
        put("reviewNote", l.reviewNote.take(2000))
        put("assignedStudentIds", l.assignedStudentIds.take(100))
        put("usageMoments", l.usageMoments.take(10))
        put("accentKey", l.accentKey.take(30))
        if (isCreate) put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }
}
