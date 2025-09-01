package com.pennapps.labs.pennmobile.dining.composables.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun DiningBalancesCard(
    diningDollars: String,
    swipes: Int,
    guestSwipes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dining Dollars
        BalanceItem(
            label = "Dining Dollars",
            amount = diningDollars,
            indicatorColor = Color(0xFFBADFB8) // Greenish
        )

        // Swipes
        BalanceItem(
            label = "Swipes",
            amount = swipes.toString(),
            indicatorColor = Color(0xFF99BCF7) // Blue
        )

        // Guest Swipes
        BalanceItem(
            label = "Guest Swipes",
            amount = guestSwipes.toString(),
            indicatorColor = Color(0xFFFED994) // Yellow
        )
    }
}

@Composable
private fun BalanceItem(
    label: String,
    amount: String,
    indicatorColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color = indicatorColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = amount,
                fontSize = 16.sp,
                color = indicatorColor
            )
        }
    }
}
