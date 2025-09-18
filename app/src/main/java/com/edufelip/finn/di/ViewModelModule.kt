package com.edufelip.finn.di

import com.edufelip.finn.shared.data.CacheTtlProvider
import com.edufelip.finn.shared.data.local.CommentCacheDataSource
import com.edufelip.finn.shared.data.local.CommunityCacheDataSource
import com.edufelip.finn.shared.data.local.PostCacheDataSource
import com.edufelip.finn.shared.data.remote.source.CommentRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.CommunityRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.PostRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.UserRemoteDataSource
import com.edufelip.finn.shared.data.repository.DefaultCommentRepository
import com.edufelip.finn.shared.data.repository.DefaultCommunityRepository
import com.edufelip.finn.shared.data.repository.DefaultPostRepository
import com.edufelip.finn.shared.data.repository.DefaultUserRepository
import com.edufelip.finn.shared.domain.repository.CommentRepository
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.repository.PostRepository
import com.edufelip.finn.shared.domain.repository.UserRepository
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
    fun providesUserRepository(remote: UserRemoteDataSource): UserRepository =
        DefaultUserRepository(remote)

    @Provides
    @ViewModelScoped
    fun providesPostRepository(
        remote: PostRemoteDataSource,
        cacheDataSource: PostCacheDataSource,
        cacheTtlProvider: CacheTtlProvider,
    ): PostRepository = DefaultPostRepository(remote, cacheDataSource, cacheTtlProvider)

    @Provides
    @ViewModelScoped
    fun providesCommunityRepository(
        remote: CommunityRemoteDataSource,
        cacheDataSource: CommunityCacheDataSource,
        cacheTtlProvider: CacheTtlProvider,
    ): CommunityRepository = DefaultCommunityRepository(remote, cacheDataSource, cacheTtlProvider)

    @Provides
    @ViewModelScoped
    fun providesCommentRepository(
        remote: CommentRemoteDataSource,
        @Named("commentPageSize") pageSize: Int,
        cacheDataSource: CommentCacheDataSource,
        cacheTtlProvider: CacheTtlProvider,
    ): CommentRepository = DefaultCommentRepository(remote, cacheDataSource, cacheTtlProvider, pageSize)
}
