package com.edufelip.finn.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.edufelip.finn.data.repositories.CommunityRepository;
import com.edufelip.finn.domain.models.Community;
import com.edufelip.finn.domain.models.repositories.ICommunityRepository;
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
public class CreateCommunityViewModel extends ViewModel {
    CompositeDisposable disposables = new CompositeDisposable();
    private final ICommunityRepository communityRepository;
    private final SavedStateHandle savedStateHandle;
    private MutableLiveData<Community> _community = new MutableLiveData<>();
    private LiveData<Community> community = _community;

    @Inject
    public CreateCommunityViewModel(SavedStateHandle handle,
                                 CommunityRepository communityRepository){
        this.communityRepository = communityRepository;
        this.savedStateHandle = handle;
    }

    public void createCommunity(RequestBody requestBody, MultipartBody.Part image) {
        communityRepository.saveCommunity(requestBody, image)
                .toObservable()
                .subscribeOn(Schedulers.io())
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


    public LiveData<Community> observeCommunity () { return community; }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}