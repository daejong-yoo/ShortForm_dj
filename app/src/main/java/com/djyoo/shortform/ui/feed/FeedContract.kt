package com.djyoo.shortform.ui.feed

import com.djyoo.shortform.domain.model.Video

data class FeedUiState(
    val videos: List<Video> = emptyList(),
    val activeIndex: Int = 0,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
)

sealed interface FeedAction {
    data class ActiveIndexChanged(val index: Int) : FeedAction

    data class PlayerHostAttached(val videoId: String, val container: android.view.ViewGroup) : FeedAction

    data class PlayerHostDetached(val videoId: String) : FeedAction

    data object PlayRequested : FeedAction

    data object PauseRequested : FeedAction

    data object TogglePlayPauseRequested : FeedAction

    data object AppForeground : FeedAction

    data object AppBackground : FeedAction
}
