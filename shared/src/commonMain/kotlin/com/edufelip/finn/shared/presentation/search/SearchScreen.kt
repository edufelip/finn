package com.edufelip.finn.shared.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.readableMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchCommunities: SearchCommunitiesUseCase,
    onBack: () -> Unit = {},
    onCommunityClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(SearchUiState()) }
    val strings = com.edufelip.finn.shared.i18n.LocalStrings.current
    val scope = rememberCoroutineScope()

    fun submit() {
        val q = state.query
        scope.launch {
            searchCommunities(q).collect { result ->
                when (result) {
                    is Result.Loading -> state = state.copy(loading = true, error = null)
                    is Result.Success -> state = state.copy(loading = false, results = result.value, error = null)
                    is Result.Error -> state = state.copy(loading = false, error = result.error.readableMessage())
                }
            }
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text(strings.search) }, navigationIcon = { TextButton(onClick = onBack) { Text(strings.back) } }) }) { padding ->
        Column(modifier.fillMaxSize().padding(padding).padding(16.dp)) {
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
                    HorizontalDivider()
                }
            }
        }
    }
}
