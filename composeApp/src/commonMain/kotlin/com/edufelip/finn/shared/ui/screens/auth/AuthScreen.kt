package com.edufelip.finn.shared.ui.screens.auth

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.asResult
import com.edufelip.finn.shared.domain.util.readableMessage
import com.edufelip.finn.shared.i18n.LocalStrings
import finn.composeapp.generated.resources.Res
import finn.composeapp.generated.resources.ic_google
import kotlinx.coroutines.flow.Flow
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
    onCreateAccount: (String, String) -> Unit,
) {
    val strings = LocalStrings.current
    var state by remember { mutableStateOf(AuthState()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            userIdFlow.asResult().collect { result ->
                when (result) {
                    is Result.Loading -> state = state.copy(loading = true, error = null)
                    is Result.Success -> {
                        state = state.copy(loading = false, userId = result.value, error = null)
                        result.value?.let { onSignedIn() }
                    }

                    is Result.Error -> state = state.copy(loading = false, error = result.error.readableMessage())
                }
            }
        }
    }

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

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { onCreateAccount(email, password) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !state.loading,
        ) { Text(strings.create_account) }

        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(strings.forgot_your_password)
            Spacer(modifier = Modifier.width(4.dp))
            Text(strings.continue_label, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRequestSignIn,
            modifier = Modifier.fillMaxWidth().height(65.dp),
            enabled = !state.loading,
            colors = ButtonDefaults.buttonColors(
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
