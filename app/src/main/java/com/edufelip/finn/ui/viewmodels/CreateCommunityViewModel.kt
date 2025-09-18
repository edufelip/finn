package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.usecase.CreateCommunityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateCommunityViewModel @Inject constructor(
    communityRepository: CommunityRepository,
) : ViewModel() {
    val createCommunity = CreateCommunityUseCase(communityRepository)
}
