package com.edufelip.finn.shared.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.i18n.LocalStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class AuthState(val loading: Boolean = false, val userId: String? = null, val error: String? = null)

@Composable
fun AuthScreen(
    userIdFlow: Flow<String?>,
    onRequestSignIn: () -> Unit,
    onRequestSignOut: () -> Unit,
    onSignedIn: () -> Unit,
) {
    var state by remember { mutableStateOf(AuthState()) }
    val scope = remember { CoroutineScope(Dispatchers.Main) }
    LaunchedEffect(Unit) {
        scope.launch {
            userIdFlow
                .onStart { state = state.copy(loading = true) }
                .catch { e -> state = state.copy(loading = false, error = e.message) }
                .collect { id ->
                    state = state.copy(loading = false, userId = id)
                    if (id != null) onSignedIn()
                }
        }
    }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(LocalStrings.current.login_welcome, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        if (state.loading) {
            CircularProgressIndicator()
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(24.dp))
        if (state.userId == null) {
            Button(onClick = onRequestSignIn) { Text(LocalStrings.current.sign_in_with_google) }
        } else {
            Text("${LocalStrings.current.user}: ${'$'}{state.userId}")
            Spacer(Modifier.height(8.dp))
            Button(onClick = onRequestSignOut) { Text(LocalStrings.current.sign_out) }
        }
    }
}
