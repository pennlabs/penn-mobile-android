// fragments/HomeSlidingToolbar.kt
package com.pennapps.labs.pennmobile.fragments

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.data_classes.HomeSlidingToolbarElement
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign

val homeSlidingToolbarItems = listOf(
    HomeSlidingToolbarElement(
        iconRes = R.drawable.ic_dining_square,
        title = "Dining",
    ),
    HomeSlidingToolbarElement(
        iconRes = R.drawable.ic_gsr_square,
        title = "GSR",
    ),
    HomeSlidingToolbarElement(
        iconRes = R.drawable.ic_laundry_square,
        title = "Laundry",
    ),
    HomeSlidingToolbarElement(
        iconRes = R.drawable.ic_news2,
        title = "News",
    ),
    HomeSlidingToolbarElement(
        iconRes = R.drawable.ic_contacts2,
        title = "Contacts", // TODO Confirm rename from Penn Contacts
    ),
    HomeSlidingToolbarElement(
        iconRes = R.drawable.ic_fitness2,
        title = "Fitness",
    ),
)

@Composable
fun HomeSlidingToolbar(context: Context = LocalContext.current, onFeatureClick: (Int) -> Unit) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val firstName = sharedPreferences.getString(context.getString(R.string.first_name), null) ?: "Guest"
    val textColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.color_primary_light)
    } else {
        colorResource(id = R.color.color_primary_dark)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.padding(10.dp))
        Text(
            text = "Welcome, $firstName!",
            fontSize = 24.sp,
            color = textColor,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.padding(10.dp))
        LazyRow {
            item{
                Spacer(modifier = Modifier.width(8.dp))
            }
            items(homeSlidingToolbarItems.size) { index ->
                HomeSlidingToolbarItem(index, onFeatureClick)
            }
            item{
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun HomeSlidingToolbarItem(
    index: Int,
    onFeatureClick: (Int) -> Unit
) {
    val feature = homeSlidingToolbarItems[index]
    val lastPaddingEnd = if (index == homeSlidingToolbarItems.size - 1) 12.dp else 0.dp

    // Set the textColor and backgroundColor
    val textColor = colorResource(R.color.gray)
    val backgroundColor = if (isSystemInDarkTheme()) {
        colorResource(id = R.color.color_background_dark)
    } else {
        colorResource(id = R.color.color_background)
    }

    Box(
        modifier = Modifier
            .padding(6.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .clickable { onFeatureClick(index) }
                .size(90.dp, 130.dp)
                .padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Container
            Image(
                painter = painterResource(id = feature.iconRes),
                contentDescription = feature.title,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(70.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = feature.title,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
