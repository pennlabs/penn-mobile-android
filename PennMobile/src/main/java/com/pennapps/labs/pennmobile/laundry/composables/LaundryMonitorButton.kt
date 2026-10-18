package com.pennapps.labs.pennmobile.laundry.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pennapps.labs.pennmobile.laundry.domain.MonitorMode

/**
 * Stateless picker for which machine type to watch. The leading bell is a label, not a button.
 */
@Composable
fun LaundryMonitorButton(
    selectedMode: MonitorMode,
    onModeChange: (MonitorMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val modes = MonitorMode.entries
    val totalItems = modes.size + 1

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        SegmentedButton(
            modifier = Modifier.weight(0.5f),
            selected = false,
            onClick = {},
            enabled = false,
            shape = SegmentedButtonDefaults.itemShape(0, totalItems),
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
            )
        }

        modes.forEachIndexed { index, mode ->
            SegmentedButton(
                modifier = Modifier.weight(1f),
                selected = mode == selectedMode,
                onClick = { onModeChange(mode) },
                shape = SegmentedButtonDefaults.itemShape(index + 1, totalItems),
            ) {
                Text(mode.prefValue)
            }
        }
    }
}
