package com.edufelip.finn.utils

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.edufelip.finn.BuildConfig
import com.edufelip.finn.ui.models.SignInResult
import com.edufelip.finn.ui.models.UserData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context: Context,
) {
    private val auth = FirebaseAuth.getInstance()

    suspend fun signIn(activity: Activity): SignInResult {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.FIREBASE_GOOGLE_ID)
            .setAutoSelectEnabled(true)
            .build()
        val request = GetCredentialRequest(listOf(googleIdOption))
        val idToken = try {
            val result = credentialManager.getCredential(activity, request)
            when (val cred = result.credential) {
                is CustomCredential -> if (cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    GoogleIdTokenCredential.createFrom(cred.data).idToken
                } else {
                    null
                }
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        if (idToken == null) {
            return SignInResult(data = null, errorMessage = "No Google ID token")
        }
        val googleCredentials = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        userName = displayName,
                        profilePictureUrl = photoUrl?.toString(),
                    )
                },
                errorMessage = null,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message,
            )
        }
    }

    suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun setSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            userName = displayName,
            profilePictureUrl = photoUrl?.toString(),
        )
    }
}
