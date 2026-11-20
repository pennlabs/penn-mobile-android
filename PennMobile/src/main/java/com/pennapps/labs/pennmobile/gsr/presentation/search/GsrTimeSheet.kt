package com.pennapps.labs.pennmobile.gsr.presentation.search

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.gsr.domain.GsrDayPart
import com.pennapps.labs.pennmobile.gsr.domain.halfHourStartTimes
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** The section headings already say AM/PM, so the buttons drop it to fit four across. */
private val GRID_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm", Locale.US)

private const val COLUMNS = 4

/** Half-hour grid for picking the search start time; nothing changes until the user confirms. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GsrTimeSheet(
    choices: List<LocalTime>,
    initial: LocalTime,
    onSelectTime: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        GsrTimeSheetContent(
            choices = choices,
            initial = initial,
            onConfirm = { time ->
                onSelectTime(time)
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) onDismiss()
                }
            },
        )
    }
}

private sealed interface GridItem {
    data class Header(
        val part: GsrDayPart,
    ) : GridItem

    data class Time(
        val time: LocalTime,
    ) : GridItem
}

private fun gridItems(choices: List<LocalTime>): List<GridItem> =
    choices.groupBy(GsrDayPart::of).flatMap { (part, times) ->
        listOf(GridItem.Header(part)) + times.map(GridItem::Time)
    }

@Composable
private fun GsrTimeSheetContent(
    choices: List<LocalTime>,
    initial: LocalTime,
    onConfirm: (LocalTime) -> Unit,
) {
    var selected by remember(choices) {
        mutableStateOf(choices.firstOrNull { it >= initial } ?: choices.lastOrNull() ?: initial)
    }
    val items = remember(choices) { gridItems(choices) }
    // Opens on the selected time's section so a future day doesn't start scrolled to midnight.
    val gridState =
        rememberLazyGridState(
            initialFirstVisibleItemIndex =
                items.indexOf(GridItem.Header(GsrDayPart.of(selected))).coerceAtLeast(0),
        )
    val ink = MaterialTheme.colorScheme.onSurfaceVariant

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.gsr_start_time),
            fontFamily = GilroyFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = ink,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(COLUMNS),
            state = gridState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            // Capped so a whole day scrolls inside a bottom sheet instead of filling the screen.
            modifier = Modifier.heightIn(max = 420.dp),
        ) {
            items.forEach { item ->
                when (item) {
                    is GridItem.Header -> {
                        item(key = item.part, span = { GridItemSpan(maxLineSpan) }, contentType = "header") {
                            DayPartHeader(item.part)
                        }
                    }

                    is GridItem.Time -> {
                        item(key = item.time.toSecondOfDay(), contentType = "time") {
                            TimeButton(item.time, selected = item.time == selected) { selected = item.time }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onConfirm(selected) },
            shape = RoundedCornerShape(26.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            modifier =
                Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp)
                    .fillMaxWidth()
                    .height(52.dp),
        ) {
            Text(
                text = stringResource(R.string.gsr_show_rooms_from, selected.format(TIME_FORMAT)),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun DayPartHeader(part: GsrDayPart) {
    val label =
        when (part) {
            GsrDayPart.MORNING -> R.string.gsr_morning
            GsrDayPart.AFTERNOON -> R.string.gsr_afternoon
            GsrDayPart.EVENING -> R.string.gsr_evening
            GsrDayPart.NIGHT -> R.string.gsr_night
        }
    Text(
        text = stringResource(label).uppercase(Locale.US),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Composable
private fun TimeButton(
    time: LocalTime,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    val modifier = Modifier.height(44.dp)
    val padding = PaddingValues(0.dp)
    val label = time.format(GRID_TIME_FORMAT)
    if (selected) {
        Button(
            onClick = onClick,
            shape = shape,
            contentPadding = padding,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            modifier = modifier,
        ) { Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = shape,
            contentPadding = padding,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = modifier,
        ) { Text(label, fontSize = 15.sp) }
    }
}

@Preview(name = "Time sheet, today from 4 PM", showBackground = true)
@Preview(name = "Time sheet (dark)", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GsrTimeSheetPreview() {
    AppTheme {
        Surface {
            GsrTimeSheetContent(
                choices = halfHourStartTimes(LocalTime.of(16, 0)),
                initial = LocalTime.of(16, 0),
                onConfirm = {},
            )
        }
    }
}
