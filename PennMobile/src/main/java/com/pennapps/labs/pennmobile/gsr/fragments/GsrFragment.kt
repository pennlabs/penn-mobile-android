package com.pennapps.labs.pennmobile.gsr.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.pennapps.labs.pennmobile.MainActivity
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.api.StudentLife
import com.pennapps.labs.pennmobile.databinding.FragmentGsrBinding
import com.pennapps.labs.pennmobile.gsr.adapters.GsrBuildingAdapter
import com.pennapps.labs.pennmobile.gsr.classes.GSRContainer
import com.pennapps.labs.pennmobile.gsr.classes.GSRRoom
import com.pennapps.labs.pennmobile.gsr.classes.GSRSlot
import com.pennapps.labs.pennmobile.isOnline
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.Calendar
import java.util.Date

class GsrFragment : Fragment() {
    // ui components
    lateinit var selectDateButton: Button
    lateinit var selectTimeButton: Button
    lateinit var gsrLocationDropDown: Spinner
    lateinit var durationDropDown: Spinner
    lateinit var loadingPanel: LinearLayout
    lateinit var noResultsPanel: LinearLayout
    lateinit var sortingSwitch: Switch

    private var _binding: FragmentGsrBinding? = null
    val binding get() = _binding!!

    // api manager
    private lateinit var mStudentLife: StudentLife

    // list that holds all GSR rooms
    private val gsrHashMap = HashMap<String, String?>()

    // list that holds all GSR rooms and their gids
    private val gsrGIDHashMap = HashMap<String, Int>()

    // all the gsrs
    private var mGSRS = ArrayList<GSRContainer>()

    private lateinit var selectedDateTime: DateTime

    private val spinnerDateFormatter = DateTimeFormat.forPattern("M/d/yyyy")
    private val timeFormatter = DateTimeFormat.forPattern("h:mm a")
    private val adjustedDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
    private val gsrSlotFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ")

    private lateinit var durationAdapter: ArrayAdapter<String>
    private lateinit var whartonDurationAdapter: ArrayAdapter<String>
    private lateinit var biotechDurationAdapter: ArrayAdapter<String>

    private var populatedDropDownGSR = false

    private var isWharton = false
    private var sortByTime = false

