package com.edufelip.finn.shared.presentation.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.util.format.formatRelative
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun CommentsList(
    comments: List<Comment>,
    onSend: (String) -> Unit = {},
    onReply: (Comment) -> Unit = {},
    endReached: Boolean = true,
    onLoadMore: () -> Unit = {},
    cacheAgeMillis: Long? = null,
    errorMessage: String? = null,
) {
    val strings = LocalStrings.current
    val listState = rememberLazyListState()
    LaunchedEffect(listState, endReached) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index to listState.layoutInfo.totalItemsCount }
            .distinctUntilChanged()
            .filter { (last, total) -> last != null && total > 0 && last >= total - 2 && !endReached }
            .collect { onLoadMore() }
    }
    Column {
        cacheAgeMillis?.let {
            Text(
                text = LocalStrings.current.cached_label.replace("%1s", formatRelative(it)),
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.height(8.dp))
        }
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
        }
        Text(strings.comments_header, style = MaterialTheme.typography.titleMedium)
        if (comments.isEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(strings.no_comments)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth(), state = listState) {
                items(comments) { c ->
                    Column(Modifier.fillMaxWidth()) {
                        val time = c.dateMillis?.let { formatRelative(it) } ?: ""
                        Text("${c.userName ?: strings.unknown} • $time", style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.height(4.dp))
                        Text(c.content, style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { onReply(c) }) { Text(strings.reply) }
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        var input by remember { mutableStateOf("") }
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(strings.write_comment) },
            singleLine = false,
        )
        TextButton(onClick = {
            if (input.isNotBlank()) {
                onSend(input)
                input = ""
            }
        }) { Text(strings.send) }
    }
}
