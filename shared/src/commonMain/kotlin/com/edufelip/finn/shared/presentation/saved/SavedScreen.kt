package com.edufelip.finn.shared.presentation.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.ui.components.SharedImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

data class SavedState(
    val loading: Boolean = false,
    val posts: List<com.edufelip.finn.shared.domain.model.Post> = emptyList(),
    val nextPage: Int = 1,
    val error: String? = null,
)

@Composable
fun SavedScreen(userIdFlow: Flow<String?>, repo: PostRepository, onBack: () -> Unit) {
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
    LaunchedEffect(Unit) {
        scope.launch {
            userIdFlow.filterNotNull().collect { id ->
                currentUserId = id
                load(1)
            }
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        if (state.loading && state.posts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }
        state.posts.firstOrNull()?.cachedAtMillis?.let {
            Text(
                text = com.edufelip.finn.shared.i18n.LocalStrings.current.cached_label.replace(
                    "%1s",
                    com.edufelip.finn.shared.util.format.formatRelative(it),
                ),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }
        LazyColumn(Modifier.fillMaxSize()) {
            items(state.posts) { p ->
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(p.content, style = MaterialTheme.typography.bodyLarge)
                    if (!p.image.isNullOrEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        SharedImage(url = p.image!!, contentDescription = p.communityTitle)
                    }
                }
            }
            item { Button(onClick = { load(state.nextPage) }, modifier = Modifier.fillMaxWidth()) { Text(com.edufelip.finn.shared.i18n.LocalStrings.current.load_more) } }
            item { Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text(com.edufelip.finn.shared.i18n.LocalStrings.current.back) } }
        }
    }
}
