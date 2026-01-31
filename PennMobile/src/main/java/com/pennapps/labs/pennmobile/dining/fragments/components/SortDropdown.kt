package com.pennapps.labs.pennmobile.dining.fragments.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.compose.presentation.theme.googleSansFontFamily
import com.pennapps.labs.pennmobile.dining.classes.DiningHallSortOrder

@Composable
fun SortDropdown(
    sortMenuExpanded: Boolean,
    toggleExpandedMode: () -> Unit,
    currentSortOption: DiningHallSortOrder,
    sortOptions: List<DiningHallSortOrder>,
    changeSortOption: (DiningHallSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedPushDropdown(
        expanded = sortMenuExpanded,
        toggleExpandedMode = toggleExpandedMode,
        title = {
            Text(
                text = "Sort by ${currentSortOption.toDisplayString()}",
                fontFamily = GilroyFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp
            )
        },
        content = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
            ) {
                sortOptions.forEach { orderOption ->
                    Surface(
                        color = Color(0xFF000000).copy(alpha = 0.05f),
                        shape = RoundedCornerShape(4.dp),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 5.dp)
                                .clickable {
                                    changeSortOption(orderOption)
                                }
                    ) {
                        Text(
                            text = orderOption.key,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = googleSansFontFamily,
                            fontWeight = FontWeight.Normal,
                            modifier =
                                Modifier
                                    .padding(
                                        vertical = 6.dp,
                                        horizontal = 8.dp,
                                    )
                                    .fillMaxWidth(),
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}


@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewSortDropdown() =
    AppTheme {
        Column (
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            var sortMenuExpanded by remember { mutableStateOf(true) }
            var sortOption by remember { mutableStateOf(DiningHallSortOrder.Residential) }

            SortDropdown(
                sortMenuExpanded,
                toggleExpandedMode = { sortMenuExpanded = !sortMenuExpanded },
                currentSortOption = sortOption,
                sortOptions = DiningHallSortOrder.entries,
                changeSortOption = { sortOption = it },
            )
        }
    }

