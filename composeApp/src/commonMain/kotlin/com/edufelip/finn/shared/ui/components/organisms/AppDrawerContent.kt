package com.edufelip.finn.shared.ui.components.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.User
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.navigation.Route
import com.edufelip.finn.shared.ui.components.atoms.SharedImage
import com.edufelip.finn.shared.util.format.formatJoined

@Composable
fun AppDrawerContent(
    user: User?,
    onNavigate: (Route) -> Unit,
    onLogout: () -> Unit,
    onOpenPrivacy: () -> Unit,
) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (user != null) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                user.photoUrl?.takeIf { it.isNotBlank() }?.let { url ->
                    SharedImage(
                        url = url,
                        contentDescription = strings.profile,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                    )
                } ?: Icon(Icons.Filled.Person, contentDescription = strings.profile)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(user.name ?: strings.user, style = MaterialTheme.typography.titleMedium)
                    user.joinedAtMillis?.let {
                        Text(text = formatJoined(it), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        NavigationDrawerItem(
            label = { Text(strings.home) },
            selected = false,
            onClick = { onNavigate(Route.Home) },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
        )
        NavigationDrawerItem(
            label = { Text(strings.profile) },
            selected = false,
            onClick = { onNavigate(Route.Profile) },
            icon = { Icon(Icons.Filled.Person, contentDescription = null) },
        )
        NavigationDrawerItem(
            label = { Text(strings.saved) },
            selected = false,
            onClick = { onNavigate(Route.Saved) },
            icon = { Icon(Icons.Filled.Bookmark, contentDescription = null) },
        )
        NavigationDrawerItem(
            label = { Text(strings.settings) },
            selected = false,
            onClick = { onNavigate(Route.Settings) },
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
        )
        NavigationDrawerItem(
            label = { Text(strings.privacy_policy) },
            selected = false,
            onClick = onOpenPrivacy,
            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
        )
        Spacer(modifier = Modifier.height(24.dp))
        NavigationDrawerItem(
            label = { Text(strings.sign_out) },
            selected = false,
            onClick = onLogout,
            icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
        )
    }
}
