package com.projects.finn.utils.extensions

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.projects.finn.utils.RemoteConfigUtils
import javax.inject.Inject

class GlideUtils {
    @Inject
    private lateinit var glide: RequestManager

    @Inject
    private lateinit var remoteConfigUtils: RemoteConfigUtils

    fun loadFromServer(url: String, communityIcon: ImageView) {
        glide.load(remoteConfigUtils.remoteServerAddress + "/" + url)
            .into(communityIcon)
    }

    fun load(uri: Uri, communityIcon: ImageView) {
        glide.load(uri)
            .into(communityIcon)
    }

    fun glideClear(iv: ImageView) {
        glide.clear(iv)
    }
}