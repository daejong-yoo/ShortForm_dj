package com.djyoo.shortform.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.djyoo.shortform.data.VideoRepository
import com.djyoo.shortform.player.PlayerController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val repository: VideoRepository,
    private val playerController: PlayerController,
) : ViewModel() {
    private val _state = MutableStateFlow(FeedUiState())
    val state: StateFlow<FeedUiState> = _state.asStateFlow()

    private var loadJob: Job? = null
    private var attachedVideoId: String? = null

    init {
        loadVideos()
    }

    fun onAction(action: FeedAction) {
        when (action) {
            is FeedAction.ActiveIndexChanged -> {
                val nextState = _state.value.copy(activeIndex = action.index)
                _state.value = nextState
                requestPlaybackFor(nextState)
            }
            is FeedAction.PlayerHostAttached -> {
                attachedVideoId = action.videoId
                playerController.attach(action.container)
                requestPlaybackFor(_state.value)
            }
            is FeedAction.PlayerHostDetached -> {
                if (attachedVideoId == action.videoId) {
                    attachedVideoId = null
                    playerController.detach()
                }
            }
            FeedAction.PlayRequested -> {
                _state.value = _state.value.copy(isPlaying = true)
                playerController.play()
            }
            FeedAction.PauseRequested -> {
                _state.value = _state.value.copy(isPlaying = false)
                playerController.pause()
            }
            FeedAction.AppForeground -> playerController.onAppForeground()
            FeedAction.AppBackground -> playerController.onAppBackground()
        }
    }

    private fun loadVideos() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val videos = repository.getVideos()
            _state.value = _state.value.copy(
                videos = videos,
                activeIndex = 0,
                isLoading = false,
            )
            requestPlaybackFor(_state.value)
        }
    }

    private fun requestPlaybackFor(state: FeedUiState) {
        if (attachedVideoId == null) return
        val current = state.videos.getOrNull(state.activeIndex) ?: return

        val next = state.videos.getOrNull(state.activeIndex + 1)
        val nextUrl = next?.takeIf { it.preload }?.videoUrl

        playerController.prepare(url = current.videoUrl, nextUrl = nextUrl)
        if (state.isPlaying) {
            playerController.play()
        } else {
            // 기본 정책: active로 바뀌면 재생 상태를 true로 전환 (숏폼 UX)
            _state.value = state.copy(isPlaying = true)
            playerController.play()
        }
    }

    override fun onCleared() {
        playerController.release()
        super.onCleared()
    }

    class Factory(
        private val repository: VideoRepository,
        private val playerController: PlayerController,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedViewModel(repository, playerController) as T
        }
    }
}

