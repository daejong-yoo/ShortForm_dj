package com.djyoo.shortform.ui.feed

import com.djyoo.shortform.domain.model.Video

data class FeedUiState(
    val videos: List<Video> = emptyList(),
    val activeIndex: Int = 0,
    val isPlaying: Boolean = false,
)

sealed interface FeedAction {
    data class ActiveIndexChanged(val index: Int) : FeedAction
    data object PlayRequested : FeedAction
    data object PauseRequested : FeedAction
}

