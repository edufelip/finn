package com.edufelip.finn.shared.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchCommunities: SearchCommunitiesUseCase,
    onBack: () -> Unit = {},
    onCommunityClick: (Int) -> Unit = {},
) {
    var state by remember { mutableStateOf(SearchUiState()) }
    val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
    val scope = remember { CoroutineScope(Dispatchers.Main) }

    fun submit() {
        val q = state.query
        scope.launch {
            searchCommunities(q)
                .catch { e -> state = state.copy(loading = false, error = e.message) }
                .collect { list -> state = state.copy(loading = false, results = list) }
        }
        state = state.copy(loading = true, error = null)
    }

    Scaffold(topBar = { TopAppBar(title = { Text(strings.search) }, navigationIcon = { TextButton(onClick = onBack) { Text(strings.back) } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { state = state.copy(query = it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(strings.query) },
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))
            Button(onClick = { submit() }, enabled = !state.loading) { Text(strings.search) }
            Spacer(Modifier.height(16.dp))
            when {
                state.loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                state.error != null -> Text(state.error ?: strings.error, color = MaterialTheme.colorScheme.error)
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.results) { c ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onCommunityClick(c.id) }
                            .padding(vertical = 8.dp),
                    ) {
                        Text(c.title, style = MaterialTheme.typography.titleMedium)
                        if (!c.description.isNullOrEmpty()) Text(c.description ?: "")
                        Text("${'$'}{c.subscribersCount} subscribers", style = MaterialTheme.typography.labelSmall)
                    }
                    Divider()
                }
            }
        }
    }
}
