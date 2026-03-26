package com.djyoo.shortform.ui.feed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedViewModel : ViewModel() {
    private val _state = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    fun onAction(action: FeedAction) {
        when (action) {
            is FeedAction.ActiveIndexChanged -> _state.value = _state.value.copy(activeIndex = action.index)
            FeedAction.PlayRequested -> _state.value = _state.value.copy(isPlaying = true)
            FeedAction.PauseRequested -> _state.value = _state.value.copy(isPlaying = false)
        }
    }
}

