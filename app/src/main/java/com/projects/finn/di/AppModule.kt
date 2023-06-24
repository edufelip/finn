package com.projects.finn.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.projects.finn.BuildConfig
import com.projects.finn.R
import com.projects.finn.data.network.ApiService
import com.projects.finn.ui.delegators.auth.AuthExecutor
import com.projects.finn.ui.delegators.auth.GeneralAuthExecutor
import com.projects.finn.ui.delegators.auth.GoogleAuthExecutor
import com.projects.finn.utils.GoogleAuthUiClient
import com.projects.finn.utils.RemoteConfigUtils
import com.projects.finn.utils.extensions.GlideUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {
    @Provides
    @Singleton
    fun providesBackendApi(
        factory: GsonConverterFactory,
        remoteConfigUtils: RemoteConfigUtils,
        okHttpClient: OkHttpClient
    ): ApiService {
        val baseUrl = remoteConfigUtils.remoteServerAddress
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(factory)
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
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
    fun providesGSO(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.FIREBASE_GOOGLE_ID)
            .requestEmail()
            .build()
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
                .placeholder(R.drawable.user_icon)
        )
    }

    @Provides
    @Singleton
    fun providesGlideUtils(
        glide: RequestManager,
        remoteConfigUtils: RemoteConfigUtils
    ): GlideUtils {
        return GlideUtils(glide, remoteConfigUtils)
    }

    @Provides
    @Singleton
    fun providesGoogleAuthUiClient(@ApplicationContext context: Context?): GoogleAuthUiClient {
        return GoogleAuthUiClient(
            context!!,
            Identity.getSignInClient(context)
        )
    }

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
        generalAuthExecutor: GeneralAuthExecutor
    ): AuthExecutor {
        return AuthExecutor(googleAuthExecutor, generalAuthExecutor)
    }
}