package com.edufelip.finn.shared.domain.repository

import com.edufelip.finn.shared.domain.model.Community
import com.edufelip.finn.shared.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    fun search(query: String): Flow<List<Community>>
    fun getById(id: Int): Flow<Community>
    fun create(title: String, description: String?, image: ByteArray? = null): Flow<Community>
    fun subscribe(userId: String, communityId: Int): Flow<Subscription>
    fun unsubscribe(userId: String, communityId: Int): Flow<Unit>
    fun getSubscription(userId: String, communityId: Int): Flow<Subscription?>
    fun delete(id: Int): Flow<Unit>
}
