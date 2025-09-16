package com.edufelip.finn.di

import com.edufelip.finn.data.network.ApiServiceV2
import com.edufelip.finn.shared.domain.repository.CommentRepository
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.repository.UserRepository
import com.edufelip.finn.sharedimpl.CommentRepositoryAndroid
import com.edufelip.finn.sharedimpl.CommunityRepositoryAndroid
import com.edufelip.finn.sharedimpl.PostRepositoryAndroid
import com.edufelip.finn.sharedimpl.UserRepositoryAndroid
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    @ViewModelScoped
    fun providesUserRepository(apiService: ApiServiceV2): UserRepository =
        UserRepositoryAndroid(apiService)

    @Provides
    @ViewModelScoped
    fun providesPostRepository(apiService: ApiServiceV2): PostRepository =
        PostRepositoryAndroid(apiService)

    @Provides
    @ViewModelScoped
    fun providesCommunityRepository(apiService: ApiServiceV2): CommunityRepository =
        CommunityRepositoryAndroid(apiService)

    @Provides
    @ViewModelScoped
    fun providesCommentRepository(
        apiService: ApiServiceV2,
        @Named("commentPageSize") pageSize: Int,
    ): CommentRepository = CommentRepositoryAndroid(apiService, pageSize)
}
