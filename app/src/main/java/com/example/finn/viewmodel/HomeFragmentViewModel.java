package com.example.finn.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finn.data.Post;
import com.example.finn.data.User;
import com.example.finn.network.APIService;
import com.example.finn.network.RetroInstance;

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
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                postList.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                postList.postValue(null);
            }
        });
    }
}
