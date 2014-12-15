package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.ArrayList;

public class DirectoryAdapter extends ArrayAdapter<Person> {

    public DirectoryAdapter(Context context, ArrayList<Person> persons) {
        super(context, R.layout.directory_list_item, persons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Person person = getItem(position);
        final Person currentPerson = person;
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.directory_list_item, null);

        TextView tvName = (TextView) view.findViewById(R.id.tv_person_name);
        TextView tvAffiliation = (TextView) view.findViewById(R.id.tv_person_affiliation);
        TextView tvEmail = (TextView) view.findViewById(R.id.tv_person_email);
        TextView tvPhone = (TextView) view.findViewById(R.id.tv_person_phone);
        ImageView contact = (ImageView) view.findViewById(R.id.contact_icon);

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                String name = currentPerson.getFirstName() + " " + currentPerson.getLastName();

                intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, currentPerson.getPhone());
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, currentPerson.getEmail());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getContext().startActivity(intent);
            }
        });

        tvName.setText(person.getFirstName() + " " + person.getLastName());
        tvAffiliation.setText(person.getAffiliation());

        if (person.getEmail().length() == 0) {
            tvEmail.setVisibility(View.GONE);
        } else {
            tvEmail.setText(person.getEmail());
            tvEmail.setPaintFlags(tvEmail.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            tvEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "mailto:" + currentPerson.getEmail();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
        }

        if (person.getPhone().length() == 0) {
            tvPhone.setVisibility(View.GONE);
        } else {
            tvPhone.setText(person.getPhone());
            tvPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + currentPerson.getPhone();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
        }

        return view;
    }

}
