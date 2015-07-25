package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DirectoryAdapter extends ArrayAdapter<Person> {
    private final LayoutInflater inflater;

    public DirectoryAdapter(Context context, List<Person> persons) {
        super(context, R.layout.directory_list_item, persons);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.directory_list_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        Person person = getItem(position);
        final Person currentPerson = person;

        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                String name = currentPerson.getName();
                intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, currentPerson.phone);
                intent.putExtra(ContactsContract.Intents.Insert.EMAIL, currentPerson.email);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                getContext().startActivity(intent);
            }
        });

        holder.tvName.setText(person.getName());
        holder.tvAffiliation.setText(person.affiliation);

        if (person.email.length() == 0) {
            holder.tvEmail.setVisibility(View.GONE);
        } else {
            holder.tvEmail.setText(person.email);
            holder.tvEmail.setPaintFlags(holder.tvEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.tvEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "mailto:" + currentPerson.email;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
        }

        if (person.phone.length() == 0) {
            holder.tvPhone.setVisibility(View.GONE);
        } else {
            holder.tvPhone.setText(person.phone);
            holder.tvPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = "tel:" + currentPerson.phone;
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        Set<String> starredContacts = sharedPref.getStringSet("starred", new HashSet<String>());
        holder.star.setChecked(starredContacts.contains(currentPerson.getName()));
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                Set<String> starredContacts = sharedPref.getStringSet("starred", new HashSet<String>());
                SharedPreferences.Editor editedPreferences = sharedPref.edit();
                ToggleButton star = (ToggleButton) v;
                boolean starred = star.isChecked();
                String currentName = currentPerson.getName();
                if (starred) {
                    System.out.println("Starred " + currentPerson.getName());
                    if (currentName != null) {
                        starredContacts.add(currentName);
                    }
                } else {
                    System.out.println("Unstarred " + currentPerson.getName());
                    starredContacts.remove(currentName);
                }
                editedPreferences.putStringSet("starred", starredContacts);
                editedPreferences.apply();
            }
        });

        return view;
    }

    static class ViewHolder {
        @Bind(R.id.tv_person_name) TextView tvName;
        @Bind(R.id.tv_person_affiliation) TextView tvAffiliation;
        @Bind(R.id.tv_person_email) TextView tvEmail;
        @Bind(R.id.tv_person_phone) TextView tvPhone;
        @Bind(R.id.star_contact) ToggleButton star;
        @Bind(R.id.contact_icon) ImageView contact;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
