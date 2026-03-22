package com.pennapps.labs.pennmobile.compose.presentation.components.snackbar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.cabinFontFamily
import com.pennapps.labs.pennmobile.compose.presentation.theme.sfProFontFamily
import kotlinx.coroutines.delay

/**
 *
 * The default implementation of a snackbar.
 * This is to be used as snackbar parameter in all @see SnackbarHost, to ensure consistency in snackBar appearance throughout the app.
 *
 * @param snackBarActionLabel The label for the action button in the snackBar. If null, we have no action button.
 * @param performSnackBarAction Called when the user clicks the action button in the snackBar. If null, we have no action button.
 * @param dismiss Called when the snackBar is dismissed (in this snackBar, we dismiss it automatically after X amount of time - similar to how a normal snackBar would)
 * @param actionButtonColors Colors for the action button
 */
@Composable
fun AppSnackBar(
    snackBarContainerColor: Color,
    snackBarContentColor: Color,
    message: String,
    dismiss: () -> Unit,
    modifier: Modifier = Modifier,
    snackBarActionLabel: String? = null,
    performSnackBarAction: (() -> Unit)? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
    actionButtonColors: ButtonColors =
        ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = AppColors.TabTextBlue,
        ),
) {
    val currentDismiss by rememberUpdatedState(dismiss)

    val durationMillis =
        when (duration) {
            SnackbarDuration.Short -> 3000L
            SnackbarDuration.Long -> 5000L
            SnackbarDuration.Indefinite -> Long.MAX_VALUE
        }

    LaunchedEffect(message) {
        Log.d("AppSnackBar", "message: $message")

        if (durationMillis != Long.MAX_VALUE) {
            delay(durationMillis)
            currentDismiss()
        }
    }

    Card(
        colors =
            CardDefaults.cardColors(
                containerColor = snackBarContainerColor,
                contentColor = snackBarContentColor,
            ),
        shape = RoundedCornerShape(6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 45.dp)
                    .padding(vertical = 6.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                color = snackBarContentColor,
                fontFamily = cabinFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                lineHeight = 17.sp,
            )

            if (snackBarActionLabel != null && performSnackBarAction != null) {
                Button(
                    onClick = {
                        performSnackBarAction()
                        dismiss()
                    },
                    colors = actionButtonColors,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = snackBarActionLabel,
                        fontSize = 14.sp,
                        fontFamily = sfProFontFamily,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewAppSnackBar() =
    AppTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            AppSnackBar(
                snackBarContainerColor = AppColors.LabelRed,
                snackBarContentColor = Color.White,
                message = "Log in to add dining halls to favourites",
                snackBarActionLabel = null,
                dismiss = {},
                performSnackBarAction = { },
                duration = SnackbarDuration.Short,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
