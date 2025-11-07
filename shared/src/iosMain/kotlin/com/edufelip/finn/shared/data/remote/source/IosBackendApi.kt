package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.dto.CommentCreateRequestDto
import com.edufelip.finn.shared.data.remote.dto.CommentDto
import com.edufelip.finn.shared.data.remote.dto.CommunityDto
import com.edufelip.finn.shared.data.remote.dto.LikeRequestDto
import com.edufelip.finn.shared.data.remote.dto.PostDto
import com.edufelip.finn.shared.data.remote.dto.SubscriptionDto
import com.edufelip.finn.shared.data.remote.dto.UserDto
import com.edufelip.finn.shared.data.remote.dto.UserReferenceDto
import com.edufelip.finn.shared.network.createIosHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val jsonConfig = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
}

class IosBackendApi(
    private val baseUrl: String,
    private val client: HttpClient = createIosHttpClient(),
) {
    private val apiBase = baseUrl.trimEnd('/')

    suspend fun getFeed(userId: String, page: Int): List<PostDto> =
        client.get("$apiBase/posts/users/$userId/feed") {
            parameter("page", page)
        }.body()

    suspend fun getCommunityPosts(communityId: Int, page: Int): List<PostDto> =
        client.get("$apiBase/posts/communities/$communityId") {
            parameter("page", page)
        }.body()

    suspend fun getUserPosts(userId: String, page: Int): List<PostDto> =
        client.get("$apiBase/posts/users/$userId") {
            parameter("page", page)
        }.body()

    suspend fun createPost(payload: PostPayload): PostDto {
        val jsonBody = jsonConfig.encodeToString(payload)
        return client.submitFormWithBinaryData(
            url = "$apiBase/posts",
            formData = formData {
                append(
                    "post",
                    jsonBody,
                    Headers.build {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    },
                )
                payload.image?.let { bytes ->
                    append(
                        key = "image",
                        value = bytes,
                        headers = Headers.build {
                            append(HttpHeaders.ContentDisposition, "filename=\"upload.jpg\"")
                            append(HttpHeaders.ContentType, "image/jpeg")
                        },
                    )
                }
            },
        ).body()
    }

    suspend fun likePost(like: LikeRequestDto) {
        client.post("$apiBase/posts/likes") {
            contentType(ContentType.Application.Json)
            setBody(like)
        }
    }

    suspend fun dislikePost(postId: Int, user: UserReferenceDto) {
        client.post("$apiBase/posts/likes/$postId") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    suspend fun deletePost(postId: Int) {
        client.delete("$apiBase/posts/$postId")
    }

    suspend fun getCommunities(query: String): List<CommunityDto> =
        client.get("$apiBase/communities") {
            parameter("search", query)
        }.body()

    suspend fun getCommunity(id: Int): CommunityDto =
        client.get("$apiBase/communities/$id").body()

    suspend fun getCommunitySubscribers(id: Int): Int =
        client.get("$apiBase/communities/$id/subscribers").body()

    suspend fun createCommunity(payload: CommunityCreatePayload): CommunityDto {
        val jsonBody = jsonConfig.encodeToString(payload)
        return client.submitFormWithBinaryData(
            url = "$apiBase/communities",
            formData = formData {
                append(
                    "community",
                    jsonBody,
                    Headers.build { append(HttpHeaders.ContentType, ContentType.Application.Json.toString()) },
                )
                payload.image?.let { bytes ->
                    append(
                        "image",
                        bytes,
                        Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=\"community.jpg\"")
                        },
                    )
                }
            },
        ).body()
    }

    suspend fun subscribe(subscription: SubscriptionDto): SubscriptionDto =
        client.post("$apiBase/communities/subscribe") {
            contentType(ContentType.Application.Json)
            setBody(subscription)
        }.body()

    suspend fun unsubscribe(subscription: SubscriptionDto) {
        client.post("$apiBase/communities/unsubscribe") {
            contentType(ContentType.Application.Json)
            setBody(subscription)
        }
    }

    suspend fun getSubscription(userId: String, communityId: Int): SubscriptionDto? =
        client.get("$apiBase/communities/$communityId/users/$userId").body()

    suspend fun deleteCommunity(id: Int) {
        client.delete("$apiBase/communities/$id")
    }

    suspend fun getComments(postId: Int, page: Int, limit: Int): List<CommentDto> =
        client.get("$apiBase/comments/posts/$postId") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    suspend fun addComment(request: CommentCreateRequestDto): CommentDto =
        client.post("$apiBase/comments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getUser(id: String): UserDto =
        client.get("$apiBase/users/$id").body()
}

data class PostPayload(
    val content: String,
    val user_id: String,
    val community_id: Int? = null,
    val image: ByteArray? = null,
)

data class CommunityCreatePayload(
    val title: String,
    val description: String?,
    val image: ByteArray? = null,
)
