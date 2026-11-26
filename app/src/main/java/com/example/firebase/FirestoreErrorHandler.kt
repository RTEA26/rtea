package com.example.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException

enum class OperationType {
    CREATE,
    UPDATE,
    DELETE,
    LIST,
    GET,
    WRITE
}

fun handleFirestoreError(error: Throwable, operationType: OperationType, path: String) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    val errorDetails = buildString {
        append("Firestore Error [$operationType] at '$path': ${error.message}")
        append(" | User: uid=${user?.uid}, email=${user?.email}")
    }

    Log.e("FirestoreError", errorDetails, error)

    if (error is FirebaseFirestoreException &&
        error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
    ) {
        throw SecurityException(errorDetails, error)
    }
}
