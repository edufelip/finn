package com.edufelip.finn.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.edufelip.finn.BuildConfig
import com.edufelip.finn.R
import com.edufelip.finn.data.network.ApiServiceV2
import com.edufelip.finn.ui.delegators.auth.AuthExecutor
import com.edufelip.finn.ui.delegators.auth.GeneralAuthExecutor
import com.edufelip.finn.ui.delegators.auth.GoogleAuthExecutor
import com.edufelip.finn.utils.GoogleAuthUiClient
import com.edufelip.finn.utils.RemoteConfigUtils
import com.edufelip.finn.utils.extensions.GlideUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {
    // Legacy ApiService (RxJava) removed; using ApiServiceV2 with coroutines.

    @Provides
    @Singleton
    fun providesBackendApiV2(
        factory: GsonConverterFactory,
        remoteConfigUtils: RemoteConfigUtils,
        okHttpClient: OkHttpClient,
    ): ApiServiceV2 {
        val baseUrl = remoteConfigUtils.getRemoteServerAddress()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(factory)
            .client(okHttpClient)
            .build()
            .create(ApiServiceV2::class.java)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
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
    fun providesGsonConverter(): GsonConverterFactory {
        return GsonConverterFactory.create()
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
    fun providesGlideInstance(@ApplicationContext context: Context?): RequestManager {
        return Glide.with(context!!).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.user_icon),
        )
    }

    @Provides
    @Singleton
    fun providesGlideUtils(
        glide: RequestManager,
        remoteConfigUtils: RemoteConfigUtils,
    ): GlideUtils {
        return GlideUtils(glide, remoteConfigUtils)
    }

    @Provides
    @Singleton
    fun providesGoogleAuthUiClient(@ApplicationContext context: Context?): GoogleAuthUiClient =
        GoogleAuthUiClient(context!!)

    @Provides
    @Singleton
    fun providesGoogleAuthExecutor(googleAuthUiClient: GoogleAuthUiClient): GoogleAuthExecutor {
        return GoogleAuthExecutor(googleAuthUiClient)
    }

    @Provides
    @Singleton
    fun providesGeneralAuthExecutor(firebaseAuth: FirebaseAuth): GeneralAuthExecutor {
        return GeneralAuthExecutor(firebaseAuth)
    }

    @Provides
    @Singleton
    fun providesAuthExecutor(
        googleAuthExecutor: GoogleAuthExecutor,
        generalAuthExecutor: GeneralAuthExecutor,
    ): AuthExecutor {
        return AuthExecutor(googleAuthExecutor, generalAuthExecutor)
    }

    @Provides
    @Singleton
    @Named("commentPageSize")
    fun provideCommentPageSize(remoteConfigUtils: RemoteConfigUtils): Int =
        remoteConfigUtils.getCommentsPageSize().takeIf { it > 0 } ?: 10
}
