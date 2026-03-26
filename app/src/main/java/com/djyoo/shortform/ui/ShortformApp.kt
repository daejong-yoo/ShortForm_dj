package com.djyoo.shortform.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.djyoo.shortform.data.VideoRepository
import com.djyoo.shortform.player.PlayerController
import com.djyoo.shortform.ui.feed.FeedRoute

@Composable
fun ShortformApp(
    repository: VideoRepository,
    playerController: PlayerController,
) {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FeedRoute(
                repository = repository,
                playerController = playerController,
            )
        }
    }
}

