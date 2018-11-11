package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Gym;
import com.pennapps.labs.pennmobile.classes.GymHours;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FitnessAdapter extends RecyclerView.Adapter<FitnessAdapter.FitnessViewHolder> {

    private Context mContext;

    // TODO 3: implement Fitness Adapter

    // gym data
    private List<Gym> gyms;

    public FitnessAdapter(Context context, List<Gym> gyms) {
        // get gym data from fragment (which gets it from the api call)
        this.gyms = gyms;
    }

    @NonNull
    @Override
    public FitnessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fitness_list_item, parent, false);
        mContext = parent.getContext();
        return new FitnessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FitnessViewHolder holder, int position) {
        Gym g = gyms.get(position);
        // get the data from Gym class
        String name = g.getName();
        List hours = g.getHours();


        Log.d("ADPFLKDPLKGFNDSPO", position + " " + name);


        // process name to remove hours and dashes and excess stupid stuff
        // String processedName = processName(name); NO MORE NEED

        // if the gym is open or not
        boolean open = g.isOpen();
        // TODO: change to not hardcoded string
        String openClosed = "OPEN";
        if (!open) {
            openClosed = "CLOSED";
            // set background to red for closed
            holder.gymStatusView.setBackgroundResource(R.drawable.label_red);
        }


        // get first word in name
        int i = name.indexOf(' ');
        String simpName = name;
        if (i >= 0) {
            simpName = name.substring(0, i);
        }
        simpName = simpName.toLowerCase();

        // build image resource name from simpName
        String src_name = "gym_" + simpName;

        // get resource identifier
        int identifier = mContext.getResources().getIdentifier(src_name, "drawable", mContext.getPackageName());
        if (identifier == 0) { // if the src name is invalid
            identifier = mContext.getResources().getIdentifier("gym_fox", "drawable", mContext.getPackageName());
            Log.d("ASFJHDPOIFH", "" + identifier);
        }


        // update ViewHolder
        // TODO: change image view based on gym
        holder.gymImageView.setImageResource(identifier);
        holder.gymNameView.setText(name);
        holder.gymStatusView.setText(openClosed);
        holder.gymHoursView.setText(intervalsToString(hours));
    }


    // turn list of hours into string
    private static String intervalsToString(List<Interval> hours) {
        Interval i1 = hours.get(0);
        // first check if it's all day
        if (i1.equals(GymHours.allDayInterval)) {
            return "";
        }
        // otherwise add intervals to String
        StringBuilder sb = new StringBuilder();
        sb.append(intervalToString(i1));
        for (int ii = 1; ii < hours.size(); ii++) {
            Interval interval = hours.get(ii);
            sb.append(" | ");
            sb.append(intervalToString(interval));
        }

        return sb.toString();
    }

    private static String intervalToString(Interval interval) {
        DateTime d1 = interval.getStart();
        DateTime d2 = interval.getEnd();
        return d1.toString("h:mm a") + " - " + d2.toString("h:mm a");
    }

    // HELPER FUNCTION: process name string
    /*private String processName(String name) {
        String[] splits = name.split("\\s");
        StringBuilder sb = new StringBuilder();
        for (String s: splits) {
            if (s.contains("-")) {
                sb.append(s.split("-")[0]);
                break;
            } else if (s.equalsIgnoreCase("hours")) {
                break;
            }
            sb.append(s);
        }
        return sb.toString();
    }*/

    @Override
    public int getItemCount() {
        Log.d("ADPFLKDPLKGFNDSPO", "" + gyms.size());
        return gyms.size();
    }

    public class FitnessViewHolder extends RecyclerView.ViewHolder {

        // TODO 2: define FitnessViewHolder
        @BindView(R.id.gym_image_view)
        public ImageView gymImageView;
        @BindView(R.id.gym_name_view)
        public TextView gymNameView;
        @BindView(R.id.gym_status_view)
        public TextView gymStatusView;
        @BindView(R.id.gym_hours_view)
        public TextView gymHoursView;

        public FitnessViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
