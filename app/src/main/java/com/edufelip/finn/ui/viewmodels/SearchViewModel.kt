package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import com.edufelip.finn.sharedimpl.CommunityRepositoryAndroid
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val communityRepository: CommunityRepositoryAndroid,
) : ViewModel() {
    val searchCommunities = SearchCommunitiesUseCase(communityRepository)
}
