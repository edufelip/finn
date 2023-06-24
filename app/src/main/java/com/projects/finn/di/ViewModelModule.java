package com.projects.finn.di;

import com.projects.finn.data.network.ApiService;
import com.projects.finn.data.repositories.CommentRepository;
import com.projects.finn.data.repositories.CommunityRepository;
import com.projects.finn.domain.models.repositories.ICommentRepository;
import com.projects.finn.domain.models.repositories.ICommunityRepository;
import com.projects.finn.domain.models.repositories.IPostRepository;
import com.projects.finn.domain.models.repositories.IUserRepository;
import com.projects.finn.data.repositories.PostRepository;
import com.projects.finn.data.repositories.UserRepository;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
abstract class ViewModelModule {
    @Provides
    @ViewModelScoped
    static IUserRepository providesUserRepository(ApiService apiService) {
        return new UserRepository(apiService);
    }

    @Provides
    @ViewModelScoped
    static IPostRepository providesPostRepository(ApiService apiService) {
        return new PostRepository(apiService);
    }

    @Provides
    @ViewModelScoped
    static ICommunityRepository providesCommunityRepository(ApiService apiService) {
        return new CommunityRepository(apiService);
    }

    @Provides
    @ViewModelScoped
    static ICommentRepository providesCommentRepository(ApiService apiService) {
        return new CommentRepository(apiService);
    }
}
