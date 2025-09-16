package com.edufelip.finn.shared.presentation.vm

import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.shared.domain.usecase.GetFeedUseCase
import com.edufelip.finn.shared.domain.usecase.GetUserUseCase
import com.edufelip.finn.shared.domain.usecase.ObserveNotificationsUseCase
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import kotlinx.coroutines.flow.Flow

interface HomeVM {
    val getFeed: GetFeedUseCase
    val postRepository: PostRepository
    val userIdProvider: () -> String
}

interface SearchVM { val searchCommunities: SearchCommunitiesUseCase }

interface CommunityDetailsVM {
    val getCommunityDetails: GetCommunityDetailsUseCase
    val getCommunityPosts: GetCommunityPostsUseCase
}

interface NotificationsVM { val observeNotifications: ObserveNotificationsUseCase? }

interface ProfileVM {
    val userIdFlow: Flow<String?>
    val getUser: GetUserUseCase
    val getUserPosts: (String, Int) -> Flow<List<Post>>
}

interface SavedVM {
    val userIdFlow: Flow<String?>
    val repo: PostRepository
}

interface AuthVM { val userIdFlow: Flow<String?> }

interface CreateCommunityVM { val createCommunity: CreateCommunityUseCase }

interface CreatePostVM {
    val repo: PostRepository
    val userIdProvider: () -> String
    val pickImage: suspend () -> ByteArray?
}

interface CommentsVM {
    val getComments: GetCommentsForPostUseCase
    val addComment: AddCommentUseCase
    val userIdProvider: () -> String
}