    private lateinit var mActivity: MainActivity
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStudentLife = MainActivity.studentLifeInstance
        mActivity = activity as MainActivity
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity)
        mActivity.closeKeyboard()

        // set default GSR selection date + time to the current date and time
        selectedDateTime = DateTime.now()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentGsrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        selectDateButton = binding.gsrSelectDate
        selectTimeButton = binding.gsrSelectTime
        gsrLocationDropDown = binding.gsrBuildingSelection
        durationDropDown = binding.gsrDuration
        loadingPanel = binding.gsrLoading
        noResultsPanel = binding.gsrNoResults
        sortingSwitch = binding.sortingSwitch
        sortByTime = sortingSwitch.isChecked

        durationAdapter =
            ArrayAdapter(mActivity, R.layout.gsr_spinner_item, arrayOf("30m", "60m", "90m", "120m"))
        whartonDurationAdapter =
            ArrayAdapter(mActivity, R.layout.gsr_spinner_item, arrayOf("30m", "60m", "90m"))
        biotechDurationAdapter =
            ArrayAdapter(
                mActivity,
                R.layout.gsr_spinner_item,
                arrayOf("30m", "60m", "90m", "120m", "150m", "180m"),
            )

        // update user status by getting the bearer token and checking wharton status
        updateStatus()

        // populate the list of gsrs
        populateDropDownGSR()

        // Set default start date for GSR booking
        selectDateButton.text = selectedDateTime.toString(spinnerDateFormatter)

        // Set default start time for GSR booking
        selectTimeButton.text = selectedDateTime.toString(timeFormatter)

        // Set up recycler view for list of GSR rooms
        val gsrRoomListLayoutManager = LinearLayoutManager(context)
        gsrRoomListLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.gsrRoomsList.layoutManager = (gsrRoomListLayoutManager)

        // On Click functions for buttons

        // set start time button
        selectTimeButton.setOnClickListener {
            // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog =
                TimePickerDialog(
                    activity,
                    { _, hourOfDay, minute ->

                        // Update hour + minute
                        selectedDateTime =
                            DateTime(
                                selectedDateTime.year,
                                selectedDateTime.monthOfYear,
                                selectedDateTime.dayOfMonth,
                                hourOfDay,
                                minute,
                            )

                        // Display the selected time; use Joda to do the formatting work
                        selectTimeButton.text = selectedDateTime.toString(timeFormatter)
                        searchForGSR(false)
                    },
                    mHour,
                    mMinute,
                    false,
                )
            timePickerDialog.show()
        }

        // day for gsr
        selectDateButton.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(
                    mActivity,
                    { _, year, monthOfYear, dayOfMonth ->
                        // account for index starting at 0
                        val entryMonth = monthOfYear + 1

                        // Update year + month + day
                        selectedDateTime =
                            DateTime(
                                year,
                                entryMonth,
                                dayOfMonth,
                                selectedDateTime.hourOfDay,
                                selectedDateTime.minuteOfHour,
                            )

                        // Display the selected date; use Joda to do the formatting work
                        selectDateButton.text = selectedDateTime.toString(spinnerDateFormatter)
                        searchForGSR(false)
                    },
                    mYear,
                    mMonth,
                    mDay,
                )

            // set min and max choices for dates. Want to limit to week.
            val today = Date()
            c.time = today
            val minDate = c.time.time

            c.time = today
            c.add(Calendar.DAY_OF_MONTH, +6)
            val maxDate = c.time.time

            datePickerDialog.datePicker.maxDate = maxDate
            datePickerDialog.datePicker.minDate = minDate
            datePickerDialog.show()
        }

        sortingSwitch.setOnClickListener {
            sortByTime = sortingSwitch.isChecked
            searchForGSR(false)
        }
        // handle swipe to refresh
        binding.gsrRefreshLayout.setColorSchemeResources(
            R.color.color_accent,
            R.color.color_primary,
        )
        binding.gsrRefreshLayout.setOnRefreshListener {
            updateStatus()
            searchForGSR(true)
        }
        binding.internetConnectionGSR.visibility = View.VISIBLE
    }

    private fun updateStatus() {
        mActivity.mNetworkManager.getAccessToken {
            checkIfFragmentAttached {
                val bearerToken =
                    sharedPreferences.getString(getString(R.string.access_token), "").toString()

                if (bearerToken.isEmpty()) {
                    Toast.makeText(activity, "You are not logged in!", Toast.LENGTH_LONG).show()
                } else {
                    try {
                        mStudentLife
                            .isWharton(
                                "Bearer $bearerToken",
                            ).subscribeOn(Schedulers.io())
                            .subscribe(
                                { status ->
                                    isWharton = status?.isWharton ?: false
                                },
                                {
                                    Log.e("GsrFragment", "Error getting Wharton status", it)
                                    isWharton = false
                                },
                            )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    // Performs GSR search
    // Called when page loads and whenever user changes start/end time, date, or building
    fun searchForGSR(calledByRefreshLayout: Boolean) {
        // displays banner if not connected
        if (!isOnline(context)) {
            binding.internetConnectionGSR.setBackgroundColor(resources.getColor(R.color.darkRedBackground))
            binding.internetConnectionMessageGsr.setText("Not Connected to Internet")
            binding.internetConnectionGSR.visibility = View.VISIBLE
        } else {
            binding.internetConnectionGSR.visibility = View.GONE
            if (!populatedDropDownGSR) {
                populateDropDownGSR()
            }
        }
        val gsrLocation = gsrLocationDropDown.selectedItem.toString()
        val location = mapGSR(gsrLocation)
        val gid = mapGID(gsrLocation)
        mActivity.mNetworkManager.getAccessToken {
            val bearerToken =
                sharedPreferences.getString(getString(R.string.access_token), "").toString()

            if (location.isEmpty() || bearerToken.isEmpty()) {
                showNoResults()
            } else {
                // display loading screen if user did not use swipe refresh
                if (!calledByRefreshLayout) {
                    loadingPanel.visibility = View.VISIBLE
                    binding.gsrRoomsList.visibility = View.GONE
                }

                noResultsPanel.visibility = View.GONE
                binding.gsrNoRooms.visibility = View.GONE
                getTimes(location, gid)
            }
        }
    }

    // Performs GET request and fetches the rooms and availability
    private fun getTimes(
        location: String,
        gId: Int,
    ) {
        val adjustedDateString = selectedDateTime.toString(adjustedDateFormat)
        selectDateButton.isClickable = false
        selectTimeButton.isClickable = false
        gsrLocationDropDown.isEnabled = false
        durationDropDown.isEnabled = false
        sortingSwitch.isClickable = false

        mActivity.mNetworkManager.getAccessToken {
            val bearerToken =
                sharedPreferences.getString(getString(R.string.access_token), "").toString()

            Log.i("GsrFragment", "Bearer Token: $bearerToken")
            Log.i("GsrFragment", "Wharton Status: $isWharton")

            try {
                mStudentLife
                    .gsrRoom(
                        "Bearer $bearerToken",
                        location,
                        gId,
                        adjustedDateString,
                    ).subscribeOn(Schedulers.io())
                    .subscribe(
                        { gsr ->
                            activity?.let { activity ->
                                activity.runOnUiThread {
                                    val gsrRooms = gsr?.rooms
                                    var timeSlotLengthZero = true

                                    if (gsrRooms.isNullOrEmpty()) {
                                        // a certification error causes "room" field to remain null
                                        showNoResults()
                                    } else {
                                        for (i in gsrRooms.indices) {
                                            val gsrRoom = gsrRooms[i]
                                            val gsrTimeSlots = gsrRoom.slots
                                            // checks if the time slots are ever nonzero
                                            val size = gsrTimeSlots?.size ?: 0
                                            if (size > 0) {
                                                timeSlotLengthZero = false
                                            }
                                            if (gsrTimeSlots != null) {
                                                filterInsertTimeSlots(gsrRoom, gsrTimeSlots, gId)
                                            }
                                        }
                                    }
                                    // remove loading icon
                                    loadingPanel.visibility = View.GONE
                                    noResultsPanel.visibility = View.GONE
                                    // stop refreshing
                                    binding.gsrRoomsList.visibility = View.VISIBLE
                                    binding.gsrRefreshLayout.isRefreshing = false

                                    if (timeSlotLengthZero) {
                                        binding.gsrNoRooms.visibility = View.VISIBLE
                                    }

                                    binding.gsrRoomsList.adapter = (
                                        context?.let {
                                            GsrBuildingAdapter(
                                                it,
                                                mGSRS,
                                                location,
                                                (durationDropDown.selectedItemPosition + 1) * 30,
                                                sortByTime,
                                            )
                                        }
                                    )

                                    mGSRS = ArrayList()
                                    selectDateButton.isClickable = true
                                    selectTimeButton.isClickable = true
                                    gsrLocationDropDown.isEnabled = true
                                    durationDropDown.isEnabled = true
                                    sortingSwitch.isClickable = true
                                }
                            }
                        },
                        {
                            Log.e("GsrFragment", "Error getting gsr times", it)
                            activity?.let { activity ->
                                activity.runOnUiThread {
                                    showNoResults()
                                    selectDateButton.isClickable = true
                                    selectTimeButton.isClickable = true
                                    gsrLocationDropDown.isEnabled = true
                                    durationDropDown.isEnabled = true
                                    sortingSwitch.isClickable = true
                                }
                            }
                        },
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterInsertTimeSlots(
        gsrRoom: GSRRoom,
        timeSlots: Array<GSRSlot>,
        gid: Int,
    ) {
        val availableSlotsAfterSelectedTime = ArrayList<GSRSlot>()

        // Filter time slots so only available slots occurring after selected time are used
        for (pos in timeSlots.indices) {
            val currSlot = timeSlots[pos]
            if (currSlot.isAvailable) {
                val startString = currSlot.startTime
                val endString = currSlot.endTime

                if (startString != null && endString != null) {
                    val endTime = gsrSlotFormatter.parseDateTime(endString)
                    if (endTime.isAfter(selectedDateTime)) {
                        availableSlotsAfterSelectedTime.add(currSlot)
                    }
                }
            }
        }
        val duration = (durationDropDown.selectedItemPosition + 1) * 30

        // Insert GSR slots that meet the specified duration
        for (pos in 0 until availableSlotsAfterSelectedTime.size - durationDropDown.selectedItemPosition) {
            // starting time and slot
            val startingSlot = availableSlotsAfterSelectedTime[pos]
            val startTime = gsrSlotFormatter.parseDateTime(startingSlot.startTime)

            // ending time and slot
            val endingSlot =
                availableSlotsAfterSelectedTime[pos + durationDropDown.selectedItemPosition]
            val endTime = gsrSlotFormatter.parseDateTime(endingSlot.endTime)

            // if start time + duration = end time then these slots meet the duration requirement
            if (startTime.plusMinutes(duration).isEqual(endTime)) {
                val stringStartTime = startTime.toString(timeFormatter)
                val stringEndTime = endTime.toString(timeFormatter)
                val start = startingSlot.startTime ?: ""
                // note that end uses ending slot to account for 30+ min booking times
                val end = endingSlot.endTime ?: ""
                val gsrName = gsrRoom.name ?: ""
                val gsrRoomId = gsrRoom.roomId ?: 0
                insertGSRSlot(
                    gsrName,
                    "$stringStartTime-$stringEndTime",
                    startTime,
                    gsrRoomId.toString(),
                    gid,
                    gsrRoomId,
                    start,
                    end,
                )
            }
        }
    }

    private fun populateDropDownGSR() {
        try {
            val bearerToken: String =
                sharedPreferences.getString(
                    getString(R.string.access_token),
                    "",
                ) ?: ""
            mStudentLife
                .location(
                    "Bearer $bearerToken",
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { locations ->
                        activity?.let { activity ->
                            populatedDropDownGSR = true
                            // reset the drop down
                            val emptyArray = arrayOfNulls<String>(0)
                            val emptyAdapter =
                                ArrayAdapter<String>(
                                    activity,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    emptyArray,
                                )
                            gsrLocationDropDown.adapter = emptyAdapter

                            val locationList = locations?.filterNotNull() ?: emptyList()

                            val numLocations = locationList.size
                            var i = 0
                            // go through all the rooms
                            while (i < numLocations) {
                                val locationName = locationList[i].name ?: ""
                                if (locationName.isEmpty()) {
                                    Log.w(
                                        "Empty location name",
                                        locationList[i].id ?: locationList[i].gid.toString(),
                                    )
                                }
                                gsrHashMap[locationName] = locationList[i].id
                                gsrGIDHashMap[locationName] = locationList[i].gid
                                i++
                            }

                            val gsrs = gsrHashMap.keys.toList().toTypedArray()

                            val adapter =
                                ArrayAdapter(activity, R.layout.gsr_spinner_item, gsrs)
                            gsrLocationDropDown.adapter = adapter

                            durationDropDown.adapter =
                                if (gsrLocationDropDown.selectedItem.toString() == "Huntsman" ||
                                    gsrLocationDropDown.selectedItem.toString() == "Academic Research"
                                ) {
                                    whartonDurationAdapter
                                } else if (gsrLocationDropDown.selectedItem.toString() == "Biomedical") {
                                    biotechDurationAdapter
                                } else {
                                    durationAdapter
                                }
                            searchForGSR(false)
                        }
                    },
                    {
                        Log.e("Gsr Fragment", "Error getting gsr locations", it)
                        activity?.let { activity ->
                            activity.runOnUiThread {
                                // hard coded in case runs into error
                                gsrHashMap["VP Ground Floor"] = "1086"
                                gsrHashMap["Weigle"] = "1086"
                                gsrHashMap["Lippincott"] = "2587"
                                gsrHashMap["Edu Commons"] = "2495"
                                gsrHashMap["Biotech Commons"] = "2683"
                                gsrHashMap["Fisher"] = "2637"
                                gsrHashMap["Levin Building"] = "1090"
                                gsrHashMap["Museum Library"] = "2634"
                                gsrHashMap["VP Seminar"] = "2636"
                                gsrHashMap["VP Special Use"] = "2611"
                                gsrHashMap["Huntsman Hall"] = "JMHH"
                                gsrHashMap["Academic Research"] = "ARB"
                                gsrHashMap["PCPSE Building"] = "4370"
                                gsrGIDHashMap["PCPSE Building"] = 7426
                                val gsrs = gsrHashMap.keys.toList().toTypedArray()
                                val adapter =
                                    ArrayAdapter(activity, R.layout.gsr_spinner_item, gsrs)
                                gsrLocationDropDown.adapter = adapter

                                durationDropDown.adapter =
                                    if (gsrLocationDropDown.selectedItem.toString() == "Huntsman" ||
                                        gsrLocationDropDown.selectedItem.toString() == "Academic Research"
                                    ) {
                                        whartonDurationAdapter
                                    } else if (gsrLocationDropDown.selectedItem.toString() == "Biotech Commons") {
                                        biotechDurationAdapter
                                    } else {
                                        durationAdapter
                                    }
                                searchForGSR(false)
                            }
                        }
                    },
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        gsrLocationDropDown.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View?,
                    i: Int,
                    l: Long,
                ) {
                    // change possible durations depending on the location
                    var durationPos = durationDropDown.selectedItemPosition
                    if (durationPos >= 3 &&
                        (
                            gsrLocationDropDown.selectedItem.toString() == "Huntsman" ||
                                gsrLocationDropDown.selectedItem.toString() == "Academic Research"
                        )
                    ) {
                        durationPos = 2
                    } else if (durationPos > 3 && gsrLocationDropDown.selectedItem.toString() != "Biotech Commons") {
                        durationPos = 3
                    }
                    durationDropDown.adapter =
                        if (gsrLocationDropDown.selectedItem.toString() == "Huntsman" ||
                            gsrLocationDropDown.selectedItem.toString() == "Academic Research"
                        ) {
                            whartonDurationAdapter
                        } else if (gsrLocationDropDown.selectedItem.toString() == "Biotech Commons") {
                            biotechDurationAdapter
                        } else {
                            durationAdapter
                        }
                    durationDropDown.setSelection(durationPos)
                    searchForGSR(false)
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {
                }
            }

        durationDropDown.onItemSelectedListener =
            object : OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    pos: Int,
                    p3: Long,
                ) {
                    searchForGSR(false)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    // Account did not change the duration
                }
            }
    }

    private fun showNoResults() {
        // get rid of loading screen and display no results
        noResultsPanel.visibility = View.VISIBLE
        loadingPanel.visibility = View.GONE
        binding.gsrRoomsList.visibility = View.GONE
        binding.gsrRefreshLayout.isRefreshing = false
    }

    // takes the name of the gsr and returns an int for the corresponding code
    fun mapGSR(name: String): String = gsrHashMap[name] ?: ""

    // takes the name of the gsr and returns an int for the corresponding gid
    fun mapGID(name: String): Int = gsrGIDHashMap[name] ?: 0

    // function that takes all available GSR sessions and populates mGSRs
    fun insertGSRSlot(
        gsrName: String,
        GSRTimeRange: String,
        GSRStartTime: DateTime,
        GSRElementId: String,
        gid: Int,
        roomId: Int,
        start: String,
        end: String,
    ) {
        var encountered = false

        for (i in mGSRS.indices) {
            val currentGSR = mGSRS[i]
            // if there is GSR, add the available session to the GSR Object
            if (currentGSR.gsrName == gsrName) {
                currentGSR.addGSRSlot(GSRTimeRange, GSRStartTime, GSRElementId, start, end)
                encountered = true
            }
        }
        // can't find existing GSR. Create new object
        if (!encountered) {
            val newGSRObject =
                GSRContainer(
                    gsrName,
                    GSRTimeRange,
                    GSRStartTime,
                    GSRElementId,
                    gid,
                    roomId,
                    start,
                    end,
                )
            mGSRS.add(newGSRObject)
        }
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }
}
