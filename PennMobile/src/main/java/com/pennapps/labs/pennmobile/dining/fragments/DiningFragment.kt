package com.pennapps.labs.pennmobile.dining.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.compose.presentation.components.AppSnackBar
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors.LabelGreen
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors.LabelRed
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.compose.utils.NetworkUtils
import com.pennapps.labs.pennmobile.compose.utils.SnackBarEvent
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.DiningHallSortOrder
import com.pennapps.labs.pennmobile.dining.classes.Venue
import com.pennapps.labs.pennmobile.dining.fragments.components.AnimatedPushDropdown
import com.pennapps.labs.pennmobile.dining.fragments.components.DiningHallCard
import com.pennapps.labs.pennmobile.dining.fragments.components.FavouriteDiningHalls
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import rx.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DiningFragment : Fragment() {
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    DiningHallListScreen()
                }
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DiningHallListScreen(viewModel: DiningViewModel = hiltViewModel()) {
        val pullToRefreshState = rememberPullToRefreshState()
        val isDataRefreshing by viewModel.isRefreshing.collectAsState()
        val allDiningHalls by viewModel.allDiningHalls.collectAsState()
        val favouriteDiningHalls by viewModel.favouriteDiningHalls.collectAsState(listOf())

        var isSortMenuExpanded by remember { mutableStateOf(false) }
        val currentSortOption by viewModel.sortOrder.collectAsState()

        val snackBarHostState = remember { SnackbarHostState() }
        val snackBarEvent by viewModel.snackBarEvent.collectAsState()

        val snackBarContainerColor by remember(snackBarEvent) {
            derivedStateOf {
                when (snackBarEvent) {
                    is SnackBarEvent.Success -> LabelGreen
                    is SnackBarEvent.Error -> LabelRed
                    is SnackBarEvent.None -> Color.Transparent
                }
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier =
                        Modifier
                            .padding(bottom = 42.dp)
                            .fillMaxWidth(),
                )
                { snackbarData ->
                    AppSnackBar(
                        snackBarContainerColor = snackBarContainerColor,
                        snackBarContentColor = Color.White,
                        message = snackbarData.visuals.message,
                        snackBarActionLabel = snackbarData.visuals.actionLabel,
                        performSnackBarAction = { snackbarData.performAction() },
                        dismiss = { snackbarData.dismiss() },
                    )
                }
            },
        ) { paddingValues ->

            val snackBarActionLabel = stringResource(R.string.log_in)

            LaunchedEffect(snackBarEvent) {
                /**
                 * Message only has a value if the event is a Success or Error.
                 * In None, the message is null
                 */
                val message = snackBarEvent.message ?: return@LaunchedEffect
                Log.d("DiningFragment", "Snackbar message: $message")

                val shouldLogIn =
                    snackBarEvent is SnackBarEvent.Error && snackBarEvent.message == NetworkUtils.LOG_IN_TO_FAVOURITES

                val result =
                    snackBarHostState.showSnackbar(
                        message = message,
                        actionLabel = if (shouldLogIn) snackBarActionLabel else null,
                    )

                if (result == SnackbarResult.ActionPerformed) {
                    mActivity.startLoginFragment()
                    viewModel.resetSnackBarEvent()
                } else if (result == SnackbarResult.Dismissed) {
                    viewModel.resetSnackBarEvent()
                }
            }

            LaunchedEffect(isDataRefreshing) {
                Log.d("DiningFragment", "PullToRefreshState: isDataRefreshing is $isDataRefreshing")
                if (isDataRefreshing) {
                    // When the ViewModel starts refreshing, tell the UI to animate
                    // the indicator into view.
                    pullToRefreshState.animateToThreshold()
                } else {
                    // When the ViewModel stops refreshing, tell the UI to hide
                    // the indicator.
                    pullToRefreshState.animateToHidden()
                }
                Log.d("DiningFragment", "End ofPullToRefreshState: isDataRefreshing is $isDataRefreshing")
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
            ) {
                PullToRefreshBox(
                    isRefreshing = isDataRefreshing,
                    state = pullToRefreshState,
                    onRefresh = {
                        viewModel.refreshData()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    indicator = {
                        Indicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            isRefreshing = isDataRefreshing,
                            state = pullToRefreshState,
                            containerColor = MaterialTheme.colorScheme.background,
                            color = AppColors.SelectedTabBlue,
                        )
                    },
                ) {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(horizontal = 6.dp)
                                .padding(bottom = 42.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            FavouriteDiningHalls(
                                diningHalls = favouriteDiningHalls,
                                toggleFavourite = { viewModel.toggleFavourite(it) },
                                openDiningHallMenu = { hall -> navigateToMenuFragment(hall) },
                                modifier =
                                    Modifier
                                        .padding(top = 6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.background)
                                        .animateContentSize(
                                            spring(
                                                stiffness = Spring.StiffnessLow,
                                                visibilityThreshold = IntSize.VisibilityThreshold,
                                            ),
                                        ),
                            )
                        }

                        item {
                            Text(
                                stringResource(R.string.all_dining_halls),
                                fontFamily = GilroyFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 21.sp,
                                modifier = Modifier.padding(top = 20.dp),
                            )

                            AnimatedPushDropdown(
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                                sortMenuExpanded = isSortMenuExpanded,
                                toggleExpandedMode = { isSortMenuExpanded = !isSortMenuExpanded },
                                currentSortOption = currentSortOption,
                                sortOptions = DiningHallSortOrder.entries,
                                changeSortOption = { option ->
                                    viewModel.setSortByMethod(option)
                                    isSortMenuExpanded = false
                                },
                            )
                        }

                        items(allDiningHalls) { diningHall ->
                            DiningHallCard(
                                diningHall = diningHall,
                                isFavourite = favouriteDiningHalls.contains(diningHall),
                                toggleFavourite = { viewModel.toggleFavourite(diningHall) },
                                openDiningHallMenu = { hall -> navigateToMenuFragment(hall) },
                            )
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMenuFragment(diningHall: DiningHall) {
        val fragment = MenuFragment()

        val args = Bundle()
        args.putParcelable("DiningHall", diningHall)
        fragment.arguments = args

        val fragmentManager = mActivity.supportFragmentManager
        fragmentManager
            .beginTransaction()
            .replace(R.id.content_frame, fragment, "DINING_INFO_FRAGMENT")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.dining)
        mActivity.setSelectedTab(MainActivity.DINING)
    }

    companion object {
        // Gets the dining hall menus
        fun getMenus(venues: MutableList<DiningHall>) {
            try {
                val idVenueMap = mutableMapOf<Int, DiningHall>()
                venues.forEach { idVenueMap[it.id] = it }
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val formatted = current.format(formatter)
                val studentLife = MainActivity.studentLifeInstance
                studentLife
                    .getMenus(formatted)
                    .subscribeOn(Schedulers.io())
                    .subscribe({ menus ->
                        menus?.filterNotNull()?.forEach { menu ->
                            menu.venue?.let { venue ->
                                idVenueMap[venue.venueId]?.let { diningHall ->
                                    val diningHallMenus = diningHall.menus
                                    diningHallMenus.add(menu)
                                    diningHall.sortMeals(diningHallMenus)
                                }
                            }
                        }
                    }, { throwable ->
                        Log.e("DiningFragment", "Error getting Menus", throwable)
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Takes a venue then adds an image and modifies venue name if name is too long
        fun createHall(venue: Venue): DiningHall {
            when (venue.id) {
                593 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_commons,
                )

                636 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_hill_house,
                )

                637 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_kceh,
                )

                638 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_hillel,
                )

                639 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_houston,
                )

                640 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_marks,
                )

                641 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_accenture,
                )

                642 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_joes_cafe,
                )

                1442 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_nch,
                )

                747 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_mcclelland,
                )

                1057 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_gourmet_grocer,
                )

                1058 -> return DiningHall(
                    venue.id,
                    "Tortas Frontera",
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_tortas,
                )

                1163 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_commons,
                )

                1731 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_nch,
                )

                1732 -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_mba_cafe,
                )

                1733 -> return DiningHall(
                    venue.id,
                    "Pret a Manger Locust",
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_pret_a_manger,
                )

                else -> return DiningHall(
                    venue.id,
                    venue.name,
                    venue.isResidential,
                    venue.getHours(),
                    venue,
                    R.drawable.dining_commons,
                )
            }
        }
    }
}
