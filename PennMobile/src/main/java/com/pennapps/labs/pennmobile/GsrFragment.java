package com.pennapps.labs.pennmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
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
import com.pennapps.labs.pennmobile.classes.GSRLocation;

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
    ArrayList<GSR> mGSRS = new ArrayList<GSR>();
    RecyclerView gsrRoomListRecylerView;

    private Labs mLabs;

    ArrayList<String> gsrLocationsArray = new ArrayList<String>();

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
                    String[] asyncTaskParams = { Integer.toString(location), dateBooking, startTime, endTime};
                    new getHours().execute(asyncTaskParams);
                }

            }

        });

        /**
         *
         *
         * END on click functions for buttons
         *
         */

        //load initial data
        loadInitialData();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.gsr);
        ((MainActivity) getActivity()).setNav(R.id.nav_gsr);
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
    public class getHours extends AsyncTask<String, Void, String[]> {

        protected void onPreExecute(){}

        //interacts with penn's servers using a post request
        protected String[] doInBackground(String... pParams) {

            try {

                String gsrCode = pParams[0];
                String date = pParams[1];
                String startTime = pParams[2];
                String endTime = pParams[3];


                URL url = new URL("http://libcal.library.upenn.edu/process_roombookings.php?m=calscroll&gid=" + gsrCode + "&date=" + date);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Referer","http://libcal.library.upenn.edu/booking/vpdlc");
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    //put the information into a string builder
                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        total.append(line).append('\n');
                    }

                    //return response and inputs for onPost
                    in.close();
                    String[] returnArray = {total.toString(), gsrCode, date, startTime, endTime};
                    return returnArray;

                }
                else {
                    String[] returnArray = {"failed"};
                    return returnArray;
                }
            }
            catch(Exception e){
                String[] returnArray = {"failed"};
                return returnArray;
            }

        }

        //take the html, parse it, and populate recycler views
        @Override
        protected void onPostExecute(String[] result) {
            if (result.length > 1) {
                String gsrCode = result[1];
                String dayOfWeek = result[2];
                String startTime = result[3];
                String endTime = result[4];

                Document doc = Jsoup.parse(result[0]);
                //each time block has associated tag below:
                Elements elements = doc.body().select("a.lc_rm_a");

                if (elements.size() == 0) {

                    Toast.makeText(getActivity(), "No GSRs available", Toast.LENGTH_LONG).show();

                    //populate recyclerview
                    LinearLayoutManager gsrRoomListLayoutManager = new LinearLayoutManager(getContext());
                    gsrRoomListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    gsrRoomListRecylerView.setLayoutManager(gsrRoomListLayoutManager);
                    gsrRoomListRecylerView.setAdapter(new GsrBuildingAdapter(getContext(), mGSRS, gsrCode));

                } else {
                    for (Element element : elements) {

                        String element_entry = element.attr("onclick") + "\n";
                        String[] parsed_data = parseEntry(element_entry).split("&");
                        String gsrName = parsed_data[0].replace("'", "");
                        String dateTime = parsed_data[1].replace("'", "");
                        String elementId = element.attr("id") + "\n";

                        //parse datetime further
                        String[] dateDataBrokenUp = parseEntry(element_entry).split(",");
                        String timeRange = dateDataBrokenUp[0].split("&")[1];
                        String dayDate = dateDataBrokenUp[1];
                        String dateNum = dateDataBrokenUp[2];
                        String duration = parsed_data[2].replace("'", "");

                        String AMPM = "";
                        if (timeRange.contains("AM")) {
                            AMPM = "AM";
                        }
                        else {AMPM = "PM";}

                        //time block times
                        String localStartTime = timeRange.split("-")[0].replace("'", "");
                        String localEndTime = timeRange.split("-")[1];

                        Boolean startDateCondition = false;
                        Boolean endDateCondition = false;

                        //convert all times
                        //local is for time block and global refers to the user's parameters' times
                        SimpleDateFormat localFormat = new SimpleDateFormat("hh:mmaa");
                        SimpleDateFormat globalFormat = new SimpleDateFormat("hh:mm aa");

                        try {
                            //now convert all times to date objects to compare
                            Date localStartDate = localFormat.parse(localStartTime);
                            Date globalStartDate = globalFormat.parse(startTime);
                            Date localEndDate = localFormat.parse(localEndTime);
                            Date globalEndDate = globalFormat.parse(endTime);

                            //now compare
                            Calendar calendarLocalStart = Calendar.getInstance();
                            Calendar calendarGlobalStart = Calendar.getInstance();
                            Calendar calendarLocalEnd = Calendar.getInstance();
                            Calendar calendarGlobalEnd = Calendar.getInstance();

                            calendarLocalEnd.setTime(localEndDate);

                            calendarGlobalEnd.setTime(globalEndDate);

                            calendarLocalStart.setTime(localStartDate);

                            calendarGlobalStart.setTime(globalStartDate);

                            //if end time is greater than time block's end time
                            if (calendarGlobalEnd.getTimeInMillis() - calendarLocalEnd.getTimeInMillis() >= 0 )
                            {
                                endDateCondition = true;

                            }

                            //if start time is less than time start end time
                            if ((calendarLocalStart.after(calendarGlobalStart)) || calendarLocalStart.equals(calendarGlobalStart)) {
                                startDateCondition = true;

                            }


                        } catch (ParseException e) {
                        }

                        if (startDateCondition && endDateCondition) {
                            //now populate mGSRs
                            insertGSRSlot(gsrName, dateTime, timeRange, dayDate, dateNum, duration, elementId);
                        }

                    }

                    //now change the ui

                    if (mGSRS.size() == 0)
                    {
                        Toast.makeText(getActivity(), "No GSRs available", Toast.LENGTH_LONG).show();
                    }

                    LinearLayoutManager gsrRoomListLayoutManager = new LinearLayoutManager(getContext());
                    gsrRoomListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    gsrRoomListRecylerView.setLayoutManager(gsrRoomListLayoutManager);
                    gsrRoomListRecylerView.setAdapter(new GsrBuildingAdapter(getContext(), mGSRS, gsrCode));

                    //reset var
                    mGSRS = new ArrayList<GSR>();

                }
            } else {
                Toast.makeText(getActivity(), "Failed to retrieve GSR data", Toast.LENGTH_LONG).show();
            }
        }
    }

    //this function clicks the search button to load initial results on the screen
    public void loadInitialData() {
//        searchGSR.performClick();
        searchGSR.setPressed(true);
        searchGSR.invalidate();
        searchGSR.setPressed(false);
        searchGSR.invalidate();
    }


    //helper function to parse the HTML response
    public String parseEntry(String gsr_entry) {
        //get the name of gsr
        Pattern p = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
        Matcher m = p.matcher(gsr_entry);
        String final_return = "";
        while(m.find()) {
            if (final_return.equals("")) {
                final_return = m.group();
            }
            else {
                final_return = final_return + "&" + m.group();
            }
        }
        return final_return;

    }

    //function that takes all available GSR sessions and populates mGSRs
    public void insertGSRSlot(String gsrName, String GSRTimeRange, String GSRDateTime,
                              String GSRDayDate, String GSRDateNum, String GSRDuration, String GSRElementId) {

        boolean encountered = false;

        for(int i=0; i<mGSRS.size(); i++) {
            GSR currentGSR = mGSRS.get(i);
            //if there is GSR, add the available session to the GSR Object
            if (currentGSR.getGsrName().equals(gsrName)) {
                currentGSR.addGSRSlot(GSRTimeRange, GSRDateTime, GSRDayDate, GSRDateNum, GSRDuration, GSRElementId);
                encountered = true;
            }
        }
        //can't find existing GSR. Create new object
        if (encountered == false) {
            GSR newGSRObject = new GSR(gsrName, GSRTimeRange, GSRDateTime, GSRDayDate, GSRDateNum, GSRDuration, GSRElementId);
            mGSRS.add(newGSRObject);
        }
    }

}
