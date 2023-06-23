package com.projects.finn.di;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.projects.finn.R;
import com.projects.finn.data.network.ApiService;
import com.projects.finn.utils.RemoteConfigUtils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
abstract class AppModule {
    @Provides
    @Singleton
    static ApiService providesBackendApi(GsonConverterFactory factory, RemoteConfigUtils remoteConfigUtils) {
        String baseUrl = remoteConfigUtils.getRemoteServerAddress();
        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(factory)
            .build()
            .create(ApiService.class);
    }

    @Provides
    @Singleton
    static GsonConverterFactory providesGsonConverter() {
        return GsonConverterFactory.create();
    }

    @Provides
    @Singleton
    static FirebaseAuth providesFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    static RemoteConfigUtils providesFirebaseRemoteConfig() {
        return new RemoteConfigUtils();
    }

    @Provides
    @Singleton
    static RequestManager providesGlideInstance(@ApplicationContext Context context) {
        return Glide.with(context).setDefaultRequestOptions(
            new RequestOptions()
                .placeholder(R.drawable.user_icon)
        );
    }
}
