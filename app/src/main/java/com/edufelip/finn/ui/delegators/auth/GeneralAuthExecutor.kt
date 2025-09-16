package com.edufelip.finn.ui.delegators.auth

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class GeneralAuthExecutor @Inject constructor(
    val firebaseAuth: FirebaseAuth,
) : IAuthExecutor {
    override val type: AuthTypes
        get() = AuthTypes.GENERAL

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}
