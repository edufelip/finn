package com.edufelip.finn.ui.delegators.auth

import com.edufelip.finn.utils.GoogleAuthUiClient
import javax.inject.Inject

class GoogleAuthExecutor @Inject constructor(
    private val googleAuthUiClient: GoogleAuthUiClient,
) : IAuthExecutor {
    override val type: AuthTypes
        get() = AuthTypes.GOOGLE

    override suspend fun signOut() {
        googleAuthUiClient.signOut()
    }
}
