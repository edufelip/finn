package com.edufelip.finn.shared.ui.screens.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.atoms.SharedImageBytes
import kotlinx.coroutines.launch

@Composable
fun CreatePostScreen(
    repo: PostRepository,
    userIdProvider: () -> String,
    pickImage: suspend () -> ByteArray?,
    onCreated: (Int) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(CreatePostState()) }
    val scope = rememberCoroutineScope()
    val strings = LocalStrings.current

    fun pick() {
        scope.launch { state = state.copy(imageBytes = pickImage()) }
    }

    fun submit() {
        val snapshot = state
        val userId = userIdProvider()
        if (userId.isBlank()) {
            state = snapshot.copy(error = strings.not_authenticated)
            return
        }
        if (snapshot.content.isBlank()) {
            state = snapshot.copy(error = strings.content_empty)
            return
        }
        scope.launch {
            repo.createPost(content = snapshot.content, userId = userId, image = snapshot.imageBytes)
                .asResult()
                .collect { result ->
                    when (result) {
                        is Result.Loading -> state = state.copy(loading = true, error = null, createdId = null)
                        is Result.Success -> state = state.copy(loading = false, createdId = result.value.id, error = null)
                        is Result.Error -> state = state.copy(loading = false, error = result.error.readableMessage())
                    }
                }
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
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
            state.imageBytes?.let { Text("${it.size} bytes selected") }
        }
        state.imageBytes?.let { bytes ->
            Spacer(Modifier.height(8.dp))
            SharedImageBytes(bytes = bytes, contentDescription = strings.image)
        }
        if (state.loading) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(12.dp))
        Row {
            Button(
                onClick = { submit() },
                enabled = state.content.isNotBlank() && !state.loading,
            ) { Text(strings.create) }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onCancel, enabled = !state.loading) { Text(strings.cancel) }
        }
        state.createdId?.let { onCreated(it) }
    }
}
