package com.djyoo.shortform.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import android.widget.FrameLayout

@Composable
fun FeedScreen(
    state: FeedUiState,
    onAction: (FeedAction) -> Unit,
) {
    val listState = rememberLazyListState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    FeedLifecycleBridge(onAction = onAction)
    FeedActiveIndexTracker(
        listState = listState,
        onActiveIndexChanged = { onAction(FeedAction.ActiveIndexChanged(it)) },
    )

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        itemsIndexed(
            items = state.videos,
            key = { _, video -> video.id },
        ) { index, video ->
            val isActive = index == state.activeIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight)
            ) {
                if (isActive) {
                    PlayerHost(
                        videoId = video.id,
                        onAction = onAction,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize())
                }
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
@OptIn(FlowPreview::class)
private fun FeedActiveIndexTracker(
    listState: LazyListState,
    onActiveIndexChanged: (Int) -> Unit,
) {
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { isScrolling -> !isScrolling }
            .debounce(120)
            .map {
                val layoutInfo = listState.layoutInfo
                val viewportStart = layoutInfo.viewportStartOffset
                val viewportEnd = layoutInfo.viewportEndOffset
                val best = layoutInfo.visibleItemsInfo.maxByOrNull { item ->
                    val itemStart = item.offset
                    val itemEnd = item.offset + item.size
                    val visibleStart = maxOf(itemStart, viewportStart)
                    val visibleEnd = minOf(itemEnd, viewportEnd)
                    maxOf(0, visibleEnd - visibleStart)
                }
                best?.index ?: 0
            }
            .distinctUntilChanged()
            .collect { onActiveIndexChanged(it) }
    }
}

