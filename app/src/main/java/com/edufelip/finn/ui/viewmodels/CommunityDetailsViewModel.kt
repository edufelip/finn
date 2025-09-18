package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.usecase.DeleteCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunitySubscriptionUseCase
import com.edufelip.finn.shared.domain.usecase.SubscribeToCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.UnsubscribeFromCommunityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommunityDetailsViewModel @Inject constructor(
    communityRepository: CommunityRepository,
    postRepository: PostRepository,
) : ViewModel() {
    val getCommunityDetails = GetCommunityDetailsUseCase(communityRepository)
    val getCommunityPosts = GetCommunityPostsUseCase(postRepository)
    val subscribe = SubscribeToCommunityUseCase(communityRepository)
    val unsubscribe = UnsubscribeFromCommunityUseCase(communityRepository)
    val getSubscription = GetCommunitySubscriptionUseCase(communityRepository)
    val deleteCommunity = DeleteCommunityUseCase(communityRepository)
}
