package com.pennapps.labs.pennmobile.games.compose

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.games.classes.Game
import com.pennapps.labs.pennmobile.games.viewmodels.GamesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    state: GamesUiState,
    onBack: () -> Unit,
    onGameClick: (Game) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Scaffold(
        modifier = modifier,
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.background),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.penn_games),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground,
                )
            }

            if (state.games.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_results),
                        color = colors.onSurfaceVariant,
                    )
                }
            }

            items(state.games, key = { it.id }) { game ->
                GameCard(game = game, onClick = { onGameClick(game) })
            }

            if (state.showComingSoon) {
                item {
                    Text(
                        text = stringResource(R.string.more_games_coming_soon),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                    )
                }
            }

            // Showing hedgehog if we need to construct / maintenance
            if (state.showConstructionMascot) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.games_construction_mascot),
                            contentDescription = stringResource(R.string.games_under_construction),
                            contentScale = ContentScale.Fit,
                            alpha = 0.61f,
                            modifier = Modifier.widthIn(max = 220.dp).fillMaxWidth().aspectRatio(219f / 263.274f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameCard(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val comingSoon = stringResource(R.string.games_coming_soon)
    // Artwork is a presentation concern; the catalog's Game model stays Android-independent
    val bannerRes =
        when (game.id) {
            "word_hunt" -> R.drawable.word_hunt_banner
            else -> null
        }
    val cardModifier =
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(13.dp))
            .clickable(enabled = game.isPlayable, role = Role.Button, onClick = onClick)
            .semantics {
                if (!game.isPlayable) stateDescription = comingSoon
            }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (bannerRes != null) {
            Image(
                painter = painterResource(bannerRes),
                contentDescription = game.title,
                contentScale = ContentScale.Fit,
                modifier = cardModifier.aspectRatio(377f / 132.746f),
            )
        } else {
            Card(modifier = cardModifier) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = game.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = game.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (!game.isPlayable) {
            Text(
                text = comingSoon,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
