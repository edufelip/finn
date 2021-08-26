package com.projects.finn.di;

import com.projects.finn.data.network.ApiService;
import com.projects.finn.repositories.IUserRepository;
import com.projects.finn.repositories.UserRepository;

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
}
