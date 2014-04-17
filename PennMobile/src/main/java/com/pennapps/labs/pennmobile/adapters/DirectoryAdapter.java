package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.Person;
import com.pennapps.labs.pennmobile.R;

import java.util.ArrayList;

public class DirectoryAdapter extends ArrayAdapter<Person> {

    public DirectoryAdapter(Context context, ArrayList<Person> persons) {
        super(context, R.layout.directory_list_item, persons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Person person = getItem(position);
        View view = convertView;

        view = LayoutInflater.from(getContext())
                .inflate(R.layout.directory_list_item, null);

        TextView tvName = (TextView) view.findViewById(R.id.tv_person_name);
        TextView tvAffiliation = (TextView) view.findViewById(R.id.tv_person_affiliation);
        TextView tvEmail = (TextView) view.findViewById(R.id.tv_person_email);
        TextView tvPhone = (TextView) view.findViewById(R.id.tv_person_phone);

        tvName.setText(person.getFirstName() + " " + person.getLastName());
        tvAffiliation.setText(person.getAffiliation());

        if (person.getEmail().length() == 0) {
            ((ViewGroup) view).removeView(tvEmail);
        } else {
            tvEmail.setText(person.getEmail());
        }

        if (person.getPhone().length() == 0) {
            ((ViewGroup) view).removeView(tvPhone);
        } else {
            tvPhone.setText(person.getPhone());
            final Person currentPerson = person;
            tvPhone.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String uri = "tel:" + currentPerson.getPhone();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                    return true;
                }
            });
        }

        return view;
    }

}
