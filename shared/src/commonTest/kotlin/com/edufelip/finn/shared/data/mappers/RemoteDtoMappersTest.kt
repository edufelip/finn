package com.edufelip.finn.shared.data.mappers

import com.edufelip.finn.shared.data.remote.dto.CommentDto
import com.edufelip.finn.shared.data.remote.dto.CommunityDto
import com.edufelip.finn.shared.data.remote.dto.PostDto
import com.edufelip.finn.shared.data.remote.dto.UserDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RemoteDtoMappersTest {
    @Test
    fun postDtoToDomain_preservesCoreFields() {
        val dto = PostDto(
            id = 7,
            content = "Hello",
            communityTitle = "Kotlin",
            communityId = 42,
            communityImage = "community.png",
            userId = "user-1",
            userName = "Alice",
            image = "url",
            likesCount = 3,
            commentsCount = 2,
            isLiked = true,
            dateMillis = 1234L,
        )

        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.content, domain.content)
        assertEquals(dto.communityTitle, domain.communityTitle)
        assertEquals(dto.communityId, domain.communityId)
        assertEquals(dto.communityImage, domain.communityImage)
        assertEquals(dto.userId, domain.userId)
        assertEquals(dto.userName, domain.userName)
        assertEquals(dto.image, domain.image)
        assertEquals(dto.likesCount, domain.likesCount)
        assertEquals(dto.commentsCount, domain.commentsCount)
        assertEquals(dto.isLiked, domain.isLiked)
        assertEquals(dto.dateMillis, domain.dateMillis)
        assertNull(domain.cachedAtMillis)
    }

    @Test
    fun commentDtoToDomain_coercesNullContent() {
        val dto = CommentDto(
            id = 5,
            content = null,
            postId = 10,
            userId = "user-2",
            userImage = "avatar.png",
            userName = null,
            dateMillis = 50L,
        )

        val domain = dto.toDomain()

        assertEquals("", domain.content)
        assertEquals(dto.id, domain.id)
        assertEquals(dto.postId, domain.postId)
        assertEquals(dto.userId, domain.userId)
        assertEquals(dto.userImage, domain.userImage)
        assertEquals(dto.userName, domain.userName)
        assertEquals(dto.dateMillis, domain.dateMillis)
        assertNull(domain.cachedAtMillis)
    }

    @Test
    fun communityDtoToDomain_injectsSubscriberCount() {
        val dto = CommunityDto(id = 9, title = "Compose", description = "", image = null, ownerId = "owner", createdAtMillis = 123L)
        val domain = dto.toDomain(subscribersCount = 42)
        assertEquals(42, domain.subscribersCount)
        assertEquals(dto.title, domain.title)
        assertEquals(dto.ownerId, domain.ownerId)
        assertEquals(dto.createdAtMillis, domain.createdAtMillis)
        assertNull(domain.cachedAtMillis)
    }

    @Test
    fun userDtoToDomain_defaultsEmptyId() {
        val dto = UserDto(id = null, name = "Bob", photoUrl = null, joinedAtMillis = null)
        val domain = dto.toDomain()
        assertEquals("", domain.id)
        assertEquals(dto.name, domain.name)
        assertNull(domain.joinedAtMillis)
        assertNull(domain.photoUrl)
    }
}
