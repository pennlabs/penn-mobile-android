package com.pennapps.labs.pennmobile.more.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.repeatCount
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.compose.presentation.theme.sfProFontFamily
import com.pennapps.labs.pennmobile.more.classes.TeamMember
import com.pennapps.labs.pennmobile.more.viewmodels.AboutUiState

private const val GRID_COLUMNS = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    state: AboutUiState,
    onBack: () -> Unit,
    onLearnMoreClick: () -> Unit,
    onLicensesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier,
    ) { innerPadding ->
        AboutContent(
            state = state,
            onLearnMoreClick = onLearnMoreClick,
            onLicensesClick = onLicensesClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun AboutContent(
    state: AboutUiState,
    onLearnMoreClick: () -> Unit,
    onLicensesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalTextStyle provides TextStyle(fontFamily = sfProFontFamily)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMNS),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 32.dp),
        ) {
            fullWidthItem(key = "logo") {
                LabsLogoHeader(modifier = Modifier.padding(top = 40.dp))
            }
            fullWidthItem(key = "intro") {
                Text(
                    text = stringResource(R.string.hi_we_are_labs).trim(),
                    modifier = Modifier.padding(top = 32.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = sfProFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                )
            }
            fullWidthItem(key = "mission") {
                Text(
                    text = stringResource(R.string.labs_mission).trim(),
                    modifier = Modifier.padding(top = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                )
            }
            fullWidthItem(key = "learn_more") {
                AboutOutlinedButton(
                    text = stringResource(R.string.learn_more),
                    onClick = onLearnMoreClick,
                    modifier = Modifier.padding(top = 18.dp),
                )
            }

            teamSection(
                key = "members",
                titleRes = R.string.our_team,
                people = state.members,
                titleTopPadding = 24.dp,
            )
            teamSection(
                key = "alumni",
                titleRes = R.string.alumni,
                people = state.alumni,
                titleTopPadding = 16.dp,
            )
            fullWidthItem(key = "licenses") {
                AboutOutlinedButton(
                    text = stringResource(R.string.licenses),
                    onClick = onLicensesClick,
                    modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
                )
            }
        }
    }
}

private fun LazyGridScope.fullWidthItem(
    key: String,
    content: @Composable LazyGridItemScope.() -> Unit,
) {
    item(key = key, span = { GridItemSpan(maxLineSpan) }, contentType = "header") {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            content()
        }
    }
}

private fun LazyGridScope.teamSection(
    key: String,
    titleRes: Int,
    people: List<TeamMember>,
    titleTopPadding: Dp,
) {
    fullWidthItem(key = "$key-title") {
        SectionTitle(
            text = stringResource(titleRes).trim(),
            modifier = Modifier.padding(top = titleTopPadding, bottom = 16.dp),
        )
    }
    items(
        items = people,
        key = { "$key-${it.name}" },
        contentType = { "team_member" },
    ) { member ->
        TeamMemberCell(member)
    }
}

@Composable
private fun LabsLogoHeader(modifier: Modifier = Modifier) {
    Row(modifier = modifier.size(width = 240.dp, height = 96.dp)) {
        AnimatedLabsLogo(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
        )
        Image(
            painter = painterResource(R.drawable.logo_name),
            contentDescription = stringResource(R.string.labs_icon),
            modifier =
                Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
                    .fillMaxHeight(),
        )
    }
}

@Composable
private fun AnimatedLabsLogo(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val request =
        remember(context) {
            ImageRequest
                .Builder(context)
                .data(R.drawable.logo_gif_transparent)
                .repeatCount(0)
                .build()
        }
    AsyncImage(
        model = request,
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
private fun AboutOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(width = 135.dp, height = 32.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontFamily = GilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
    )
}

private val TeamMemberPhotoSize = 80.dp

@Composable
private fun TeamMemberCell(
    member: TeamMember,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            modifier =
                Modifier
                    .padding(top = 8.dp)
                    .size(TeamMemberPhotoSize),
            shape = CircleShape,
            shadowElevation = 2.dp,
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(member.photoUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.penn_labs_logo)
                        .error(R.drawable.penn_labs_logo)
                        .build(),
                contentDescription = member.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Text(
            text = member.name,
            modifier =
                Modifier
                    .padding(vertical = 8.dp)
                    .width(TeamMemberPhotoSize),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun AboutScreenPreview() {
    AppTheme(darkTheme = false) {
        AboutScreen(
            state =
                AboutUiState(
                    members =
                        listOf(
                            TeamMember("Trini Feng", "https://i.imgur.com/LyTB20Bm.jpg"),
                            TeamMember("Joe MacDougall", "https://i.imgur.com/OU2TGRam.jpg"),
                            TeamMember("Baron Ping-Yeh Hsieh", "https://i.imgur.com/wDs56DNm.jpg"),
                            TeamMember("David Fu", "https://i.imgur.com/1za1mAGm.png"),
                        ),
                    alumni =
                        listOf(
                            TeamMember("Rohan Chhaya", "https://i.imgur.com/foDwf0Om.jpeg"),
                            TeamMember("No Photo", null),
                        ),
                ),
            onBack = {},
            onLearnMoreClick = {},
            onLicensesClick = {},
        )
    }
}
