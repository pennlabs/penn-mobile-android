package com.pennapps.labs.pennmobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

import com.pennapps.labs.pennmobile.classes.GSR;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Mike Abelar on 9/24/2017.
 */

public class gsrFragment extends Fragment {

    ArrayList<GSR> mGSRS = new ArrayList<GSR>();

    RecyclerView gsrRoomListRecylerView;


    TextView instructions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).closeKeyboard();
        getActivity().setTitle(R.string.gsr);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gsr, container, false);
        final Button calendarButton = (Button) v.findViewById(R.id.select_date);
        final Button startButton = (Button) v.findViewById(R.id.select_start_time);
        final Button endButton = (Button) v.findViewById(R.id.select_end_time);
        Button searchGSR = (Button) v.findViewById(R.id.search_GSR);
        final Spinner gsrDropDown = (Spinner) v.findViewById(R.id.gsr_building_selection);
        instructions = (TextView) v.findViewById(R.id.instructions);

        //set dropdown
        String[] gsrs = new String[]{
                "Weigle",
                "VP GSR",
                "Lippincott",
                "Edu Commons",
                "Levin Building",
                "VP Sem. Rooms",
                "Lippincott Sem. Rooms",
                "Glossberg Recording Room",
                //"Dental Sem",
                "Biomedical Lib."
        };
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, gsrs);
        //set the spinners adapter to the previously created one.
        gsrDropDown.setAdapter(adapter);



        // Get calendar time and date
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int ampm = calendar.get(Calendar.AM_PM);


        calendarButton.setText(year + "-" + month + "-" + day);


        // Set default start/end times for GSR booking
        String[] ampmTimes = getStartEndTimes(hour, minutes, ampm);
        startButton.setText(ampmTimes[0]);
        endButton.setText(ampmTimes[1]);

        // Set up recycler view for list of GSR rooms
         gsrRoomListRecylerView = (RecyclerView) v.findViewById(R.id.gsr_rooms_list);



        //on clicks
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
                                if (hourOfDay > 12) {
                                    hourString = Integer.toString(hourOfDay - 12);
                                }


                                //strign version of minute
                                String minuteString = Integer.toString(minute);

                                if (minute < 10) {
                                    minuteString = "0" + minute;
                                }

                                startButton.setText(hourString + ":" + minuteString + " " + AM_PM);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }

        });

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


                                //strign version of minute
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


                                //acount for index
                                int entryMonth = monthOfYear + 1;

                                calendarButton.setText(year + "-" + entryMonth + "-" + dayOfMonth);

                            }
                        }, mYear, mMonth, mDay);

                //set min and max choices

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

        searchGSR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                instructions.setText(getString(R.string.select_intrusctions));

                //get vars
                String dateBooking = calendarButton.getText().toString();
                String startTime = startButton.getText().toString();
                String endTime = endButton.getText().toString();
                int location = mapGSR(gsrDropDown.getSelectedItem().toString());
                if (location == -1) {
                    Toast.makeText(getActivity(), "Sorry, an error has occured", Toast.LENGTH_LONG);
                }
                else {
                    String[] asyncTaskParams = { Integer.toString(location), dateBooking};
                    new getHours().execute(asyncTaskParams);
                }

            }

        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.gsr);
        ((MainActivity) getActivity()).setNav(R.id.nav_gsr);
    }

    //takes the name of the gsr and returns an int for the corresponding code
    public int mapGSR(String name) {
        switch (name) {
            case "Weigle":
                return 1722;
            case "VP GSR":
                return 1799;
            case "Lippincott":
                return 1768;
            case "Edu Commons":
                return 848;
            case "Levin Building":
                return 13489;
            case "VP Sem. Rooms":
                return 4409;
            case "Lippincott Sem. Rooms":
                return 2587;
            case "Glossberg Recording Room":
                return 1819;
            case "Dental Sem":
                return 13532;
            case "Biomedical Lib.":
                return 505;
            default:
                return -1;
        }
    }

    public int mapGSRSlot(String name) {
        if (name.contains("VP WIC")) {return 1722;}
        return -1;
    }

    // Parameters: the starting time's hour, minutes, and AM/PM as formatted by Java.utils.Calendar
    // AM = 0, PM = 1
    // Returns a string array of length 2 where first element is properly formatted starting time
    // Second element is properly formatted ending time, which is one hour after starting time
    public static String[] getStartEndTimes(int hour, int minutes, int ampm) {
        String[] results = new String[2];
        String strampm = (ampm == 0) ? "AM" : "PM";
        // Add 0 if minutes < 10
        if (minutes < 10) {
            results[0] = hour + ":0" + minutes + " " + strampm;
            // Change AM to PM and vice versa if start time hour is 11
            if (hour == 11 && strampm.equals("AM")) {
                results[1] = "12:0" + minutes + " PM";
            } else if (hour == 11 && strampm.equals("PM")) {
                results[1] = "12:0" + minutes + " AM";
            } else {
                results[1] = Integer.toString((hour + 1) % 12) + ":0" + minutes + " " + strampm;
            }
        } else {
            results[0] = hour + ":" + minutes + " " + strampm;
            // Change AM to PM and vice versa if start time hour is 11
            if (hour == 11 && strampm.equals("AM")) {
                results[1] = "12:" + minutes + " PM";
            } else if (hour == 11 && strampm.equals("PM")) {
                results[1] = "12:" + minutes + " AM";
            } else {
                results[1] = Integer.toString((hour + 1) % 12) + ":" + minutes + " " + strampm;
            }
        }
        return results;
    }

    private String getDayOfWeek(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "Saturday";
        }
    }

    private String getMonthName(int month) {
        switch (month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "March";
        }
    }


    public class getHours extends AsyncTask<String, Void, String[]> {

        protected void onPreExecute(){}

        protected String[] doInBackground(String... pParams) {

            try {

                String gsrCode = pParams[0];
                String date = pParams[1];

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

                    //return a string for do in background
                    in.close();
                    String[] returnArray = {total.toString(), gsrCode, date};
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

        @Override
        protected void onPostExecute(String[] result) {


            String gsrCode = result[1];
            String dayOfWeek = result[2];


            //parse the data
            Document doc = Jsoup.parse(result[0]);
            //just return the names of all attributes
            Elements elements = doc.body().select("a.lc_rm_a");

            Log.e("hey", "" + elements.size());

            if (elements.size() == 0) {

                Toast.makeText(getActivity(), "No GSRs available", Toast.LENGTH_LONG).show();
                LinearLayoutManager gsrRoomListLayoutManager = new LinearLayoutManager(getContext());
                gsrRoomListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                gsrRoomListRecylerView.setLayoutManager(gsrRoomListLayoutManager);
                gsrRoomListRecylerView.setAdapter(new GsrBuildingAdapter(getContext(), mGSRS, gsrCode));
            } else {
                for (Element element : elements) {
                    //parse element entry
                    String element_entry = element.attr("onclick") + "\n";

                    //get main attribute data
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


                    //now popular mGSRs
                    insertGSRSlot(gsrName, dateTime, timeRange, dayDate, dateNum, duration, elementId);

                }

                //now change the ui




                LinearLayoutManager gsrRoomListLayoutManager = new LinearLayoutManager(getContext());
                gsrRoomListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                gsrRoomListRecylerView.setLayoutManager(gsrRoomListLayoutManager);
                gsrRoomListRecylerView.setAdapter(new GsrBuildingAdapter(getContext(), mGSRS, gsrCode));

                mGSRS = new ArrayList<GSR>();

            }
        }
    }



    //helper function to parse response
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
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
