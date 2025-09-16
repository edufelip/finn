package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.auth.authUserIdFlow
import com.edufelip.finn.shared.domain.usecase.GetUserUseCase
import com.edufelip.finn.sharedimpl.PostRepositoryAndroid
import com.edufelip.finn.sharedimpl.UserRepositoryAndroid
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepositoryAndroid,
    private val postRepository: PostRepositoryAndroid,
    private val auth: FirebaseAuth,
) : ViewModel() {
    val userIdFlow = com.edufelip.finn.auth.authUserIdFlow(auth)
    val getUser = GetUserUseCase(userRepository)
    val getUserPosts: (String, Int) -> kotlinx.coroutines.flow.Flow<List<com.edufelip.finn.shared.domain.model.Post>> =
        { id, page -> postRepository.postsByUser(id, page) }
}
