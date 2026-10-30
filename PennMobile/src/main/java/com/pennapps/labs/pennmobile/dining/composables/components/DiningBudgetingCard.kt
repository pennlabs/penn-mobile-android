package com.pennapps.labs.pennmobile.dining.composables.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pennapps.labs.pennmobile.ui.theme.DiningBlue
import com.pennapps.labs.pennmobile.ui.theme.DiningGreen

@Suppress("ktlint:standard:function-naming")
@Composable
fun DiningBudgetingCard(
    diningDollars: String,
    swipes: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BudgetItem(
            "Dining Dollars",
            diningDollars.toString(),
            DiningGreen,
            modifier = Modifier.weight(1f),
        )

        BudgetItem(
            "Swipes",
            swipes.toString(),
            DiningBlue,
            modifier = Modifier.weight(1f),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun BudgetItem(
    label: String,
    amount: String,
    indicatorColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(64.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.W900,
                )

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = amount,
                        style =
                            MaterialTheme.typography.bodyLarge.copy(
                                color = indicatorColor,
                            ),
                        fontWeight = FontWeight.W900,
                        modifier = Modifier.alignByBaseline(),
                    )

                    Text(
                        text = "a week",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }
        }
    }
}
