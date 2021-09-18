package com.projects.finn.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.projects.finn.data.repositories.interfaces.ICommunityRepository;
import com.projects.finn.models.Community;
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
public class SearchFragmentViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private final SavedStateHandle savedStateHandle;
    private final ICommunityRepository communityRepository;
    private MutableLiveData<List<Community>> _communities = new MutableLiveData<>();
    private MutableLiveData<Community> _updatedCommunity = new MutableLiveData<>();
    private LiveData<List<Community>> communities = _communities;
    private LiveData<Community> updatedCommunity = _updatedCommunity;

    @Inject
    public SearchFragmentViewModel(SavedStateHandle savedStateHandle,
                                   ICommunityRepository communityRepository) {
        this.savedStateHandle = savedStateHandle;
        this.communityRepository = communityRepository;
    }

    public void getTrendingCommunities(String query) {
        getCommunitiesObservable(query)
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
                        _updatedCommunity.setValue(community);
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

    private Observable<Community> getCommunitiesObservable(String query) {
        return communityRepository.getCommunities(query)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Community>, ObservableSource<Community>>() {
                    @Override
                    public ObservableSource<Community> apply(List<Community> communities) throws Throwable {
                        _communities.postValue(communities);
                        return Observable.fromIterable(communities)
                            .subscribeOn(Schedulers.io());
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

    public LiveData<List<Community>> observeCommunities() {
        return communities;
    }

    public LiveData<Community> observeUpdatedCommunity() { return updatedCommunity; }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}
