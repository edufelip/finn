package com.edufelip.finn.shared.di

import com.edufelip.finn.shared.domain.model.Post
import com.edufelip.finn.shared.presentation.vm.CommentsVM
import org.koin.core.Koin

// Factory for per-post CommentsVM instances
interface CommentsVMFactory {
    fun create(postId: Int): CommentsVM
}

// Platform auth actions consumed by shared UI
interface AuthActions {
    fun requestSignIn()
    fun requestSignOut()
    fun emailPasswordLogin(email: String, password: String)
    fun createAccount(email: String, password: String)
}

// Platform share actions consumed by shared UI
interface ShareActions {
    fun share(post: Post)
}

interface LinkActions {
    fun openUrl(url: String)
}

// Simple access to Koin from common code
object DI {
    private var provider: (() -> Koin)? = null

    val koin: Koin
        get() = provider?.invoke() ?: error("Koin not configured for shared module")

    fun configure(block: () -> Koin) {
        provider = block
    }
}
