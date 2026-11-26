package com.example.data

object SeedData {
    val defaultStudents: List<Student> = listOf(
        Student(
            id = "mateo",
            fullName = "Mateo García",
            preferredName = "Mate",
            classroom = "Aula 3-B · Primaria Integrada",
            avatarIndex = 1,
            supportLevel = 1,
            interests = listOf("Dinosaurios", "Puzzles"),
            commSystem = "PECS / Cuaderno de Pictogramas Digital",
            sensoryNotes = "Hipersensibilidad táctil leve en manga derecha; utiliza auriculares de cancelación en patio.",
            lastSessionLabel = "Hoy, 10:30 h",
            mainReportDocsCount = 2,
            programDocsCount = 1,
            aiReportSummary = "Avances excelentes en contacto visual y regulación sensorial con auriculares. Consolidando autonomía en lavado (78%). Atención en transiciones al comedor escolar.",
            activeResourceIds = listOf("res_lavado", "res_comedor"),
            progressAreas = listOf(
                ProgressArea("a1", "Autonomía & Vida Diaria", 14, 18, "#4BA2E8"),
                ProgressArea("a2", "Regulación Sensorial", 13, 20, "#4DB896"),
                ProgressArea("a3", "Comunicación Funcional", 16, 19, "#FF9D6C"),
                ProgressArea("a4", "Flexibilidad Social", 9, 15, "#9581E6")
            )
        ),
        Student(
            id = "sofia",
            fullName = "Sofía López",
            preferredName = "Sofi",
            classroom = "Aula 3-B · Primaria Integrada",
            avatarIndex = 3,
            supportLevel = 2,
            interests = listOf("Música", "Pintura suave"),
            commSystem = "Lenguaje Verbal con apoyo de pictogramas",
            sensoryNotes = "Responde muy bien a anticipadores visuales de reloj de arena y música ambiental.",
            lastSessionLabel = "Ayer, 16:00 h",
            mainReportDocsCount = 3,
            programDocsCount = 1,
            aiReportSummary = "Excelente respuesta a apoyos visuales de comedor y juego simbólico compartido (10 min). Reforzar comunicación espontánea en cambios de actividad.",
            activeResourceIds = listOf("res_comedor", "res_cepillado"),
            progressAreas = listOf(
                ProgressArea("s1", "Autonomía & Vida Diaria", 11, 18, "#4BA2E8"),
                ProgressArea("s2", "Regulación Sensorial", 15, 20, "#4DB896"),
                ProgressArea("s3", "Comunicación Funcional", 11, 19, "#FF9D6C"),
                ProgressArea("s4", "Atención Sostenida", 9, 12, "#F7CA55"),
                ProgressArea("s5", "Flexibilidad Social", 9, 15, "#9581E6")
            )
        ),
        Student(
            id = "lucas",
            fullName = "Lucas Morales",
            preferredName = "Luqui",
            classroom = "Aula 3-B · Primaria Integrada",
            avatarIndex = 4,
            supportLevel = 1,
            interests = listOf("Astronomía", "Trenes"),
            commSystem = "Dispositivo SAAC de alta tecnología",
            sensoryNotes = "Estrena cascos antirruido en el patio de las 11:30. Avisar con 5 min de antelación antes de timbre.",
            lastSessionLabel = "12 Mayo, 09:15 h",
            mainReportDocsCount = 1,
            programDocsCount = 2,
            aiReportSummary = "Progreso motriz fino consolidado. Usa cascos antirruido de forma autónoma al percibir saturación acústica en recreo.",
            activeResourceIds = listOf("res_lavado"),
            progressAreas = listOf(
                ProgressArea("l1", "Autonomía & Vida Diaria", 15, 18, "#4BA2E8"),
                ProgressArea("l2", "Regulación Sensorial", 14, 20, "#4DB896"),
                ProgressArea("l3", "Motricidad Fina", 12, 14, "#76A8D8"),
                ProgressArea("l4", "Comunicación Funcional", 14, 19, "#FF9D6C"),
                ProgressArea("l5", "Flexibilidad Social", 10, 15, "#9581E6")
            )
        )
    )

