package com.edufelip.finn.shared.domain.util

import com.edufelip.finn.shared.data.network.NetworkErrorMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Maps upstream emissions into the shared [Result] contract so callers can
 * handle loading/error/success states without manual try/catch blocks.
 */
fun <T> Flow<T>.asResult(emitLoading: Boolean = true): Flow<Result<T>> = flow {
    if (emitLoading) emit(Result.Loading)
    try {
        collect { value -> emit(Result.Success(value)) }
    } catch (throwable: Throwable) {
        val error = if (throwable is UseCaseException) throwable.domainError else NetworkErrorMapper.map(throwable)
        emit(Result.Error(error))
    }
}
