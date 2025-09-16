package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.sharedimpl.CommentRepositoryAndroid
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel @Inject constructor(
    commentRepository: CommentRepositoryAndroid,
    private val auth: FirebaseAuth,
) : ViewModel() {
    val getComments = GetCommentsForPostUseCase(commentRepository)
    val addComment = AddCommentUseCase(commentRepository)
    val userIdProvider: () -> String = { auth.currentUser?.uid ?: "" }
}
