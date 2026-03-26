package com.djyoo.shortform.player

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class ExoPlayerController(
    private val context: Context,
) : PlayerController {
    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = true
        }
    }

    private val playerView: PlayerView by lazy {
        PlayerView(context).apply {
            useController = false
            player = exoPlayer
        }
    }

    override fun attach(container: ViewGroup) {
        detach()
        container.addView(
            playerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun detach() {
        val parent = playerView.parent as? ViewGroup ?: return
        parent.removeView(playerView)
    }

    override fun prepare(url: String, nextUrl: String?) {
        val mediaItems = buildList {
            add(MediaItem.fromUri(url))
            nextUrl?.let { add(MediaItem.fromUri(it)) }
        }
        exoPlayer.setMediaItems(mediaItems, /* resetPosition = */ true)
        exoPlayer.prepare()
    }

    override fun play() {
        exoPlayer.playWhenReady = true
        exoPlayer.play()
    }

    override fun pause() {
        exoPlayer.pause()
    }

    override fun stop() {
        exoPlayer.stop()
    }

    override fun onAppForeground() {
        // 정책은 ViewModel에서 결정하고, 여기서는 플레이어를 재개 가능한 상태로 둔다.
    }

    override fun onAppBackground() {
        pause()
    }

    override fun release() {
        detach()
        exoPlayer.release()
        clearPlayerViewPlayer()
    }

    private fun clearPlayerViewPlayer() {
        // PlayerView는 ExoPlayer release 이후 참조를 끊어 누수 위험을 줄인다.
        playerView.player = null
        playerView.visibility = View.GONE
    }
}

