package com.edufelip.finn.shared.ui.components.organisms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.i18n.LocalStrings

@Composable
fun CreateMenuSheet(
    onCreateCommunity: () -> Unit,
    onCreatePost: () -> Unit,
) {
    val strings = LocalStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = strings.create, style = MaterialTheme.typography.titleMedium)
        ListItem(
            headlineContent = { Text(strings.create_community) },
            supportingContent = { Text(strings.create_community_description) },
            leadingContent = { Icon(Icons.Filled.People, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCreateCommunity() },
        )
        ListItem(
            headlineContent = { Text(strings.create_post) },
            supportingContent = { Text(strings.create_post_description) },
            leadingContent = { Icon(Icons.Filled.Edit, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCreatePost() },
        )
    }
}
