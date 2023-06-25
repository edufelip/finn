package com.edufelip.finn.utils.extensions

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.edufelip.finn.utils.RemoteConfigUtils
import javax.inject.Inject

class GlideUtils @Inject constructor(
    val glide: RequestManager,
    val remoteConfigUtils: RemoteConfigUtils
) {

    fun loadFromServer(url: String?, communityIcon: ImageView) {
        if (url == null) return
        glide.load(remoteConfigUtils.remoteServerAddress + "/" + url)
            .into(communityIcon)
    }

    fun load(uri: Uri?, communityIcon: ImageView) {
        glide.load(uri)
            .into(communityIcon)
    }

    fun load(url: String?, communityIcon: ImageView) {
        glide.load(url)
            .into(communityIcon)
    }

    fun glideClear(iv: ImageView) {
        glide.clear(iv)
    }
}