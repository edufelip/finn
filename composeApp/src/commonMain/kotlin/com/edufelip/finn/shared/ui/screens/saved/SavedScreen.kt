package com.edufelip.finn.shared.ui.screens.saved

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.atoms.SharedImage
import com.edufelip.finn.shared.util.format.formatRelative
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
fun SavedScreen(
    userIdFlow: Flow<String?>,
    repo: PostRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strings = LocalStrings.current
    var state by remember { mutableStateOf(SavedState()) }
    val scope = rememberCoroutineScope()
    var currentUserId by remember { mutableStateOf<String?>(null) }

    fun load(page: Int) {
        val id = currentUserId ?: return
        scope.launch {
            repo.postsByUser(id, page).asResult().collect { result ->
                when (result) {
                    is Result.Loading -> if (page == 1) state = state.copy(loading = true, error = null)
                    is Result.Success -> state = state.copy(
                        loading = false,
                        posts = if (page == 1) result.value else state.posts + result.value,
                        nextPage = page + 1,
                        error = null,
                    )

                    is Result.Error -> state = state.copy(loading = false, error = result.error.readableMessage())
                }
            }
        }
    }

    LaunchedEffect(userIdFlow) {
        userIdFlow.filterNotNull().collect { id ->
            currentUserId = id
            load(page = 1)
        }
    }

    Column(modifier.fillMaxSize().padding(16.dp)) {
        if (state.loading && state.posts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        state.posts.firstOrNull()?.cachedAtMillis?.let {
            Text(
                text = strings.cached_label.replace("%1s", formatRelative(it)),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        LazyColumn(Modifier.fillMaxSize()) {
            items(state.posts) { post ->
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(post.content, style = MaterialTheme.typography.bodyLarge)
                    post.image?.takeIf { it.isNotEmpty() }?.let { imageUrl ->
                        Spacer(Modifier.height(8.dp))
                        SharedImage(url = imageUrl, contentDescription = post.communityTitle)
                    }
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                Button(onClick = { load(state.nextPage) }, modifier = Modifier.fillMaxWidth()) { Text(strings.load_more) }
            }
            item {
                Spacer(Modifier.height(12.dp))
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text(strings.back) }
            }
        }
    }
}
