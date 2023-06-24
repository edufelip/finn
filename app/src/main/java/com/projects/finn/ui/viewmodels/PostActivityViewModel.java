package com.projects.finn.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.projects.finn.domain.models.repositories.ICommentRepository;
import com.projects.finn.domain.models.repositories.IPostRepository;
import com.projects.finn.domain.models.repositories.IUserRepository;
import com.projects.finn.domain.models.Comment;
import com.projects.finn.domain.models.Post;
import com.projects.finn.domain.models.User;

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
public class PostActivityViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private SavedStateHandle handle;
    private IUserRepository userRepository;
    private IPostRepository postRepository;
    private ICommentRepository commentRepository;
    private MutableLiveData<Comment> _createdComment = new MutableLiveData<>();
    private MutableLiveData<List<Comment>> _comments = new MutableLiveData<>();
    private MutableLiveData<Comment> _updatedComment = new MutableLiveData<>();
    private MutableLiveData<Post> _post = new MutableLiveData<>();
    private LiveData<List<Comment>> comments = _comments;
    private LiveData<Comment> updatedComment = _updatedComment;
    private LiveData<Comment> createdComment = _createdComment;
    private LiveData<Post> post = _post;

    @Inject
    public PostActivityViewModel(SavedStateHandle handle,
                                 ICommentRepository commentRepository,
                                 IUserRepository userRepository,
                                 IPostRepository postRepository) {
        this.handle = handle;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public Observable<Comment> getCommentsObservable(int postId) {
        return commentRepository.getCommentsPost(postId)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Comment>, ObservableSource<Comment>>() {
                    @Override
                    public ObservableSource<Comment> apply(List<Comment> comments) throws Throwable {
                        _comments.postValue(comments);
                        return Observable.fromIterable(comments)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }

    public void getComments(int postId) {
        getCommentsObservable(postId)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Comment, ObservableSource<Comment>>() {
                    @Override
                    public ObservableSource<Comment> apply(Comment comment) throws Throwable {
                        return getCommentUser(comment);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Comment>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Comment comment) {
                        _updatedComment.setValue(comment);
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

    private Observable<Comment> getCommentUser(Comment comment) {
        return userRepository.getUser(comment.getUserId())
                .toObservable()
                .map(new Function<User, Comment>() {
                    @Override
                    public Comment apply(User user) throws Throwable {
                        comment.setUserName(user.getName());
                        comment.setUserImage(user.getPhoto());
                        return comment;
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    public void createComment(Comment comment) {
        commentRepository.saveComment(comment)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Comment>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Comment comment) {
                        _createdComment.postValue(comment);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Comment comment = new Comment();
                        comment.setId(-1);
                        _createdComment.postValue(comment);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void deletePost(String userId, Post post) {
        if(post.getUserId().equals(userId)) {
            postRepository.deletePost(post.getId())
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
                            Post post = new Post();
                            post.setId(-1);
                            _post.postValue(post);
                        }

                        @Override
                        public void onComplete() {
                            Post post = new Post();
                            post.setId(-2);
                            _post.postValue(post);
                        }
                    });
        }
    }

    public LiveData<Comment> observeCreatedComment() {
        return this.createdComment;
    }

    public LiveData<List<Comment>> observeComments() {
        return this.comments;
    }

    public LiveData<Comment> observeUpdatedComment() {
        return this.updatedComment;
    }

    public LiveData<Post> observePost() {
        return this.post;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
