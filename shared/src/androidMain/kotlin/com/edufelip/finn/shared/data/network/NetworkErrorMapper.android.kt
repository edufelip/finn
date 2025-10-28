package com.edufelip.finn.shared.data.network

import com.edufelip.finn.shared.domain.util.DomainError
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

actual object NetworkErrorMapper {
    actual fun map(throwable: Throwable): DomainError = when (throwable) {
        is HttpException -> when (throwable.code()) {
            401 -> DomainError.Unauthorized(throwable.message())
            404 -> DomainError.NotFound(throwable.message())
            in 500..599 -> DomainError.Network(throwable.message(), isTransient = true)
            else -> DomainError.Validation(throwable.message())
        }
        is IOException, is UnknownHostException -> DomainError.Network(throwable.message, isTransient = true)
        is SerializationException -> DomainError.Unknown("Serialization error: ${throwable.message}")
        else -> DomainError.Unknown(throwable.message)
    }
}
