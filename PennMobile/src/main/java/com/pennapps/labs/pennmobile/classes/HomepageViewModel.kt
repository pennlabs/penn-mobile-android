package com.pennapps.labs.pennmobile.classes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.utils.Utils.getSha256Hash
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.CountDownLatch

/**
 * ViewModel for the homepage cells used by HomeFragment and HomeAdapter. Handles network requests
 * for generating HomeCell content and also tells HomeFragment when the blur views (for news and
 * posts) have finished loaded.
 */

class HomepageViewModel : HomepageDataModel, ViewModel() {
    companion object {
        private const val NUM_CELLS = 7
        private const val NUM_CELLS_LOGGED_IN = NUM_CELLS
        private const val NUM_CELLS_GUEST = 2

        // Types of Home Cells
        // Corresponds to the position of the cell in the RecyclerView
        private const val POLL_POS = 0
        private const val CALENDAR_POS = 1
        private const val NEWS_POS = 2
        private const val POST_POS = 3
        private const val DINING_POS = 4
        private const val LAUNDRY_POS = 5
        private const val GSR_POS = 6

        private const val TAG = "HomepageVM"
        private const val UPDATE_TAG = "CellUpdate"
        private const val BLUR_TAG = "HomeBlurViewStatus"
    }

    private val homepageCells = mutableListOf<HomeCell>()
    private val cellMutex = Mutex()

    data class ItemUpdateEvents(val positions: List<Int> = emptyList())

    private val _updateState = MutableStateFlow(ItemUpdateEvents())
    val updateState: StateFlow<ItemUpdateEvents> = _updateState.asStateFlow()

    /* Changes to true once both of the blur views are done generating.
    Should be changed to false whenever HomeFragment is initially created because the RecyclerView
    is only shown when this value changes from false to true
     */
    private val _blurViewsLoaded = MutableLiveData(false)
    val blurViewsLoaded: LiveData<Boolean>
        get() = _blurViewsLoaded

    private var postBlurViewLoaded = false
    private var newsBlurViewLoaded = false
    private val postBlurMutex = Mutex()
    private val newsBlurMutex = Mutex()

    init {
        // homepageCells should always be populated
        for (i in 1..NUM_CELLS) {
            homepageCells.add(HomeCell())
        }
    }

    fun updatedPosition(pos: Int) {
        _updateState.update { currentUpdateState ->
            val npositions = currentUpdateState.positions.filterNot { it == pos }
            currentUpdateState.copy(positions = npositions)
        }
    }

    fun updatePosition(pos: Int) {
        _updateState.update { currentUpdateState ->
            val npositions = currentUpdateState.positions + pos
            currentUpdateState.copy(positions = npositions)
        }
    }

    /**
     * Resets the blur view. Should be called from main thread because updating _blurViewsLoaded
     * must be synchronous.
     * This function should be called whenever HomeFragment is created.
     */
    fun resetBlurViews() {
        _blurViewsLoaded.value = false
        viewModelScope.launch {
            setNewsBlurView(false)
            setPostBlurView(false)
        }
    }

    /**
     * Returns a list of updated cell positions.
     */
    @Synchronized
    fun updateHomePageCells(
        studentLife: StudentLife,
        isLoggedIn: Boolean,
        bearerToken: String,
        deviceID: String,
    ): List<Int> {
        val prevList = homepageCells.toList()
        populateHomePageCells(studentLife, isLoggedIn, bearerToken, deviceID)

        val updatedIndices = mutableListOf<Int>()

        for (i in 0 until NUM_CELLS) {
            if (prevList[i] != homepageCells[i]) {
                updatedIndices.add(i)
                Log.i(UPDATE_TAG, "updated index $i")
            } else {
                Log.i(UPDATE_TAG, "saved an update at index $i")
            }
        }

        return updatedIndices
    }

