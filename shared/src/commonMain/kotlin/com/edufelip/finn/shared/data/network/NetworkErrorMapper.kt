package com.edufelip.finn.shared.data.network

import com.edufelip.finn.shared.domain.util.DomainError

object NetworkErrorMapper {
    fun map(throwable: Throwable): DomainError = when {
        throwable.message?.contains("unauthorized", ignoreCase = true) == true ->
            DomainError.Unauthorized(throwable.message)
        throwable.message?.contains("not found", ignoreCase = true) == true ->
            DomainError.NotFound(throwable.message)
        throwable.message?.contains("timeout", ignoreCase = true) == true ||
            throwable.message?.contains("connection", ignoreCase = true) == true ->
            DomainError.Network(throwable.message, isTransient = true)
        else -> DomainError.Unknown(throwable.message)
    }
}
