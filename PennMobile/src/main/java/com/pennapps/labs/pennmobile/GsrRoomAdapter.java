package com.pennapps.labs.pennmobile;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

            //holder.gsrTime.setText(times.get(position).substring(0, times.get(position).indexOf(",")));
            holder.gsrTime.setText(times.get(position));
            holder.gsrId.setText(ids.get(position));
            holder.locationId.setText(gsrLocationCode);
            //whenever a time slot is clicked

            holder.gsrTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, BookGSRActivity.class);
                    intent.putExtra("gsrID", localGSRID);
                    intent.putExtra("gsrLocationCode", gsrLocationCode);
                    intent.putExtra("startTime", transformStartTime(holder.gsrTime.getText().toString()));
                    intent.putExtra("endTime", transformEndTime(holder.gsrTime.getText().toString()));
                    context.startActivity(intent);

                }
            });

        }
    }

    String transformStartTime(String inputTime) {
        String start = inputTime.split("-")[0];
        if (start.length() == 4) {
            start = start + "0";
        }
        start = start.replace(":", "");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = df.format(c.getTime());
        return formattedDate + "T" + start + "00+0500";
    }

    String transformEndTime(String inputTime) {
        String start = inputTime.split("-")[1];
        if (start.length() == 4) {
            start = start + "0";
        }
        start = start.replace(":", "");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = df.format(c.getTime());
        return formattedDate + "T" + start + "00+0500";
    }

    @Override
    public int getItemCount() {
        return times.size();
    }
}
