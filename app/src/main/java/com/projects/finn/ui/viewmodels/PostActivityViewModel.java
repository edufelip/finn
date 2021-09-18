package com.projects.finn.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.projects.finn.data.repositories.interfaces.ICommentRepository;
import com.projects.finn.data.repositories.interfaces.IPostRepository;
import com.projects.finn.data.repositories.interfaces.IUserRepository;
import com.projects.finn.models.Comment;
import com.projects.finn.models.User;

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
    private ICommentRepository commentRepository;
    private MutableLiveData<Comment> _createdComment = new MutableLiveData<>();
    private MutableLiveData<List<Comment>> _comments = new MutableLiveData<>();
    private MutableLiveData<Comment> _updatedComment = new MutableLiveData<>();
    private LiveData<List<Comment>> comments = _comments;
    private LiveData<Comment> updatedComment = _updatedComment;
    private LiveData<Comment> createdComment = _createdComment;

    @Inject
    public PostActivityViewModel(SavedStateHandle handle,
                                 ICommentRepository commentRepository,
                                 IUserRepository userRepository,
                                 IPostRepository postRepository) {
        this.handle = handle;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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
        return userRepository.getUser(comment.getUser_id())
                .toObservable()
                .map(new Function<User, Comment>() {
                    @Override
                    public Comment apply(User user) throws Throwable {
                        comment.setUser_name(user.getName());
                        comment.setUser_image(user.getPhoto());
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

    public LiveData<Comment> observeCreatedComment() {
        return this.createdComment;
    }

    public LiveData<List<Comment>> observeComments() {
        return this.comments;
    }

    public LiveData<Comment> observeUpdatedComment() {
        return this.updatedComment;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
