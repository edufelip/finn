package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.auth.authUserIdFlow
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    val postRepository: PostRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {
    val userIdFlow = com.edufelip.finn.auth.authUserIdFlow(auth)
}
