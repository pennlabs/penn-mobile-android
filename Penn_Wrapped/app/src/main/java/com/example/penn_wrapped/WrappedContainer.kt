package com.example.penn_wrapped

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WrappedContainer(viewModel: WrappedViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val pages = uiState.pages
    val compositions = uiState.preloadedCompositions

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val progress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        progress.snapTo(0f)
    }

    LaunchedEffect(pagerState.currentPage) {
        viewModel.preparePageBitmaps(pagerState.currentPage)
    }

    LaunchedEffect(pagerState.currentPage, uiState.isPlaying) {
        if (uiState.isPlaying && pages.isNotEmpty()) {
            val duration = pages[pagerState.currentPage].durationMillis

            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (duration * (1f - progress.value)).toInt(),
                    easing = LinearEasing
                )
            )

            if (progress.value == 1f) {
                if (pagerState.currentPage < pages.lastIndex) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    viewModel.finish()
                }
            }
        } else {
            progress.stop()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { tapOffset ->
                        // Start a timer — if held longer than 200ms, treat as hold
                        val pressStartTime = System.currentTimeMillis()
                        viewModel.pause()
                        val released = tryAwaitRelease()
                        val pressDuration = System.currentTimeMillis() - pressStartTime
                        viewModel.play()

                        // Only treat as tap if it was a quick press
                        if (released && pressDuration < 200) {
                            coroutineScope.launch {
                                if (tapOffset.x > size.width / 2) {
                                    if (pagerState.currentPage < pages.lastIndex) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    } else {
                                        viewModel.finish()
                                    }
                                } else {
                                    if (progress.value > 0.2f) {
                                        progress.snapTo(0f)
                                    } else if (pagerState.currentPage > 0) {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }
                        }
                    }
                )
            }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
            val unit = pages[pageIndex]

            WrappedUnitView(
                unit = unit,
                preloadedComposition = compositions[unit.id],
                pageOffset = pageOffset,
                progressFraction = if (pageIndex == pagerState.currentPage) progress.value else 0f
            )
        }

        WrappedProgressBar(
            pageCount = pages.size,
            currentPageIndex = pagerState.currentPage,
            currentPageProgress = progress.value,
            modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp)
        )
    }
}