    val defaultNotes: List<QuickNote> = listOf(
        QuickNote(
            id = "note_1",
            category = "ANTICIPACION",
            tagLabel = "ANTICIPACIÓN",
            timeLabel = "11:30 Recreo",
            content = "Recordatorio patio: Lucas estrena cascos antirruido en el recreo de las 11:30. Avisar a monitores para guiar la autorregulación.",
            studentName = "Lucas M.",
            contextLabel = "",
            completed = false
        ),
        QuickNote(
            id = "note_2",
            category = "MATERIALES",
            tagLabel = "MATERIALES",
            timeLabel = "Pendiente",
            content = "Faltan plastificar 4 pictogramas de la secuencia de comedor para Sofía (bandeja, vaso, servilleta, cubiertos).",
            studentName = "Sofía L.",
            contextLabel = "Rincón de Autonomía",
            completed = false
        ),
        QuickNote(
            id = "note_3",
            category = "SENSORIAL",
            tagLabel = "SENSORIAL / BIENESTAR",
            timeLabel = "Hoy",
            content = "Mateo hoy ha llegado con hipersensibilidad táctil (no tocar manga derecha ni insistir con texturas húmedas en plástica).",
            studentName = "Mateo G.",
            contextLabel = "Reporte de familia",
            completed = false
        )
    )

    val defaultEvaluations: List<Evaluation> = listOf(
        Evaluation(
            id = "eval_1",
            studentId = "mateo",
            studentName = "Mateo García",
            medalType = "BRONZE",
            overallScore = 3,
            templateCategory = "Autonomía",
            notes = "Autonomía en Lavado: Mateo G. completó la secuencia de 4 pasos sin instigación física.",
            shareWithFamily = true,
            recordInHistory = true,
            draft = false,
            dateLabel = "Ayer, 16:20",
            dimensionScores = listOf(
                EvaluationDimension("d1", "Atención y Enfoque", "Sostuvo 18 minutos en puzzle", 4, "#3F82B8"),
                EvaluationDimension("d2", "Regulación Sensorial", "Autorregulación óptima con cascos", 5, "#2FA37A")
            )
        ),
        Evaluation(
            id = "eval_2",
            studentId = "sofia",
            studentName = "Sofía López",
            medalType = "GOLD",
            overallScore = 5,
            templateCategory = "Social / Juego",
            notes = "Atención Compartida: Sofía L. mantuvo contacto visual y turno en juego simbólico 10 min.",
            shareWithFamily = true,
            recordInHistory = true,
            draft = false,
            dateLabel = "21 Octubre",
            dimensionScores = listOf(
                EvaluationDimension("d1", "Interacción y Turno", "Compartió piezas amablemente", 5, "#FF8A5C"),
                EvaluationDimension("d2", "Flexibilidad ante Cambios", "Aceptó el cambio con apoyo visual", 4, "#7C5CC7")
            )
        )
    )

    val defaultFolders: List<ResourceFolder> = listOf(
        ResourceFolder("folder_autonomia", "Autonomía", "Rutinas paso a paso de vida cotidiana", "peach", "self_improvement"),
        ResourceFolder("folder_sensorial", "Reg. Sensorial", "Calma & Pausas", "mint", "spa"),
        ResourceFolder("folder_pictos", "Pictos & Rutinas", "Paneles visuales", "sky", "view_timeline"),
        ResourceFolder("folder_pecs", "PECS & Habla", "Cuadernos CAA", "lavender", "forum"),
        ResourceFolder("folder_juego", "Juego Social", "Turnos y recreo", "coral", "toys")
    )

    val defaultResources: List<ResourceItem> = listOf(
        ResourceItem(
            id = "res_lavado",
            folderId = "folder_autonomia",
            title = "Secuencia_Lavado_Manos_3Pasos.pdf",
            description = "Secuencia anticipatoria para el baño del aula y hogar con pictogramas de apoyo.",
            fileType = "PDF",
            sizeLabel = "1.4 MB",
            dateLabel = "Hoy, 09:30",
            statusBadge = "Listo imprimir",
            locationBadge = "Activo en Tablet",
            assignedStudentIds = listOf("mateo", "sofia"),
            previewSummary = "1. Subir mangas y abrir el grifo suave.\n2. Aplicar una dosis de jabón y frotar 20 segundos.\n3. Aclarar y secar con toalla personal."
        ),
        ResourceItem(
            id = "res_comedor",
            folderId = "folder_autonomia",
            title = "Menu_Comedor_Pictos.png",
            description = "Tablero de elecciones visuales de comida para mesa de integración.",
            fileType = "PNG",
            sizeLabel = "840 KB",
            dateLabel = "Ayer, 16:45",
            statusBadge = "En Uso",
            locationBadge = "Mesa 3",
            assignedStudentIds = listOf("mateo", "sofia", "lucas"),
            previewSummary = "Tablero de pictogramas ARASAAC con opciones visuales de primer plato, segundo plato, agua y fruta."
        ),
        ResourceItem(
            id = "res_vestirse",
            folderId = "folder_autonomia",
            title = "Guia_Vestirse_Autonomo.pdf",
            description = "Desglose encadenado hacia atrás: abrigo, cremallera y calzado con velcro.",
            fileType = "PDF",
            sizeLabel = "3.1 MB",
            dateLabel = "12 May",
            statusBadge = "Ficha A4",
            locationBadge = "Perchero Aula",
            assignedStudentIds = emptyList(),
            previewSummary = "Secuencia de 5 tarjetas plastificables para transición de abrigo y mochila al finalizar la jornada."
        ),
        ResourceItem(
            id = "res_cepillado",
            folderId = "folder_sensorial",
            title = "Audio_Cancion_Cepillado_2Min.mp3",
            description = "Temporizador sensorial rítmico con aviso no estridente de cambio de cuadrante.",
            fileType = "MP3",
            sizeLabel = "2.8 MB",
            dateLabel = "08 May",
            statusBadge = "Audio 2:00",
            locationBadge = "Auriculares",
            assignedStudentIds = listOf("mateo"),
            previewSummary = "Audio guía calmada con tonos de marimba suave cada 30 segundos sin pitidos bruscos."
        )
    )

