package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.sharedimpl.CommunityRepositoryAndroid
import com.edufelip.finn.sharedimpl.PostRepositoryAndroid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommunityDetailsViewModel @Inject constructor(
    communityRepository: CommunityRepositoryAndroid,
    postRepository: PostRepositoryAndroid,
) : ViewModel() {
    val getCommunityDetails = GetCommunityDetailsUseCase(communityRepository)
    val getCommunityPosts = GetCommunityPostsUseCase(postRepository)
}