    /**
     * Makes the network requests that populates the Homepage Cells.
     * This function requires a correct (non-expired) bearerToken!!
     */
    @Synchronized
    fun populateHomePageCells(
        studentLife: StudentLife,
        isLoggedIn: Boolean,
        bearerToken: String,
        deviceID: String,
    ) {
        if (isLoggedIn) {
            val latch = CountDownLatch(NUM_CELLS_LOGGED_IN)
            getPolls(studentLife, bearerToken, deviceID, latch)
            getNews(studentLife, latch)
            getCalendar(studentLife, latch)
            getLaundry(studentLife, bearerToken, latch)
            getPosts(studentLife, bearerToken, latch)
            getDiningPrefs(studentLife, bearerToken, latch)
            getGSRReservations(studentLife, bearerToken, latch)
            // waits until all of the network calls are processed
            latch.await()
        } else {
            val latch = CountDownLatch(NUM_CELLS_GUEST)
            clearLoggedIn()
            getCalendar(studentLife, latch)
            getNews(studentLife, latch)
            latch.await()
        }
    }

    /**
     * Clears the unused cells for guest mode. Also postBlurViewLoaded is vacuously true since guest
     * mode does not use it
     */
    private fun clearLoggedIn() {
        addCell(HomeCell(), POLL_POS)
        addCell(HomeCell(), LAUNDRY_POS)
        addCell(HomeCell(), POST_POS)
        addCell(HomeCell(), DINING_POS)

        setPostBlurView(true)
    }

    /**
     * Updates the cell at position pos.
     */
    private fun addCell(
        cell: HomeCell,
        pos: Int,
    ) = runBlocking {
        cellMutex.withLock {
            homepageCells[pos] = cell
        }
    }

