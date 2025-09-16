package com.edufelip.finn.shared.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import finn.shared.generated.resources.*
import finn.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource

data class StringsRes(
    val home: String,
    val search: String,
    val alerts: String,
    val profile: String,
    val login_welcome: String,
    val sign_in_with_google: String,
    val sign_out: String,
    val create: String,
    val cancel: String,
    val back: String,
    val load_more: String,
    val like: String,
    val unlike: String,
    val error: String,
    val query: String,
    val title: String,
    val description: String,
    val content: String,
    val pick_image: String,
    val saved: String,
    val joined: String,
    val user: String,
    val by: String,
    val likes: String,
    val comments: String,
    val unknown: String,
    val image: String,
    val not_authenticated: String,
    val content_empty: String,
    val unknown_error: String,
    val login_placeholder: String,
    val continue_label: String,
    val comments_header: String,
    val no_comments: String,
    val reply: String,
    val write_comment: String,
    val send: String,
    val ago: String,
    val just_now: String,
    val minute_abbrev: String,
    val hour_abbrev: String,
    val day_abbrev: String,
    val week_abbrev: String,
    val month_abbrev: String,
    val year_abbrev: String,
    val settings: String,
    val language: String,
    val system_default: String,
    val english: String,
    val portuguese: String,
    val spanish: String,
    val apply: String,
    val end_of_list: String,
)

@Composable
private fun stringsFromResources(): StringsRes = StringsRes(
    home = stringResource(Res.string.home),
    search = stringResource(Res.string.search),
    alerts = stringResource(Res.string.alerts),
    profile = stringResource(Res.string.profile),
    login_welcome = stringResource(Res.string.login_welcome),
    sign_in_with_google = stringResource(Res.string.sign_in_with_google),
    sign_out = stringResource(Res.string.sign_out),
    create = stringResource(Res.string.create),
    cancel = stringResource(Res.string.cancel),
    back = stringResource(Res.string.back),
    load_more = stringResource(Res.string.load_more),
    like = stringResource(Res.string.like),
    unlike = stringResource(Res.string.unlike),
    error = stringResource(Res.string.error),
    query = stringResource(Res.string.query),
    title = stringResource(Res.string.title),
    description = stringResource(Res.string.description),
    content = stringResource(Res.string.content),
    pick_image = stringResource(Res.string.pick_image),
    saved = stringResource(Res.string.saved),
    joined = stringResource(Res.string.joined),
    user = stringResource(Res.string.user),
    by = stringResource(Res.string.by),
    likes = stringResource(Res.string.likes),
    comments = stringResource(Res.string.comments),
    unknown = stringResource(Res.string.unknown),
    image = stringResource(Res.string.image),
    not_authenticated = stringResource(Res.string.not_authenticated),
    content_empty = stringResource(Res.string.content_empty),
    unknown_error = stringResource(Res.string.unknown_error),
    login_placeholder = stringResource(Res.string.login_placeholder),
    continue_label = stringResource(Res.string.continue_label),
    comments_header = stringResource(Res.string.comments_header),
    no_comments = stringResource(Res.string.no_comments),
    reply = stringResource(Res.string.reply),
    write_comment = stringResource(Res.string.write_comment),
    send = stringResource(Res.string.send),
    ago = stringResource(Res.string.ago),
    just_now = stringResource(Res.string.just_now),
    minute_abbrev = stringResource(Res.string.minute_abbrev),
    hour_abbrev = stringResource(Res.string.hour_abbrev),
    day_abbrev = stringResource(Res.string.day_abbrev),
    week_abbrev = stringResource(Res.string.week_abbrev),
    month_abbrev = stringResource(Res.string.month_abbrev),
    year_abbrev = stringResource(Res.string.year_abbrev),
    settings = stringResource(Res.string.settings),
    language = stringResource(Res.string.language),
    system_default = stringResource(Res.string.system_default),
    english = stringResource(Res.string.english),
    portuguese = stringResource(Res.string.portuguese),
    spanish = stringResource(Res.string.spanish),
    apply = stringResource(Res.string.apply),
    end_of_list = stringResource(Res.string.end_of_list),
)

val LocalStrings = staticCompositionLocalOf {
    // Empty defaults; UI should always be wrapped in ProvideStrings
    StringsRes(
        home = "", search = "", alerts = "", profile = "",
        login_welcome = "", sign_in_with_google = "", sign_out = "",
        create = "", cancel = "", back = "", load_more = "",
        like = "", unlike = "", error = "", query = "", title = "", description = "", content = "",
        pick_image = "", saved = "", joined = "", user = "", by = "", likes = "", comments = "", unknown = "", image = "",
        not_authenticated = "", content_empty = "", unknown_error = "",
        login_placeholder = "", continue_label = "",
        comments_header = "", no_comments = "", reply = "", write_comment = "", send = "",
        ago = "", just_now = "", minute_abbrev = "", hour_abbrev = "", day_abbrev = "", week_abbrev = "", month_abbrev = "", year_abbrev = "",
        settings = "", language = "", system_default = "", english = "", portuguese = "", spanish = "", apply = "", end_of_list = "",
    )
}

// Fallback shim for Compose Multiplatform versions where ProvideLocalizedResources
// is unavailable in org.jetbrains.compose.resources. This no-op preserves
// runtime behavior (uses system locale) while keeping call sites intact.
@Composable
private fun ProvideLocalizedResources(localeTag: String, content: @Composable () -> Unit) {
    content()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ProvideStrings(localeOverride: String? = null, content: @Composable () -> Unit) {
    if (localeOverride.isNullOrBlank()) {
        CompositionLocalProvider(LocalStrings provides stringsFromResources()) { content() }
    } else {
        ProvideLocalizedResources(localeOverride) {
            CompositionLocalProvider(LocalStrings provides stringsFromResources()) { content() }
        }
    }
}

expect fun currentLanguage(): String
