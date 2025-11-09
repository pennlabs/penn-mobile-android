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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.dining.classes.DiningHallSortOrder

@Composable
fun AnimatedPushDropdown(
    sortMenuExpanded: Boolean,
    toggleExpandedMode: () -> Unit,
    currentSortOption: DiningHallSortOrder,
    sortOptions: List<DiningHallSortOrder>,
    changeSortOption: (DiningHallSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (sortMenuExpanded) 180f else 0f,
        label = "Dropdown Arrow Rotation",
    )

    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { toggleExpandedMode() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Sort by ${currentSortOption.toDisplayString()}",
                    fontFamily = GilroyFontFamily,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Toggle Sort Menu",
                    modifier = Modifier.rotate(rotationAngle),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            AnimatedVisibility(
                visible = sortMenuExpanded,
                enter = expandVertically(animationSpec = tween(800)) + fadeIn(
                    animationSpec = tween(
                        800
                    )
                ),
                exit = shrinkVertically(animationSpec = tween(800)) + fadeOut(
                    animationSpec = tween(
                        800
                    )
                ),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    sortOptions.forEach { orderOption ->
                        Text(
                            text = orderOption.key,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = GilroyFontFamily,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                .clickable {
                                    changeSortOption(orderOption)
                                    // onExpandedChange() // This would also work
                                }
                                .padding(
                                    vertical = 6.dp,
                                    horizontal = 8.dp
                                )
                                .fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun PreviewAnimatedPushDropdown() = AppTheme {
    Column {
        var sortMenuExpanded by remember { mutableStateOf(false) }
        var sortOption by remember { mutableStateOf(DiningHallSortOrder.Residential) }

        AnimatedPushDropdown(
            sortMenuExpanded,
            toggleExpandedMode = { sortMenuExpanded =!sortMenuExpanded },
            currentSortOption = sortOption,
            sortOptions = DiningHallSortOrder.entries,
            changeSortOption = { sortOption = it }
        )
    }
}