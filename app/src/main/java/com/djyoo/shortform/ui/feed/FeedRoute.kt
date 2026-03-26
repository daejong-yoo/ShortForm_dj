package com.djyoo.shortform.ui.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.djyoo.shortform.data.VideoRepository
import com.djyoo.shortform.player.PlayerController

@Composable
fun FeedRoute(
    repository: VideoRepository,
    playerController: PlayerController,
    viewModel: FeedViewModel =
        viewModel(
            factory =
                FeedViewModel.Factory(
                    repository = repository,
                    playerController = playerController,
                ),
        ),
) {
    val state by viewModel.state.collectAsState()
    FeedScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}