    private fun getPolls(
        studentLife: StudentLife,
        bearerToken: String,
        deviceID: String,
        latch: CountDownLatch,
    ) {
        val idHash = getSha256Hash(deviceID)
        try {
            studentLife.browsePolls(bearerToken, idHash).subscribe({ poll ->
                if (poll.size > 0) {
                    val pollCell = PollCell(poll[0])
                    pollCell.poll.options.forEach { pollCell.poll.totalVotes += it.voteCount }
                    addCell(pollCell, POLL_POS)
                }

                Log.i(TAG, "Loaded polls")

                latch.countDown()
            }, { throwable ->
                Log.i(TAG, "Could not load polls")
                throwable.printStackTrace()
                latch.countDown()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNews(
        studentLife: StudentLife,
        latch: CountDownLatch,
    ) {
        try {
            studentLife.news.subscribe({ article ->
                val newsCell = NewsCell(article)
                addCell(newsCell, NEWS_POS)

                Log.i(TAG, "Loaded news")

                latch.countDown()
            }, { throwable ->
                Log.i(TAG, "Could not load news")
                throwable.printStackTrace()
                latch.countDown()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCalendar(
        studentLife: StudentLife,
        latch: CountDownLatch,
    ) {
        try {
            studentLife.calendar.subscribe({ events ->
                val calendarCell = CalendarCell(events)

                Log.i(TAG, "Loaded calendar")

                addCell(calendarCell, CALENDAR_POS)
                latch.countDown()
            }, { throwable ->
                Log.i(TAG, "Could not load calendar")
                throwable.printStackTrace()
                latch.countDown()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLaundry(
        studentLife: StudentLife,
        bearerToken: String,
        latch: CountDownLatch,
    ) {
        try {
            studentLife.getLaundryPref(bearerToken).subscribe({ preferences ->
                val laundryCell =
                    if (preferences.isNullOrEmpty()) LaundryCell(0) else LaundryCell(preferences[0])

                Log.i(TAG, "Loaded laundry")

                addCell(laundryCell, LAUNDRY_POS)
                latch.countDown()
            }, { throwable ->
                setNewsBlurView(true)
                Log.i(TAG, "Could not load laundry")
                throwable.printStackTrace()
                latch.countDown()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPosts(
        studentLife: StudentLife,
        bearerToken: String,
        latch: CountDownLatch,
    ) {
        try {
            studentLife.validPostsList(bearerToken).subscribe({ post ->
                if (post.size >= 1) { // there exists a post
                    val postCell = PostCell(post[0])

                    addCell(postCell, POST_POS)
                } else {
                    setPostBlurView(true)
                }

                Log.i(TAG, "Loaded posts")

                latch.countDown()
            }, { throwable ->
                Log.i(TAG, "Could not load posts")
                setPostBlurView(true)
                throwable.printStackTrace()
                latch.countDown()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDiningPrefs(
        studentLife: StudentLife,
        bearerToken: String,
        latch: CountDownLatch,
    ) {
        try {
            studentLife.getDiningPreferences(bearerToken).subscribe({ preferences ->
                val list = preferences.preferences
                val venues = mutableListOf<Int>()
                if (list?.isEmpty() == true) {
                    venues.add(593)
                    venues.add(1442)
                    venues.add(636)
                } else {
                    list?.forEach {
                        it.id?.let { it1 -> venues.add(it1) }
                    }
                }

                val diningCell = DiningCell(venues)
                addCell(diningCell, DINING_POS)

                Log.i(TAG, "Loaded dining")

                latch.countDown()
            }, { throwable ->
                Log.i(TAG, "Could not load dining")
                throwable.printStackTrace()
                latch.countDown()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getGSRReservations(
        studentLife: StudentLife,
        bearerToken: String,
        latch: CountDownLatch,
    ) {
        try {
            studentLife.getGsrReservations(bearerToken).subscribe({ reservationsList ->
                if (reservationsList.isEmpty()) {
                    addCell(HomeCell(), GSR_POS)
                } else {
                    val gsrCell = GSRCell(reservationsList)
                    Log.i(TAG, "Loaded GSR Reservations")
                    addCell(gsrCell, GSR_POS)
                }
                latch.countDown()
            }, { throwable ->
                Log.i(TAG, "Could not load GSR reservations")
                throwable.printStackTrace()
                latch.countDown()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setPostBlurView(status: Boolean) =
        runBlocking {
            postBlurMutex.withLock {
                postBlurViewLoaded = status
            }
        }

    private fun setNewsBlurView(status: Boolean) =
        runBlocking {
            newsBlurMutex.withLock {
                newsBlurViewLoaded = status
            }
        }

    /**
     * Updates blurViewsLoaded based on the states of postBlurView and newsBlurView
     */
    private fun updateBlurViewStatus() =
        runBlocking {
            postBlurMutex.lock()
            newsBlurMutex.lock()
            Log.i(BLUR_TAG, "Called updateBlurViewStatus")
            Log.i(BLUR_TAG, "News: $newsBlurViewLoaded")
            Log.i(BLUR_TAG, "Posts: $postBlurViewLoaded")
            if (newsBlurViewLoaded && postBlurViewLoaded) {
                _blurViewsLoaded.postValue(true)
            } else {
                _blurViewsLoaded.postValue(false)
            }
            postBlurMutex.unlock()
            newsBlurMutex.unlock()
        }

    /**
     * Allows adapter to tell the ViewModel that the post blur view is processed
     */
    override fun notifyPostBlurLoaded() {
        setPostBlurView(true)
        updateBlurViewStatus()
    }

    /**
     * Allows adapter to tell the ViewModel that the news blur view is processed
     */
    override fun notifyNewsBlurLoaded() {
        setNewsBlurView(true)
        updateBlurViewStatus()
    }

    /**
     * Since homepageCells is always populated, it should always have NUM_CELLS cells. The idea is
     * that we keep the unused cells empty.
     */
    override fun getSize(): Int {
        return NUM_CELLS
    }

    override fun getCell(position: Int): HomeCell {
        // be careful to not read an old value
        return homepageCells[position]
    }

    /**
     * Updates the dining hall preferences. Used from the
     * dining pref cell on the homepage
     */
    override fun updateDining(venues: List<Int>) {
        addCell(DiningCell(venues), DINING_POS)
        updatePosition(DINING_POS)
    }

    /**
     * Gets the dining hall preferences as a list. Used by the dining preferences
     * cell on the homepage
     */
    override fun getDiningHallPrefs(): List<Int> {
        // if empty, return an empty list
        val diningCell = homepageCells[DINING_POS]
        if (diningCell.type != "dining") {
            return emptyList()
        }
        return (diningCell as DiningCell).venues
    }
}
