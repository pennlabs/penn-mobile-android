package com.pennapps.labs.pennmobile;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
    public void onBindViewHolder(GsrRoomHolder holder, int position) {
        if (position < getItemCount()) {

            final String localGSRID = ids.get(position);

            holder.gsr_time.setText(times.get(position).substring(0, times.get(position).indexOf(",")));
            holder.gsrId.setText(ids.get(position));
            holder.locationId.setText(gsrLocationCode);
            //whenever a time slot is clicked
            holder.gsr_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, bookGSRActivity.class);
                    intent.putExtra("gsrID", localGSRID);
                    intent.putExtra("gsrLocationCode", gsrLocationCode);
                    context.startActivity(intent);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return times.size();
    }
}
