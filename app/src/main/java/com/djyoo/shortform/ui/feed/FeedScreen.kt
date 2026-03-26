package com.djyoo.shortform.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.flow.distinctUntilChanged
import android.widget.FrameLayout

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun FeedScreen(
    state: FeedUiState,
    onAction: (FeedAction) -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = state.activeIndex,
        pageCount = { state.videos.size },
    )

    FeedLifecycleBridge(onAction = onAction)
    FeedActivePageTracker(
        pagerState = pagerState,
        onActiveIndexChanged = { onAction(FeedAction.ActiveIndexChanged(it)) },
    )

    LaunchedEffect(state.activeIndex, state.videos.size) {
        if (state.videos.isNotEmpty() && pagerState.currentPage != state.activeIndex) {
            pagerState.scrollToPage(state.activeIndex)
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2B2B2B)),
        key = { page -> state.videos[page].id },
    ) { page ->
        val video = state.videos[page]
        val isActive = page == state.activeIndex
        Box(modifier = Modifier.fillMaxSize()) {
            if (isActive) {
                PlayerHost(
                    videoId = video.id,
                    onAction = onAction,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun PlayerHost(
    videoId: String,
    onAction: (FeedAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    DisposableEffect(videoId) {
        onDispose { onAction(FeedAction.PlayerHostDetached(videoId = videoId)) }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            FrameLayout(context).also { container ->
                onAction(FeedAction.PlayerHostAttached(videoId = videoId, container = container))
            }
        }
    )
}

@Composable
private fun FeedLifecycleBridge(
    onAction: (FeedAction) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> onAction(FeedAction.AppForeground)
                Lifecycle.Event.ON_STOP -> onAction(FeedAction.AppBackground)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun FeedActivePageTracker(
    pagerState: androidx.compose.foundation.pager.PagerState,
    onActiveIndexChanged: (Int) -> Unit,
) {
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { onActiveIndexChanged(it) }
    }
}

