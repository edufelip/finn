package com.edufelip.finn.shared.ui.screens.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.atoms.SharedImage
import com.edufelip.finn.shared.util.format.formatRelative
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun PostDetailsScreen(
    postId: Int,
    post: Post?,
    postRepository: PostRepository,
    currentUserId: String,
    onBack: () -> Unit,
    onShare: (Post) -> Unit,
    onPostUpdated: (Post) -> Unit,
    onPostDeleted: (Int) -> Unit,
    getComments: GetCommentsForPostUseCase,
    addComment: AddCommentUseCase,
    userIdProvider: () -> String,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    var postState by remember(postId) { mutableStateOf(post) }
    var deleting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(post) {
        post?.let { postState = it }
    }

    if (postState == null) {
        Column(
            modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(strings.error)
            TextButton(onClick = onBack) { Text(strings.back) }
        }
        return
    }

    val currentPost = postState!!

    fun updatePost(newPost: Post) {
        postState = newPost
        onPostUpdated(newPost)
    }

    fun toggleLike() {
        if (currentUserId.isBlank()) {
            error = strings.not_authenticated
            return
        }
        val base = postState ?: return
        val currentlyLiked = base.isLiked
        val updated = base.copy(
            isLiked = !currentlyLiked,
            likesCount = if (currentlyLiked) (base.likesCount - 1).coerceAtLeast(0) else base.likesCount + 1,
        )
        updatePost(updated)
        error = null
        scope.launch {
            val flow = if (currentlyLiked) postRepository.dislike(base.id, currentUserId) else postRepository.like(base.id, currentUserId)
            flow.asResult(emitLoading = false).collect { result ->
                if (result is Result.Error) {
                    updatePost(base)
                    error = result.error.readableMessage()
                }
            }
        }
    }

    fun deletePost() {
        val base = postState ?: return
        error = null
        scope.launch {
            postRepository.delete(base.id, currentUserId, base.communityId).asResult().collect { result ->
                when (result) {
                    is Result.Loading -> deleting = true
                    is Result.Success -> {
                        deleting = false
                        onPostDeleted(base.id)
                        onBack()
                    }

                    is Result.Error -> {
                        error = result.error.readableMessage()
                        deleting = false
                    }
                }
            }
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
            }
            Text(text = currentPost.communityTitle ?: strings.unknown, style = MaterialTheme.typography.titleLarge)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val communityImage = currentPost.communityImage
            if (!communityImage.isNullOrBlank()) {
                SharedImage(
                    url = communityImage,
                    contentDescription = currentPost.communityTitle,
                    modifier = Modifier.size(48.dp),
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = currentPost.userName ?: strings.user, style = MaterialTheme.typography.bodyMedium)
                currentPost.dateMillis?.let {
                    Text(
                        text = formatRelative(it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            TextButton(onClick = { onShare(currentPost) }) {
                Text(strings.share)
            }
        }
        if (currentPost.content.isNotBlank()) {
            Text(currentPost.content, style = MaterialTheme.typography.bodyLarge)
        }
        val postImage = currentPost.image
        if (!postImage.isNullOrBlank()) {
            SharedImage(
                url = postImage,
                contentDescription = strings.image,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(onClick = { toggleLike() }) {
                Text(if (currentPost.isLiked) strings.unlike else strings.like)
            }
            Text("${currentPost.likesCount} ${strings.likes}")
            Text("${currentPost.commentsCount} ${strings.comments}")
        }
        if (currentPost.userId == currentUserId) {
            TextButton(onClick = { if (!deleting) deletePost() }, enabled = !deleting) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = strings.delete)
            }
        }
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        if (deleting) {
            CircularProgressIndicator()
        }
        CommentsSection(
            postId = currentPost.id,
            getComments = getComments,
            addComment = addComment,
            userIdProvider = userIdProvider,
            onCommentAdded = {
                val latest = postState ?: return@CommentsSection
                updatePost(latest.copy(commentsCount = latest.commentsCount + 1))
            },
        )
    }
}
