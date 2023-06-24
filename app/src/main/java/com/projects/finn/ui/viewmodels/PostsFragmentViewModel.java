package com.projects.finn.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.projects.finn.data.repositories.CommunityRepository;
import com.projects.finn.domain.models.repositories.ICommentRepository;
import com.projects.finn.domain.models.repositories.ICommunityRepository;
import com.projects.finn.domain.models.repositories.IPostRepository;
import com.projects.finn.domain.models.repositories.IUserRepository;
import com.projects.finn.domain.models.Post;

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
public class PostsFragmentViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private final ICommunityRepository communityRepository;
    private final IUserRepository userRepository;
    private final IPostRepository postRepository;
    private final ICommentRepository commentRepository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<List<Post>> _posts = new MutableLiveData<>();
    private MutableLiveData<Post> _updatedPost = new MutableLiveData<>();
    private MutableLiveData<String> _userName = new MutableLiveData<>();
    private LiveData<List<Post>> posts = _posts;
    private LiveData<Post> updatedPost = _updatedPost;

    @Inject
    public PostsFragmentViewModel(SavedStateHandle handle,
                              CommunityRepository communityRepository,
                              IPostRepository postRepository,
                              IUserRepository userRepository,
                              ICommentRepository commentRepository){
        this.communityRepository = communityRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.savedStateHandle = handle;
    }

    private Observable<Post> getPostsObservable(String userId, int page) {
        return postRepository.getPostsFromUser(userId, page) // from user
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<List<Post>, ObservableSource<Post>>) receivedPosts -> {
                    List<Post> list = new ArrayList<>();
                    if(posts.getValue() != null) list.addAll(posts.getValue());
                    list.addAll(receivedPosts);
                    _posts.postValue(list);
                    return Observable.fromIterable(receivedPosts)
                            .subscribeOn(Schedulers.io());
                });
    }

    public void getUserPosts(String userId, int page) {
        getPostsObservable(userId, page)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<Post, ObservableSource<Post>>) this::getPostCommentsObservable)
                .flatMap((Function<Post, ObservableSource<Post>>) this::getPostLikesObservable)
                .flatMap((Function<Post, ObservableSource<Post>>) this::getPostCommunityObservable)
                .flatMap((Function<Post, ObservableSource<Post>>) post -> getIsPostLiked(post, userId))
                .map(post -> {
                    if(_userName.getValue() != null) {
                        post.setUserName(_userName.getValue());
                    }
                    return post;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Post post) {
                        _updatedPost.setValue(post);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("Error", "Something wrong happened:" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<Post> getPostCommunityObservable(final Post post) {
        return communityRepository.getCommunity(post.getCommunityId())
                .toObservable()
                .map(community -> {
                    post.setCommunityTitle(community.getTitle());
                    post.setCommunityImage(community.getImage());
                    return post;
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getPostCommentsObservable(Post post) {
        return commentRepository.getCommentsPost(post.getId())
                .toObservable()
                .map(comments -> {
                    post.setCommentsCount(comments.size());
                    post.setComments(comments);
                    return post;
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getPostLikesObservable(Post post) {
        return postRepository.getPostLikes(post.getId())
                .toObservable()
                .map(integer -> {
                    post.setLikesCount(integer);
                    return post;
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getIsPostLiked(final Post post, String userId) {
        return postRepository.findLike(post.getId(), userId)
                .toObservable()
                .map(new Function<Integer, Post>() {
                    @Override
                    public Post apply(Integer integer) throws Throwable {
                        post.setLiked(integer == 1);
                        return post;
                    }
                });
    }

    public LiveData<List<Post>> observePosts() { return posts; }

    public LiveData<Post> observeUpdatedPost() { return updatedPost; }

    public void setUserName(String name) {
        this._userName.postValue(name);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
