package com.edufelip.finn.shared.data.network

import com.edufelip.finn.shared.domain.util.DomainError

expect object NetworkErrorMapper {
    fun map(throwable: Throwable): DomainError
}
