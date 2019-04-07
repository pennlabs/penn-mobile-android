package com.pennapps.labs.pennmobile

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.pennapps.labs.pennmobile.api.Labs
import com.pennapps.labs.pennmobile.classes.GSRContainer
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.fragment_gsr.*
import kotlinx.android.synthetic.main.fragment_gsr.view.*
import kotlinx.android.synthetic.main.loading_panel.*
import kotlinx.android.synthetic.main.no_results.*
import org.joda.time.format.DateTimeFormat
import java.util.*


class GsrFragment : Fragment() {

    // ui components
    lateinit var calendarButton: Button
    lateinit var startButton: Button
    lateinit var durationDropDown: Spinner
    lateinit var gsrLocationDropDown: Spinner

    // api manager
    private lateinit var mLabs: Labs

    //list that holds all GSR rooms
    private val gsrHashMap = HashMap<String, Int>()

    private var gsrLocationsArray = ArrayList<String>()

    // all the gsrs
    private var mGSRS = ArrayList<GSRContainer>()

    private var formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLabs = MainActivity.getLabsInstance()
        (activity as MainActivity).closeKeyboard()
        activity?.setTitle(R.string.gsr)
        // fabric report handling
        Fabric.with(context, Crashlytics())
        Answers.getInstance().logContentView(ContentViewEvent()
                .putContentName("GSR")
                .putContentType("App Feature")
                .putContentId("0"))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // link UI elements
        val v = inflater.inflate(R.layout.fragment_gsr, container, false)
        calendarButton = v.gsr_select_date
        startButton = v.gsr_select_time
        gsrLocationDropDown = v.gsr_building_selection

        // populate the list of gsrs
        populateDropDownGSR()

