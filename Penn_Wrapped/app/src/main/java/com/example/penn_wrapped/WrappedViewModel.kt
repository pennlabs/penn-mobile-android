package com.example.penn_wrapped

import android.content.Context
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
import kotlinx.coroutines.withTimeout

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

import android.util.Log
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.graphics.scale

enum class ExperienceState { LOADING, ACTIVE, FINISHED, ERROR }

data class WrappedUiState(
    val state: ExperienceState = ExperienceState.LOADING,
    val pages: List<WrappedUnit> = emptyList(),
    val preloadedCompositions: Map<Int, LottieComposition> = emptyMap(),
    val isPlaying: Boolean = true,
    val errorMessage: String? = null
)

class WrappedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WrappedUiState())
    val uiState: StateFlow<WrappedUiState> = _uiState.asStateFlow()

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

    private fun recycleBitmaps(comp: LottieComposition) {
        comp.images.forEach { (_, asset) ->
            asset.bitmap?.recycle()
            asset.bitmap = null
        }
    }

    fun preparePageBitmaps(pageIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val pages = _uiState.value.pages
            val compositions = _uiState.value.preloadedCompositions

            // Decode current and next page
            for (i in pageIndex..(pageIndex + 1).coerceAtMost(pages.lastIndex)) {
                compositions[pages[i].id]?.let { decodeBitmaps(it) }
            }

            // Recycle pages that are 2+ behind
            for (i in 0 until (pageIndex - 1).coerceAtLeast(0)) {
                compositions[pages[i].id]?.let { recycleBitmaps(it) }
            }
        }
    }

    private suspend fun loadComposition(context: Context, url: String): LottieComposition {
        return suspendCancellableCoroutine { cont ->
            val task = LottieCompositionFactory.fromUrl(context, url)
            task.addListener { composition ->
                if (cont.isActive) cont.resume(composition)
            }
            task.addFailureListener { e ->
                if (cont.isActive) cont.resumeWithException(e)
            }
        }
    }

    fun loadExperience(context: Context) {
        _uiState.update { it.copy(state = ExperienceState.LOADING, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            try {
                withTimeout(10_000L) {
                    val sortedPages = DummyData.mockPages.sortedBy { it.id }

                    if (sortedPages.isEmpty()) {
                        _uiState.update {
                            it.copy(state = ExperienceState.ERROR, errorMessage = "Not enough data for your Wrapped yet!")
                        }
                        return@withTimeout
                    }

                    val compositions = mutableMapOf<Int, LottieComposition>()

                    // Load all compositions (JSON parsing only, no bitmap decoding)
                    sortedPages.forEach { page ->
                        val comp = loadComposition(context, page.lottieUrl)
                        compositions[page.id] = comp
                    }

                    // Only decode bitmaps for first 2 pages
                    sortedPages.take(2).forEach { page ->
                        compositions[page.id]?.let { decodeBitmaps(it) }
                    }

                    _uiState.update {
                        it.copy(
                            state = ExperienceState.ACTIVE,
                            pages = sortedPages,
                            preloadedCompositions = compositions,
                            isPlaying = true
                        )
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