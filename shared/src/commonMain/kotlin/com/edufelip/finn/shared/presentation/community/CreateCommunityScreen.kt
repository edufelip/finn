package com.edufelip.finn.shared.presentation.community

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.readableMessage
import kotlinx.coroutines.launch

@Composable
fun CreateCommunityScreen(
    createCommunity: CreateCommunityUseCase,
    onCreated: (Int) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(CreateCommunityState()) }
    val scope = rememberCoroutineScope()
    val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
    fun submit() {
        val s = state
        scope.launch {
            createCommunity(s.title, s.description, s.image).collect { result ->
                when (result) {
                    is Result.Loading -> state = state.copy(loading = true, error = null)
                    is Result.Success -> state = state.copy(loading = false, createdId = result.value.id, error = null)
                    is Result.Error -> state = state.copy(loading = false, error = result.error.readableMessage())
                }
            }
        }
    }
    Column(modifier.fillMaxSize().padding(16.dp)) {
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
