package com.projects.finn.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.projects.finn.data.models.User;
import com.projects.finn.repositories.IUserRepository;
import com.projects.finn.repositories.UserRepository;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class HomeFragmentViewModel extends ViewModel {
    private final IUserRepository userRepository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<User> _user = new MutableLiveData<>();
    private LiveData<User> user = _user;
    CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public HomeFragmentViewModel(SavedStateHandle handle, UserRepository userRepository){
        this.userRepository = userRepository;
        this.savedStateHandle = handle;
    }

    public void getUser(User user) {
        userRepository.getUser(user.getId()).subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(@NonNull User user) {
                _user.postValue(user);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                createUser(user);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void createUser(User user) {
        userRepository.createUser(user).subscribe(new Observer<User>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                disposables.add(d);
            }

            @Override
            public void onNext(@NonNull User user) {
                _user.postValue(user);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                User user = new User();
                user.setId("-1");
                _user.postValue(user);
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public LiveData<User> observeUser() {
        return user;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
