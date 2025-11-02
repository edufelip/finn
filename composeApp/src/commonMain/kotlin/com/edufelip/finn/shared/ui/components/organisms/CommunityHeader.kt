package com.edufelip.finn.shared.ui.components.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.model.Subscription
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.atoms.SharedImage
import com.edufelip.finn.shared.util.format.formatRelative

@Composable
fun CommunityHeader(
    community: Community?,
    currentUserId: String,
    membership: Subscription?,
    membershipLoading: Boolean,
    deleting: Boolean,
    onBack: () -> Unit,
    onSubscribe: () -> Unit,
    onUnsubscribe: () -> Unit,
    onDelete: () -> Unit,
) {
    val strings = LocalStrings.current
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
            }
            Text(community?.title ?: "", style = MaterialTheme.typography.headlineSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            community?.image?.takeIf { it.isNotBlank() }?.let { imageUrl ->
                SharedImage(
                    url = imageUrl,
                    contentDescription = community.title,
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                community?.description?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
                community?.createdAtMillis?.let {
                    Text(
                        text = strings.created_on.replace("%1s", formatRelative(it)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                community?.subscribersCount?.let {
                    Text(
                        text = strings.subscribers_count.replace("%1d", it.toString()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val isSubscribed = membership != null
            if (currentUserId.isBlank()) {
                OutlinedButton(onClick = {}, enabled = false) {
                    Text(strings.subscribe)
                }
            } else {
                if (isSubscribed) {
                    OutlinedButton(onClick = onUnsubscribe, enabled = !membershipLoading) {
                        Text(strings.unsubscribe)
                    }
                } else {
                    Button(onClick = onSubscribe, enabled = !membershipLoading) {
                        Text(strings.subscribe)
                    }
                }
            }
            if (community?.ownerId == currentUserId) {
                TextButton(onClick = onDelete, enabled = !deleting) {
                    Text(strings.delete)
                }
            }
            if (membershipLoading && currentUserId.isNotBlank()) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        }
    }
}
