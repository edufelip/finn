package com.edufelip.finn.shared.ui.components.organisms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.i18n.LocalStrings
import com.edufelip.finn.shared.ui.components.atoms.SharedImage
import com.edufelip.finn.shared.util.format.formatRelative

@Composable
fun PostCard(
    post: Post,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onCommunityClick: (() -> Unit)? = null,
    onToggleLike: (Boolean) -> Unit,
    onCommentsClick: () -> Unit,
    onShareClick: () -> Unit,
    onHide: (() -> Unit)? = null,
) {
    val strings = LocalStrings.current
    var menuExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val communityClickModifier = if (onCommunityClick != null) Modifier.clickable { onCommunityClick() } else Modifier
                val communityImage = post.communityImage
                if (!communityImage.isNullOrBlank()) {
                    SharedImage(
                        url = communityImage,
                        contentDescription = post.communityTitle,
                        modifier = communityClickModifier.size(40.dp).clip(CircleShape),
                    )
                } else {
                    Column(
                        modifier = communityClickModifier
                            .size(40.dp)
                            .clip(CircleShape),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = post.communityTitle?.take(1)?.uppercase() ?: "?", style = MaterialTheme.typography.titleMedium)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .then(onCommunityClick?.let { Modifier.clickable { it() } } ?: Modifier),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = post.communityTitle ?: strings.unknown,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    val subtitle = buildString {
                        append(strings.by)
                        append(' ')
                        append(post.userName ?: strings.unknown)
                        post.dateMillis?.let {
                            append(" • ")
                            append(formatRelative(it))
                        }
                    }
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (onHide != null) {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = strings.more)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(strings.hide) },
                            onClick = {
                                menuExpanded = false
                                onHide()
                            },
                        )
                    }
                }
            }
            if (post.content.isNotBlank()) {
                Text(post.content, style = MaterialTheme.typography.bodyLarge)
            }
            val postImage = post.image
            if (!postImage.isNullOrBlank()) {
                SharedImage(
                    url = postImage,
                    contentDescription = strings.image,
                    modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconToggleButton(checked = post.isLiked, onCheckedChange = { onToggleLike(post.isLiked) }) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = strings.likes,
                            tint = if (post.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text("${post.likesCount}", style = MaterialTheme.typography.bodyMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCommentsClick) {
                        Icon(imageVector = Icons.Outlined.ChatBubbleOutline, contentDescription = strings.comments)
                    }
                    Text("${post.commentsCount}", style = MaterialTheme.typography.bodyMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onShareClick) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = strings.share)
                    }
                    Text(strings.share, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
