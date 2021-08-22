package com.projects.finn.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.projects.finn.data.models.Post;
import com.projects.finn.data.network.APIService;
import com.projects.finn.data.network.RetroInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragmentViewModel extends ViewModel {
    private MutableLiveData<List<Post>> postList;

    public HomeFragmentViewModel(){
        postList = new MutableLiveData<>();
    }

    public MutableLiveData<List<Post>> getUserListObserver() {
        return postList;
    }

    public void makeApiCall(){
        APIService apiService = RetroInstance.getRetroClient().create(APIService.class);
        Call<List<Post>> call = apiService.getPostList();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(@NonNull Call<List<Post>> call, @NonNull Response<List<Post>> response) {
                postList.postValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Post>> call, @NonNull Throwable t) {
                postList.postValue(null);
            }
        });
    }
}
