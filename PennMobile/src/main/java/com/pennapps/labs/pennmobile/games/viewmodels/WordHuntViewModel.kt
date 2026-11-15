package com.pennapps.labs.pennmobile.games.viewmodels

import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.games.classes.WordHuntBoardPos
import com.pennapps.labs.pennmobile.games.classes.WordHuntGameState
import com.pennapps.labs.pennmobile.games.classes.WordType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

class WordHuntViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WordHuntGameState())
    val uiState: StateFlow<WordHuntGameState> = _uiState.asStateFlow()

    fun onGameStart() {
        // load game board here from repository
    }

    private fun getWordScore(word: String): Int {
        when (word.length) {
            3 -> return 100
            4 -> return 400
            5 -> return 800
            6 -> return 1400
            7 -> return 1800
            else -> return 2200
        }
    }

    private fun getWordType(word: String): WordType {
        if (word.length < 3) {
            return WordType.NOT_A_WORD
        }

        if (uiState.value.foundWords.contains(word)) {
            return WordType.FOUND_WORD
        }

        if (uiState.value.allWordsInBoard.contains(word)) {
            return WordType.NEW_WORD
        }

        return WordType.NOT_A_WORD
    }

    fun onWordSelected(): Boolean {
        val state = uiState.value
        val selectedWord = state.currSelectedWord
        val selWordType: WordType = getWordType(selectedWord)

        if (selectedWord.isNotEmpty()) {
            _uiState.update {
                if (selWordType == WordType.NEW_WORD) {
                    it.copy(
                        foundWords = it.foundWords + selectedWord,
                        score = it.score + getWordScore(selectedWord),
                        lastPos = WordHuntBoardPos(),
                        currSelectedWord = "",
                        currSelectedPositions = emptySet(),
                        currSelectedPositionList = emptyList(),
                    )
                } else {
                    it.copy(
                        lastPos = WordHuntBoardPos(),
                        currSelectedWord = "",
                        currSelectedPositions = emptySet(),
                        currSelectedPositionList = emptyList(),
                    )
                }
            }
        }

        return selWordType == WordType.NEW_WORD
    }

    fun onSelectCharacter(
        row: Int,
        column: Int,
    ): WordType {
        _uiState.update { state ->
            val nextCharacter = state.board.getOrNull(row)?.getOrNull(column) ?: return@update state
            val currPos = WordHuntBoardPos(row, column)
            val selectedPositions = state.currSelectedPositionList
            val onePositionBehind = selectedPositions.getOrNull(selectedPositions.lastIndex - 1)

            if (currPos == onePositionBehind) {
                val updatedPositionList = selectedPositions.dropLast(1)

                return@update state.copy(
                    currSelectedWord = state.currSelectedWord.dropLast(1),
                    currSelectedPositions = state.currSelectedPositions - selectedPositions.last(),
                    currSelectedPositionList = updatedPositionList,
                    lastPos = updatedPositionList.lastOrNull() ?: WordHuntBoardPos(),
                )
            }

            if (currPos in state.currSelectedPositions) {
                return@update state
            }

            if (
                state.lastPos.row > -1 &&
                state.lastPos.column > -1 &&
                (abs(row - state.lastPos.row) > 1 || abs(column - state.lastPos.column) > 1)
            ) {
                return@update state
            }

            state.copy(
                currSelectedWord = state.currSelectedWord + nextCharacter,
                lastPos = currPos,
                currSelectedPositions = state.currSelectedPositions + currPos,
                currSelectedPositionList = selectedPositions + currPos,
            )
        }

        return getWordType(uiState.value.currSelectedWord)
    }

    fun onGameEnd() {
        // send score, etc
    }
}