        // Get calendar time and date: standardize to EST
        val tz = TimeZone.getTimeZone("America/New_York")
        val calendar = Calendar.getInstance(tz)
        val minutes = calendar.get(Calendar.MINUTE)
        val hour = calendar.get(Calendar.HOUR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val ampm = calendar.get(Calendar.AM_PM)

        calendarButton.text = (month.toString() + "/" + day + "/" + year)

        // Set default start/end times for GSR booking
        val ampmTimes = getStartEndTimes(hour, minutes, ampm)
        startButton.text = ampmTimes[0]
        durationDropDown.text = ampmTimes[1]

        // Set up recycler view for list of GSR rooms
        val gsrRoomListLayoutManager = LinearLayoutManager(context)
        gsrRoomListLayoutManager.orientation = LinearLayoutManager.VERTICAL
        v.gsr_rooms_list.layoutManager = (gsrRoomListLayoutManager)

        /**
         * On Click functions for buttons
         */

        //set start time button
        startButton.setOnClickListener {
            // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(activity,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        val AM_PM = if (hourOfDay < 12) "AM" else "PM"

                        var hourString = Integer.toString(hourOfDay)
                        if (hourOfDay == 0) {
                            hourString = "12"
                        }
                        //convert to nonmilitary time
                        if (hourOfDay > 12) {
                            hourString = Integer.toString(hourOfDay - 12)
                        }

                        var minuteString = Integer.toString(minute)

                        //Android treats minutes less than 10 as single digit
                        if (minute < 10) {
                            minuteString = "0$minute"
                        }

                        //display selected time
                        startButton.text = String.format(getString(R.string.start_end_button_text), hourString, minuteString, AM_PM)
                        searchForGSR()
                    }, mHour, mMinute, false)
            timePickerDialog.show()
        }

        //end time button
        durationDropDown.setOnClickListener {
            // Get Current Time
            val c = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(activity,
                    TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                        val AM_PM = if (hourOfDay < 12) "AM" else "PM"

                        var hourString = Integer.toString(hourOfDay)
                        if (hourOfDay == 0) {
                            hourString = "12"
                        }
                        if (hourOfDay > 12) {
                            hourString = Integer.toString(hourOfDay - 12)
                        }

                        var minuteString = Integer.toString(minute)

                        if (minute < 10) {
                            minuteString = "0$minute"
                        }

                        durationDropDown?.text = String.format(getString(R.string.start_end_button_text), hourString, minuteString, AM_PM)
                        searchForGSR()
                    }, mHour, mMinute, false)

            timePickerDialog.show()
        }


        //day for gsr
        calendarButton.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(activity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        //account for index starting at 0
                        val entryMonth = monthOfYear + 1

                        calendarButton.text = entryMonth.toString() + "/" + dayOfMonth + "/" + year
                        searchForGSR()
                    }, mYear, mMonth, mDay)

            //set min and max choices for dates. Want to limit to week.
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

        // handle swipe to refresh
        v.gsr_refresh_layout.setColorSchemeResources(R.color.color_accent, R.color.color_primary)
        v.gsr_refresh_layout.setOnRefreshListener {
            //get vars
            val dateBooking = calendarButton.text.toString()
            val startTime = startButton.text.toString()
            val endTime = durationDropDown.text.toString()
            val location = mapGSR(gsrLocationDropDown?.selectedItem.toString())
            if (location == -1) {
                showNoResults()
                Toast.makeText(activity, "Error: could not load buildings", Toast.LENGTH_LONG).show()
            } else {
                no_results.visibility = View.GONE
                // Do not make the normal loading panel visible since the refresh layout already shows a loading icon
                // Get the hours
                getTimes(location, dateBooking, startTime, endTime)
            }
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        activity?.setTitle(R.string.gsr)
        (activity as MainActivity).setNav(R.id.nav_gsr)
        populateDropDownGSR()
    }

    // Performs GSR search
    // Called when page loads and whenever user changes start/end time, date, or building
    fun searchForGSR() {
        //get vars
        val dateBooking = calendarButton.text.toString()
        val startTime = startButton.text.toString()
        val endTime = durationDropDown.text.toString()
        val location = mapGSR(gsrLocationDropDown?.selectedItem.toString())
        if (location == -1) {
            showNoResults()
            Toast.makeText(activity, "Error: could not load buildings", Toast.LENGTH_LONG).show()
        } else {
            // display loading screen
            no_results.visibility = View.GONE
            loadingPanel.visibility = View.VISIBLE
            gsr_rooms_list.visibility = View.GONE
            //get the hours
            getTimes(location, dateBooking, startTime, endTime)
        }
    }

    // Performs GET request and fetches the rooms and availability
    private fun getTimes(location: Int, dateBooking: String, startTime: String, endTime: String) {
        var startTime = startTime
        var endTime = endTime

        //deal with exception of time starting with 0:--
        if (startTime[0] == '0') {
            startTime = "12" + startTime.substring(1)
        }

        if (endTime[0] == '0') {
            endTime = "12" + endTime.substring(1)
        }

        //convert times to military

        val originalDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy")
        val adjustedDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")
        val adjustedDateString = adjustedDateFormat.print(originalDateFormat.parseDateTime(dateBooking))
        mLabs.gsrRoom(location, adjustedDateString, adjustedDateString)
                ?.subscribe({ gsr ->
                    activity?.let {activity ->
                    activity.runOnUiThread {
                        val gsrRooms = gsr.rooms
                        var timeSlotLengthZero = true

                        if (gsrRooms == null) {
                            // a certification error causes "room" field to remain null
                            showNoResults()
                            Toast.makeText(activity, "Error: Could not load GSRs", Toast.LENGTH_LONG).show()
                        } else {
                            for (i in gsrRooms.indices) {
                                val gsrRoom = gsrRooms[i]
                                val gsrTimeSlots = gsrRoom.slots
                                //checks if the time slots are ever nonzero
                                val size = gsrTimeSlots?.size ?: 0
                                if (size > 0) {
                                    timeSlotLengthZero = false
                                }
                                if (gsrTimeSlots != null) {
                                    for (j in gsrTimeSlots.indices) {
                                        val currSlot = gsrTimeSlots[j]
                                        if (currSlot.isAvailable) {


                                            val startString = currSlot.startTime
                                            val endString = currSlot.endTime

                                            if (startString != null && endString != null) {
                                                val startTime = formatter.parseDateTime(startString.substring(0,
                                                        startString.length - 6))
                                                val endTime = formatter.parseDateTime(endString.substring(0,
                                                        endString.length - 6))
                                                if (endTime.isAfterNow) {
                                                    var stringStartTime = safeToString(startTime.hourOfDay) + ":" +
                                                            safeToString(startTime.minuteOfHour)
                                                    var stringEndTime = safeToString(endTime.hourOfDay) + ":" +
                                                            safeToString(endTime.minuteOfHour)

                                                    stringStartTime = convertToCivilianTime(stringStartTime)
                                                    stringEndTime = convertToCivilianTime(stringEndTime)

                                                    val gsrName = gsrRoom.name ?: ""
                                                    val gsrRoomId = gsrRoom.room_id ?: 0
                                                    insertGSRSlot(gsrName, "$stringStartTime-$stringEndTime", safeToString(startTime.hourOfDay) + ":" +
                                                            safeToString(startTime.minuteOfHour),
                                                            safeToString(startTime.dayOfWeek),
                                                            safeToString(startTime.dayOfMonth), "30", Integer.toString(gsrRoomId))
                                                }
                                            }
                                        }
                                    }


                                }
                            }
                        }
                        // remove loading icon
                        loadingPanel.visibility = View.GONE
                        no_results.visibility = View.GONE
                        // stop refreshing
                        try {
                            gsr_rooms_list.visibility = View.VISIBLE
                            gsr_refresh_layout.isRefreshing = false
                        } catch (e: NullPointerException) {
                            // no need to do anything, we've just moved away from this activity
                        }

                        if (timeSlotLengthZero) {
                            Toast.makeText(context, "No GSRs available", Toast.LENGTH_LONG).show()
                        }

                        gsr_rooms_list.adapter = (context?.let {
                            GsrBuildingAdapter(it, mGSRS, Integer.toString(location))
                        })

                        mGSRS = ArrayList()
                    }
                }
                }, { activity?.let {
                    activity ->
                    activity.runOnUiThread {
                        showNoResults()
                        Toast.makeText(activity, "Error: could not load GSRs", Toast.LENGTH_LONG).show()
                    } }
                }
                )
    }


    private fun populateDropDownGSR() {
        mLabs.location()
                ?.subscribe({ locations ->
                    activity?.let {activity ->
                        activity.runOnUiThread {
                            //reset the drop down
                            val emptyArray = arrayOfNulls<String>(0)
                            val emptyAdapter = ArrayAdapter<String>(activity,
                                    android.R.layout.simple_spinner_dropdown_item, emptyArray)
                            gsrLocationDropDown?.adapter = emptyAdapter

                            gsrLocationsArray = ArrayList()

                            val numLocations = locations.size


                            var i = 0
                            // go through all the rooms
                            while (i < numLocations) {
                                val locationName = locations[i]?.name ?: ""
                                gsrHashMap[locationName] = locations[i].id
                                gsrLocationsArray.add(locationName)
                                i++
                            }

                            val gsrs = gsrLocationsArray.toTypedArray<String?>()

                            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                            //There are multiple variations of this, but this is the basic variant.
                            val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, gsrs)

                            //set the spinners adapter to the previously created one.
                            gsrLocationDropDown?.setAdapter(adapter)
                            searchForGSR()
                        }
                    }
                }, {
                    activity?.let {activity ->
                        activity.runOnUiThread {
                            //hard coded in case runs into error
                            gsrHashMap["Weigle"] = 1086
                            gsrHashMap["VP Ground Floor"] = 1086
                            gsrHashMap["VP 3rd Floor"] = 1086
                            gsrHashMap["VP 4th Floor"] = 1086
                            gsrHashMap["Lippincott"] = 2587
                            gsrHashMap["Education Commons"] = 2495
                            gsrHashMap["Biomedical Library"] = 2683
                            gsrHashMap["Fisher Fine Arts"] = 2637
                            gsrHashMap["Levin Building"] = 1090
                            gsrHashMap["Museum Library"] = 2634
                            gsrHashMap["VP Seminar"] = 2636
                            gsrHashMap["VP Special Use"] = 2611


                            gsrLocationsArray.add("Weigle")
                            gsrLocationsArray.add("VP Ground Floor")
                            gsrLocationsArray.add("VP 3rd Floor")
                            gsrLocationsArray.add("VP 4th Floor")
                            gsrLocationsArray.add("Lippincott")
                            gsrLocationsArray.add("Education Commons")
                            gsrLocationsArray.add("Biomedical Library")
                            gsrLocationsArray.add("Fisher Fine Arts")
                            gsrLocationsArray.add("Levin Building")
                            gsrLocationsArray.add("Museum Library")
                            gsrLocationsArray.add("VP Seminar")
                            gsrLocationsArray.add("VP Special Use")


                            val gsrs = gsrLocationsArray.toTypedArray<String?>()

                            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                            //There are multiple variations of this, but this is the basic variant.
                            activity?.let {activity ->
                                val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, gsrs)
                                //set the spinners adapter to the previously created one.
                                gsrLocationDropDown?.setAdapter(adapter)
                                searchForGSR()
                            }
                        }
                    }
                }
                )
        gsrLocationDropDown?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                searchForGSR()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
    }

    private fun showNoResults() {
        // get rid of loading screen and display no results
        no_results.visibility = View.VISIBLE
        loadingPanel.visibility = View.GONE
        gsr_rooms_list.visibility = View.GONE
        gsr_refresh_layout.isRefreshing = false
    }

    //helper function that turns military to civilian time
    fun convertToCivilianTime(input: String): String {
        val militaryTimeFormatter = DateTimeFormat.forPattern("HH:mm")
        val civilianTimeFormatter = DateTimeFormat.forPattern("hh:mm a")
        return civilianTimeFormatter.print(militaryTimeFormatter.withLocale(Locale.ENGLISH).parseLocalTime(input))
    }

    //helper function that converts string to int but keeps zeros
    fun safeToString(input: Int): String = if (input == 0) "00" else Integer.toString(input)

    //takes the name of the gsr and returns an int for the corresponding code
    fun mapGSR(name: String): Int = gsrHashMap[name] ?: 0

    // Parameters: the starting time's hour, minutes, and AM/PM as formatted by Java.utils.Calendar
    // AM = 0, PM = 1
    // Returns a string array of length 2 where first element is properly formatted starting time
    // Second element is properly formatted ending time, which is one hour after starting time
    fun getStartEndTimes(hour: Int, minutes: Int, ampm: Int): Array<String> {
        val results = arrayOf("0", "0")
        val strampm = if (ampm == 0) "AM" else "PM"
        results[0] = hour.toString() + ":00" + " " + strampm
        results[1] = "11:59 PM"
        return results
    }

    //function that takes all available GSR sessions and populates mGSRs
    fun insertGSRSlot(gsrName: String, GSRTimeRange: String, GSRDateTime: String,
                      GSRDayDate: String, GSRDateNum: String, GSRDuration: String, GSRElementId: String) {

        var encountered = false

        for (i in mGSRS.indices) {
            val currentGSR = mGSRS[i]
            //if there is GSR, add the available session to the GSR Object
            if (currentGSR.gsrName == gsrName) {
                currentGSR.addGSRSlot(GSRTimeRange, GSRDateTime, GSRDayDate, GSRDateNum, GSRDuration, GSRElementId)
                encountered = true
            }
        }
        //can't find existing GSR. Create new object
        if (!encountered) {
            val newGSRObject = GSRContainer(gsrName, GSRTimeRange, GSRDateTime, GSRDayDate, GSRDateNum, GSRDuration, GSRElementId)
            mGSRS.add(newGSRObject)
        }
    }




}