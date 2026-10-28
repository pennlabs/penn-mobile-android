@file:Suppress("ktlint:standard:kdoc")

package com.pennapps.labs.pennmobile.homebar

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.dining.fragments.DiningHolderFragment
import com.pennapps.labs.pennmobile.gsr.fragments.GsrTabbedFragment
import com.pennapps.labs.pennmobile.gsr.fragments.PottruckFragment
import com.pennapps.labs.pennmobile.home.fragments.HomeFragment
import com.pennapps.labs.pennmobile.home.fragments.NewsFragment
import com.pennapps.labs.pennmobile.laundry.fragments.LaundryFragment
import com.pennapps.labs.pennmobile.more.fragments.AboutFragment
import com.pennapps.labs.pennmobile.more.fragments.MoreFragment
import com.pennapps.labs.pennmobile.more.fragments.SupportFragment

/** Every screen that can appear in the home (bottom) bar, in the order the bar shows them. */
enum class HomeBarTab(
    val key: String,
    @IdRes val menuId: Int,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @DrawableRes val featureIconRes: Int,
    val canBeInBar: Boolean = true,
    private val createFragment: () -> Fragment,
) {
    /** Each entry stores screen's name, icon, and id for the bar, and how to create screen */
    HOME(
        "home",
        R.id.nav_home,
        R.string.home,
        R.drawable.ic_bottom_nav_home_grey,
        R.drawable.ic_bottom_nav_home_grey,
        createFragment = ::HomeFragment,
    ),
    NEWS(
        "news",
        R.id.nav_news,
        R.string.news,
        R.drawable.ic_bottom_nav_news_grey,
        R.drawable.ic_news2,
        createFragment = ::NewsFragment,
    ),
    DINING(
        "dining",
        R.id.nav_dining,
        R.string.dining,
        R.drawable.ic_bottom_nav_dining_grey,
        R.drawable.ic_bottom_nav_dining_grey,
        createFragment = ::DiningHolderFragment,
    ),
    GSR(
        "gsr",
        R.id.nav_gsr,
        R.string.gsr,
        R.drawable.ic_bottom_nav_booking_grey,
        R.drawable.ic_bottom_nav_booking_grey,
        createFragment = ::GsrTabbedFragment,
    ),
    LAUNDRY(
        "laundry",
        R.id.nav_laundry,
        R.string.laundry,
        R.drawable.ic_bottom_nav_laundry_grey,
        R.drawable.ic_bottom_nav_laundry_grey,
        createFragment = ::LaundryFragment,
    ),
    FITNESS(
        "fitness",
        R.id.nav_fitness,
        R.string.fitness,
        R.drawable.ic_bottom_nav_health_grey,
        R.drawable.ic_fitness2,
        createFragment = ::PottruckFragment,
    ),
    CONTACTS(
        "contacts",
        R.id.nav_contacts,
        R.string.penn_contacts,
        R.drawable.ic_bottom_nav_campus_help_grey,
        R.drawable.ic_contacts2,
        createFragment = ::SupportFragment,
    ),
    ABOUT(
        "about",
        R.id.nav_about,
        R.string.about,
        R.drawable.ic_bottom_nav_info_grey,
        R.drawable.ic_about2,
        canBeInBar = false,
        createFragment = ::AboutFragment,
    ),
    MORE(
        "more",
        R.id.nav_more,
        R.string.more,
        R.drawable.ic_bottom_nav_more_grey,
        R.drawable.ic_bottom_nav_more_grey,
        createFragment = ::MoreFragment,
    ), ;

    /** Home and More are fixed. */
    val isPinned: Boolean get() = this == HOME || this == MORE

    /** Creates this screen as a tab in the bar. */
    fun newTabFragment(): Fragment =
        createFragment().apply {
            arguments = (arguments ?: Bundle()).apply { putBoolean(ARG_IS_HOME_BAR_TAB, true) }
        }

    /** Creates this screen to open on top of the bar (e.g. from More). */
    fun newScreenFragment(): Fragment = createFragment()

    companion object {
        const val ARG_IS_HOME_BAR_TAB = "is_home_bar_tab"
        const val CUSTOM_COUNT = 3
        val DEFAULT_PICKS = setOf(DINING, GSR, LAUNDRY)

        /** Home, 3 user-chosen tabs, then More (ordered). */
        fun barFor(picks: Set<HomeBarTab>): List<HomeBarTab> {
            val chosen = entries.filter { it in picks && it.canBeInBar && !it.isPinned }
            val middle =
                if (chosen.size == CUSTOM_COUNT) chosen else entries.filter { it in DEFAULT_PICKS }
            return listOf(HOME) + middle + MORE
        }
    }
}

/** Tabs can be opened 2 ways: via home bar or from More tab (keep back arrow & hide bar)
 Fragment is tab in home bar. */
val Fragment.isHomeBarTab: Boolean
    get() = arguments?.getBoolean(HomeBarTab.ARG_IS_HOME_BAR_TAB) == true
