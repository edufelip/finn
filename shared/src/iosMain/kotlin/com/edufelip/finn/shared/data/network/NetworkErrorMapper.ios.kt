package com.edufelip.finn.shared.data.network

import com.edufelip.finn.shared.domain.util.DomainError

actual object NetworkErrorMapper {
    actual fun map(throwable: Throwable): DomainError =
        DomainError.Network(throwable.message, isTransient = true)
}
