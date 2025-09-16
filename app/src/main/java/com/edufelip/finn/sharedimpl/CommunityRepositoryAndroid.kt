package com.edufelip.finn.sharedimpl

import com.edufelip.finn.data.network.ApiServiceV2
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import com.edufelip.finn.shared.domain.model.Community as SharedCommunity

class CommunityRepositoryAndroid @Inject constructor(
    private val api: ApiServiceV2,
) : CommunityRepository {
    override fun search(query: String): Flow<List<SharedCommunity>> = flow {
        val list = api.getCommunities(query)
        val enriched = coroutineScope {
            list.map { c ->
                async {
                    val count = api.getCommunitySubscribersCount(c.id)
                    SharedCommunity(
                        id = c.id,
                        title = c.title,
                        description = c.description,
                        image = c.image,
                        subscribersCount = count,
                    )
                }
            }.map { it.await() }
        }
        emit(enriched)
    }

    override fun getById(id: Int): Flow<SharedCommunity> = flow {
        val c = api.getCommunity(id)
        val count = api.getCommunitySubscribersCount(id)
        emit(
            SharedCommunity(
                id = c.id,
                title = c.title,
                description = c.description,
                image = c.image,
                subscribersCount = count,
            ),
        )
    }

    override fun create(title: String, description: String?, image: ByteArray?): Flow<SharedCommunity> = flow {
        val json = Gson().toJson(
            mapOf(
                "title" to title,
                "description" to (description ?: ""),
            ),
        )
        val body: RequestBody = json.toRequestBody("application/json".toMediaType())
        val imagePart: MultipartBody.Part? = image?.let {
            val temp = File.createTempFile("upload_${'$'}{UUID.randomUUID()}", ".jpg")
            temp.writeBytes(it)
            val req = temp.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("image", temp.name, req)
        }
        val c = api.saveCommunity(body, imagePart)
        val count = api.getCommunitySubscribersCount(c.id)
        emit(SharedCommunity(c.id, c.title, c.description, c.image, count))
    }
}
