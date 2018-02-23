package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Andrew on 11/5/2017.
 * RecylerView Adapter for individual GSR rooms.
 */

public class GsrRoomAdapter extends RecyclerView.Adapter<GsrRoomHolder> {

    ArrayList<String> times;
    ArrayList<String> ids;
    String gsrLocationCode;
    Context context;

    @Override
    public GsrRoomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gsr_room, parent, false);
        return new GsrRoomHolder(view);
    }

    public GsrRoomAdapter(ArrayList<String> _times, ArrayList<String> _ids, String _gsrLocationCode, Context _context) {
        this.times = _times;
        this.ids = _ids;
        this.gsrLocationCode = _gsrLocationCode;
        this.context = _context;

    }


    @Override
    public void onBindViewHolder(final GsrRoomHolder holder, int position) {
        if (position < getItemCount()) {

            final String localGSRID = ids.get(position);
            String time = times.get(position);
            final String startTime = transformStartTime(time);
            final String endTime = transformEndTime(time);
            holder.gsrStartTime.setText(time.substring(0, time.indexOf("-")));
            holder.gsrEndTime.setText(time.substring(time.indexOf("-") + 1));
            holder.gsrId.setText(ids.get(position));
            holder.locationId.setText(gsrLocationCode);
            //whenever a time slot is clicked

            holder.gsrRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BookGsrFragment bookGsrFragment = BookGsrFragment.newInstance(localGSRID, gsrLocationCode, startTime, endTime);
                    FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, bookGsrFragment)
                            .addToBackStack("GSR Fragment")
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit();

                }
            });

        }
    }

    String transformStartTime(String inputTime) {
        String start = inputTime.split("-")[0];
        start = convertToMilitaryTime(start);
        start = start.replace(":", "");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = df.format(c.getTime());
        return formattedDate + "T" + start + "00+0500";
    }

    String transformEndTime(String inputTime) {
        String end = inputTime.split("-")[1];
        end = convertToMilitaryTime(end);
        end = end.replace(":", "");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = df.format(c.getTime());
        return formattedDate + "T" + end + "00+0500";
    }

    //helper function that turns military to civilian time
    public String convertToMilitaryTime(String input) {
        DateTimeFormatter militaryTimeFormatter = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter civilianTimeFormatter = DateTimeFormat.forPattern("hh:mm a");
        return militaryTimeFormatter.print(civilianTimeFormatter.parseDateTime(input));
    }

    @Override
    public int getItemCount() {
        return times.size();
    }
}
