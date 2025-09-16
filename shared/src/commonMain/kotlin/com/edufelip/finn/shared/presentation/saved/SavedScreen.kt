package com.edufelip.finn.shared.presentation.saved

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.ui.components.SharedImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
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
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    var currentUserId by remember { mutableStateOf<String?>(null) }
    fun load(page: Int) {
        val id = currentUserId ?: return
        scope.launch {
            repo.postsByUser(id, page)
                .onStart { state = state.copy(loading = page == 1) }
                .collect { list ->
                    state = state.copy(
                        loading = false,
                        posts = if (page == 1) list else state.posts + list,
                        nextPage = page + 1,
                    )
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
