package com.edufelip.finn.shared.ui.screens.community

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.usecase.DeleteCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunitySubscriptionUseCase
import com.edufelip.finn.shared.domain.usecase.SubscribeToCommunityUseCase
import com.edufelip.finn.shared.domain.usecase.UnsubscribeFromCommunityUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.UseCaseException
import com.edufelip.finn.shared.domain.util.asResult
import com.edufelip.finn.shared.domain.util.awaitTerminal
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.pagination.DefaultPaginator
import com.edufelip.finn.shared.ui.components.organisms.CommunityHeader
import com.edufelip.finn.shared.ui.components.organisms.PostCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommunityDetailsScreen(
    getCommunityDetails: GetCommunityDetailsUseCase,
    getCommunityPosts: GetCommunityPostsUseCase,
    subscribe: SubscribeToCommunityUseCase,
    unsubscribe: UnsubscribeFromCommunityUseCase,
    getSubscription: GetCommunitySubscriptionUseCase,
    deleteCommunity: DeleteCommunityUseCase,
    postRepository: PostRepository,
    id: Int,
    currentUserId: String,
    onBack: () -> Unit = {},
    onPostClick: (Post) -> Unit,
    onPostStateChanged: (Post) -> Unit,
    onShare: (Post) -> Unit,
    postUpdates: Flow<Post> = emptyFlow(),
    postRemovals: Flow<Int> = emptyFlow(),
    modifier: Modifier = Modifier,
) {
    val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(CommunityDetailsState(loading = true)) }
    val pageSize = 10
    val paginator = remember(id) {
        DefaultPaginator(
            initialKey = 1,
            onLoadUpdated = { isLoading ->
                state = state.copy(
                    loading = isLoading && state.posts.isEmpty(),
                    loadingMore = isLoading && state.posts.isNotEmpty(),
                )
            },
            onRequest = { page ->
                when (val result = getCommunityPosts(id, page).awaitTerminal()) {
                    is Result.Success -> result.value
                    is Result.Error -> throw UseCaseException(result.error)
                    Result.Loading -> emptyList()
                }
            },
            getNextKey = { key, _ -> key + 1 },
            isEnd = { items -> items.size < pageSize },
            onError = { throwable ->
                val message = if (throwable is UseCaseException) throwable.domainError.readableMessage() else throwable.message
                state = state.copy(error = message)
            },
            onSuccess = { items, newKey, end ->
                val combined = if (newKey == 2) items else state.posts + items
                state = state.copy(posts = combined, nextPage = newKey, endReached = end, error = null)
            },
        )
    }

    fun replacePost(updated: Post, notify: Boolean) {
        val index = state.posts.indexOfFirst { it.id == updated.id }
        if (index >= 0) {
            val posts = state.posts.toMutableList()
            if (posts[index] != updated) {
                posts[index] = updated
                state = state.copy(posts = posts)
            }
            if (notify) onPostStateChanged(updated)
        }
    }

    fun removePost(postId: Int) {
        if (state.posts.any { it.id == postId }) {
            state = state.copy(posts = state.posts.filterNot { it.id == postId })
        }
    }

    fun refreshPosts() {
        scope.launch {
            paginator.reset()
            paginator.loadNext()
        }
    }

    fun toggleLike(postId: Int, currentlyLiked: Boolean) {
        if (currentUserId.isBlank()) {
            state = state.copy(error = strings.not_authenticated)
            return
        }
        val current = state.posts.firstOrNull { it.id == postId } ?: return
        val updated = current.copy(
            isLiked = !currentlyLiked,
            likesCount = if (currentlyLiked) (current.likesCount - 1).coerceAtLeast(0) else current.likesCount + 1,
        )
        replacePost(updated, notify = true)
        state = state.copy(error = null)
        scope.launch {
            val flow = if (currentlyLiked) postRepository.dislike(postId, currentUserId) else postRepository.like(postId, currentUserId)
            flow.asResult(emitLoading = false).collect { result ->
                if (result is Result.Error) {
                    replacePost(current, notify = true)
                    state = state.copy(error = result.error.readableMessage())
                }
            }
        }
    }

    fun performSubscribe() {
        if (currentUserId.isBlank()) return
        scope.launch {
            subscribe(currentUserId, id).collectLatest { result ->
                state = when (result) {
                    is Result.Loading -> state.copy(membershipLoading = true, error = null)
                    is Result.Success -> state.copy(membership = result.value, membershipLoading = false)
                    is Result.Error -> state.copy(membershipLoading = false, error = result.error.readableMessage())
                }
            }
        }
    }

    fun performUnsubscribe() {
        if (currentUserId.isBlank()) return
        scope.launch {
            unsubscribe(currentUserId, id).collectLatest { result ->
                when (result) {
                    is Result.Loading -> state = state.copy(membershipLoading = true, error = null)
                    is Result.Success -> state = state.copy(membership = null, membershipLoading = false, error = null)
                    is Result.Error -> state = state.copy(membershipLoading = false, error = result.error.readableMessage())
                }
            }
        }
    }

    fun performDelete() {
        scope.launch {
            deleteCommunity(id).collect { result ->
                when (result) {
                    is Result.Loading -> state = state.copy(deleting = true, error = null)
                    is Result.Success -> {
                        state = state.copy(deleting = false, error = null)
                        onBack()
                    }
                    is Result.Error -> state = state.copy(deleting = false, error = result.error.readableMessage())
                }
            }
        }
    }

    val pullRefreshState = rememberPullRefreshState(refreshing = state.loading, onRefresh = { refreshPosts() })

    LaunchedEffect(id) {
        state = CommunityDetailsState(loading = true)
        launch {
            getCommunityDetails(id).collectLatest { result ->
                when (result) {
                    is Result.Loading -> state = state.copy(loading = true, error = null)
                    is Result.Success -> state = state.copy(community = result.value, loading = false, error = null)
                    is Result.Error -> state = state.copy(loading = false, error = result.error.readableMessage())
                }
            }
        }
        refreshPosts()
        if (currentUserId.isNotBlank()) {
            launch {
                getSubscription(currentUserId, id).collectLatest { result ->
                    when (result) {
                        is Result.Loading -> state = state.copy(membershipLoading = true, error = null)
                        is Result.Success -> state = state.copy(membership = result.value, membershipLoading = false, error = null)
                        is Result.Error -> state = state.copy(membershipLoading = false, error = result.error.readableMessage())
                    }
                }
            }
        }
    }

    LaunchedEffect(postUpdates) {
        postUpdates.collect { replacePost(it, notify = false) }
    }

    LaunchedEffect(postRemovals) {
        postRemovals.collect { removePost(it) }
    }

    Box(modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        when {
            state.loading && state.community == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            state.error != null && state.community == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error ?: strings.error, color = MaterialTheme.colorScheme.error)
            }

            else -> {
                Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CommunityHeader(
                        community = state.community,
                        currentUserId = currentUserId,
                        membership = state.membership,
                        membershipLoading = state.membershipLoading,
                        deleting = state.deleting,
                        onBack = onBack,
                        onSubscribe = { performSubscribe() },
                        onUnsubscribe = { performUnsubscribe() },
                        onDelete = { performDelete() },
                    )
                    state.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    val listState = rememberLazyListState()
                    LaunchedEffect(listState, state.endReached, state.loadingMore) {
                        snapshotFlow { listState.layoutInfo }
                            .map { it.visibleItemsInfo.lastOrNull()?.index to it.totalItemsCount }
                            .distinctUntilChanged()
                            .filter { (last, total) ->
                                last != null && total > 0 && last >= total - 3 && !state.endReached && !state.loadingMore
                            }
                            .collect { scope.launch { paginator.loadNext() } }
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.posts, key = { it.id }) { post ->
                            PostCard(
                                post = post,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onPostClick(post) },
                                onCommunityClick = null,
                                onToggleLike = { toggleLike(post.id, post.isLiked) },
                                onCommentsClick = { onPostClick(post) },
                                onShareClick = { onShare(post) },
                                onHide = { removePost(post.id) },
                            )
                        }
                        if (state.loadingMore) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                        } else if (!state.endReached && state.posts.isNotEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    TextButton(onClick = { scope.launch { paginator.loadNext() } }) {
                                        Text(strings.load_more)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        PullRefreshIndicator(state.loading, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}
