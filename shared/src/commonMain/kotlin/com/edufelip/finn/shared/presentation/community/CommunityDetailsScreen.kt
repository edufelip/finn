package com.edufelip.finn.shared.presentation.community

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityDetailsUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommunityPostsUseCase
import com.edufelip.finn.shared.presentation.comments.CommentsSection
import com.edufelip.finn.shared.ui.components.SharedImage
import com.edufelip.finn.shared.util.format.formatRelative
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun CommunityDetailsScreen(
    getCommunityDetails: GetCommunityDetailsUseCase,
    getCommunityPosts: GetCommunityPostsUseCase,
    id: Int,
    onBack: () -> Unit = {},
    getComments: GetCommentsForPostUseCase,
    addComment: AddCommentUseCase,
    userIdProvider: () -> String,
) {
    var state by remember { mutableStateOf(CommunityDetailsState(loading = true)) }
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    val pageSize = 10
    val paginator = remember(id) {
        com.edufelip.finn.shared.pagination.DefaultPaginator(
            initialKey = 1,
            onLoadUpdated = { isLoading -> state = state.copy(loadingMore = isLoading, loading = isLoading && state.posts.isEmpty()) },
            onRequest = { page -> getCommunityPosts(id, page).first() },
            getNextKey = { key, _ -> key + 1 },
            isEnd = { items -> items.size < pageSize },
            onError = { t -> state = state.copy(error = t.message) },
            onSuccess = { items, newKey, end ->
                val combined = if (newKey == 2) items else state.posts + items
                state = state.copy(posts = combined, nextPage = newKey, endReached = end)
            },
        )
    }

    LaunchedEffect(id) {
        scope.launch { state = state.copy(community = getCommunityDetails(id).first(), loading = false) }
        scope.launch {
            paginator.reset()
            paginator.loadNext()
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        when {
            state.loading && state.community == null -> Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.error != null && state.community == null -> Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
            else -> state.community?.let { c ->
                Text(c.title, style = MaterialTheme.typography.headlineSmall)
                if (!c.description.isNullOrEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(c.description ?: "", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(12.dp))
                Text("${'$'}{c.subscribersCount} subscribers", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(12.dp))
                val listState = rememberLazyListState()
                LaunchedEffect(listState, state.endReached, state.loadingMore) {
                    snapshotFlow { listState.layoutInfo }
                        .map { it.visibleItemsInfo.lastOrNull()?.index to it.totalItemsCount }
                        .distinctUntilChanged()
                        .filter { (last, total) -> last != null && total > 0 && last >= total - 3 && !state.endReached && !state.loadingMore }
                        .collect { scope.launch { paginator.loadNext() } }
                }
                LazyColumn(Modifier.weight(1f), state = listState) {
                    items(state.posts) { p ->
                        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(p.content, style = MaterialTheme.typography.bodyLarge)
                            if (!p.image.isNullOrEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
                                SharedImage(url = p.image!!, contentDescription = p.communityTitle ?: strings.image)
                            }
                            Spacer(Modifier.height(4.dp))
                            val dateText = p.dateMillis?.let { " • ${formatRelative(it)}" } ?: ""
                            val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
                            Text("${strings.by} ${'$'}{p.userName ?: strings.unknown} • ${'$'}{p.likesCount} ${strings.likes} • ${'$'}{p.commentsCount} ${strings.comments}${'$'}dateText", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            CommentsSection(
                                postId = p.id,
                                getComments = getComments,
                                addComment = addComment,
                                userIdProvider = userIdProvider,
                            )
                        }
                    }
                    item {
                        if (state.loadingMore) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else if (!state.endReached) {
                            Button(onClick = { scope.launch { paginator.loadNext() } }, modifier = Modifier.fillMaxWidth()) { Text(com.edufelip.finn.shared.i18n.LocalStrings.current.load_more) }
                        }
                    }
                    if (state.endReached) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                                Text(text = com.edufelip.finn.shared.i18n.LocalStrings.current.end_of_list, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(onClick = onBack) { Text(com.edufelip.finn.shared.i18n.LocalStrings.current.back) }
            }
        }
    }
}
