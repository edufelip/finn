package com.edufelip.finn.di

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.edufelip.finn.BuildConfig
import com.edufelip.finn.network.FinnHttpLogger
import com.edufelip.finn.data.cache.RemoteConfigCacheTtlProvider
import com.edufelip.finn.shared.cache.FinnDatabase
import com.edufelip.finn.shared.data.CacheTtlProvider
import com.edufelip.finn.shared.data.local.CommentCacheDataSource
import com.edufelip.finn.shared.data.local.CommunityCacheDataSource
import com.edufelip.finn.shared.data.local.PostCacheDataSource
import com.edufelip.finn.shared.data.local.SqlDelightCommentCacheDataSource
import com.edufelip.finn.shared.data.local.SqlDelightCommunityCacheDataSource
import com.edufelip.finn.shared.data.local.SqlDelightPostCacheDataSource
import com.edufelip.finn.shared.data.remote.api.ApiServiceV2
import com.edufelip.finn.shared.data.remote.source.CommentRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.CommunityRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.PostRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.RetrofitCommentRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.RetrofitCommunityRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.RetrofitPostRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.RetrofitUserRemoteDataSource
import com.edufelip.finn.shared.data.remote.source.UserRemoteDataSource
import com.edufelip.finn.utils.GoogleAuthUiClient
import com.edufelip.finn.utils.RemoteConfigUtils
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {
    // Legacy ApiService (RxJava) removed; using ApiServiceV2 with coroutines.

    @Provides
    @Singleton
    @OptIn(ExperimentalSerializationApi::class)
    fun providesBackendApiV2(
        remoteConfigUtils: RemoteConfigUtils,
        okHttpClient: OkHttpClient,
    ): ApiServiceV2 {
        val baseUrl = remoteConfigUtils.getRemoteServerAddress()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpClient)
            .build()
            .create(ApiServiceV2::class.java)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor(FinnHttpLogger).setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder().apply {
            if (BuildConfig.DEBUG) addInterceptor(logger)
        }
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        return okHttpClient
    }

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseRemoteConfig(): RemoteConfigUtils {
        return RemoteConfigUtils()
    }

    @Provides
    @Singleton
    fun providesGoogleAuthUiClient(@ApplicationContext context: Context?): GoogleAuthUiClient =
        GoogleAuthUiClient(context!!)

    @Provides
    @Singleton
    fun providesSqlDriver(@ApplicationContext context: Context): SqlDriver =
        AndroidSqliteDriver(FinnDatabase.Schema, context, "finn.db")

    @Provides
    @Singleton
    fun providesDatabase(driver: SqlDriver): FinnDatabase = FinnDatabase(driver)

    @Provides
    @Singleton
    fun providesPostCacheDataSource(database: FinnDatabase): PostCacheDataSource =
        SqlDelightPostCacheDataSource(database)

    @Provides
    @Singleton
    fun providesCommunityCacheDataSource(database: FinnDatabase): CommunityCacheDataSource =
        SqlDelightCommunityCacheDataSource(database)

    @Provides
    @Singleton
    fun providesCommentCacheDataSource(database: FinnDatabase): CommentCacheDataSource =
        SqlDelightCommentCacheDataSource(database)

    @Provides
    @Singleton
    fun providesCacheTtlProvider(remoteConfigUtils: RemoteConfigUtils): CacheTtlProvider =
        RemoteConfigCacheTtlProvider(remoteConfigUtils)

    @Provides
    @Singleton
    fun providePostRemoteDataSource(api: ApiServiceV2): PostRemoteDataSource =
        RetrofitPostRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideCommunityRemoteDataSource(api: ApiServiceV2): CommunityRemoteDataSource =
        RetrofitCommunityRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideCommentRemoteDataSource(api: ApiServiceV2): CommentRemoteDataSource =
        RetrofitCommentRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(api: ApiServiceV2): UserRemoteDataSource =
        RetrofitUserRemoteDataSource(api)

    @Provides
    @Singleton
    @Named("commentPageSize")
    fun provideCommentPageSize(remoteConfigUtils: RemoteConfigUtils): Int =
        remoteConfigUtils.getCommentsPageSize().takeIf { it > 0 } ?: 10
}
