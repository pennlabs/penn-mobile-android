package com.pennapps.labs.pennmobile.games.classes

data class WordHuntBoardPos(
    val row: Int = -1,
    val column: Int = -1,
)

enum class WordType {
    NEW_WORD,
    FOUND_WORD,
    NOT_A_WORD,
}

data class WordHuntGameState(
    val board: List<List<Char>> = emptyList(),
    val currSelectedWord: String = "",
    val currSelectedPositions: Set<WordHuntBoardPos> = emptySet(),
    val currSelectedPositionList: List<WordHuntBoardPos> = emptyList(),
    val foundWords: Set<String> = emptySet(),
    val allWordsInBoard: Set<String> = emptySet(),
    val score: Int = 0,
    val timeLeft: Int = 90,
    val lastPos: WordHuntBoardPos = WordHuntBoardPos(),
)
