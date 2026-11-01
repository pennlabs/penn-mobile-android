package com.pennapps.labs.pennmobile.gsr.presentation.search

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.gsr.domain.GsrRoomOptions
import com.pennapps.labs.pennmobile.gsr.domain.GsrSearchResult
import com.pennapps.labs.pennmobile.gsr.domain.PENN_ZONE
import com.pennapps.labs.pennmobile.gsr.domain.groupByRoom
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrLocation
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
private val DATE_FORMAT = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)

/** The theme doesn't define secondaryContainer, so M3's purple default would leak through. */
private val SelectedBlue = AppColors.LabsLightBlue.copy(alpha = 0.18f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GsrSearchScreen(
    state: GsrSearchUiState,
    onSelectLocation: (GsrLocation) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectTime: (LocalTime) -> Unit,
    onSelectDuration: (Int) -> Unit,
    onSortByTimeChange: (Boolean) -> Unit,
    onRefresh: () -> Unit,
    onSlotClick: (GsrBookingOption) -> Unit,
    onLogIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(gsrListBackground()),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            // The filters scroll away with the results so the list gets the whole screen while
            // browsing. They're hidden until locations load (e.g. logged out or offline).
            if (state.locations.isNotEmpty()) {
                item(key = "filters", contentType = "filters") {
                    Surface(color = MaterialTheme.colorScheme.surface) {
                        GsrFilters(
                            state = state,
                            onSelectLocation = onSelectLocation,
                            onSelectDate = onSelectDate,
                            onSelectTime = onSelectTime,
                            onSelectDuration = onSelectDuration,
                            onSortByTimeChange = onSortByTimeChange,
                        )
                    }
                }
            }
            when (val content = state.content) {
                GsrContent.Loading -> {
                    item(key = "loading", contentType = "loading") {
                        Box(
                            modifier = Modifier.fillParentMaxWidth().padding(48.dp),
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator() }
                    }
                }

                is GsrContent.Loaded -> {
                    gsrResults(state, content, onRefresh, onSlotClick, onLogIn)
                }
            }
        }
    }
}

/** The old GSR list's #F1F1F1; dark mode gets a dark gray instead of a bright slab. */
@Composable
private fun gsrListBackground(): Color = if (isSystemInDarkTheme()) Color(0xFF1C1C1E) else colorResource(R.color.gsr_background_gray)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GsrFilters(
    state: GsrSearchUiState,
    onSelectLocation: (GsrLocation) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectTime: (LocalTime) -> Unit,
    onSelectDuration: (Int) -> Unit,
    onSortByTimeChange: (Boolean) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showLocations by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilterField(stringResource(R.string.date), state.date.format(DATE_FORMAT)) { showDatePicker = true }
            FilterField(stringResource(R.string.time), state.startTime.format(TIME_FORMAT)) { showTimePicker = true }
            Box(modifier = Modifier.weight(1f)) {
                FilterField(stringResource(R.string.gsr_location), state.selectedLocation?.name ?: "—") {
                    if (state.locations.isNotEmpty()) showLocations = true
                }
                DropdownMenu(expanded = showLocations, onDismissRequest = { showLocations = false }) {
                    state.locations.forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location.name) },
                            onClick = {
                                showLocations = false
                                onSelectLocation(location)
                            },
                        )
                    }
                }
            }
        }

        if (state.durationOptions.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                FieldLabel(stringResource(R.string.duration))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.durationOptions) { minutes ->
                        FilterChip(
                            selected = minutes == state.durationMinutes,
                            onClick = { onSelectDuration(minutes) },
                            label = { Text(stringResource(R.string.gsr_minutes, minutes)) },
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SelectedBlue,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                                ),
                        )
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FieldLabel(stringResource(R.string.sort_by))
            SingleChoiceSegmentedButtonRow {
                val options = listOf(true to R.string.gsr_sort_time, false to R.string.gsr_sort_room)
                options.forEachIndexed { index, (byTime, label) ->
                    SegmentedButton(
                        selected = state.sortByTime == byTime,
                        onClick = { onSortByTimeChange(byTime) },
                        shape = SegmentedButtonDefaults.itemShape(index, options.size),
                        colors =
                            SegmentedButtonDefaults.colors(
                                activeContainerColor = SelectedBlue,
                                activeContentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                    ) { Text(stringResource(label)) }
                }
            }
        }
    }

    if (showDatePicker) {
        GsrDatePickerDialog(state, onDismiss = { showDatePicker = false }, onSelectDate = onSelectDate)
    }
    if (showTimePicker) {
        GsrTimePickerDialog(state.startTime, onDismiss = { showTimePicker = false }, onSelectTime = onSelectTime)
    }
}

