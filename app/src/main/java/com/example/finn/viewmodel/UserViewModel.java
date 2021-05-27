package com.example.finn.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finn.data.User;
import com.example.finn.network.APIService;
import com.example.finn.network.RetroInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {
    private MutableLiveData<List<User>> userList;

    public UserViewModel(){
        userList = new MutableLiveData<>();
    }

    public MutableLiveData<List<User>> getUserListObserver() {
        return userList;
    }

    public void makeApiCall(){
        APIService apiService = RetroInstance.getRetroClient().create(APIService.class);
        Call<List<User>> call = apiService.getUsersList();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userList.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                userList.postValue(null);
            }
        });
    }
}
