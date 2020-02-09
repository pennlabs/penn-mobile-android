package com.pennapps.labs.pennmobile

import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView

import com.pennapps.labs.pennmobile.api.Labs
import java.util.concurrent.atomic.AtomicInteger

import butterknife.BindView
import butterknife.ButterKnife


/**
 * Created by Jason on 1/26/2016.
 */
abstract class SearchFavoriteTab : ListFragment() {

    protected var fav: Boolean = false
    protected lateinit var type: String
    protected var mListView: ListView? = null
    protected lateinit var mActivity: MainActivity
    protected lateinit var mLabs: Labs

    @BindView(R.id.loadingPanel)
    lateinit var loadingPanel: RelativeLayout
    @BindView(R.id.no_results)
    lateinit var no_results: TextView
    @BindView(R.id.search_instructions)
    lateinit var search_instructions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fav = arguments!!.getBoolean(getString(R.string.search_favorite), false)
        type = arguments!!.getString(getString(R.string.search_list), "")
        mActivity = activity as MainActivity
        mLabs = MainActivity.getLabsInstance()
    }

    open fun processQuery(query: String) {
        if (search_instructions.visibility == View.VISIBLE && !query.isEmpty()) {
            search_instructions.visibility = View.GONE
            loadingPanel.visibility = View.VISIBLE
        }
    }

    protected fun noResults() {
        loadingPanel.visibility = View.GONE
        no_results.visibility = View.VISIBLE
        mListView!!.visibility = View.GONE
        search_instructions.visibility = View.GONE
    }

    protected fun notFavoriteInit() {
        search_instructions.visibility = View.VISIBLE
        no_results.visibility = View.GONE
        loadingPanel.visibility = View.GONE
        mListView!!.visibility = View.GONE
    }

    abstract fun initList()

    companion object {

        private val sNextGeneratedId = AtomicInteger(1)

        fun generateViewId(): Int {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        }
    }
}