@Composable
private fun FilterField(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick).padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        FieldLabel(label)
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = GilroyFontFamily,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GsrDatePickerDialog(
    state: GsrSearchUiState,
    onDismiss: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
) {
    // DatePicker works in UTC-midnight millis, independent of the device time zone.
    fun LocalDate.utcMillis() = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val pickerState =
        rememberDatePickerState(
            initialSelectedDateMillis = state.date.utcMillis(),
            selectableDates =
                object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        val day = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                        return !day.isBefore(state.today) && !day.isAfter(state.lastBookableDate)
                    }

                    override fun isSelectableYear(year: Int) = year in state.today.year..state.lastBookableDate.year
                },
        )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                pickerState.selectedDateMillis?.let {
                    onSelectDate(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                }
                onDismiss()
            }) { Text(stringResource(android.R.string.ok)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) } },
    ) {
        DatePicker(state = pickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GsrTimePickerDialog(
    initial: LocalTime,
    onDismiss: () -> Unit,
    onSelectTime: (LocalTime) -> Unit,
) {
    val pickerState = rememberTimePickerState(initialHour = initial.hour, initialMinute = initial.minute, is24Hour = false)
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onSelectTime(LocalTime.of(pickerState.hour, pickerState.minute))
                onDismiss()
            }) { Text(stringResource(android.R.string.ok)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) } },
        text = { TimePicker(state = pickerState) },
    )
}

private fun LazyListScope.gsrResults(
    state: GsrSearchUiState,
    content: GsrContent.Loaded,
    onRetry: () -> Unit,
    onSlotClick: (GsrBookingOption) -> Unit,
    onLogIn: () -> Unit,
) {
    val result = content.result
    if (result is GsrSearchResult.Found) {
        items(content.rooms, key = { it.roomId }, contentType = { "room" }) { room ->
            RoomRow(room, onSlotClick)
        }
    } else {
        item(key = "message", contentType = "message") {
            GsrResultMessage(state, result, onRetry, onLogIn)
        }
    }
}

@Composable
private fun GsrResultMessage(
    state: GsrSearchUiState,
    result: GsrSearchResult,
    onRetry: () -> Unit,
    onLogIn: () -> Unit,
) {
    when (result) {
        is GsrSearchResult.Found -> {
            Unit
        }

        GsrSearchResult.NoneAvailable -> {
            GsrStateMessage(
                title = stringResource(R.string.no_gsr_rooms),
                body =
                    stringResource(
                        R.string.gsr_none_available,
                        state.selectedLocation?.name.orEmpty(),
                        formatDuration(state.durationMinutes),
                        state.startTime.format(TIME_FORMAT),
                    ),
            )
        }

        is GsrSearchResult.DurationTooLong -> {
            GsrStateMessage(
                title = stringResource(R.string.gsr_too_long_title, result.location.name),
                body =
                    stringResource(
                        R.string.gsr_too_long_body,
                        formatDuration(result.requestedMinutes),
                        formatDuration(result.maxMinutes),
                    ),
            )
        }

        GsrSearchResult.NotLoggedIn -> {
            GsrStateMessage(
                title = stringResource(R.string.gsr_not_logged_in_title),
                body = stringResource(R.string.gsr_not_logged_in_body),
                actionLabel = stringResource(R.string.log_in),
                onAction = onLogIn,
            )
        }

        GsrSearchResult.Offline -> {
            GsrStateMessage(
                title = stringResource(R.string.gsr_offline_title),
                body = stringResource(R.string.gsr_offline_body),
                actionLabel = stringResource(R.string.gsr_retry),
                onAction = onRetry,
            )
        }

        is GsrSearchResult.Failed -> {
            GsrStateMessage(
                title = stringResource(R.string.gsr_failed_title),
                body = result.reason,
                actionLabel = stringResource(R.string.gsr_retry),
                onAction = onRetry,
            )
        }
    }
}

@Composable
private fun RoomRow(
    room: GsrRoomOptions,
    onSlotClick: (GsrBookingOption) -> Unit,
) {
    Column {
        Text(
            text = room.roomName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(room.options, key = { it.start.toEpochSecond() }) { option ->
                SlotCard(option, onClick = { onSlotClick(option) })
            }
        }
    }
}

