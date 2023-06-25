package com.edufelip.finn.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.edufelip.finn.domain.models.repositories.IUserRepository;
import com.edufelip.finn.domain.models.User;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private SavedStateHandle handle;
    private IUserRepository userRepository;
    private MutableLiveData<User> _deleteResult = new MutableLiveData<>();
    private LiveData<User> deleteResult = _deleteResult;

    @Inject
    public SettingsViewModel(SavedStateHandle savedStateHandle,
                             IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void deleteUser(String userId) {
        userRepository.deleteUser(userId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull String response) {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        User user = new User();
                        user.setId("-1");
                        _deleteResult.postValue(user);
                    }

                    @Override
                    public void onComplete() {
                        User user = new User();
                        user.setId("1");
                        _deleteResult.postValue(user);
                    }
                });
    }

    public LiveData<User> observeDelete() {
        return deleteResult;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
