package com.edufelip.finn.shared.domain.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.retryWhen

sealed interface Result<out T> {
    data class Success<T>(val value: T) : Result<T>
    data class Error(val error: DomainError) : Result<Nothing>
    data object Loading : Result<Nothing>
}

sealed interface DomainError {
    data class Network(val message: String? = null, val isTransient: Boolean = true) : DomainError
    data class Unauthorized(val message: String? = null) : DomainError
    data class NotFound(val message: String? = null) : DomainError
    data class Validation(val message: String? = null) : DomainError
    data class Unknown(val message: String? = null) : DomainError
}

fun DomainError.readableMessage(): String = when (this) {
    is DomainError.Network -> message ?: "Network error"
    is DomainError.Unauthorized -> message ?: "Unauthorized"
    is DomainError.NotFound -> message ?: "Not found"
    is DomainError.Validation -> message ?: "Validation error"
    is DomainError.Unknown -> message ?: "Unexpected error"
}

inline fun <T> Result<T>.onSuccess(block: (T) -> Unit): Result<T> {
    if (this is Result.Success) block(value)
    return this
}

inline fun <T> Result<T>.onError(block: (DomainError) -> Unit): Result<T> {
    if (this is Result.Error) block(error)
    return this
}

inline fun <T> Result<T>.onLoading(block: () -> Unit): Result<T> {
    if (this is Result.Loading) block()
    return this
}

suspend fun <T> kotlinx.coroutines.flow.Flow<Result<T>>.awaitTerminal(): Result<T> =
    this.first { it !is Result.Loading }

fun <T> Flow<Result<T>>.retryWithBackoff(
    maxRetries: Int = 3,
    initialDelay: Long = 300L,
    shouldRetry: (Throwable) -> Boolean,
): Flow<Result<T>> =
    retryWhen { cause, attempt ->
        if (attempt >= maxRetries) return@retryWhen false
        if (shouldRetry(cause)) {
            delay(initialDelay * (attempt + 1))
            true
        } else {
            false
        }
    }
