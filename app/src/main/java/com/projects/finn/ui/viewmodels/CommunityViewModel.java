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
import com.projects.finn.models.Comment;
import com.projects.finn.models.Community;
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
public class CommunityViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private final ICommunityRepository communityRepository;
    private final IUserRepository userRepository;
    private final IPostRepository postRepository;
    private final ICommentRepository commentRepository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<Community> _community = new MutableLiveData<>();
    private MutableLiveData<List<Post>> _posts = new MutableLiveData<>();
    private MutableLiveData<Post> _updatedPost = new MutableLiveData<>();
    private MutableLiveData<Community> communityExtra = new MutableLiveData<>();
    private LiveData<Community> community = _community;
    private LiveData<List<Post>> posts = _posts;
    private LiveData<Post> updatedPost = _updatedPost;

    @Inject
    public CommunityViewModel(SavedStateHandle handle,
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

    public void getCommunity(int communityId) {
        communityRepository.getCommunity(communityId)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .flatMap(new Function<Community, ObservableSource<Community>>() {
                @Override
                public ObservableSource<Community> apply(Community community) throws Throwable {
                    return getCommunitySubscribersCount(community);
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Community>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    disposables.add(d);
                }

                @Override
                public void onNext(@NonNull Community community) {
                    _community.postValue(community);
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    Community comm = new Community();
                    comm.setId(-1);
                    comm.setTitle(e.getMessage());
                    _community.postValue(comm);
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {

                }
            });
    }

    private Observable<Community> getCommunitySubscribersCount(final Community community) {
        return communityRepository.getCommunitySubscribersCount(community.getId())
                .toObservable()
                .map(new Function<Integer, Community>() {
                    @Override
                    public Community apply(Integer integer) throws Throwable {
                        community.setSubscribersCount(integer);
                        return community;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public Observable<Post> getPostsObservable(int communityId, int page) {
        return postRepository.getPostsFromCommunity(communityId, page)
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

    public void getCommunityPosts(int communityId, String userId, int page) {
        getPostsObservable(communityId, page)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Throwable {
                        return getPostUserObservable(post);
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
                        return getIsPostLiked(post, userId);
                    }
                })
                .map(new Function<Post, Post>() {
                    @Override
                    public Post apply(Post post) throws Throwable {
                        if(communityExtra.getValue() != null) {
                            post.setCommunity_title(communityExtra.getValue().getTitle());
                            post.setCommunity_image(communityExtra.getValue().getImage());
                        }
                        return post;
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

    private Observable<Post> getPostCommentsObservable(Post post) {
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

    private Observable<Post> getPostLikesObservable(Post post) {
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

    public void setCommunityExtra(Community community) {
        this.communityExtra.postValue(community);
    }

    public LiveData<Community> observeCommunity() { return community; }

    public LiveData<List<Post>> observePosts() { return posts; }

    public LiveData<Post> observeUpdatedPost() { return updatedPost; }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}