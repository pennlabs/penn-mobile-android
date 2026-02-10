package com.pennapps.labs.pennmobile.dining.fragments.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.CustomTextStyles
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.Venue

@Composable
fun FavouriteDiningHalls(
    diningHalls: List<DiningHall>,
    toggleFavourite: (DiningHall) -> Unit,
    openDiningHallMenu: (DiningHall) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Whether the favorites section is expanded or not.
    // Defaults to true
    var expanded by remember { mutableStateOf(true) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Dropdown Arrow Rotation",
    )


    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 8.dp)
                .padding(top = 10.dp, bottom = 16.dp)
    ) {
        Row(
            Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Removes the ripple effect
                ) {
                    expanded = !expanded
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.favorites),
                modifier = Modifier.weight(1f),
                style = CustomTextStyles.DiningHallsHeader()
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Toggle Sort Menu",
                modifier = Modifier.rotate(rotationAngle),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        AnimatedVisibility(expanded) {
            if (diningHalls.isEmpty()) {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(top = 12.dp, bottom = 12.dp),
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(6.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Add your favourite dining halls to see them here 🫶🏾",
                            fontSize = 15.sp,
                            modifier = Modifier.fillMaxWidth(0.6f),
                            textAlign = TextAlign.Center,
                            fontFamily = GilroyFontFamily,
                            lineHeight = 17.sp,
                        )
                    }
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    diningHalls.forEach { diningHall ->
                        DiningHallCard(
                            diningHall = diningHall,
                            isFavourite = true,
                            toggleFavourite = { toggleFavourite(diningHall) },
                            openDiningHallMenu = openDiningHallMenu,
                        )
                    }
                }
            }
        }
    }
}


val TEST_DINING_HALL =
    DiningHall(
        10,
        "Lauder College House",
        true,
        hashMapOf(),
        venue = Venue(),
        image = 12,
    )
val TEST_LIST_OF_DINING_HALLS = listOf(TEST_DINING_HALL, TEST_DINING_HALL, TEST_DINING_HALL)

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewEmptyFavouriteDiningHalls() =
    AppTheme {
        Column {
            FavouriteDiningHalls(
                listOf(),
                openDiningHallMenu = {},
                toggleFavourite = { },
            )
        }
    }

@Preview(name = "Light Mode", showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewFavouriteDiningHalls() =
    AppTheme {
        Column {
            var diningHalls by remember { mutableStateOf(TEST_LIST_OF_DINING_HALLS) }
            FavouriteDiningHalls(
                diningHalls,
                openDiningHallMenu = {},
                toggleFavourite = { hall ->
                    diningHalls =
                        if (diningHalls.contains(hall)) {
                            diningHalls.filterNot { hall.id == it.id }
                        } else {
                            diningHalls + hall
                        }
                },
            )
        }
    }
