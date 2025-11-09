package com.pennapps.labs.pennmobile.dining.fragments.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors.BrightRed
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.compose.presentation.theme.pinkRippleConfiguration
import com.pennapps.labs.pennmobile.compose.presentation.theme.sfProFontFamily
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.Venue
import org.joda.time.Instant
import org.joda.time.Interval

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiningHallCard(
    diningHall: DiningHall,
    isFavourite: Boolean,
    toggleFavourite: (Boolean) -> Unit,
    openDiningHallMenu: (DiningHall) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardHeight = 120.dp

    Card(
        modifier = modifier.height(cardHeight),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = { openDiningHallMenu(diningHall) }
    ) {
        Row(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(diningHall.image),
                contentDescription = null,
                modifier = Modifier
                    .width(150.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = diningHall.name ?: "",
                    fontFamily = sfProFontFamily,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = stringResource(diningHall.getCurrentDiningHallStatus()),
                    color = Color.White,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (diningHall.isOpen) AppColors.LabelGreen.copy(alpha = 0.9f)
                            else AppColors.LabelRed.copy(alpha = 0.9f)
                        )
                        .padding(vertical = 1.dp, horizontal = 6.dp),
                    fontSize = 13.sp,
                    fontFamily = GilroyFontFamily
                )

                Text(
                    text = diningHall.getCurrentDiningOpenHours(),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 12.sp,
                    fontFamily = GilroyFontFamily,
                    lineHeight = 14.sp
                )
            }

            Box(
                modifier = Modifier.fillMaxHeight()
            ) {
                CompositionLocalProvider(LocalRippleConfiguration provides pinkRippleConfiguration) {
                    Icon(
                        imageVector = if (isFavourite) Icons.Filled.Favorite
                        else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavourite) BrightRed else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(36.dp)
                            .clip(CircleShape)
                            .padding(1.dp)
                            .clickable { toggleFavourite(!isFavourite) }
                            .padding(horizontal = 6.dp)
                            .padding(top = 10.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDiningHallCard() {
    DiningHallCard(
        diningHall = DiningHall(
            id = 1,
            name = "1920 Commons",
            residential = true,
            hours = hashMapOf(
                "11 AM - 3 PM" to Interval(
                    Instant.now().millis,
                    Instant.now().millis + 1000
                )
            ),
            venue = Venue(),
            image = R.drawable.dining_commons
        ),
        isFavourite = true,
        toggleFavourite = { },
        openDiningHallMenu = {},
        modifier = Modifier
    )
}