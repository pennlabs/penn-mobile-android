package com.pennapps.labs.pennmobile;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.pennapps.labs.pennmobile.classes.GSR;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by MikeD on 9/24/2017.
 */

public class gsrFragment extends Fragment {

    ArrayList<GSR> mGSRS = new ArrayList<GSR>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).closeKeyboard();
        getActivity().setTitle(R.string.gsr);

        //execute Asynctask to get hours
        new getHours().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gsr, container, false);
        Button calendarButton = (Button) v.findViewById(R.id.select_date);
        Button startButton = (Button) v.findViewById(R.id.select_start_time);
        Button endButton = (Button) v.findViewById(R.id.select_end_time);

        // Get calendar time and date
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int ampm = calendar.get(Calendar.AM_PM);

        // Determine month, day of week, AM/PM
        String monthName = getMonthName(month);
        String date = getDayOfWeek(dayOfWeek) + ", " + monthName + " " + day + ", " + year;
        calendarButton.setText(date);

        // Set default start/end times for GSR booking
        String[] ampmTimes = getStartEndTimes(hour, minutes, ampm);
        startButton.setText(ampmTimes[0]);
        endButton.setText(ampmTimes[1]);

        // Set up recycler view for list of GSR rooms
        RecyclerView gsrRoomListRecylerView = (RecyclerView) v.findViewById(R.id.gsr_rooms_list);
        LinearLayoutManager gsrRoomListLayoutManager = new LinearLayoutManager(getContext());
        gsrRoomListLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gsrRoomListRecylerView.setLayoutManager(gsrRoomListLayoutManager);
        gsrRoomListRecylerView.setAdapter(new GsrBuildingAdapter(getContext()));

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.gsr);
        ((MainActivity) getActivity()).setNav(R.id.nav_gsr);
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
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return "Sat";
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


    public class getHours extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("http://libcal.library.upenn.edu/process_roombookings.php?m=calscroll&date=Saturday&gid=1722");

                //post request is needed to get available rooms
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("m", "calscroll");
                postDataParams.put("date", "Saturday");
                postDataParams.put("gid", "1722");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Referer","http://libcal.library.upenn.edu/booking/vpdlc");
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                //get the response
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();

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
                    return total.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {

            //parse the data
            Document doc = Jsoup.parse(result);
            //just return the names of all attributes
            Elements elements = doc.body().select("a.lc_rm_a");

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

            //test the get information
            String test_string = mGSRS.get(1).getAvailableGSRSlots().get(1).getElementId();
            Toast.makeText(getActivity(), "First ID session of first GSR: " + test_string,
                    Toast.LENGTH_LONG).show();

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
        for(int i=0; i<mGSRS.size(); i++) {
            GSR currentGSR = mGSRS.get(i);
            //if there is GSR, add the available session to the GSR Object
            if (currentGSR.getGsrName().equals(gsrName)) {
                currentGSR.addGSRSlot(GSRTimeRange, GSRDateTime, GSRDayDate, GSRDateNum, GSRDuration, GSRElementId);
            }
        }
        //can't find existing GSR. Create new object
        GSR newGSRObject = new GSR(gsrName, GSRTimeRange, GSRDateTime, GSRDayDate, GSRDateNum, GSRDuration, GSRElementId);
        mGSRS.add(newGSRObject);
    }

}
