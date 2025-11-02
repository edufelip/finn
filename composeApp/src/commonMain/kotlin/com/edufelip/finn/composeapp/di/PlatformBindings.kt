package com.edufelip.finn.composeapp.di

import com.edufelip.finn.shared.di.AuthActions
import com.edufelip.finn.shared.di.CommentsVMFactory
import com.edufelip.finn.shared.di.LinkActions
import com.edufelip.finn.shared.di.ShareActions
import com.edufelip.finn.shared.presentation.vm.AuthVM
import com.edufelip.finn.shared.presentation.vm.CommunityDetailsVM
import com.edufelip.finn.shared.presentation.vm.CreateCommunityVM
import com.edufelip.finn.shared.presentation.vm.CreatePostVM
import com.edufelip.finn.shared.presentation.vm.HomeVM
import com.edufelip.finn.shared.presentation.vm.NotificationsVM
import com.edufelip.finn.shared.presentation.vm.ProfileVM
import com.edufelip.finn.shared.presentation.vm.SavedVM
import com.edufelip.finn.shared.presentation.vm.SearchVM
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Aggregates platform-specific bindings supplied by each host application so the shared
 * Compose layer can resolve platform dependencies via Koin.
 */
data class PlatformBindings(
    val homeVm: HomeVM,
    val searchVm: SearchVM,
    val communityVm: CommunityDetailsVM,
    val notificationsVm: NotificationsVM,
    val createCommunityVm: CreateCommunityVM,
    val profileVm: ProfileVM,
    val savedVm: SavedVM,
    val authVm: AuthVM,
    val createPostVm: CreatePostVM,
    val commentsFactory: CommentsVMFactory,
    val authActions: AuthActions,
    val shareActions: ShareActions,
    val linkActions: LinkActions,
)

fun composePlatformModule(bindings: PlatformBindings): Module = module {
    single<HomeVM> { bindings.homeVm }
    single<SearchVM> { bindings.searchVm }
    single<CommunityDetailsVM> { bindings.communityVm }
    single<NotificationsVM> { bindings.notificationsVm }
    single<CreateCommunityVM> { bindings.createCommunityVm }
    single<ProfileVM> { bindings.profileVm }
    single<SavedVM> { bindings.savedVm }
    single<AuthVM> { bindings.authVm }
    single<CreatePostVM> { bindings.createPostVm }
    single<CommentsVMFactory> { bindings.commentsFactory }
    single<AuthActions> { bindings.authActions }
    single<ShareActions> { bindings.shareActions }
    single<LinkActions> { bindings.linkActions }
}
