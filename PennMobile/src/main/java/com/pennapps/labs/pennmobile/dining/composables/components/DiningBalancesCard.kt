package com.pennapps.labs.pennmobile.dining.composables.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import com.pennapps.labs.pennmobile.ui.theme.DiningBlue
import com.pennapps.labs.pennmobile.ui.theme.DiningGreen

@Composable
fun DiningBalancesCard(
    diningDollars: String,
    swipes: Int,
    guestSwipes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BalanceItem(
            "Dining Dollars",
            diningDollars,
            DiningGreen,
            modifier = Modifier.weight(1f)
        )

        BalanceItem(
            "Swipes",
            swipes.toString(),
            DiningBlue,
            modifier = Modifier.weight(1f)
        )

        BalanceItem(
            "Guest Swipes",
            guestSwipes.toString(),
            Color(0xFFFED994),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BalanceItem(
    label: String,
    amount: String,
    indicatorColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(112.dp)
            .height(64.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.TopStart)
        )

        Text(
            text = amount,
            style = MaterialTheme.typography.headlineMedium.copy(color = indicatorColor),
            modifier = Modifier.align(Alignment.Center)
        )

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = indicatorColor, shape = CircleShape)
                .align(Alignment.BottomEnd)
        )
    }
}
