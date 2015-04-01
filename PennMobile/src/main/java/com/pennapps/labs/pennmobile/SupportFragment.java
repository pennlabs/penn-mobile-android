package com.pennapps.labs.pennmobile;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.classes.Person;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link ListFragment} subclass.
 */
public class SupportFragment extends ListFragment {

    private ListView mListView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        mListView = getListView();
        List<Person> contacts_list = new ArrayList<>();
        contacts_list.add(new Person("Penn Police General", "215 898-7297"));
        contacts_list.add(new Person("Penn Police Emergencies/MERT", "215 573-3333"));
        contacts_list.add(new Person("Penn Walk", "215-898-9255", "215-898-WALK"));
        contacts_list.add(new Person("Penn Ride", "215-898-7433", "215-898-RIDE"));
        contacts_list.add(new Person("Help Line", "215-898-4357", "215-898-HELP"));

        SupportAdapter supportAdapter = new SupportAdapter(getActivity().getApplicationContext(), contacts_list);

        mListView.setAdapter(supportAdapter);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_support, container, false);
    }

    private class SupportAdapter extends ArrayAdapter<Person> {

        public SupportAdapter(Context context, List<Person> contacts) {
            super(context, R.layout.support_list_item, contacts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Person currentPerson = getItem(position);

            View view = LayoutInflater.from(getContext())
                    .inflate(R.layout.support_list_item, null);

            TextView name = (TextView) view.findViewById(R.id.support_name);
            TextView phone = (TextView) view.findViewById(R.id.support_phone);

            name.setText(currentPerson.name);
            if (currentPerson.phone_words.equals("")) {
                phone.setText(currentPerson.phone);
            } else {
                phone.setText(currentPerson.phone_words + " (" + currentPerson.phone + ")");
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + currentPerson.phone;
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });

            return view;
        }

    }


}
