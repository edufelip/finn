package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.sharedimpl.PostRepositoryAndroid
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    val postRepository: PostRepositoryAndroid,
    private val auth: FirebaseAuth,
) : ViewModel() {
    val userIdProvider: () -> String = { auth.currentUser?.uid ?: "" }
}
