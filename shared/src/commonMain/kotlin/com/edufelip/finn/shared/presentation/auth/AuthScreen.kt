package com.edufelip.finn.shared.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edufelip.finn.shared.i18n.LocalStrings
import finn.shared.generated.resources.Res
import finn.shared.generated.resources.ic_google
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AuthScreen(
    userIdFlow: Flow<String?>,
    onRequestSignIn: () -> Unit,
    onRequestSignOut: () -> Unit,
    onSignedIn: () -> Unit,
    onEmailPasswordLogin: (String, String) -> Unit,
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
    val strings = LocalStrings.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = strings.login_welcome,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(8.dp))
        Text(text = strings.login_placeholder, fontSize = 20.sp)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            label = { Text(strings.e_mail) },
            singleLine = true,
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            label = { Text(strings.password) },
            singleLine = true,
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onEmailPasswordLogin(email, password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !state.loading,
        ) { Text(strings.login) }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(strings.forgot_your_password)
            Spacer(modifier = Modifier.width(4.dp))
            Text(strings.continue_label, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRequestSignIn,
            modifier = Modifier.fillMaxWidth().height(65.dp),
            enabled = !state.loading,
            colors = buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier.height(18.dp).width(18.dp),
                )
                Spacer(Modifier.width(12.dp))
                Text(strings.sign_in_with_google)
            }
        }

        Spacer(Modifier.height(16.dp))
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        if (state.loading) {
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
