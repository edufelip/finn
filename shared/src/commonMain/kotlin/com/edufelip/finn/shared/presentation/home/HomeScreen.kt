package com.edufelip.finn.shared.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.usecase.GetFeedUseCase
import com.edufelip.finn.shared.pagination.DefaultPaginator
import com.edufelip.finn.shared.util.format.formatRelative
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    getFeed: GetFeedUseCase,
    postRepository: PostRepository,
    userIdProvider: () -> String,
    onShare: (Post) -> Unit,
) {
    var state by remember { mutableStateOf(HomeUiState()) }
    val pageSize = 10
    val scope = remember { CoroutineScope(kotlinx.coroutines.Dispatchers.Main) }
    val paginator = remember {
        DefaultPaginator(
            initialKey = 1,
            onLoadUpdated = { isLoading -> state = state.copy(loading = isLoading) },
            onRequest = { page -> getFeed(userIdProvider(), page).first() },
            getNextKey = { key, _ -> key + 1 },
            isEnd = { items -> items.size < pageSize },
            onError = { t -> state = state.copy(error = t.message) },
            onSuccess = { items, newKey, end ->
                val combined = if (newKey == 2) items else state.posts + items
                state = state.copy(posts = combined, nextPage = newKey, endReached = end)
            },
        )
    }

    fun loadInitialIfNeeded() {
        if (state.posts.isEmpty() && !state.loading) {
            scope.launch {
                paginator.reset()
                paginator.loadNext()
            }
        }
    }

    fun loadMore() {
        scope.launch { paginator.loadNext() }
    }

    fun toggleLike(postId: Int, currentlyLiked: Boolean) {
        val userId = userIdProvider()
        scope.launch {
            val flow = if (currentlyLiked) postRepository.dislike(postId, userId) else postRepository.like(postId, userId)
            flow.collect {
                val updated = state.posts.map { p ->
                    if (p.id == postId) {
                        p.copy(
                            isLiked = !currentlyLiked,
                            likesCount = if (currentlyLiked) (p.likesCount - 1).coerceAtLeast(0) else p.likesCount + 1,
                        )
                    } else {
                        p
                    }
                }
                state = state.copy(posts = updated)
            }
        }
    }

    LaunchedEffect(Unit) { loadInitialIfNeeded() }
    Box(Modifier.fillMaxSize()) {
        when {
            state.loading && state.posts.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null && state.posts.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
                Text(text = state.error ?: strings.error, color = MaterialTheme.colorScheme.error)
            }
            else -> {
                val listState = rememberLazyListState()
                LaunchedEffect(listState, state.endReached, state.loading) {
                    snapshotFlow { listState.layoutInfo }
                        .map { it.visibleItemsInfo.lastOrNull()?.index to it.totalItemsCount }
                        .distinctUntilChanged()
                        .filter { (last, total) -> last != null && total > 0 && last >= total - 3 && !state.endReached && !state.loading }
                        .collect { loadMore() }
                }
                LazyColumn(Modifier.fillMaxSize().padding(16.dp), state = listState) {
                    items(state.posts) { post ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(post.communityTitle ?: "", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(post.content, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val dateText = post.dateMillis?.let { " • ${formatRelative(it)}" } ?: ""
                                val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
                                Text(
                                    "${strings.by} ${'$'}{post.userName ?: strings.unknown} • ${'$'}{post.likesCount} ${strings.likes} • ${'$'}{post.commentsCount} ${strings.comments}${'$'}dateText",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f),
                                )
                                ElevatedButton(onClick = { toggleLike(post.id, post.isLiked) }) {
                                    Text(if (post.isLiked) strings.unlike else strings.like)
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(onClick = { onShare(post) }) {
                                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
