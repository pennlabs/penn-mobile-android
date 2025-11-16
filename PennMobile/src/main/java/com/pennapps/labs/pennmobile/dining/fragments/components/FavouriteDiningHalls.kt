package com.pennapps.labs.pennmobile.dining.fragments.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.Venue

@Composable
fun FavouriteDiningHalls(
    diningHalls: List<DiningHall>,
    toggleFavourite: (DiningHall) -> Unit,
    openDiningHallMenu: (DiningHall) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 8.dp)
            .padding(top = 10.dp, bottom = 16.dp)
            .animateContentSize()
    ) {

        Text(
            text = stringResource(R.string.favorites),
            fontFamily = GilroyFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 21.sp,
        )

        if (diningHalls.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(top = 12.dp, bottom = 12.dp),
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add your favourite dining halls to see them here 🫶🏾",
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(0.6f),
                        textAlign = TextAlign.Center,
                        fontFamily = GilroyFontFamily,
                        lineHeight = 17.sp
                    )
                }
            }
        } else {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                diningHalls.forEach { diningHall ->
                    DiningHallCard(
                        diningHall = diningHall,
                        isFavourite = true,
                        toggleFavourite = { toggleFavourite(diningHall) },
                        openDiningHallMenu = openDiningHallMenu
                    )
                }
            }
        }
    }
}


val TEST_DINING_HALL = DiningHall(
    10,
    "Lauder College House",
    true,
    hashMapOf(),
    venue = Venue(),
    image = 12
)
val TEST_LIST_OF_DINING_HALLS = listOf(TEST_DINING_HALL, TEST_DINING_HALL, TEST_DINING_HALL)

@Preview
@Composable
private fun PreviewEmptyFavouriteDiningHalls() = AppTheme {
    Column {
        FavouriteDiningHalls(
            listOf(),
            openDiningHallMenu = {},
            toggleFavourite = { hall -> }
        )
    }
}


@Preview
@Composable
private fun PreviewFavouriteDiningHalls() = AppTheme {
    Column {
        var diningHalls by remember { mutableStateOf(TEST_LIST_OF_DINING_HALLS) }
        FavouriteDiningHalls(
            diningHalls,
            openDiningHallMenu = {},
            toggleFavourite = { hall ->
                diningHalls = if (diningHalls.contains(hall))
                    diningHalls.filterNot { hall.id == it.id }
                else
                    diningHalls + hall
            })
    }
}