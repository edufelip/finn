package com.edufelip.finn.shared.ui.screens.home

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
import androidx.compose.material.pullrefresh.PullRefreshState
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
import com.edufelip.finn.shared.domain.usecase.GetFeedUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.UseCaseException
import com.edufelip.finn.shared.domain.util.asResult
import com.edufelip.finn.shared.domain.util.awaitTerminal
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.pagination.DefaultPaginator
import com.edufelip.finn.shared.ui.components.molecules.HomeTopBar
import com.edufelip.finn.shared.ui.components.organisms.PostCard
import com.edufelip.finn.shared.util.format.formatRelative
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    getFeed: GetFeedUseCase,
    postRepository: PostRepository,
    userIdProvider: () -> String,
    onShare: (Post) -> Unit,
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    profileImageUrl: String? = null,
    onPostClick: (Post) -> Unit,
    onCommunityClick: (Int) -> Unit,
    onPostStateChanged: (Post) -> Unit = {},
    postUpdates: Flow<Post> = emptyFlow(),
    postRemovals: Flow<Int> = emptyFlow(),
) {
    val strings = LocalStrings.current
    var state by remember { mutableStateOf(HomeUiState()) }
    val scope = rememberCoroutineScope()
    val pageSize = 10
    val paginator = remember {
        DefaultPaginator(
            initialKey = 1,
            onLoadUpdated = { isLoading -> state = state.copy(loading = isLoading) },
            onRequest = { page ->
                when (val result = getFeed(userIdProvider(), page).awaitTerminal()) {
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

    fun refresh() {
        scope.launch {
            paginator.reset()
            paginator.loadNext()
        }
    }

    fun toggleLike(postId: Int, currentlyLiked: Boolean) {
        val userId = userIdProvider()
        if (userId.isBlank()) {
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
            val flow = if (currentlyLiked) postRepository.dislike(postId, userId) else postRepository.like(postId, userId)
            flow.asResult(emitLoading = false).collect { result ->
                if (result is Result.Error) {
                    replacePost(current, notify = true)
                    state = state.copy(error = result.error.readableMessage())
                }
            }
        }
    }

    val refreshing = state.loading && state.posts.isNotEmpty()
    val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = { refresh() })

    LaunchedEffect(Unit) { refresh() }

    LaunchedEffect(postUpdates) {
        postUpdates.collect { replacePost(it, notify = false) }
    }

    LaunchedEffect(postRemovals) {
        postRemovals.collect { removePost(it) }
    }

    HomeScreenContent(
        state = state,
        modifier = modifier,
        profileImageUrl = profileImageUrl,
        onOpenDrawer = onOpenDrawer,
        onSearchClick = onSearchClick,
        pullRefreshState = pullRefreshState,
        refreshing = refreshing,
        onLoadMore = { scope.launch { paginator.loadNext() } },
        onPostClick = onPostClick,
        onCommunityClick = onCommunityClick,
        onShare = onShare,
        onToggleLike = { id, liked -> toggleLike(id, liked) },
        onHide = { removePost(it) },
        error = state.error,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun HomeScreenContent(
    state: HomeUiState,
    modifier: Modifier,
    profileImageUrl: String?,
    onOpenDrawer: () -> Unit,
    onSearchClick: () -> Unit,
    pullRefreshState: PullRefreshState,
    refreshing: Boolean,
    onLoadMore: () -> Unit,
    onPostClick: (Post) -> Unit,
    onCommunityClick: (Int) -> Unit,
    onShare: (Post) -> Unit,
    onToggleLike: (Int, Boolean) -> Unit,
    onHide: (Int) -> Unit,
    error: String?,
) {
    val strings = LocalStrings.current
    Column(modifier.fillMaxSize()) {
        HomeTopBar(profileImageUrl = profileImageUrl, onMenuClick = onOpenDrawer, onSearchClick = onSearchClick)
        Box(Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
            when {
                state.loading && state.posts.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                state.error != null && state.posts.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error, color = MaterialTheme.colorScheme.error)
                }

                else -> {
                    val listState = rememberLazyListState()
                    LaunchedEffect(listState, state.endReached, state.loading) {
                        snapshotFlow { listState.layoutInfo }
                            .map { it.visibleItemsInfo.lastOrNull()?.index to it.totalItemsCount }
                            .distinctUntilChanged()
                            .filter { (last, total) ->
                                last != null && total > 0 && last >= total - 3 && !state.endReached && !state.loading
                            }
                            .collect { onLoadMore() }
                    }

                    Column(Modifier.fillMaxSize()) {
                        state.posts.firstOrNull()?.cachedAtMillis?.let {
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                text = strings.cached_label.replace("%1s", formatRelative(it)),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        error?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            )
                        }
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.posts, key = { it.id }) { post ->
                                PostCard(
                                    post = post,
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { onPostClick(post) },
                                    onCommunityClick = post.communityId?.let { id -> { onCommunityClick(id) } },
                                    onToggleLike = { onToggleLike(post.id, post.isLiked) },
                                    onCommentsClick = { onPostClick(post) },
                                    onShareClick = { onShare(post) },
                                    onHide = { onHide(post.id) },
                                )
                            }
                            if (state.loading && state.posts.isNotEmpty()) {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                            } else if (!state.endReached && state.posts.isNotEmpty()) {
                                item {
                                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                        TextButton(onClick = onLoadMore) {
                                            Text(strings.load_more)
                                        }
                                    }
                                }
                            }
                        }
                        PullRefreshIndicator(refreshing = refreshing, state = pullRefreshState, modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                }
            }
        }
    }
}
