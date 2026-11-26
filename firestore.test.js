const { test, before, after } = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const {
  initializeTestEnvironment,
  assertSucceeds,
  assertFails,
} = require("@firebase/rules-unit-testing");
const firebase = require("/usr/local/lib/node_modules/@firebase/rules-unit-testing/node_modules/firebase/compat/app").default;
require("/usr/local/lib/node_modules/@firebase/rules-unit-testing/node_modules/firebase/compat/firestore");

const PROJECT_ID = "rtea-test-project";
let testEnv;

before(async () => {
  const [host, portStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      host,
      port: Number(portStr),
      rules: fs.readFileSync("firestore.rules", "utf8"),
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

function dbFor(uid) {
  const ctx = uid ? testEnv.authenticatedContext(uid) : testEnv.unauthenticatedContext();
  return ctx.firestore();
}

const serverTimestamp = () => firebase.firestore.FieldValue.serverTimestamp();

const validStudentPayload = () => ({
  fullName: "Mateo García",
  preferredName: "Mate",
  classroom: "Aula 3-B · Primaria Integrada",
  avatarIndex: 1,
  customAvatarUri: "",
  supportLevel: 1,
  interests: ["Dinosaurios", "Puzzles"],
  commSystem: "PECS / Cuaderno de Pictogramas Digital",
  sensoryNotes: "Sensibilidad acústica moderada.",
  lastSessionLabel: "Hoy, 10:30 h",
  progressAreas: [{ id: "a1", title: "Autonomía", completedSteps: 14, totalSteps: 18, colorHex: "#2C628E" }],
  mainReportDocsCount: 2,
  programDocsCount: 1,
  aiReportSummary: "",
  activeResourceIds: ["res_lavado"],
  evalTemplate: "Sensorial",
  evalReinforcement: "¡Mateo logró la transición de aula sin angustia!",
  evalMedal: "GOLD",
  evalScore: 4,
  evalDimensions: [{ id: "d1", title: "Atención y Enfoque", subtitle: "Sostuvo 18 min", score: 4, colorHex: "#3F82B8" }],
  evalNotes: "Usó plastilina sensorial.",
  evalShareFamily: true,
  evalRecordHistory: true,
  createdAt: serverTimestamp(),
  updatedAt: serverTimestamp(),
});

test("Unauthenticated user cannot read or write students", async () => {
  const db = dbFor(null);
  await assertFails(db.doc("users/teacher1/students/st1").get());
  await assertFails(db.doc("users/teacher1/students/st1").set(validStudentPayload()));
});

test("Teacher can create, read, update, and delete own student", async () => {
  const db = dbFor("teacher1");
  const ref = db.doc("users/teacher1/students/st1");
  await assertSucceeds(ref.set(validStudentPayload()));
  const snap = await assertSucceeds(ref.get());
  assert.equal(snap.exists, true);

  await assertSucceeds(
    ref.update({
      preferredName: "Mateo",
      evalScore: 5,
      updatedAt: serverTimestamp(),
    })
  );
  await assertSucceeds(ref.delete());
});

test("Cross-user access is denied", async () => {
  const db1 = dbFor("teacher1");
  const db2 = dbFor("teacher2");
  await assertSucceeds(db1.doc("users/teacher1/students/st2").set(validStudentPayload()));
  await assertFails(db2.doc("users/teacher1/students/st2").get());
  await assertFails(
    db2.doc("users/teacher1/students/st2").update({
      preferredName: "Hacked",
      updatedAt: serverTimestamp(),
    })
  );
  await assertFails(db2.doc("users/teacher1/students/st2").delete());
});

test("Invalid supportLevel or future createdAt timestamp is rejected", async () => {
  const db = dbFor("teacher1");
  const badSupport = { ...validStudentPayload(), supportLevel: 5 };
  await assertFails(db.doc("users/teacher1/students/st_bad1").set(badSupport));

  const futureTime = firebase.firestore.Timestamp.fromMillis(Date.now() + 3600 * 1000);
  const badTimestamp = { ...validStudentPayload(), createdAt: futureTime };
  await assertFails(db.doc("users/teacher1/students/st_bad2").set(badTimestamp));
});

test("QuickNote, Evaluation, DiaryEntry, Resource, and LibraryItem enforce schemas", async () => {
  const db = dbFor("teacher1");

  await assertSucceeds(
    db.doc("users/teacher1/notes/n1").set({
      category: "ANTICIPACION",
      tagLabel: "ANTICIPACIÓN",
      timeLabel: "11:30 Recreo",
      content: "Lucas estrena cascos antirruido.",
      studentName: "Lucas M.",
      contextLabel: "",
      completed: false,
      replyText: "",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  await assertFails(
    db.doc("users/teacher1/notes/n_bad").set({
      category: "INVALID_CAT",
      tagLabel: "TAG",
      timeLabel: "",
      content: "Test",
      studentName: "",
      contextLabel: "",
      completed: false,
      replyText: "",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  await assertSucceeds(
    db.doc("users/teacher1/evaluations/ev1").set({
      studentId: "st1",
      studentName: "Mateo García",
      medalType: "GOLD",
      overallScore: 4,
      dimensionScores: [],
      templateCategory: "Sensorial",
      notes: "Gran calma en transición.",
      shareWithFamily: true,
      recordInHistory: true,
      draft: false,
      dateLabel: "Hoy, 24 de Octubre",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );

  await assertSucceeds(
    db.doc("users/teacher1/libraryItems/lib1").set({
      title: "El monstruo de colores",
      author: "Anna Llenas",
      materialType: "cuento",
      tags: ["Emociones", "3-6 años"],
      metaInfo: "Edición con apoyos visuales",
      pedagogicalTitle: "Uso pedagógico",
      reviewNote: "Cuento esencial para emociones.",
      assignedStudentIds: ["st1"],
      usageMoments: ["Asamblea matutina"],
      accentKey: "blue",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
});

test("Full SeedData initial batch succeeds", async () => {
  const db = dbFor("seed_user");
  const batch = db.batch();
  batch.set(db.doc("users/seed_user/students/mateo"), validStudentPayload());
  batch.set(db.doc("users/seed_user/notes/note_1"), {
    category: "ANTICIPACION",
    tagLabel: "ANTICIPACIÓN",
    timeLabel: "11:30 Recreo",
    content: "Recordatorio patio: Lucas estrena cascos antirruido.",
    studentName: "Lucas M.",
    contextLabel: "",
    completed: false,
    replyText: "",
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  });
  batch.set(db.doc("users/seed_user/evaluations/eval_1"), {
    studentId: "mateo",
    studentName: "Mateo García",
    medalType: "BRONZE",
    overallScore: 3,
    dimensionScores: [],
    templateCategory: "Autonomía",
    notes: "Autonomía en Lavado.",
    shareWithFamily: true,
    recordInHistory: true,
    draft: false,
    dateLabel: "Ayer, 16:20",
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  });
  batch.set(db.doc("users/seed_user/resourceFolders/folder_autonomia"), {
    name: "Autonomía",
    subtitle: "Rutinas paso a paso de vida cotidiana",
    accentKey: "peach",
    iconKey: "self_improvement",
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  });
  batch.set(db.doc("users/seed_user/resources/res_lavado"), {
    folderId: "folder_autonomia",
    title: "Secuencia_Lavado_Manos_3Pasos.pdf",
    description: "Secuencia anticipatoria para el baño.",
    fileType: "PDF",
    sizeLabel: "1.4 MB",
    dateLabel: "Hoy, 09:30",
    statusBadge: "Listo imprimir",
    locationBadge: "Activo en Tablet",
    assignedStudentIds: ["mateo", "sofia"],
    localUri: "",
    previewSummary: "1. Subir mangas.",
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  });
  batch.set(db.doc("users/seed_user/diaryEntries/diary_mateo_2024_10_23"), {
    studentId: "mateo",
    studentName: "Mateo García",
    dateKey: "2024-10-23",
    dayLabel: "Miércoles 23",
    emotionalState: "Radiante y Tranquilo",
    emotionalSubtitle: "Conectado con sus compañeros y flexible.",
    medalLabel: "¡Misión Hecha!",
    dayIndicator: "calma",
    anticipationTitle: "Anticipación para mañana (Jueves)",
    anticipationTime: "11:00 AM",
    anticipationText: "Cambio de aula.",
    areas: [],
    internalNotes: [],
    afternoonRoutine: [],
    syncedLabel: "14:15 Sincronizado",
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  });
  batch.set(db.doc("users/seed_user/libraryItems/lib_monstruo"), {
    title: "El monstruo de colores",
    author: "Anna Llenas",
    materialType: "cuento",
    tags: ["Emociones", "3-6 años", "Adaptado TEA"],
    metaInfo: "Edición con apoyos visuales",
    pedagogicalTitle: "Uso pedagógico",
    reviewNote: "Cuento esencial.",
    assignedStudentIds: ["mateo", "sofia"],
    usageMoments: ["Asamblea matutina"],
    accentKey: "blue",
    createdAt: serverTimestamp(),
    updatedAt: serverTimestamp(),
  });
  await assertSucceeds(batch.commit());
});

test("Student PDF document lists and DOC resources with mimeType are allowed", async () => {
  const db = dbFor("teacher_docs");
  const studentRef = db.doc("users/teacher_docs/students/st_pdfs");
  await assertSucceeds(
    studentRef.set({
      ...validStudentPayload(),
      mainReportDocsCount: 1,
      programDocsCount: 1,
      mainReportDocs: [
        {
          id: "doc1",
          title: "Informe_Inicial_Mateo.pdf",
          localPath: "/data/user/0/app/files/rtea_uploads/doc1.pdf",
          sizeLabel: "1.2 MB",
          dateLabel: "Hoy",
          mimeType: "application/pdf",
        },
      ],
      programDocs: [
        {
          id: "doc2",
          title: "Programacion_TEA.pdf",
          localPath: "/data/user/0/app/files/rtea_uploads/doc2.pdf",
          sizeLabel: "820 KB",
          dateLabel: "Hoy",
          mimeType: "application/pdf",
        },
      ],
    })
  );

  await assertSucceeds(
    db.doc("users/teacher_docs/resources/res_doc").set({
      folderId: "folder_autonomia",
      title: "Ficha_Adaptada.docx",
      description: "Documento general desde dispositivo.",
      fileType: "DOC",
      sizeLabel: "420 KB",
      dateLabel: "Hoy",
      statusBadge: "Subido",
      locationBadge: "Dispositivo",
      assignedStudentIds: ["st_pdfs"],
      localUri: "/data/user/0/app/files/rtea_uploads/ficha.docx",
      mimeType: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
      previewSummary: "Archivo subido desde dispositivo.",
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp(),
    })
  );
});


