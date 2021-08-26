package com.projects.finn.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.projects.finn.data.models.User;
import com.projects.finn.repositories.IUserRepository;
import com.projects.finn.repositories.UserRepository;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeFragmentViewModel extends ViewModel {
    private final IUserRepository userRepository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<User> _user = new MutableLiveData<>();
    private LiveData<User> user = _user;

    @Inject
    public HomeFragmentViewModel(SavedStateHandle handle, UserRepository userRepository){
        this.userRepository = userRepository;
        this.savedStateHandle = handle;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void getOrCreate(User user) {
        User repoUser = userRepository.getOrCreate(user);
        this._user.postValue(repoUser);
    }
}
