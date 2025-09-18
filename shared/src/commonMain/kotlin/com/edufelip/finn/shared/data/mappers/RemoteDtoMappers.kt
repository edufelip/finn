package com.edufelip.finn.shared.data.mappers

import com.edufelip.finn.shared.data.remote.dto.CommentDto
import com.edufelip.finn.shared.data.remote.dto.CommunityDto
import com.edufelip.finn.shared.data.remote.dto.PostDto
import com.edufelip.finn.shared.data.remote.dto.SubscriptionDto
import com.edufelip.finn.shared.data.remote.dto.UserDto
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.domain.model.Subscription
import com.edufelip.finn.shared.domain.model.User

fun PostDto.toDomain(): Post =
    Post(
        id = id,
        content = content.orEmpty(),
        communityId = communityId,
        communityTitle = communityTitle,
        communityImage = communityImage,
        userId = userId,
        userName = userName,
        image = image,
        likesCount = likesCount,
        commentsCount = commentsCount,
        isLiked = isLiked,
        dateMillis = dateMillis,
        cachedAtMillis = null,
    )

fun CommentDto.toDomain(): Comment =
    Comment(
        id = id,
        postId = postId,
        userId = userId,
        userImage = userImage,
        userName = userName,
        content = content.orEmpty(),
        dateMillis = dateMillis,
        cachedAtMillis = null,
    )

fun CommunityDto.toDomain(subscribersCount: Int): Community =
    Community(
        id = id,
        title = title,
        description = description,
        image = image,
        subscribersCount = subscribersCount,
        ownerId = ownerId,
        createdAtMillis = createdAtMillis,
        cachedAtMillis = null,
    )

fun UserDto.toDomain(): User =
    User(
        id = id.orEmpty(),
        name = name,
        photoUrl = photoUrl,
        joinedAtMillis = joinedAtMillis,
    )

fun List<PostDto>.toDomainPosts(): List<Post> = map { it.toDomain() }

fun List<CommentDto>.toDomainComments(): List<Comment> = map { it.toDomain() }

fun SubscriptionDto.toDomain(): Subscription =
    Subscription(
        id = id,
        userId = userId,
        communityId = communityId,
        isModerator = isModerator,
    )
