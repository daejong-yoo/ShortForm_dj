package com.djyoo.shortform.player

import android.view.ViewGroup

interface PlayerController {
    fun attach(container: ViewGroup)
    fun detach()
    fun prepare(url: String)
    fun play()
    fun pause()
    fun release()
}

