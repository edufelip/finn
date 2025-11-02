package com.edufelip.finn.shared.ui.screens.community

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
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.i18n.LocalStrings
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
    val strings = LocalStrings.current

    fun submit() {
        val snapshot = state
        if (snapshot.title.isBlank()) {
            state = snapshot.copy(error = strings.title)
            return
        }
        scope.launch {
            createCommunity(snapshot.title, snapshot.description, snapshot.image).collect { result ->
                when (result) {
                    is Result.Loading -> state = state.copy(loading = true, error = null)
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
            value = state.title,
            onValueChange = { state = state.copy(title = it) },
            label = { Text(strings.title) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = { state = state.copy(description = it) },
            label = { Text(strings.description) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }
        Row {
            Button(onClick = { submit() }, enabled = state.title.isNotBlank() && !state.loading) {
                Text(strings.create)
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onCancel, enabled = !state.loading) { Text(strings.cancel) }
        }
        state.createdId?.let { onCreated(it) }
    }
}
