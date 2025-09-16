package com.edufelip.finn.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun authUserIdFlow(auth: FirebaseAuth): Flow<String?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        trySend(firebaseAuth.currentUser?.uid)
    }
    auth.addAuthStateListener(listener)
    trySend(auth.currentUser?.uid)
    awaitClose { auth.removeAuthStateListener(listener) }
}
