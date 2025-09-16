package com.edufelip.finn.shared.domain.repository

import com.edufelip.finn.shared.domain.model.Community
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun search(query: String): Flow<List<Community>>
    fun getById(id: Int): Flow<Community>
    fun create(title: String, description: String?, image: ByteArray? = null): Flow<Community>
}
