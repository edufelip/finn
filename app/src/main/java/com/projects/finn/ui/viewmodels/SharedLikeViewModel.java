package com.projects.finn.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.projects.finn.data.repositories.interfaces.IPostRepository;
import com.projects.finn.models.Like;
import com.projects.finn.data.repositories.PostRepository;
import com.projects.finn.models.User;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class SharedLikeViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private final IPostRepository postRepository;
    private MutableLiveData<Like> _like = new MutableLiveData<>();
    private LiveData<Like> like = _like;

    @Inject
    public SharedLikeViewModel(SavedStateHandle handle,
                               PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void likePost(String userId, int postId) {
        Like like = new Like();
        like.setUser_id(userId);
        like.setPost_id(postId);
        postRepository.likePost(like)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Like>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Like like) {
                        _like.postValue(like);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Like like = new Like();
                        like.setId(-1);
                        _like.postValue(like);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void dislikePost(int postId, User user) {
        postRepository.dislikePost(postId, user)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Void unused) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Like like = new Like();
                        like.setId(-1);
                        _like.postValue(like);
                    }

                    @Override
                    public void onComplete() {
                        Like like = new Like();
                        like.setId(-2);
                        _like.postValue(like);
                    }
                });
    }

    public LiveData<Like> observeLike() { return like; }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
