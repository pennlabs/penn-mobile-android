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
            Color(0xFFBADFB8),
            modifier = Modifier.weight(1f)
        )

        BalanceItem(
            "Swipes",
            swipes.toString(),
            Color(0xFF99BCF7),
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
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .width(112.dp)
            .height(64.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        // Label - top start
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // Amount - center
        Text(
            text = amount,
            fontSize = 16.sp,
            color = indicatorColor,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 4.dp)
        )

        // Dot - bottom end
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = indicatorColor, shape = CircleShape)
                .padding(8.dp)
                .align(Alignment.BottomEnd)
        )
    }
}
