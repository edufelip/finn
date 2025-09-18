package com.edufelip.finn.notifications

import com.edufelip.finn.shared.data.remote.api.ApiServiceV2
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class TokenUploaderAndroid @Inject constructor(
    private val api: ApiServiceV2,
    private val auth: FirebaseAuth,
) {
    suspend fun upload(token: String) {
        val uid = auth.currentUser?.uid ?: return
        try {
            api.uploadFcmToken(mapOf("userId" to uid, "token" to token))
        } catch (_: Exception) {
            // Swallow errors; token upload is best-effort
        }
    }
}
