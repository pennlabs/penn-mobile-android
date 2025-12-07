package com.pennapps.labs.pennmobile.dining.composables.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennapps.labs.pennmobile.ui.theme.DiningBlue
import com.pennapps.labs.pennmobile.ui.theme.DiningGreen

enum class BalanceIconType {
    DOLLAR,
    SWIPE,
    GUEST
}

@Composable
fun DiningBalancesCard(
    diningDollars: String,
    swipes: Int,
    guestSwipes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BalanceItem(
            "Dining Dollars",
            diningDollars,
            DiningGreen,
            iconType = BalanceIconType.DOLLAR,
            modifier = Modifier.weight(1f)
        )

        BalanceItem(
            "Swipes",
            swipes.toString(),
            DiningBlue,
            iconType = BalanceIconType.SWIPE,
            modifier = Modifier.weight(1f)
        )

        BalanceItem(
            "Guest Swipes",
            guestSwipes.toString(),
            Color(0xFFFED994),
            iconType = BalanceIconType.GUEST,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BalanceItem(
    label: String,
    amount: String,
    indicatorColor: Color,
    iconType: BalanceIconType,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(64.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = amount,
                    style = MaterialTheme.typography.bodyLarge .copy(
                        color = indicatorColor,
                        // fontSize = 16.sp
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Bottom right indicator
            when (iconType) {
                BalanceIconType.DOLLAR -> {
                    Icon(
                        imageVector = Icons.Filled.AttachMoney,
                        contentDescription = "Dollar icon",
                        tint = indicatorColor,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
                BalanceIconType.SWIPE -> {
                    Icon(
                        imageVector = Icons.Filled.CreditCard,
                        contentDescription = "Swipe icon",
                        tint = indicatorColor,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
                BalanceIconType.GUEST -> {
                    Icon(
                        imageVector = Icons.Filled.Group,
                        contentDescription = "Guest icon",
                        tint = indicatorColor,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}