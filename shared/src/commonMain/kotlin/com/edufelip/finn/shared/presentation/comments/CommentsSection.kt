package com.edufelip.finn.shared.presentation.comments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.pagination.DefaultPaginator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CommentsState(
    val loading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val endReached: Boolean = false,
    val error: String? = null,
)

@Composable
fun CommentsSection(
    postId: Int,
    getComments: GetCommentsForPostUseCase,
    addComment: AddCommentUseCase,
    userIdProvider: () -> String,
) {
    var state by remember { mutableStateOf(CommentsState()) }
    val pageSize = 10
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    val paginator = remember(postId) {
        DefaultPaginator(
            initialKey = 1,
            onLoadUpdated = { isLoading -> state = state.copy(loading = isLoading) },
            onRequest = { page -> getComments(postId, page).first() },
            getNextKey = { key, _ -> key + 1 },
            isEnd = { items -> items.size < pageSize },
            onError = { t -> state = state.copy(error = t.message) },
            onSuccess = { items, newKey, end ->
                val combined = if (newKey == 2) items else state.comments + items
                state = state.copy(comments = combined, endReached = end)
            },
        )
    }

    fun load() {
        scope.launch {
            paginator.reset()
            paginator.loadNext()
        }
    }
    fun loadMore() {
        scope.launch { paginator.loadNext() }
    }
    fun send(content: String) {
        val uid = userIdProvider()
        scope.launch {
            addComment(postId, uid, content)
                .catch { e -> state = state.copy(error = e.message) }
                .collect { c -> state = state.copy(comments = state.comments + c) }
        }
    }

    LaunchedEffect(postId) { load() }
    CommentsList(
        comments = state.comments,
        onSend = { text -> send(text) },
        onReply = { /* TODO: implement replies */ },
        endReached = state.endReached,
        onLoadMore = { loadMore() },
    )
}
