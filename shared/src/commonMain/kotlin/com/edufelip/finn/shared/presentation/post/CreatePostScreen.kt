package com.edufelip.finn.shared.presentation.post

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.SharedImageBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen(
    repo: PostRepository,
    userIdProvider: () -> String,
    pickImage: suspend () -> ByteArray?,
    onCreated: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    var state by remember { mutableStateOf(CreatePostState()) }
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    val strings = LocalStrings.current
    fun pick() {
        scope.launch { state = state.copy(imageBytes = pickImage()) }
    }
    fun submit() {
        val s = state
        val userId = userIdProvider()
        if (userId.isBlank()) {
            state = s.copy(error = "Not authenticated")
            return
        }
        if (s.content.isBlank()) {
            state = s.copy(error = "Content is empty")
            return
        }
        scope.launch {
            state = s.copy(loading = true, error = null, createdId = null)
            repo.createPost(content = s.content, userId = userId, image = s.imageBytes)
                .catch { e -> state = state.copy(loading = false, error = e.message ?: "Unknown error") }
                .collect { p -> state = state.copy(loading = false, createdId = p.id) }
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = state.content,
            onValueChange = { state = state.copy(content = it) },
            label = { Text(strings.content) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
        )
        Spacer(Modifier.height(8.dp))
        Row {
            Button(onClick = { pick() }) { Text(strings.pick_image) }
            Spacer(Modifier.width(8.dp))
            state.imageBytes?.let { Text("${'$'}{it.size} bytes selected") }
        }
        state.imageBytes?.let { bytes ->
            Spacer(Modifier.height(8.dp))
            SharedImageBytes(bytes = bytes, contentDescription = strings.image)
        }
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(12.dp))
        Row {
            Button(onClick = { submit() }, enabled = state.content.isNotBlank() && !state.loading) { Text(strings.create) }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onCancel) { Text(strings.cancel) }
        }
        state.createdId?.let { onCreated(it) }
    }
}
