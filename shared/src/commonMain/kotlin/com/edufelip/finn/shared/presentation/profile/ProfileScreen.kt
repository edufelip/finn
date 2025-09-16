package com.edufelip.finn.shared.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.domain.usecase.GetUserUseCase
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.SharedImage
import com.edufelip.finn.shared.util.format.formatJoined
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class ProfileState(
    val loading: Boolean = false,
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val nextPage: Int = 1,
    val error: String? = null,
)

@Composable
fun ProfileScreen(
    userIdFlow: Flow<String?>,
    getUser: GetUserUseCase,
    getUserPosts: (String, Int) -> Flow<List<Post>>,
    goToSaved: () -> Unit,
    goToSettings: () -> Unit,
) {
    var state by remember { mutableStateOf(ProfileState()) }
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    fun loadPosts(id: String, page: Int) {
        scope.launch {
            getUserPosts(id, page)
                .onStart { state = state.copy(loading = page == 1) }
                .collect { list -> state = state.copy(loading = false, posts = if (page == 1) list else state.posts + list, nextPage = page + 1) }
        }
    }
    LaunchedEffect(Unit) {
        scope.launch {
            userIdFlow.filterNotNull().collect { id ->
                scope.launch { getUser(id).collect { u -> state = state.copy(user = u, loading = false) } }
                loadPosts(id, 1)
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        if (state.loading && state.user == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Column
        }
        state.user?.let { u ->
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                if (!u.photoUrl.isNullOrEmpty()) SharedImage(url = u.photoUrl!!, contentDescription = u.name)
                Spacer(Modifier.width(12.dp))
                val strings = LocalStrings.current
                Column(Modifier.weight(1f)) {
                    Text(u.name ?: strings.user, style = MaterialTheme.typography.titleLarge)
                    u.joinedAtMillis?.let { millis ->
                        Text("${strings.joined} ${formatJoined(millis)}")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = goToSaved) { Text(strings.saved) }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = goToSettings) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = strings.settings)
                    }
                }
            }
        }
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            items(state.posts) { p ->
                Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Text(p.content, style = MaterialTheme.typography.bodyLarge)
                    if (!p.image.isNullOrEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        SharedImage(url = p.image!!, contentDescription = p.communityTitle)
                    }
                }
            }
            item { Button(onClick = { state.user?.id?.let { loadPosts(it, state.nextPage) } }, modifier = Modifier.fillMaxWidth()) { Text(LocalStrings.current.load_more) } }
        }
    }
}
