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
        private const val NUM_CELLS = 6
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
    }

    private val homepageCells = mutableListOf<HomeCell>()
    private val cellMutex = Mutex()

    data class ItemUpdateEvents(val positions : List<Int> = emptyList())

    
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
     * Updates each homepage cell. If a cell changes, then update is called with the index of the
     * cell that was changed (created with the intention to play nice with RecyclerView and is used
     * in conjunction with NotifyItemChanged().
     */
    @Synchronized
    fun updateHomePageCells(studentLife: StudentLife, bearerToken: String, deviceID: String,
                              update: (Int) -> Unit, callback: () -> Unit) {
        val prevList = homepageCells.toList()
        populateHomePageCells(studentLife, bearerToken, deviceID) {
            for (i in 0 until NUM_CELLS) {
                if (prevList[i] != homepageCells[i]) {
                    update(i)
                    Log.i("CellUpdates", "updated index ${i}")
                } else {
                    Log.i("CellUpdates", "saved an update at index ${i}")
                }
            }
            callback.invoke()
        }
    }

    /**
     * Makes the network requests that populates the Homepage Cells.
     * This function requires a correct (non-expired) bearerToken!!
     */
    @Synchronized
    fun populateHomePageCells(studentLife: StudentLife, bearerToken: String, deviceID: String,
                              callback: () -> Unit) {
        val isLoggedIn = bearerToken != "Bearer "

        if (isLoggedIn) {
            val latch = CountDownLatch(NUM_CELLS_LOGGED_IN)
            getPolls(studentLife, bearerToken, deviceID, latch)
            getNews(studentLife, latch)
            getCalendar(studentLife, latch)
            getLaundry(studentLife, bearerToken, latch)
            getPosts(studentLife, bearerToken, latch)
            getDiningPrefs(studentLife, bearerToken, latch)
            // waits until all of the network calls are processed
            latch.await()
        } else {
            val latch = CountDownLatch(NUM_CELLS_GUEST)
            clearLoggedIn()
            getCalendar(studentLife, latch)
            getNews(studentLife, latch)
            latch.await()
        }
        callback.invoke()
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
    private fun addCell(cell: HomeCell, pos: Int) = runBlocking {
        cellMutex.withLock {
            homepageCells[pos] = cell
        }
    }

    private fun getPolls(studentLife: StudentLife, bearerToken: String, deviceID: String,
    latch: CountDownLatch) {
        val idHash = getSha256Hash(deviceID)
        studentLife.browsePolls(bearerToken, idHash).subscribe({ poll ->
            if (poll.size > 0) {
                val pollCell = PollCell(poll[0])
                pollCell.poll.options.forEach { pollCell.poll.totalVotes += it.voteCount }
                addCell(pollCell, POLL_POS)
            }

            Log.i("HomepageViewModel", "Loaded polls") 

            latch.countDown()
        }, { throwable ->
            Log.i("HomepageViewModel", "Could not load polls")
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getNews(studentLife: StudentLife, latch: CountDownLatch) {
        studentLife.news.subscribe({ article ->
            val newsCell = NewsCell(article)
            addCell(newsCell, NEWS_POS)

            Log.i("HomepageViewModel", "Loaded news") 

            latch.countDown()
            
        }, { throwable ->
            Log.i("HomepageViewModel", "Could not load news")
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getCalendar(studentLife: StudentLife, latch: CountDownLatch) {
        studentLife.calendar.subscribe({ events ->
            val calendarCell = CalendarCell(events)

            Log.i("HomepageViewModel", "Loaded calendar")

            addCell(calendarCell, CALENDAR_POS)
            latch.countDown()
        }, { throwable ->
            Log.i("HomepageViewModel", "Could not load calendar")
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getLaundry(studentLife: StudentLife, bearerToken: String, latch: CountDownLatch) {
        studentLife.getLaundryPref(bearerToken).subscribe({ preferences ->
            val laundryCell = if (preferences.isNullOrEmpty()) LaundryCell(0) else LaundryCell(preferences[0])

            Log.i("HomepageViewModel", "Loaded laundry")

            addCell(laundryCell, LAUNDRY_POS)
            latch.countDown()
        }, { throwable ->
            setNewsBlurView(true)
            Log.i("HomepageViewModel", "Could not load laundry")
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getPosts(studentLife: StudentLife, bearerToken: String, latch: CountDownLatch) {
        studentLife.validPostsList(bearerToken).subscribe({ post ->
            if (post.size >= 1) { //there exists a post
                val postCell = PostCell(post[0])

                addCell(postCell, POST_POS)
            } else {
                setPostBlurView(true)
            }

            Log.i("HomepageViewModel", "Loaded posts")

            latch.countDown()
        }, { throwable ->
            Log.i("HomepageViewModel", "Could not load posts")
            setPostBlurView(true)
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getDiningPrefs(studentLife: StudentLife, bearerToken: String, latch: CountDownLatch) {
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

            Log.i("HomepageViewModel", "Loaded dining")

            latch.countDown()
        }, { throwable ->
            Log.i("HomepageViewModel", "Could not load dining")
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun setPostBlurView(status: Boolean) = runBlocking {
        postBlurMutex.withLock {
            postBlurViewLoaded = status
        }
    }

    private fun setNewsBlurView(status: Boolean) = runBlocking {
        newsBlurMutex.withLock {
           newsBlurViewLoaded = status
        }
    }

    /**
     * Updates blurViewsLoaded based on the states of postBlurView and newsBlurView
     */
    private fun updateBlurViewStatus() = runBlocking {
        postBlurMutex.lock()
        newsBlurMutex.lock()
        Log.i("HomeBlurViewLoadStatus", "Called updateBlurViewStatus")
        Log.i("HomeBlurViewLoadStatus", "News: $newsBlurViewLoaded")
        Log.i("HomeBlurViewLoadStatus", "Posts: $postBlurViewLoaded")
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

    override fun updateDining(venues : List<Int>) {
        addCell(DiningCell(venues), DINING_POS) 
        updatePosition(DINING_POS)
    }

    override fun getDiningHallPrefs() : List<Int> {
        // if empty, return an empty list
        val diningCell = homepageCells[DINING_POS]
        if (diningCell.type != "dining") {
            return emptyList()
        }
        return (diningCell as DiningCell).venues
    }
}
