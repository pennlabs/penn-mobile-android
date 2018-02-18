package com.pennapps.labs.pennmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pennapps.labs.pennmobile.api.Labs;
import com.pennapps.labs.pennmobile.classes.GSR;
import com.pennapps.labs.pennmobile.classes.GSRContainer;
import com.pennapps.labs.pennmobile.classes.GSRLocation;
import com.pennapps.labs.pennmobile.classes.GSRRoom;
import com.pennapps.labs.pennmobile.classes.GSRSlot;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by Mike Abelar on 9/24/2017.
 */

public class GsrFragment extends Fragment {


    //list that holds all GSR rooms
    private Map<String, Integer> gsrHashMap = new HashMap<String, Integer>();
    RecyclerView gsrRoomListRecylerView;

    private Labs mLabs;

    ArrayList<String> gsrLocationsArray = new ArrayList<String>();

    ArrayList<GSRContainer> mGSRS = new ArrayList<GSRContainer>();

    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Bind(R.id.select_date) Button calendarButton;
    @Bind(R.id.select_start_time) Button startButton;
    @Bind(R.id.select_end_time) Button endButton;
    @Bind(R.id.search_GSR) Button searchGSR;
    @Bind(R.id.gsr_building_selection) Spinner gsrDropDown;
    @Bind(R.id.instructions) TextView instructions;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabs = MainActivity.getLabsInstance();
        ((MainActivity) getActivity()).closeKeyboard();
        getActivity().setTitle(R.string.gsr);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gsr, container, false);

        ButterKnife.bind(this, v);


        populateDropDownGSR();






        // Get calendar time and date
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int ampm = calendar.get(Calendar.AM_PM);


        calendarButton.setText(month + "/" + day + "/" + year);


        // Set default start/end times for GSR booking
        String[] ampmTimes = getStartEndTimes(hour, minutes, ampm);
        startButton.setText(ampmTimes[0]);
        endButton.setText(ampmTimes[1]);

         // Set up recycler view for list of GSR rooms
         gsrRoomListRecylerView = (RecyclerView) v.findViewById(R.id.gsr_rooms_list);

        /**
         *
         *
         * START on click functions for buttons
         *
         */

        //set start time button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String AM_PM ;
                                if(hourOfDay < 12) {
                                    AM_PM = "AM";
                                } else {
                                    AM_PM = "PM";
                                }

                                String hourString = Integer.toString(hourOfDay);
                                if (hourOfDay == 0) {
                                    hourString = "12";
                                }
                                //convert to nonmilitary time
                                if (hourOfDay > 12) {
                                    hourString = Integer.toString(hourOfDay - 12);
                                }

                                String minuteString = Integer.toString(minute);

                                //Android treats minutes less than 10 as single digit
                                if (minute < 10) {
                                    minuteString = "0" + minute;
                                }

                                //display selected time
                                startButton.setText(hourString + ":" + minuteString + " " + AM_PM);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }

        });

        //end time button
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                String AM_PM ;
                                if(hourOfDay < 12) {
                                    AM_PM = "AM";
                                } else {
                                    AM_PM = "PM";
                                }

                                String hourString = Integer.toString(hourOfDay);
                                if (hourOfDay == 0) {
                                    hourString = "12";
                                }
                                if (hourOfDay > 12) {
                                    hourString = Integer.toString(hourOfDay - 12);
                                }

                                String minuteString = Integer.toString(minute);

                                if (minute < 10) {
                                    minuteString = "0" + minute;
                                }

                                endButton.setText(hourString + ":" + minuteString + " " + AM_PM);
                            }
                        }, mHour, mMinute, false);

                timePickerDialog.show();
            }

        });



        //day for gsr
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //acount for index starting at 0
                                int entryMonth = monthOfYear + 1;

                                calendarButton.setText(entryMonth + "/" + dayOfMonth + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                //set min and max choices for dates. Want to limit to week.
                Date today = new Date();
                c.setTime(today);
                long minDate = c.getTime().getTime();

                c.setTime(today);
                c.add( Calendar.DAY_OF_MONTH, +6 );
                long maxDate = c.getTime().getTime();

                datePickerDialog.getDatePicker().setMaxDate(maxDate);
                datePickerDialog.getDatePicker().setMinDate(minDate);
                datePickerDialog.show();
            }

        });

        //execute the seatch
        searchGSR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT).show();

                instructions.setText(getString(R.string.select_intrusctions));

                //get vars
                String dateBooking = calendarButton.getText().toString();
                String startTime = startButton.getText().toString();
                String endTime = endButton.getText().toString();
                int location = mapGSR(gsrDropDown.getSelectedItem().toString());
                if (location == -1) {
                    Toast.makeText(getActivity(), "Sorry, an error has occurred", Toast.LENGTH_LONG).show();
                }
                else {
                    //get the hours
                    getTimes(location, dateBooking, startTime, endTime);

                }

            }

        });

        /**
         *
         *
         * END on click functions for buttons
         *
         */


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.gsr);
        ((MainActivity) getActivity()).setNav(R.id.nav_gsr);
    }

    private void getTimes(final int location, final String dateBooking, String startTime, String endTime) {

        //deal with exception of time starting with 0:--
        if (startTime.charAt(0) == '0') {
            startTime = "12" + startTime.substring(1);
        }

        if (endTime.charAt(0) == '0') {
            endTime = "12" + endTime.substring(1);
        }

        //convert times to military
        DateTimeFormatter toMilitaryTimeFormatter = DateTimeFormat.forPattern("hh:mm a");

        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
        String startMilitary = fmt.print(toMilitaryTimeFormatter.parseDateTime(startTime));
        String endMilitary = fmt.print(toMilitaryTimeFormatter.parseDateTime(endTime));

        DateTimeFormatter originalDateFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTimeFormatter adjustedDateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        String adjustedDateString = adjustedDateFormat.print(originalDateFormat.parseDateTime(dateBooking));

        String startParam = adjustedDateString + "T" + startMilitary + "-0500";
        String endParam = adjustedDateString + "T" + endMilitary + "-0500";

        mLabs.gsrRoom(location, startParam, endParam)
                .subscribe(new Action1<GSR>() {
                    @Override
                    public void call(final GSR gsr) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                GSRRoom[] gsrRooms = gsr.getRooms();



                                boolean timeSlotLengthZero = true;

                                for (int i = 0; i < gsrRooms.length; i++) {
                                    GSRRoom gsrRoom = gsrRooms[i];
                                    GSRSlot[] GSRTimeSlots = gsrRoom.getSlots();
                                    //checks if the time slots are ever nonzero
                                    if (GSRTimeSlots.length > 0) {timeSlotLengthZero = false;}
                                    for (int j=0; j < GSRTimeSlots.length; j++) {
                                        GSRSlot currSlot = GSRTimeSlots[j];
                                        if (currSlot.isAvailable()) {



                                            String startString = currSlot.getStartTime();
                                            String endString = currSlot.getEndTime();




                                            DateTime startTime = formatter.parseDateTime(startString.substring(0,
                                                    startString.length() - 6));
                                            DateTime endTime = formatter.parseDateTime(endString.substring(0,
                                                    endString.length() - 6));
                                            String stringStartTime = safeToString(startTime.getHourOfDay()) + ":" +
                                                    safeToString(startTime.getMinuteOfHour());
                                            String stringEndTime = safeToString(endTime.getHourOfDay()) + ":" +
                                                    safeToString(endTime.getMinuteOfHour());

                                            stringStartTime = convertToCivilianTime(stringStartTime);
                                            stringEndTime = convertToCivilianTime(stringEndTime);


                                            insertGSRSlot(gsrRoom.getName(), stringStartTime + "-" + stringEndTime, safeToString(startTime.getHourOfDay()) + ":" +
                                                            safeToString(startTime.getMinuteOfHour()),
                                                    safeToString(startTime.getDayOfWeek()),
                                                    safeToString(startTime.getDayOfMonth()), "30", Integer.toString(gsrRoom.getRoom_id()));

                                        }
                                    }


                                }

                                if (timeSlotLengthZero) {
                                    Toast.makeText(getContext(), "No GSRs available", Toast.LENGTH_LONG).show();
                                }

                                LinearLayoutManager gsrRoomListLayoutManager = new LinearLayoutManager(getContext());
                                gsrRoomListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                gsrRoomListRecylerView.setLayoutManager(gsrRoomListLayoutManager);
                                gsrRoomListRecylerView.setAdapter(new GsrBuildingAdapter(getContext(), mGSRS, Integer.toString(location)));

                                mGSRS = new ArrayList<GSRContainer>();
                            }
                        });
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(final Throwable throwable) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Error: Could not retrieve GSRs", Toast.LENGTH_LONG).show();
                                Log.e("error", throwable.toString());
                            }
                        });
                    }
                });
    }



    private void populateDropDownGSR() {
        mLabs.location()
                .subscribe(new Action1<List<GSRLocation>>() {
                               @Override
                               public void call(final List<GSRLocation> locations) {
                                   getActivity().runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {


                                           int numLocations = locations.size();


                                           int i = 0;
                                           // go through all the rooms
                                           while (i < numLocations) {

                                               gsrHashMap.put(locations.get(i).name, locations.get(i).id);
                                               gsrLocationsArray.add(locations.get(i).name);
                                               i++;
                                           }

                                           String[] gsrs = new String[gsrLocationsArray.size()];
                                           gsrs = gsrLocationsArray.toArray(gsrs);

                                           //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                                           //There are multiple variations of this, but this is the basic variant.
                                           ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, gsrs);
                                           //set the spinners adapter to the previously created one.
                                           gsrDropDown.setAdapter(adapter);
                                           loadInitialData();


                                       }
                                   });

                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   getActivity().runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {


                                           gsrHashMap.put("Weigle", 1722);
                                           gsrHashMap.put("VP GSR", 1086);
                                           gsrHashMap.put("Museum Library", 2634);
                                           gsrHashMap.put("Lippincott", 1768);
                                           gsrHashMap.put("Edu Commons", 2495);
                                           gsrHashMap.put("Levin Building", 1090);
                                           gsrHashMap.put("Fisher Fine Arts Library", 2637);
                                           gsrHashMap.put("VP Sem. Rooms", 2636);
                                           gsrHashMap.put("Lippincott Sem. Rooms", 2587);
                                           gsrHashMap.put("Glossberg Recording Room", 1819);
                                           //gsrHashMap.put("Dental Sem", 13532);
                                           gsrHashMap.put("Biomedical Lib.", 2683);


                                           gsrLocationsArray.add("Weigle");
                                           gsrLocationsArray.add("VP GSR");
                                           gsrLocationsArray.add("Lippincott");
                                           gsrLocationsArray.add("Edu Commons");
                                           gsrLocationsArray.add("Levin Building");
                                           gsrLocationsArray.add("VP Sem. Rooms");
                                           gsrLocationsArray.add("Lippincott Sem. Rooms");
                                           gsrLocationsArray.add("Glossberg Recording Room");
                                           gsrLocationsArray.add("Biomedical Lib.");

                                           String[] gsrs = new String[gsrLocationsArray.size()];
                                           gsrs = gsrLocationsArray.toArray(gsrs);

                                           //create an adapter to describe how the items are displayed, adapters are used in several places in android.
                                           //There are multiple variations of this, but this is the basic variant.
                                           ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, gsrs);
                                           //set the spinners adapter to the previously created one.
                                           gsrDropDown.setAdapter(adapter);

                                       }
                                   });

                               }
                           }
                );
    }


    //helper function that turns military to civilian time
    public String convertToCivilianTime(String input) {
        DateTimeFormatter militaryTimeFormatter = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter civilianTimeFormatter = DateTimeFormat.forPattern("hh:mm a");
        return civilianTimeFormatter.print(militaryTimeFormatter.parseDateTime(input));
    }

    //helper function that converts string to int but keeps zeros
    public String safeToString (int input) {
        if (input == 0) {
            return "00";
        }
        else {return Integer.toString(input);}
    }

    //takes the name of the gsr and returns an int for the corresponding code
    public int mapGSR(String name) {
        return gsrHashMap.get(name);
    }

    // Parameters: the starting time's hour, minutes, and AM/PM as formatted by Java.utils.Calendar
    // AM = 0, PM = 1
    // Returns a string array of length 2 where first element is properly formatted starting time
    // Second element is properly formatted ending time, which is one hour after starting time
    public static String[] getStartEndTimes(int hour, int minutes, int ampm) {
        String[] results = new String[2];
        String strampm = (ampm == 0) ? "AM" : "PM";
        results[0] = hour + ":00" + " " + strampm;
        results[1] = "11:59 PM";
        return results;
    }

    //async task that gets the hours of the given gsr and time options


    //this function clicks the search button to load initial results on the screen
    public void loadInitialData() {
        searchGSR.performClick();
        searchGSR.setPressed(true);
        searchGSR.invalidate();
        searchGSR.setPressed(false);
        searchGSR.invalidate();
    }



    //function that takes all available GSR sessions and populates mGSRs
    public void insertGSRSlot(String gsrName, String GSRTimeRange, String GSRDateTime,
                              String GSRDayDate, String GSRDateNum, String GSRDuration, String GSRElementId) {

        boolean encountered = false;

        for(int i=0; i<mGSRS.size(); i++) {
            GSRContainer currentGSR = mGSRS.get(i);
            //if there is GSR, add the available session to the GSR Object
            if (currentGSR.getGsrName().equals(gsrName)) {
                currentGSR.addGSRSlot(GSRTimeRange, GSRDateTime, GSRDayDate, GSRDateNum, GSRDuration, GSRElementId);
                encountered = true;
            }
        }
        //can't find existing GSR. Create new object
        if (encountered == false) {
            GSRContainer newGSRObject = new GSRContainer(gsrName, GSRTimeRange, GSRDateTime, GSRDayDate, GSRDateNum, GSRDuration, GSRElementId);
            mGSRS.add(newGSRObject);
        }
    }

}
