import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsList(settingsList: List<Pair<String, Boolean>>) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    title = {
                        Text(
                            "Notification Settings",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { /* handle back */ }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { saveNotificationSettings() }) {
                            Icon(
                                imageVector = Icons.Filled.Save,
                                contentDescription = "Save"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = 24.dp))
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(innerPadding)
            ) {
                Text(
                    text = "Alerts",
                    modifier = Modifier
                        .padding(start = 32.dp, top = 32.dp, end = 32.dp, bottom = 4.dp),
                    color = Color(0xFF339FE6)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(settingsList) { setting ->
                        NotificationSettingRow(
                            label = setting.first,
                            isEnabledInitial = setting.second
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun NotificationSettingRow(label: String, isEnabledInitial: Boolean) {
    var isEnabled by remember { mutableStateOf(isEnabledInitial) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Switch(
            checked = isEnabled,
            onCheckedChange = { isChecked ->
                isEnabled = isChecked
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.Gray.copy(alpha = 0.5f),
                checkedTrackColor = Color(0xFF6ED668),
                uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f),
                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

// Do later
fun saveNotificationSettings() {

}

@Preview(showBackground = true)
@Composable
fun AppWithListPreview() {
    var settingsList = listOf(
        "Penn Course Alert" to false,
        "Laundry" to true,
        "GSR Bookings" to true,
        "OHQ" to true
    )
    NotificationSettingsList(settingsList)
}