    val defaultDiaryEntries: List<DiaryEntry> = listOf(
        DiaryEntry(
            id = "diary_mateo_2024_10_23",
            studentId = "mateo",
            studentName = "Mateo García",
            dateKey = "2024-10-23",
            dayLabel = "Miércoles 23",
            emotionalState = "Radiante y Tranquilo",
            emotionalSubtitle = "Conectado con sus compañeros y flexible.",
            medalLabel = "¡Misión Hecha!",
            dayIndicator = "calma",
            anticipationTitle = "Anticipación para mañana (Jueves)",
            anticipationTime = "11:00 AM",
            anticipationText = "Cambio de aula: La clase de Música se realizará en el salón de psicomotricidad. Pictograma y apoyo visual ya colocados en su agenda de mesa.",
            syncedLabel = "14:15 Sincronizado",
            areas = listOf(
                DiaryAreaItem(
                    id = "da1",
                    title = "Alimentación & Autonomía",
                    badgeLabel = "4/5 Autónomo",
                    objective = "Comer la fruta de forma independiente con cuchara adaptada y retirar su bandeja.",
                    observation = "Comió toda la merienda (plátano y galletas) con completa autonomía y sin derrames. Llevó su plato a la papelera con refuerzo verbal mínimo.",
                    nextSession = "Traer merienda cortada en tiras para continuar fomentando pinza fina sin fatiga.",
                    accentKey = "peach"
                ),
                DiaryAreaItem(
                    id = "da2",
                    title = "Regulación Sensorial en Recreo",
                    badgeLabel = "5/5 Logrado",
                    objective = "Solicitar los auriculares de cancelación antes de entrar en sobrecarga acústica en el patio.",
                    observation = "Reconoció el exceso de ruido en el patio de primaria e indicó el pictograma de auriculares de forma espontánea. Permaneció 25 min tranquilo.",
                    nextSession = "Mantener los cascos cargados en la mochila y verificar que la almohadilla esté limpia.",
                    accentKey = "sky"
                ),
                DiaryAreaItem(
                    id = "da3",
                    title = "Atención en Tarea / Actividad Guiada",
                    badgeLabel = "4/5 Autónomo",
                    objective = "Mantener foco sostenido en rompecabezas durante 15 minutos con apoyos visuales de reloj de arena.",
                    observation = "Construcción con bloques de madera y plastilina sensorial suave. Muy concentrado y receptivo a los turnos compartidos con Sofía.",
                    nextSession = "Reforzar el inicio del juego sin instigación verbal directa; usar timer visual de 15 min.",
                    accentKey = "mint"
                ),
                DiaryAreaItem(
                    id = "da4",
                    title = "Descanso y Energía",
                    badgeLabel = "3/5 Con apoyo",
                    objective = "Aceptar el rincón blando para relajación de mediodía tras el almuerzo.",
                    observation = "Energía balanceada en la mañana. Tomó una siesta reparadora de 20 min en el rincón blando tras escuchar música instrumental suave.",
                    nextSession = "Mantener la misma manta con peso y la lista de reproducción ambiental de piano.",
                    accentKey = "lavender"
                )
            ),
            internalNotes = listOf(
                InternalNoteItem(
                    id = "in1",
                    authorName = "Laura (Tutora TEA)",
                    authorRole = "Tutora",
                    timeLabel = "13:40",
                    text = "Mateo ha tenido una mañana excelente. Muy concentrado en los puzzles de emparejamiento visual y compartió voluntariamente el material con Sofía. Progreso notable en tolerancia a la espera de turno.",
                    tag = "Objetivo cumplido",
                    accentKey = "peach"
                ),
                InternalNoteItem(
                    id = "in2",
                    authorName = "Marcos (Terapeuta Ocupacional - AL)",
                    authorRole = "Terapeuta",
                    timeLabel = "11:15",
                    text = "Sesión de regulación propioceptiva completada con éxito. Buena respuesta al chaleco de peso durante la actividad de mesa. Recomendado continuar con apoyos de baja estimulación táctil previa a la salida.",
                    tag = "Regulación lograda",
                    accentKey = "mint"
                )
            ),
            afternoonRoutine = listOf(
                RoutineStep("rt1", "15:00", "1. Merienda", "Autonomía comedor", "done", "peach"),
                RoutineStep("rt2", "15:45", "2. Aula Multisensorial", "Estimulación suave", "active", "sky"),
                RoutineStep("rt3", "16:30", "3. Psicomotricidad", "Circuito motor", "pending", "lavender"),
                RoutineStep("rt4", "17:15", "4. Cierre Sesión", "Revisión agenda", "pending", "mint")
            )
        )
    )

