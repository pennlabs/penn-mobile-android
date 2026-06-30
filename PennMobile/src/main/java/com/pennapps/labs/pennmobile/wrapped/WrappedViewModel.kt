package com.pennapps.labs.pennmobile.wrapped

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

enum class ExperienceState { LOADING, ACTIVE, FINISHED, ERROR }

data class WrappedUiState(
    val state: ExperienceState = ExperienceState.LOADING,
    val pages: List<WrappedUnit> = emptyList(),
    val preloadedCompositions: Map<Int, LottieComposition> = emptyMap(),
    val isPlaying: Boolean = true,
    val errorMessage: String? = null,
)

class WrappedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WrappedUiState())
    val uiState: StateFlow<WrappedUiState> = _uiState.asStateFlow()

    private val bitmapMutex = Mutex()

    private fun decodeBitmaps(comp: LottieComposition) {
        comp.images.forEach { (_, asset) ->
            if (asset.bitmap == null && asset.fileName.startsWith("data:image")) {
                val base64Data = asset.fileName.substringAfter("base64,")
                val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                if (bitmap != null) {
                    asset.bitmap = bitmap
                }
            }
        }
    }

    private fun clearBitmapReferences(comp: LottieComposition) {
        comp.images.forEach { (_, asset) ->
            // Don't call bitmap.recycle() — the renderer may still be drawing it
            // on the main thread. Nulling the reference lets GC collect it safely.
            asset.bitmap = null
        }
    }

    fun preparePageBitmaps(pageIndex: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            bitmapMutex.withLock {
                val pages = _uiState.value.pages
                val compositions = _uiState.value.preloadedCompositions

                // Decode bitmaps for current page and neighbors
                val keepRange = (pageIndex - 1).coerceAtLeast(0)..(pageIndex + 2).coerceAtMost(pages.lastIndex)
                for (i in keepRange) {
                    compositions[pages[i].id]?.let { decodeBitmaps(it) }
                }

                // Clear distant pages
                for (i in pages.indices) {
                    if (i !in keepRange) {
                        compositions[pages[i].id]?.let { clearBitmapReferences(it) }
                    }
                }
            }
        }
    }

    private suspend fun loadComposition(
        context: Context,
        url: String,
    ): LottieComposition =
        suspendCancellableCoroutine { cont ->
            val task = LottieCompositionFactory.fromUrl(context, url)
            task.addListener { composition ->
                if (cont.isActive) cont.resume(composition)
            }
            task.addFailureListener { e ->
                if (cont.isActive) cont.resumeWithException(e)
            }
        }

    fun loadExperience(context: Context) {
        _uiState.update { it.copy(state = ExperienceState.LOADING, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            try {
                withTimeout(50_000L) {
                    val sortedPages = DummyData.mockPages.sortedBy { it.id }

                    if (sortedPages.isEmpty()) {
                        _uiState.update {
                            it.copy(state = ExperienceState.ERROR, errorMessage = "Not enough data for your Wrapped yet!")
                        }
                        return@withTimeout
                    }

                    // Load only the first 2 pages before showing the experience
                    val initialCount = 2.coerceAtMost(sortedPages.size)
                    val compositions = mutableMapOf<Int, LottieComposition>()

                    for (i in 0 until initialCount) {
                        val page = sortedPages[i]
                        compositions[page.id] = loadComposition(context, page.lottieUrl)
                    }

                    sortedPages.take(initialCount).forEach { page ->
                        compositions[page.id]?.let { decodeBitmaps(it) }
                    }

                    // Show the experience immediately with what we have
                    _uiState.update {
                        it.copy(
                            state = ExperienceState.ACTIVE,
                            pages = sortedPages,
                            preloadedCompositions = compositions,
                            isPlaying = true,
                        )
                    }

                    // Continue loading remaining pages in background
                    for (i in initialCount until sortedPages.size) {
                        val page = sortedPages[i]
                        try {
                            val comp = loadComposition(context, page.lottieUrl)
                            _uiState.update {
                                it.copy(preloadedCompositions = it.preloadedCompositions + (page.id to comp))
                            }
                        } catch (e: Exception) {
                            Log.w("WrappedViewModel", "Failed to load page ${page.id}", e)
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _uiState.update {
                    it.copy(state = ExperienceState.ERROR, errorMessage = "Loading timed out. Please check your connection.")
                }
            } catch (e: Exception) {
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed < 1000) {
                    kotlinx.coroutines.delay(1000 - elapsed)
                }
                Log.e("WrappedViewModel", "Load failed", e)
                _uiState.update {
                    it.copy(state = ExperienceState.ERROR, errorMessage = "Oops! Something went wrong loading your Wrapped.")
                }
            }
        }
    }

    fun play() = _uiState.update { it.copy(isPlaying = true) }

    fun pause() = _uiState.update { it.copy(isPlaying = false) }

    fun finish() = _uiState.update { it.copy(state = ExperienceState.FINISHED) }
}
