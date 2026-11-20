package com.pennapps.labs.pennmobile.gsr.presentation.search

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.gsr.domain.PENN_ZONE
import com.pennapps.labs.pennmobile.gsr.domain.model.GsrBookingOption
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val SHEET_DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.US)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GsrBookingSheet(
    option: GsrBookingOption,
    show: Boolean,
    onHide: () -> Unit,
    buildingName: String?,
    bookerName: String?,
    isBooking: Boolean,
    error: String?,
    needsLogin: Boolean,
    onConfirm: () -> Unit,
    onLogIn: () -> Unit,
    onDismiss: () -> Unit,
) {
    val currentIsBooking by rememberUpdatedState(isBooking)
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { it != SheetValue.Hidden || !currentIsBooking },
        )
    val scope = rememberCoroutineScope()

    val currentOnHide by rememberUpdatedState(onHide)
    LaunchedEffect(show) {
        if (!show) {
            sheetState.hide()
            currentOnHide()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        GsrBookingSheetContent(
            option = option,
            buildingName = buildingName,
            bookerName = bookerName,
            isBooking = isBooking,
            error = error,
            needsLogin = needsLogin,
            onConfirm = onConfirm,
            onLogIn = onLogIn,
            onCancel = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) onDismiss()
                }
            },
        )
    }
}

@Composable
private fun GsrBookingSheetContent(
    option: GsrBookingOption,
    buildingName: String?,
    bookerName: String?,
    isBooking: Boolean,
    error: String?,
    needsLogin: Boolean,
    onConfirm: () -> Unit,
    onLogIn: () -> Unit,
    onCancel: () -> Unit,
) {
    val start = option.start.atZoneSameInstant(PENN_ZONE)
    val end = option.end.atZoneSameInstant(PENN_ZONE)
    val ink = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column {
            if (!buildingName.isNullOrBlank()) {
                Text(
                    text = buildingName.uppercase(Locale.US),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
            Text(
                text = stringResource(R.string.gsr_book_room_title, option.roomName),
                fontFamily = GilroyFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = ink,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            DetailRow(Icons.Outlined.CalendarToday) { DetailText(start.format(SHEET_DATE_FORMAT)) }
            DetailRow(Icons.Outlined.Schedule) {
                DetailText(
                    stringResource(
                        R.string.gsr_time_range,
                        start.format(TIME_FORMAT),
                        end.format(TIME_FORMAT),
                    ),
                )
                DurationChip(Duration.between(start, end).toMinutes().toInt())
            }
            if (!bookerName.isNullOrBlank()) {
                DetailRow(Icons.Outlined.Person) { DetailText(bookerName) }
            }
        }

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Column {
            Button(
                // Kept enabled while booking so it doesn't flash gray; the ViewModel ignores repeat taps.
                onClick = if (needsLogin) onLogIn else onConfirm,
                shape = RoundedCornerShape(26.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp),
            ) {
                if (isBooking) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp),
                    )
                } else {
                    Text(
                        text =
                            stringResource(
                                when {
                                    needsLogin -> R.string.log_in
                                    error != null -> R.string.gsr_try_again
                                    else -> R.string.gsr_book_room
                                },
                            ),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            TextButton(
                onClick = onCancel,
                enabled = !isBooking,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(48.dp),
            ) {
                Text(
                    text = stringResource(android.R.string.cancel),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isBooking) ink.copy(alpha = 0.38f) else ink,
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
        content()
    }
}

@Composable
private fun DetailText(text: String) {
    Text(text = text, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun DurationChip(minutes: Int) {
    Text(
        text = formatDuration(minutes),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = colorResource(R.color.gsr_green),
        modifier =
            Modifier
                .background(
                    colorResource(R.color.gsr_inside_green).compositeOver(MaterialTheme.colorScheme.surface),
                    RoundedCornerShape(50),
                ).padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

private fun previewOption(): GsrBookingOption {
    val start = ZonedDateTime.of(2026, 10, 23, 19, 0, 0, 0, PENN_ZONE).toOffsetDateTime()
    return GsrBookingOption("1090", 1909, 15, "L15", start, start.plusMinutes(90))
}

@Composable
private fun SheetPreview(
    isBooking: Boolean = false,
    error: String? = null,
) {
    AppTheme {
        Surface {
            GsrBookingSheetContent(
                option = previewOption(),
                buildingName = "Levin Building",
                bookerName = "Ben Franklin",
                isBooking = isBooking,
                error = error,
                needsLogin = false,
                onConfirm = {},
                onLogIn = {},
                onCancel = {},
            )
        }
    }
}

@Preview(name = "Booking sheet", showBackground = true)
@Preview(name = "Booking sheet (dark)", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GsrBookingSheetPreview() = SheetPreview()

@Preview(name = "Booking sheet, in flight", showBackground = true)
@Composable
private fun GsrBookingSheetBookingPreview() = SheetPreview(isBooking = true)

@Preview(name = "Booking sheet, failed", showBackground = true)
@Composable
private fun GsrBookingSheetErrorPreview() = SheetPreview(error = "Error: Not enough credits")
