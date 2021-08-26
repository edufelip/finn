package com.projects.finn.repositories;
import com.projects.finn.data.models.User;
import com.projects.finn.data.network.ApiService;
import java.io.IOException;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Response;

public class UserRepository implements IUserRepository {
    private final ApiService apiService;

    @Inject
    public UserRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public User getUser(String userId) {
        Call<User> call = apiService.getUser(userId);
        try {
            Response<User> response = call.execute();
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getOrCreate(User user) {
        User foundUser = getUser(user.getId());
        if(foundUser == null) {
            Call<User> call = apiService.createUser(user);
            try {
                Response<User> response = call.execute();
                return response.body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return foundUser;
    }
}
