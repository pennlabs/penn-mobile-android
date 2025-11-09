package com.pennapps.labs.pennmobile.dining.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.material.snackbar.Snackbar
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppColors
import com.pennapps.labs.pennmobile.compose.presentation.theme.AppTheme
import com.pennapps.labs.pennmobile.compose.presentation.theme.GilroyFontFamily
import com.pennapps.labs.pennmobile.databinding.FragmentDiningBinding
import com.pennapps.labs.pennmobile.dining.classes.DiningHall
import com.pennapps.labs.pennmobile.dining.classes.DiningHallSortOrder
import com.pennapps.labs.pennmobile.dining.classes.Venue
import com.pennapps.labs.pennmobile.dining.fragments.components.AnimatedPushDropdown
import com.pennapps.labs.pennmobile.dining.fragments.components.DiningHallCard
import com.pennapps.labs.pennmobile.dining.fragments.components.FavouriteDiningHalls
import dagger.hilt.android.AndroidEntryPoint
import rx.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * TODO: Andrew Project
 * Create sort functionality in dining halls: Sort by Favorites
 *
 */
@AndroidEntryPoint
class DiningFragment : Fragment() {
    private lateinit var mActivity: MainActivity
    private lateinit var mStudentLife: StudentLife

    private var _binding: FragmentDiningBinding? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        mActivity.closeKeyboard()
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDiningBinding.inflate(inflater, container, false)
        val v = binding.root
//        binding.diningSwiperefresh.setColorSchemeResources(
//            R.color.color_accent,
//            R.color.color_primary,
//        )
//
//        binding.diningHallsRecyclerView.layoutManager =
//            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
//        binding.diningSwiperefresh.setOnRefreshListener { getDiningHalls() }
//
//        binding.diningHallsRecyclerView.visibility = View.GONE
        // initAppBar(v)
        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

//        binding.internetConnectionDining.visibility = View.GONE
//        binding.loadingPanel.root.visibility = View.GONE

        binding.diningCompose.setContent {
            AppTheme {
                DiningHallListScreen()
            }
        }


//        getDiningHalls()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DiningHallListScreen(viewModel: DiningViewModel = hiltViewModel()) {

        val isRefreshing by viewModel.isRefreshing.collectAsState()
        val allDiningHalls by viewModel.allDiningHalls.collectAsState()
        val favouriteDiningHalls by viewModel.favouriteDiningHalls.collectAsState(listOf())

        var isSortMenuExpanded by remember { mutableStateOf(false) }
        val currentSortOption by viewModel.sortOrder.collectAsState()

        val pullToRefreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.refreshData()
            },
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = isRefreshing,
                    state = pullToRefreshState,
                    containerColor = MaterialTheme.colorScheme.background,
                    color = AppColors.SelectedTabBlue
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 6.dp)
                    .padding(top = 12.dp, bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                item {
                    FavouriteDiningHalls(
                        diningHalls = favouriteDiningHalls,
                        toggleFavourite = { viewModel.toggleFavourite(it) },
                        openDiningHallMenu = { hall -> navigateToMenuFragment(hall) },
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .animateContentSize(
                                spring(
                                    stiffness = Spring.StiffnessLow,
                                    visibilityThreshold = IntSize.VisibilityThreshold,
                                )
                            )
                    )
                }

                item {
                    Text(
                        stringResource(R.string.all_dining_halls),
                        fontFamily = GilroyFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 21.sp,
                        modifier = Modifier.padding(top = 20.dp)
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
                        }
                    )
                }

                items(allDiningHalls) { diningHall ->
                    DiningHallCard(
                        diningHall = diningHall,
                        isFavourite = favouriteDiningHalls.contains(diningHall),
                        toggleFavourite = { viewModel.toggleFavourite(diningHall) },
                        openDiningHallMenu = { hall -> navigateToMenuFragment(hall) }
                    )
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

    private fun getDiningHalls() {
        // displays banner if not connected

        // TODO: FIX THIS
//        if (!isOnline(context)) {
//            binding.internetConnectionDining.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
//            binding.internetConnectionMessageDining.text = "Not Connected to Internet"
//            binding.internetConnectionDining.visibility = View.VISIBLE
//        } else {
//            binding.internetConnectionDining.visibility = View.GONE
//        }

        // Map each item in the list of venues to a Venue Observable, then map each Venue to a DiningHall Observable
//        try {
//            mStudentLife
//                .venues()
//                .subscribeOn(Schedulers.io())
//                .flatMap { venues -> Observable.from(venues) }
//                .flatMap { venue ->
//                    venue?.let {
//                        val hall = createHall(it)
//                        Observable.just(hall)
//                    } ?: Observable.empty()
//                }.toSortedList { diningHall1, diningHall2 ->
//                    compareDiningHallsForSort(diningHall1, diningHall2)
//                }.subscribe({ diningHalls ->
//                    mActivity.runOnUiThread {
//
//                        Log.d("DiningFragment", "Dining Halls: $diningHalls")
//                        _diningHalls.value = diningHalls
//
//                        getMenus(diningHalls)
//                        val adapter = DiningAdapter(diningHalls)
//                        binding.loadingPanel.root.visibility = View.GONE
//                        if (diningHalls.size > 0) {
//                            binding.noResults.root.visibility = View.GONE
//                        }
//
//                        // Log non-fatal error to crashyltics if null
//                        // this error should not really be happening
//                        // it is *possible* but be rare: ideally network stuff
//                        // is decoupled with UI updates
//                        try {
//                            binding.diningHallsRecyclerView.adapter = adapter
//                            binding.diningSwiperefresh.isRefreshing = false
//                        } catch (e: Exception) {
//                            FirebaseCrashlytics.getInstance().recordException(e)
//                        }
//                        view?.let { displaySnack("Just Updated") }
//                    }
//                }, {
//                    Log.e("DiningFragment", "Error getting dining halls", it)
//                    mActivity.runOnUiThread {
//                        Log.e("Dining", "Could not load Dining page", it)
//                        binding.loadingPanel.root.visibility = View.GONE
//                        binding.diningSwiperefresh.isRefreshing = false
//                    }
//                })
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    override fun onResume() {
        super.onResume()
        mActivity.removeTabs()
        mActivity.setTitle(R.string.dining)
        mActivity.setSelectedTab(MainActivity.DINING)
    }

    /**
     * Shows SnackBar message right below the app bar
     */
    @SuppressLint("RestrictedApi")
    private fun displaySnack(text: String) {
        val snackBar = Snackbar.make(binding.snackBarDining, text, Snackbar.LENGTH_SHORT)
        snackBar.setTextColor(resources.getColor(R.color.white, context?.theme))
        snackBar.setBackgroundTint(resources.getColor(R.color.penn_mobile_grey, context?.theme))
        // SnackBar message and action TextViews are placed inside a LinearLayout
        val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
        for (i in 0 until snackBarLayout.childCount) {
            val parent = snackBarLayout.getChildAt(i)
            if (parent is LinearLayout) {
                parent.rotation = 180F
                break
            }
        }
        snackBar.show()
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
