package com.pennapps.labs.pennmobile.games.compose

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.games.classes.WordHuntBoardPos
import com.pennapps.labs.pennmobile.games.classes.WordHuntGameState
import com.pennapps.labs.pennmobile.games.classes.WordType
import kotlin.math.abs

@Composable
fun WordHuntBoard(
    letters: List<List<Char>>,
    modifier: Modifier = Modifier,
    selectedPositions: Set<WordHuntBoardPos> = emptySet(),
    selectedPositionList: List<WordHuntBoardPos> = emptyList(),
    onSelectCharacter: ((row: Int, column: Int) -> WordType)? = null,
    onWordSelect: (() -> Unit)? = null,
) {
    var selectedWordType by remember { mutableStateOf<WordType?>(null) }

    Surface(
        modifier =
            modifier
                .widthIn(max = WordHuntBoardDefaults.MaxBoardWidth)
                .aspectRatio(1f)
                .wordHuntGestures(
                    onSelectCharacter = onSelectCharacter,
                    onWordSelect = onWordSelect,
                    onWordTypeChange = { selectedWordType = it },
                ),
        color = WordHuntBoardDefaults.BoardBackgroundColor,
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(WordHuntBoardDefaults.BoardPadding),
                verticalArrangement = Arrangement.spacedBy(WordHuntBoardDefaults.TileSpacing),
            ) {
                repeat(WordHuntBoardDefaults.BOARD_SIZE) { rowIndex ->
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(WordHuntBoardDefaults.TileSpacing),
                    ) {
                        repeat(WordHuntBoardDefaults.BOARD_SIZE) { columnIndex ->
                            WordHuntTile(
                                letter = letters.getOrNull(rowIndex)?.getOrNull(columnIndex),
                                isSelected = WordHuntBoardPos(rowIndex, columnIndex) in selectedPositions,
                                selectedWordType = selectedWordType,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            if (selectedPositionList.size > 1) {
                Canvas(Modifier.fillMaxSize()) {
                    val padding = WordHuntBoardDefaults.BoardPadding.toPx()
                    val spacing = WordHuntBoardDefaults.TileSpacing.toPx()
                    val tileSize =
                        (size.width - (padding * 2) - (spacing * (WordHuntBoardDefaults.BOARD_SIZE - 1))) /
                            WordHuntBoardDefaults.BOARD_SIZE
                    val stride = tileSize + spacing

                    selectedPositionList.zipWithNext().forEach { (start, end) ->
                        drawLine(
                            color = Color.White,
                            start =
                                Offset(
                                    x = padding + (start.column * stride) + (tileSize / 2),
                                    y = padding + (start.row * stride) + (tileSize / 2),
                                ),
                            end =
                                Offset(
                                    x = padding + (end.column * stride) + (tileSize / 2),
                                    y = padding + (end.row * stride) + (tileSize / 2),
                                ),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WordHuntTile(
    letter: Char?,
    isSelected: Boolean,
    selectedWordType: WordType?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (isSelected) {
                        when (selectedWordType) {
                            WordType.NEW_WORD -> Modifier.background(WordHuntBoardDefaults.NewWordTileBackground)

                            WordType.FOUND_WORD -> Modifier.background(WordHuntBoardDefaults.FoundWordTileBackground)

                            WordType.NOT_A_WORD,
                            null,
                            -> Modifier.background(WordHuntBoardDefaults.NotAWordTileBackground)
                        }
                    } else {
                        Modifier.background(WordHuntBoardDefaults.TileBackground)
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = letter?.uppercaseChar()?.toString().orEmpty(),
            color = Color.White,
            style =
                MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp,
                ),
            maxLines = 1,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
        )
    }
}

private fun Modifier.wordHuntGestures(
    onSelectCharacter: ((row: Int, column: Int) -> WordType)?,
    onWordSelect: (() -> Unit)?,
    onWordTypeChange: (WordType?) -> Unit,
): Modifier =
    pointerInput(onSelectCharacter, onWordSelect) {
        if (onSelectCharacter == null) return@pointerInput

        fun positionAt(offset: Offset): WordHuntBoardPos? {
            val padding = WordHuntBoardDefaults.BoardPadding.toPx()
            val spacing = WordHuntBoardDefaults.TileSpacing.toPx()
            val tileSize =
                (size.width - (padding * 2) - (spacing * (WordHuntBoardDefaults.BOARD_SIZE - 1))) /
                    WordHuntBoardDefaults.BOARD_SIZE
            val boardX = offset.x - padding
            val boardY = offset.y - padding

            if (boardX < 0 || boardY < 0 || tileSize <= 0) return null

            val stride = tileSize + spacing
            val column = (boardX / stride).toInt()
            val row = (boardY / stride).toInt()
            val xInsideTile = boardX - (column * stride) <= tileSize
            val yInsideTile = boardY - (row * stride) <= tileSize

            return if (
                column in 0 until WordHuntBoardDefaults.BOARD_SIZE &&
                row in 0 until WordHuntBoardDefaults.BOARD_SIZE &&
                xInsideTile &&
                yInsideTile
            ) {
                WordHuntBoardPos(row, column)
            } else {
                null
            }
        }

        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val firstPosition = positionAt(down.position) ?: return@awaitEachGesture
            var previousPosition = firstPosition

            onWordTypeChange(onSelectCharacter(firstPosition.row, firstPosition.column))

            while (true) {
                val event = awaitPointerEvent()
                val change = event.changes.firstOrNull { it.id == down.id } ?: break
                val currentPosition = positionAt(change.position)

                if (currentPosition != null && currentPosition != previousPosition) {
                    onWordTypeChange(onSelectCharacter(currentPosition.row, currentPosition.column))
                    previousPosition = currentPosition
                }

                if (change.changedToUpIgnoreConsumed()) {
                    onWordSelect?.invoke()
                    onWordTypeChange(null)
                    break
                }

                if (!change.pressed) break
            }
        }
    }

object WordHuntBoardDefaults {
    const val BOARD_SIZE = 4
    val BoardPadding = 16.dp
    val TileSpacing = 7.dp
    val MaxBoardWidth = 420.dp
    val BoardBackgroundColor = Color(0xFF514267)
    val NewWordTileBackground = Color(0xFF3B9B65)
    val FoundWordTileBackground = Color(0xFFF0D979)
    val NotAWordTileBackground = Color(0xFF8A8A8A)
    val TileBackground =
        Brush.verticalGradient(
            colors =
                listOf(
                    Color(0xFF83A4DF),
                    Color(0xFFB166CB),
                ),
        )
}

private val PreviewLetters =
    listOf(
        listOf('W', 'O', 'R', 'D'),
        listOf('H', 'U', 'N', 'T'),
        listOf('P', 'E', 'N', 'N'),
        listOf('G', 'A', 'M', 'E'),
    )

private val PreviewPossibleWords = setOf("WORD", "HUNT", "PENN", "GAME")

private val PreviewGameState =
    WordHuntGameState(
        board = PreviewLetters,
        allWordsInBoard = PreviewPossibleWords,
    )

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewWordHuntBoard() =
    AppTheme {
        var selectedPositionList by remember { mutableStateOf(emptyList<WordHuntBoardPos>()) }
        var foundWords by remember { mutableStateOf(emptySet<String>()) }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            WordHuntBoard(
                letters = PreviewGameState.board,
                modifier =
                    Modifier
                        .fillMaxWidth(0.84f)
                        .padding(16.dp),
                selectedPositions = selectedPositionList.toSet(),
                selectedPositionList = selectedPositionList,
                onSelectCharacter = { row, column ->
                    val currentPosition = WordHuntBoardPos(row, column)
                    val lastPosition = selectedPositionList.lastOrNull()
                    val onePositionBehind =
                        selectedPositionList.getOrNull(selectedPositionList.lastIndex - 1)

                    val updatedPositionList =
                        if (currentPosition == onePositionBehind) {
                            selectedPositionList.dropLast(1)
                        } else if (currentPosition in selectedPositionList) {
                            selectedPositionList
                        } else if (
                            lastPosition == null ||
                            (
                                abs(row - lastPosition.row) <= 1 &&
                                    abs(column - lastPosition.column) <= 1
                            )
                        ) {
                            selectedPositionList + currentPosition
                        } else {
                            selectedPositionList
                        }

                    selectedPositionList = updatedPositionList

                    val selectedWord =
                        updatedPositionList.joinToString("") { position ->
                            PreviewGameState.board[position.row][position.column].toString()
                        }

                    when {
                        selectedWord in foundWords -> WordType.FOUND_WORD
                        selectedWord in PreviewGameState.allWordsInBoard -> WordType.NEW_WORD
                        else -> WordType.NOT_A_WORD
                    }
                },
                onWordSelect = {
                    val selectedWord =
                        selectedPositionList.joinToString("") { position ->
                            PreviewGameState.board[position.row][position.column].toString()
                        }

                    if (
                        selectedWord !in foundWords &&
                        selectedWord in PreviewGameState.allWordsInBoard
                    ) {
                        foundWords = foundWords + selectedWord
                    }

                    selectedPositionList = emptyList()
                },
            )
        }
    }
