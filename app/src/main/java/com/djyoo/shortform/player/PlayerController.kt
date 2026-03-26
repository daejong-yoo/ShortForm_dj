package com.djyoo.shortform.player

import android.view.ViewGroup

interface PlayerController {
    fun attach(container: ViewGroup)

    fun detach()

    fun prepare(
        url: String,
        nextUrl: String? = null,
    )

    fun play()

    fun pause()

    fun stop()

    fun onAppForeground()

    fun onAppBackground()

    fun release()
}
