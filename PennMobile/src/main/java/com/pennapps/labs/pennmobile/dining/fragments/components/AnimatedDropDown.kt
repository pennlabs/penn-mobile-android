package com.pennapps.labs.pennmobile.dining.fragments.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp

/**
 * A generic, animated dropdown card component.
 *
 * This composable displays a title row that, when clicked, expands or collapses to reveal
 * the content area. The transition is animated, and a dropdown arrow icon rotates to
 * reflect the current state.
 *
 * @param expanded State representing whether the dropdown is currently expanded.
 * @param toggleExpandedMode Event lambda invoked when the title row is clicked.
 * @param title The composable content to display in the header row of the dropdown.
 * @param content The composable content to display in the expandable area.
 * @param modifier The [Modifier] to be applied to the Card container.
 */
@Composable
fun AnimatedPushDropdown(
    expanded: Boolean,
    toggleExpandedMode: () -> Unit,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Dropdown Arrow Rotation",
    )

    Card(
        modifier =
            modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        shape = RoundedCornerShape(6.dp),
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { toggleExpandedMode() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                title()

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle Sort Menu",
                    modifier = Modifier.rotate(rotationAngle),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter =
                    expandVertically(animationSpec = tween(800)) +
                        fadeIn(
                            animationSpec =
                                tween(
                                    800,
                                ),
                        ),
                exit =
                    shrinkVertically(animationSpec = tween(800)) +
                        fadeOut(
                            animationSpec =
                                tween(
                                    800,
                                ),
                        ),
            ) { content() }
        }
    }
}
