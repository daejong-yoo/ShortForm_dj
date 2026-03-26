package com.djyoo.shortform.player

import android.content.Context
import android.view.ViewGroup

class ExoPlayerController(
    private val context: Context,
) : PlayerController {
    override fun attach(container: ViewGroup) = Unit
    override fun detach() = Unit
    override fun prepare(url: String) = Unit
    override fun play() = Unit
    override fun pause() = Unit
    override fun release() = Unit
}

