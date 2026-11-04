package com.pennapps.labs.pennmobile.games.compose

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme

@Composable
fun GamesEntryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Figma banner 59:1445: preserve the artwork's proportions on different phones.
    Image(
        painter = painterResource(R.drawable.penn_games_banner),
        contentDescription = stringResource(R.string.games),
        contentScale = ContentScale.Fit,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .aspectRatio(355f / 125f)
                .clip(RoundedCornerShape(12.dp))
                .clickable(role = Role.Button, onClick = onClick),
    )
}
