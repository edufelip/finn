package com.edufelip.finn.shared.presentation.community

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Composable
fun CreateCommunityScreen(
    createCommunity: CreateCommunityUseCase,
    onCreated: (Int) -> Unit,
    onCancel: () -> Unit,
) {
    var state by remember { mutableStateOf(CreateCommunityState()) }
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
    fun submit() {
        val s = state
        scope.launch {
            state = s.copy(loading = true, error = null)
            createCommunity(s.title, s.description, s.image)
                .catch { e -> state = state.copy(loading = false, error = e.message) }
                .collect { c -> state = state.copy(loading = false, createdId = c.id) }
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = state.title, onValueChange = { state = state.copy(title = it) }, label = { Text(strings.title) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = state.description, onValueChange = { state = state.copy(description = it) }, label = { Text(strings.description) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (state.error != null) Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(12.dp))
        Row {
            Button(onClick = ::submit, enabled = state.title.isNotBlank() && !state.loading) { Text(strings.create) }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onCancel) { Text(strings.cancel) }
        }
        state.createdId?.let { onCreated(it) }
    }
}
