package com.edufelip.finn.shared.presentation.comments

import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.UseCaseException
import com.edufelip.finn.shared.domain.util.awaitTerminal
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.pagination.DefaultPaginator
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
    onCommentAdded: (Comment) -> Unit = {},
) {
    var state by remember { mutableStateOf(CommentsState()) }
    val pageSize = 10
    val scope = rememberCoroutineScope()
    val paginator = remember(postId) {
        DefaultPaginator(
            initialKey = 1,
            onLoadUpdated = { isLoading -> state = state.copy(loading = isLoading) },
            onRequest = { page ->
                when (val result = getComments(postId, page).awaitTerminal()) {
                    is Result.Success -> result.value
                    is Result.Error -> throw UseCaseException(result.error)
                    Result.Loading -> emptyList()
                }
            },
            getNextKey = { key, _ -> key + 1 },
            isEnd = { items -> items.size < pageSize },
            onError = { t ->
                val msg = if (t is UseCaseException) t.domainError.readableMessage() else t.message
                state = state.copy(error = msg)
            },
            onSuccess = { items, newKey, end ->
                val combined = if (newKey == 2) items else state.comments + items
                state = state.copy(comments = combined, endReached = end, error = null)
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
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            state = state.copy(comments = state.comments + result.value, error = null)
                            onCommentAdded(result.value)
                        }
                        is Result.Error -> state = state.copy(error = result.error.readableMessage())
                        Result.Loading -> Unit
                    }
                }
        }
    }

    LaunchedEffect(postId) { load() }
    CommentsList(
        comments = state.comments,
        onSend = { text -> send(text) },
        onReply = { /* TODO: implement replies */ },
        endReached = state.endReached,
        onLoadMore = { loadMore() },
        cacheAgeMillis = state.comments.firstOrNull()?.cachedAtMillis,
        errorMessage = state.error,
    )
}
