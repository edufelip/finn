package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.auth.authUserIdFlow
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    auth: FirebaseAuth,
) : ViewModel() {
    val userIdFlow = authUserIdFlow(auth)
}
