package com.edufelip.finn.shared.domain.usecase

import com.edufelip.finn.shared.data.network.NetworkErrorMapper
import com.edufelip.finn.shared.domain.model.Comment
import com.edufelip.finn.shared.domain.repository.CommentRepository
import com.edufelip.finn.shared.domain.util.Result
import com.edufelip.finn.shared.domain.util.UseCaseException
import com.edufelip.finn.shared.domain.util.retryWithBackoff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class AddCommentUseCase(private val repo: CommentRepository) {
    operator fun invoke(postId: Int, userId: String, content: String): Flow<Result<Comment>> =
        repo.add(postId, userId, content)
            .map<Comment, Result<Comment>> { Result.Success(it) }
            .onStart { emit(Result.Loading) }
            .retryWithBackoff(maxRetries = 2, initialDelay = 200L) { throwable ->
                val error = if (throwable is UseCaseException) throwable.domainError else NetworkErrorMapper.map(throwable)
                error is com.edufelip.finn.shared.domain.util.DomainError.Network && error.isTransient
            }
            .catch { emit(Result.Error(NetworkErrorMapper.map(it))) }
}
