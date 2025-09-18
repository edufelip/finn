package com.edufelip.finn.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.edufelip.finn.shared.domain.repository.CommunityRepository
import com.edufelip.finn.shared.domain.usecase.SearchCommunitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
) : ViewModel() {
    val searchCommunities = SearchCommunitiesUseCase(communityRepository)
}
