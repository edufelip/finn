package com.edufelip.finn.shared.data.remote.source

import com.edufelip.finn.shared.data.remote.api.ApiServiceV2
import com.edufelip.finn.shared.data.remote.dto.CommunityDto
import com.edufelip.finn.shared.data.remote.dto.SubscriptionDto
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
class RetrofitCommunityRemoteDataSource(
    private val api: ApiServiceV2,
) : CommunityRemoteDataSource {
    override suspend fun search(query: String): List<CommunityDto> = api.getCommunities(query)

    override suspend fun getById(id: Int): CommunityDto = api.getCommunity(id)

    override suspend fun getSubscribersCount(id: Int): Int = api.getCommunitySubscribersCount(id)

    override suspend fun create(title: String, description: String?, image: ByteArray?): CommunityDto {
        val payload = buildJsonObject {
            put("title", title)
            put("description", description ?: "")
        }
        val body: RequestBody = payload.toString().toRequestBody("application/json".toMediaType())
        val imagePart = image?.let { bytes ->
            val temp = File.createTempFile("upload_${'$'}{UUID.randomUUID()}", ".jpg")
            temp.writeBytes(bytes)
            val requestBody = temp.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("image", temp.name, requestBody)
        }
        return api.saveCommunity(body, imagePart)
    }

    override suspend fun subscribe(userId: String, communityId: Int): SubscriptionDto =
        api.subscribeToCommunity(SubscriptionDto(id = 0, userId = userId, communityId = communityId))

    override suspend fun unsubscribe(userId: String, communityId: Int) {
        api.unsubscribeFromCommunity(SubscriptionDto(id = 0, userId = userId, communityId = communityId))
    }

    override suspend fun getSubscription(userId: String, communityId: Int): SubscriptionDto? =
        api.getSubscription(userId, communityId)

    override suspend fun delete(id: Int) {
        api.deleteCommunity(id)
    }
}
