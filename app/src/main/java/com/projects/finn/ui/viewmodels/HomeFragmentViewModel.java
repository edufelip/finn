package com.projects.finn.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.projects.finn.data.repositories.CommentRepository;
import com.projects.finn.data.repositories.CommunityRepository;
import com.projects.finn.data.repositories.interfaces.ICommentRepository;
import com.projects.finn.data.repositories.interfaces.ICommunityRepository;
import com.projects.finn.models.Comment;
import com.projects.finn.models.Community;
import com.projects.finn.models.Post;
import com.projects.finn.models.User;
import com.projects.finn.data.repositories.interfaces.IPostRepository;
import com.projects.finn.data.repositories.interfaces.IUserRepository;
import com.projects.finn.data.repositories.PostRepository;
import com.projects.finn.data.repositories.UserRepository;

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
public class HomeFragmentViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private final IUserRepository userRepository;
    private final IPostRepository postRepository;
    private final ICommunityRepository communityRepository;
    private final ICommentRepository commentRepository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<User> _user = new MutableLiveData<>();
    private MutableLiveData<List<Post>> _posts = new MutableLiveData<>();
    private MutableLiveData<Post> _updatedPost = new MutableLiveData<>();
    private MutableLiveData<Integer> _nextPage = new MutableLiveData<>();
    private LiveData<User> user = _user;
    private LiveData<List<Post>> posts = _posts;
    private LiveData<Post> updatedPost = _updatedPost;
    private LiveData<Integer> nextPage = _nextPage;

    @Inject
    public HomeFragmentViewModel(SavedStateHandle handle,
                                 UserRepository userRepository,
                                 PostRepository postRepository,
                                 CommunityRepository communityRepository,
                                 CommentRepository commentRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.communityRepository = communityRepository;
        this.commentRepository = commentRepository;
        this.savedStateHandle = handle;
        this._nextPage.postValue(1);
    }

    public void getUser(User user) {
        userRepository.getUser(user.getId())
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
                Log.e("ERROR", e.getMessage());
                createUser(user);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void createUser(User user) {
        userRepository.createUser(user)
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

    public Observable<Post> getPostsObservable(String userId, int page) {
        return postRepository.getUserFeed(userId, page)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Post>, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(List<Post> receivedPosts) throws Throwable {
                        List<Post> list = new ArrayList<>();
                        if(posts.getValue() != null) list.addAll(posts.getValue());
                        list.addAll(receivedPosts);
                        _posts.postValue(list);
                        return Observable.fromIterable(receivedPosts)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }

    public void getPosts(String userId, int page) {
        getPostsObservable(userId, page)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Throwable {
                        return getCommunityPostObservable(post);
                    }
                })
                .flatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Throwable {
                        return getPostCommentsObservable(post);
                    }
                })
                .flatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Throwable {
                        return getPostLikesObservable(post);
                    }
                })
                .flatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Throwable {
                        return getPostUserObservable(post);
                    }
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
                        if(nextPage.getValue() != null) {
                            _nextPage.postValue(page + 1);
                        }
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

    private Observable<Post> getCommunityPostObservable(final Post post) {
        return communityRepository.getCommunity(post.getCommunity_id())
                .toObservable()
                .map(new Function<Community, Post>() {
                    @Override
                    public Post apply(Community community) throws Throwable {
                        post.setCommunity_image(community.getImage());
                        post.setCommunity_title(community.getTitle());
                        post.setCommunity_id(community.getId());
                        return post;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getPostLikesObservable(final Post post) {
        return postRepository.getPostLikes(post.getId())
                .toObservable()
                .map(new Function<Integer, Post>() {
                    @Override
                    public Post apply(Integer integer) throws Throwable {
                        post.setLikes_count(integer);
                        return post;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getPostCommentsObservable(final Post post) {
        return commentRepository.getCommentsPost(post.getId())
                .toObservable()
                .map(new Function<List<Comment>, Post>() {
                    @Override
                    public Post apply(List<Comment> comments) throws Throwable {
                        post.setComments_count(comments.size());
                        post.setComments(comments);
                        return post;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getPostUserObservable(final Post post) {
        return userRepository.getUser(post.getUser_id())
                .toObservable()
                .map(new Function<User, Post>() {
                    @Override
                    public Post apply(User user) throws Throwable {
                        post.setUser_name(user.getName());
                        return post;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public LiveData<User> observeUser() {
        return user;
    }

    public LiveData<List<Post>> observePosts() {
        return posts;
    }

    public LiveData<Post> observeUpdatedPost() {
        return updatedPost;
    }

    public LiveData<Integer> observeNextPage() { return nextPage; }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
