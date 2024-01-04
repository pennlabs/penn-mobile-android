package com.pennapps.labs.pennmobile.classes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.utils.Utils.getSha256Hash
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.CountDownLatch

class HomepageViewModel : HomepageDataModel, ViewModel() {
    companion object {
        private const val NUM_CELLS = 6

        // Types of Home Cells
        private const val POLL_POS = 0
        private const val CALENDAR_POS = 1
        private const val NEWS_POS = 2
        private const val POST_POS = 3
        private const val DINING_POS = 4
        private const val LAUNDRY_POS = 5
    }

    private val homepageCells = mutableListOf<HomeCell>()
    private val cellMutex = Mutex()
    private val _blurViewsLoaded = MutableLiveData<Boolean>(false)
    val blurViewsLoaded: LiveData<Boolean>
        get() = _blurViewsLoaded

    private var postBlurViewLoaded = false
    private var newsBlurViewLoaded = false
    private val postBlurMutex = Mutex()
    private val newsBlurMutex = Mutex()

    init {
        for (i in 1..NUM_CELLS) {
            homepageCells.add(HomeCell())
        }
    }
    @Synchronized
    fun updateHomePageCells(studentLife: StudentLife, bearerToken: String, deviceID: String,
                              update: (Int) -> Unit, callback: () -> Unit) {
        val prevList = homepageCells.toList()
        populateHomePageCells(studentLife, bearerToken, deviceID) {
           for (i in 0 until NUM_CELLS) {
               if (prevList[i] != homepageCells[i]) {
                   update(i)
               }
           }
           callback.invoke()
        }
    }

    @Synchronized
    fun populateHomePageCells(studentLife: StudentLife, bearerToken: String, deviceID: String,
                              callback: () -> Unit) {
        val isLoggedIn = bearerToken != "Bearer "

        if (isLoggedIn) {
            val latch = CountDownLatch(6)
            getPolls(studentLife, bearerToken, deviceID, latch)
            getNews(studentLife, latch)
            getCalendar(studentLife, latch)
            getLaundry(studentLife, bearerToken, latch)
            getPosts(studentLife, bearerToken, latch)
            getDiningPrefs(studentLife, bearerToken, latch)
            latch.await()
        } else {
            val latch = CountDownLatch(2)
            getCalendar(studentLife, latch)
            getNews(studentLife, latch)
            latch.await()
        }
        callback.invoke()
    }

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

            latch.countDown()
        }, { throwable ->
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getNews(studentLife: StudentLife, latch: CountDownLatch) {
        studentLife.news.subscribe({ article ->
            val newsCell = HomeCell()
            newsCell.info = HomeCellInfo()
            newsCell.info?.article = article
            newsCell.type = "news"

            addCell(newsCell, NEWS_POS)
            latch.countDown()
        }, { throwable ->
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getCalendar(studentLife: StudentLife, latch: CountDownLatch) {
        studentLife.calendar.subscribe({ events ->
            val calendar = HomeCell()
            calendar.type = "calendar"
            calendar.events = events

            addCell(calendar, CALENDAR_POS)
            latch.countDown()
        }, { throwable ->
            throwable.printStackTrace()
            latch.countDown()
        })
    }


    private fun getLaundry(studentLife: StudentLife, bearerToken: String, latch: CountDownLatch) {
        studentLife.getLaundryPref(bearerToken).subscribe({ preferences ->
            val laundryCell = HomeCell()
            laundryCell.type = "laundry"
            val laundryCellInfo = HomeCellInfo()
            if (preferences?.isEmpty() == false) {
                laundryCellInfo.roomId = preferences[0]
            }
            laundryCell.info = laundryCellInfo

            addCell(laundryCell, LAUNDRY_POS)
            latch.countDown()
        }, { throwable ->
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getPosts(studentLife: StudentLife, bearerToken: String, latch: CountDownLatch) {
        studentLife.validPostsList(bearerToken).subscribe({ post ->
            if (post.size >= 1) { //there exists a post
                val postCell = HomeCell()
                postCell.info = HomeCellInfo()
                postCell.type = "post"
                postCell.info?.post = post[0]

                addCell(postCell, POST_POS)
            } else {
                setPostBlurView(true)
            }

            latch.countDown()

        }, { throwable ->
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun getDiningPrefs(studentLife: StudentLife, bearerToken: String, latch: CountDownLatch) {
        studentLife.getDiningPreferences(bearerToken).subscribe({ preferences ->
            val list = preferences.preferences
            val venues = mutableListOf<Int>()
            val diningCell = HomeCell()
            diningCell.type = "dining"
            val diningCellInfo = HomeCellInfo()
            if (list?.isEmpty() == true) {
                venues.add(593)
                venues.add(1442)
                venues.add(636)
            } else {
                list?.forEach {
                    it.id?.let { it1 -> venues.add(it1) }
                }
            }

            diningCellInfo.venues = venues
            diningCell.info = diningCellInfo

            addCell(diningCell, DINING_POS)
            latch.countDown()
        }, { throwable ->
            throwable.printStackTrace()
            latch.countDown()
        })
    }

    private fun setPostBlurView(status: Boolean) = runBlocking {
        postBlurMutex.withLock {
            postBlurViewLoaded = status
        }
        updateBlurViewStatus()
    }
    private fun setNewsBlurView(status: Boolean) = runBlocking {
        newsBlurMutex.withLock {
           newsBlurViewLoaded = status
        }
        updateBlurViewStatus()
    }
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

    override fun notifyPostBlurLoaded() {
        setPostBlurView(true)
    }

    override fun notifyNewsBlurLoaded() {
        setNewsBlurView(true)
    }

    override fun getSize(): Int {
        return homepageCells.size
    }

    override fun getCell(position: Int): HomeCell {
        return homepageCells[position]
    }
}