package com.projects.finn.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.projects.finn.data.repositories.CommunityRepository;
import com.projects.finn.data.repositories.interfaces.ICommentRepository;
import com.projects.finn.data.repositories.interfaces.ICommunityRepository;
import com.projects.finn.data.repositories.interfaces.IPostRepository;
import com.projects.finn.data.repositories.interfaces.IUserRepository;
import com.projects.finn.models.Post;
import com.projects.finn.models.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private final IUserRepository userRepository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<User> _user = new MutableLiveData<>();
    private LiveData<User> user = _user;

    @Inject
    public ProfileViewModel(SavedStateHandle handle,
                                  CommunityRepository communityRepository,
                                  IPostRepository postRepository,
                                  IUserRepository userRepository,
                                  ICommentRepository commentRepository){
        this.userRepository = userRepository;
        this.savedStateHandle = handle;
    }

    public void getUser(String id) {
        userRepository.getUser(id)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
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
