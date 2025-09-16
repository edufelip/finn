package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import com.edufelip.finn.sharedimpl.CommunityRepositoryAndroid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    communityRepository: CommunityRepositoryAndroid,
) : ViewModel() {
    val createCommunity = CreateCommunityUseCase(communityRepository)
}
