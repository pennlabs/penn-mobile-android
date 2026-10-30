package com.pennapps.labs.pennmobile.dining.composables.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pennapps.labs.pennmobile.ui.theme.DiningBlue
import com.pennapps.labs.pennmobile.ui.theme.DiningGreen

@Suppress("ktlint:standard:function-naming")
@Composable
fun BudgetPlanCard(
    maxDollars: Double,
    maxSwipes: Int,
    onDollarsChange: (Double?) -> Unit,
    onSwipesChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BudgetPlanItem("Dining Dollars", maxDollars, onDollarsChange, Modifier.weight(1f))
        BudgetPlanItem("Swipes", maxSwipes.toDouble(), onSwipesChange, Modifier.weight(1f))
    }
}

@Composable
private fun BudgetPlanItem(
    suffix: String,
    max: Double,
    onValidValue: (Double?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf("") }

    fun parse(input: String): Double? = input.toDoubleOrNull()?.takeIf { it in 0.0..max }

    val isError = text.isNotEmpty() && parse(text) == null

    Column(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = if (isError) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null,
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onValidValue(parse(it))
                        Log.d("Budget", "typed: $it, parsed: ${parse(it)}")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    textStyle =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.W900,
                        ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = suffix,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.W900,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        if (isError) {
            Text(
                text = "Enter 0 to ${"%.2f".format(max)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp),
            )
        }
    }
}
