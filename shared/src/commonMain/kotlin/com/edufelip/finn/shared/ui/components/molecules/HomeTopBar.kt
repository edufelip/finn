package com.edufelip.finn.shared.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.atoms.SharedImage

@Composable
fun HomeTopBar(
    profileImageUrl: String?,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (!profileImageUrl.isNullOrBlank()) {
            SharedImage(
                url = profileImageUrl,
                contentDescription = strings.profile,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onMenuClick() },
            )
        } else {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = strings.profile)
            }
        }
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(CircleShape)
                .clickable { onSearchClick() },
            tonalElevation = 2.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Text(strings.search, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
