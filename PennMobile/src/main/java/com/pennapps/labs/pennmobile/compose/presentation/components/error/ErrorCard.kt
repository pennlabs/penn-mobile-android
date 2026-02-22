package com.pennapps.labs.pennmobile.compose.presentation.components.error

import SFProDisplayMedium
import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme

@Composable
fun ErrorCard(
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ),
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = SFProDisplayMedium,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 24.dp, horizontal = 12.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewErrorCard() {
    AppTheme {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            ErrorCard(
                UserDisplayErrors.CAMPUS_EXPRESS_DOWN,
                Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(0.95f),
            )
        }
    }
}