    val defaultLibraryItems: List<LibraryItem> = listOf(
        LibraryItem(
            id = "lib_monstruo",
            title = "El monstruo de colores",
            author = "Anna Llenas",
            materialType = "cuento",
            tags = listOf("Emociones", "3-6 años", "Adaptado TEA"),
            metaInfo = "Edición con apoyos visuales",
            pedagogicalTitle = "Uso pedagógico",
            reviewNote = "Cuento esencial para identificación de emociones con apoyos de tarros y códigos de color. Muy útil para autorregulación emocional en la asamblea matutina.",
            assignedStudentIds = listOf("mateo", "sofia"),
            usageMoments = listOf("Asamblea matutina", "Rincón de la calma"),
            accentKey = "blue"
        ),
        LibraryItem(
            id = "lib_lucas",
            title = "Lucas tiene superpoderes",
            author = "Equipo de Orientación TEA",
            materialType = "cuento",
            tags = listOf("Anticipación", "Historias Sociales", "Sensorial"),
            metaInfo = "Regulación auditiva en patio",
            pedagogicalTitle = "Uso pedagógico",
            reviewNote = "Historia social diseñada para anticipar el ruido del recreo y desmitificar el uso de auriculares de cancelación sonora entre los compañeros de clase.",
            assignedStudentIds = listOf("lucas"),
            usageMoments = listOf("Anticipación de recreo"),
            accentKey = "peach"
        ),
        LibraryItem(
            id = "lib_vermeulen",
            title = "El autismo y las emociones",
            author = "Peter Vermeulen",
            materialType = "libro",
            tags = listOf("Docencia", "Método TEACCH", "Guía Práctica"),
            metaInfo = "320 páginas · Formato Físico / Digital",
            pedagogicalTitle = "Puntos clave",
            reviewNote = "Enfoque sobre la 'ceguera al contexto' en el espectro autista y cómo estructurar el aula sensorial para reducir la sobrecarga cognitiva y fomentar la comprensión predictiva.",
            assignedStudentIds = emptyList(),
            usageMoments = listOf("Formación docente"),
            accentKey = "peach"
        ),
        LibraryItem(
            id = "lib_paper",
            title = "Efectividad de apoyos visuales en transiciones (2023)",
            author = "Revista de Neuropsicología Infantil",
            materialType = "paper",
            tags = listOf("Evidencia", "Transiciones", "PDF 12 pág."),
            metaInfo = "Revisado por pares · DOI 10.1016",
            pedagogicalTitle = "Evidencia clínica",
            reviewNote = "Estudio cuantitativo sobre la reducción del 64% de episodios de desregulación mediante paneles de anticipación táctiles durante el cambio de asignaturas en el aula.",
            assignedStudentIds = emptyList(),
            usageMoments = listOf("Protocolo de aula"),
            accentKey = "mint"
        ),
        LibraryItem(
            id = "lib_viaje_lucas",
            title = "El Viaje de Lucas",
            author = "Colección Serene TEA",
            materialType = "cuento",
            tags = listOf("TEA & Empatía", "Transiciones", "Visual"),
            metaInfo = "Lectura Recomendada",
            pedagogicalTitle = "Uso pedagógico",
            reviewNote = "Cuento visual sobre anticipación de rutinas y transiciones escolares pacíficas.",
            assignedStudentIds = listOf("mateo", "lucas"),
            usageMoments = listOf("Lectura compartida"),
            accentKey = "lavender"
        )
    )
}
