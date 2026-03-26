package com.djyoo.shortform.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.djyoo.shortform.data.VideoRepository
import com.djyoo.shortform.player.PlayerController
import com.djyoo.shortform.ui.feed.FeedRoute

@Composable
fun ShortformApp(
    repository: VideoRepository,
    playerController: PlayerController,
) {
    val appBackground = Color(0xFF2B2B2B)
    MaterialTheme {
        Surface(color = appBackground) {
            FeedRoute(
                repository = repository,
                playerController = playerController,
            )
        }
    }
}

