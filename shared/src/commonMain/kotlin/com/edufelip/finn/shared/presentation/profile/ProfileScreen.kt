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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.domain.usecase.GetUserUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.SharedImage
import com.edufelip.finn.shared.util.format.formatJoined
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
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
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(ProfileState()) }
    val scope = rememberCoroutineScope()
    fun loadPosts(id: String, page: Int) {
        scope.launch {
            getUserPosts(id, page).asResult().collect { result ->
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
                scope.launch {
                    getUser(id).collect { result ->
                        when (result) {
                            is Result.Loading -> state = state.copy(loading = true, error = null)
                            is Result.Success -> state = state.copy(user = result.value, loading = false, error = null)
                            is Result.Error -> state = state.copy(loading = false, error = result.error.readableMessage())
                        }
                    }
                }
                loadPosts(id, 1)
            }
        }
    }
    Column(modifier.fillMaxSize()) {
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
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }
        LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
            state.posts.firstOrNull()?.cachedAtMillis?.let {
                item {
                    Text(
                        text = LocalStrings.current.cached_label.replace("%1s", com.edufelip.finn.shared.util.format.formatRelative(it)),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
            }
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
