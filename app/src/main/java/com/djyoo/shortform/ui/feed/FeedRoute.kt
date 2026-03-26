package com.djyoo.shortform.ui.feed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun FeedRoute(
    viewModel: FeedViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()
    FeedScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

