package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SupportAdapter extends ArrayAdapter<Person> {
    private final LayoutInflater inflater;

    public SupportAdapter(Context context, List<Person> contacts) {
        super(context, R.layout.support_list_item, contacts);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final Person currentPerson = getItem(position);
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.support_list_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.name.setText(currentPerson.name);
        if (currentPerson.phone_words.equals("")) {
            holder.phone.setText(currentPerson.phone);
        } else {
            holder.phone.setText(currentPerson.phone_words + " (" + currentPerson.phone + ")");
        }

        if (currentPerson.isURL()) {
            holder.icon.setVisibility(View.GONE);
        } else {
            holder.icon.setVisibility(View.VISIBLE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (currentPerson.isURL()) {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(currentPerson.phone));
                } else {
                    String uri = "tel:" + currentPerson.phone;
                    intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        return view;
    }

    public static class ViewHolder {
        @BindView(R.id.support_name) TextView name;
        @BindView(R.id.support_phone) TextView phone;
        @BindView(R.id.support_phone_icon) ImageView icon;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
