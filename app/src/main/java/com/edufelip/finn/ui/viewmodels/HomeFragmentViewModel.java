package com.edufelip.finn.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edufelip.finn.data.repositories.CommentRepository;
import com.edufelip.finn.data.repositories.CommunityRepository;
import com.edufelip.finn.data.repositories.PostRepository;
import com.edufelip.finn.data.repositories.UserRepository;
import com.edufelip.finn.domain.models.Post;
import com.edufelip.finn.domain.models.User;
import com.edufelip.finn.domain.models.repositories.ICommentRepository;
import com.edufelip.finn.domain.models.repositories.ICommunityRepository;
import com.edufelip.finn.domain.models.repositories.IPostRepository;
import com.edufelip.finn.domain.models.repositories.IUserRepository;

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
    private final MutableLiveData<User> _user = new MutableLiveData<>();
    private final LiveData<User> user = _user;
    private final MutableLiveData<List<Post>> _posts = new MutableLiveData<>();
    private final LiveData<List<Post>> posts = _posts;
    private final MutableLiveData<Post> _updatedPost = new MutableLiveData<>();
    private final LiveData<Post> updatedPost = _updatedPost;
    private final MutableLiveData<Integer> _nextPage = new MutableLiveData<>();
    private final LiveData<Integer> nextPage = _nextPage;

    @Inject
    public HomeFragmentViewModel(UserRepository userRepository,
                                 PostRepository postRepository,
                                 CommunityRepository communityRepository,
                                 CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.communityRepository = communityRepository;
        this.commentRepository = commentRepository;
        this._nextPage.postValue(1);
    }

    public void getUser(User user) {
        userRepository.getUser(user.getId())
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<>() {
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
                    createUser(user);
                }

                @Override
                public void onComplete() {
                    _user.postValue(user);
                }
            });
    }

    public void createUser(User user) {
        userRepository.createUser(user)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<>() {
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
                    _user.postValue(user);
                }
            });
    }

    public Observable<Post> getPostsObservable(String userId, int page, boolean refresh) {
        return postRepository.getUserFeed(userId, page)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap((Function<List<Post>, ObservableSource<Post>>) receivedPosts -> {
                if (refresh) {
                    _posts.postValue(receivedPosts);
                } else {
                    List<Post> list = new ArrayList<>();
                    if (posts.getValue() != null) list.addAll(posts.getValue());
                    list.addAll(receivedPosts);
                    _posts.postValue(list);
                }
                return Observable.fromIterable(receivedPosts)
                    .subscribeOn(Schedulers.io());
            });
    }

    public void getPosts(String userId, int page, boolean refresh) {
        getPostsObservable(userId, page, refresh)
            .subscribeOn(Schedulers.io())
            .flatMap((Function<Post, ObservableSource<Post>>) this::getCommunityPostObservable)
            .flatMap((Function<Post, ObservableSource<Post>>) this::getPostCommentsObservable)
            .flatMap((Function<Post, ObservableSource<Post>>) this::getPostLikesObservable)
            .flatMap((Function<Post, ObservableSource<Post>>) this::getPostUserObservable)
            .flatMap((Function<Post, ObservableSource<Post>>) post -> getIsPostLiked(post, userId))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<>() {
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
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                    _nextPage.postValue(page + 1);

                }
            });
    }

    private Observable<Post> getCommunityPostObservable(final Post post) {
        return communityRepository.getCommunity(post.getCommunityId())
            .toObservable()
            .map(community -> {
                post.setCommunityImage(community.getImage());
                post.setCommunityTitle(community.getTitle());
                post.setCommunityId(community.getId());
                return post;
            })
            .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getPostLikesObservable(final Post post) {
        return postRepository.getPostLikes(post.getId())
            .toObservable()
            .map(integer -> {
                post.setLikesCount(integer);
                return post;
            })
            .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getPostCommentsObservable(final Post post) {
        return commentRepository.getCommentsPost(post.getId())
            .toObservable()
            .map(comments -> {
                post.setCommentsCount(comments.size());
                post.setComments(comments);
                return post;
            })
            .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getPostUserObservable(final Post post) {
        return userRepository.getUser(post.getUserId())
            .toObservable()
            .map(user -> {
                post.setUserName(user.getName());
                return post;
            })
            .subscribeOn(Schedulers.io());
    }

    private Observable<Post> getIsPostLiked(final Post post, String userId) {
        return postRepository.findLike(post.getId(), userId)
            .toObservable()
            .map(integer -> {
                post.setLiked(integer == 1);
                return post;
            });
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

    public LiveData<Integer> observeNextPage() {
        return nextPage;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
