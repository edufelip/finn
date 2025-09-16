package com.edufelip.finn.shared.presentation.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.usecase.AddCommentUseCase
import com.edufelip.finn.shared.domain.usecase.GetCommentsForPostUseCase
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.presentation.comments.CommentsSection

@Composable
fun PostDetailsScreen(
    postId: Int,
    onBack: () -> Unit,
    getComments: GetCommentsForPostUseCase,
    addComment: AddCommentUseCase,
    userIdProvider: () -> String,
) {
    val strings = LocalStrings.current
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(strings.comments_header)
        Spacer(Modifier.height(8.dp))
        CommentsSection(
            postId = postId,
            getComments = getComments,
            addComment = addComment,
            userIdProvider = userIdProvider,
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onBack, modifier = Modifier) { Text(strings.back) }
    }
}
