package com.edufelip.finn.shared.domain.util

class UseCaseException(val domainError: DomainError) : Exception(domainError.toString())
