package com.pennapps.labs.pennmobile.dining.composables.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.cabinFontFamily

/**
 * Card for the upcoming April fools day. Allows the user to enable/disable April Fools Pranks.
 *
 * @param allowPrank True means enable prank
 */
@Composable
fun AprilFoolsCard(
    modifier: Modifier = Modifier,
    allowPrank: Boolean,
    onPrankListener: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Enable April Fools Prank",
            fontFamily = cabinFontFamily,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Switch(
            checked = allowPrank,
            onCheckedChange = onPrankListener,
            colors =
                SwitchDefaults.colors(
                    checkedThumbColor = AppColors.LabsLightBlue,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                ),
        )
    }
}

@Preview(
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewAprilFoolsCard() =
    AppTheme {
        var allowPrank by remember { mutableStateOf(true) }

        Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(vertical = 12.dp)) {
            AprilFoolsCard(allowPrank = allowPrank, onPrankListener = { allowPrank = it })
        }
    }