@Composable
private fun SlotCard(
    option: GsrBookingOption,
    onClick: () -> Unit,
) {
    val green = colorResource(R.color.gsr_green)
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, green),
        // gsr_inside_green is translucent; flatten it onto the surface so the gray list doesn't show through.
        colors =
            CardDefaults.cardColors(
                containerColor = colorResource(R.color.gsr_inside_green).compositeOver(MaterialTheme.colorScheme.surface),
            ),
        modifier = Modifier.widthIn(min = 96.dp),
    ) {
        Text(
            text =
                stringResource(
                    R.string.gsr_slot_time,
                    option.start.atZoneSameInstant(PENN_ZONE).format(TIME_FORMAT),
                    option.end.atZoneSameInstant(PENN_ZONE).format(TIME_FORMAT),
                ),
            color = green,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
        )
    }
}

/** Title, explanation and an optional action, shown as a list item so pull-to-refresh still works. */
@Composable
private fun GsrStateMessage(
    title: String,
    body: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontFamily = GilroyFontFamily,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        if (actionLabel != null && onAction != null) {
            Button(onClick = onAction, modifier = Modifier.padding(top = 16.dp)) { Text(actionLabel) }
        }
    }
}

private fun formatDuration(minutes: Int): String {
    val hours = minutes / 60
    val rest = minutes % 60
    return when {
        hours == 0 -> "$rest min"
        rest == 0 -> if (hours == 1) "1 hour" else "$hours hours"
        else -> "$hours hr $rest min"
    }
}

private val PREVIEW_LOCATION = GsrLocation("1090", 1909, "Levin Building", GsrLocation.Kind.LIBCAL, 7)

private fun previewState(content: GsrContent) =
    GsrSearchUiState(
        today = LocalDate.of(2026, 10, 23),
        date = LocalDate.of(2026, 10, 23),
        startTime = LocalTime.of(18, 21),
        locations = listOf(PREVIEW_LOCATION),
        selectedLocation = PREVIEW_LOCATION,
        durationMinutes = 90,
        content = content,
    )

private fun previewFound(): GsrContent {
    fun option(
        room: String,
        hour: Int,
        minute: Int,
    ): GsrBookingOption {
        val start = ZonedDateTime.of(2026, 10, 23, hour, minute, 0, 0, PENN_ZONE).toOffsetDateTime()
        return GsrBookingOption("1090", 1909, room.hashCode(), room, start, start.plusMinutes(90))
    }
    val options =
        listOf("L15", "L17", "L20", "L23", "L25", "L28", "L30").flatMapIndexed { i, room ->
            listOf(option(room, 18 + i % 3, 0), option(room, 18 + i % 3, 30), option(room, 19 + i % 3, 0))
        }
    return GsrContent.Loaded(GsrSearchResult.Found(options), options.groupByRoom(sortByTime = true))
}

@Composable
private fun PreviewScreen(content: GsrContent) {
    AppTheme {
        Surface {
            GsrSearchScreen(
                state = previewState(content),
                onSelectLocation = {},
                onSelectDate = {},
                onSelectTime = {},
                onSelectDuration = {},
                onSortByTimeChange = {},
                onRefresh = {},
                onSlotClick = {},
                onLogIn = {},
            )
        }
    }
}

/** The whole Book a Room tab: filters, then rooms sorted by earliest opening. */
@Preview(name = "GSR search screen", showBackground = true, showSystemUi = true)
@Preview(name = "GSR search screen (dark)", showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GsrSearchScreenPreview() = PreviewScreen(previewFound())

@Preview(name = "Not logged in", showBackground = true)
@Preview(name = "Not logged in (dark)", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NotLoggedInPreview() = PreviewScreen(GsrContent.Loaded(GsrSearchResult.NotLoggedIn, emptyList()))

@Preview(name = "Offline", showBackground = true)
@Composable
private fun OfflinePreview() = PreviewScreen(GsrContent.Loaded(GsrSearchResult.Offline, emptyList()))

@Preview(name = "None available", showBackground = true)
@Composable
private fun NoneAvailablePreview() = PreviewScreen(GsrContent.Loaded(GsrSearchResult.NoneAvailable, emptyList()))

@Preview(name = "Failed", showBackground = true)
@Composable
private fun FailedPreview() = PreviewScreen(GsrContent.Loaded(GsrSearchResult.Failed("Error: Not enough credits"), emptyList()))

@Preview(name = "Duration too long", showBackground = true)
@Composable
private fun DurationTooLongPreview() =
    PreviewScreen(GsrContent.Loaded(GsrSearchResult.DurationTooLong(180, 120, PREVIEW_LOCATION), emptyList()))

@Preview(name = "Loading", showBackground = true)
@Composable
private fun LoadingPreview() = PreviewScreen(GsrContent.Loading)
