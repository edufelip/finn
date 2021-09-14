package com.projects.finn.ui.viewmodels;

import android.view.accessibility.AccessibilityNodeInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.projects.finn.data.repositories.CommunityRepository;
import com.projects.finn.data.repositories.PostRepository;
import com.projects.finn.data.repositories.interfaces.ICommentRepository;
import com.projects.finn.data.repositories.interfaces.ICommunityRepository;
import com.projects.finn.data.repositories.interfaces.IPostRepository;
import com.projects.finn.data.repositories.interfaces.IUserRepository;
import com.projects.finn.models.Community;
import com.projects.finn.models.Post;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class CreatePostViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private final ICommunityRepository communityRepository;
    private final IPostRepository postRepository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<List<Community>> _userCommunities = new MutableLiveData<>();
    private MutableLiveData<Post> _post = new MutableLiveData<>();
    private LiveData<List<Community>> userCommunities = _userCommunities;
    private LiveData<Post> post = _post;

    @Inject
    public CreatePostViewModel(SavedStateHandle handle,
                               CommunityRepository communityRepository,
                               PostRepository postRepository){
        this.communityRepository = communityRepository;
        this.postRepository = postRepository;
        this.savedStateHandle = handle;
    }

    public void getCommunitiesFromUser(String userId) {
        communityRepository.getCommunitiesFromUser(userId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Community>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<Community> communities) {
                        _userCommunities.postValue(communities);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        ArrayList<Community> error = new ArrayList();
                        Community community = new Community();
                        community.setId(-1);
                        error.add(community);
                        _userCommunities.postValue(error);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void createPost(RequestBody requestBody, MultipartBody.Part postImage) {
        postRepository.savePost(requestBody, postImage)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Post post) {
                        _post.postValue(post);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Post post = new Post();
                        post.setId(-1);
                        _post.postValue(post);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public LiveData<List<Community>> observeUserCommunities() {
        return userCommunities;
    }

    public LiveData<Post> observePost() {
        return post;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
