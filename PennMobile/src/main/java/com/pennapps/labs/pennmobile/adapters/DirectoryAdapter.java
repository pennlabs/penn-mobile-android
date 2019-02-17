package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;
import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DirectoryAdapter extends RecyclerView.Adapter<DirectoryAdapter.DirectoryViewHolder> {
    private List<Person> persons;
    private Context mContext;

    public DirectoryAdapter(Context context, List<Person> persons) {
        this.persons = persons;
        mContext = context;
    }

    public static class DirectoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_person_name) TextView tvName;
        @BindView(R.id.tv_person_affiliation) TextView tvAffiliation;
        @BindView(R.id.tv_person_email) TextView tvEmail;
        @BindView(R.id.star_contact) ToggleButton star;
        @BindView(R.id.contact_icon) ImageView contact;

        public DirectoryViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public DirectoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DirectoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.directory_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DirectoryViewHolder holder, int position) {
        View view = holder.itemView;
        SwipeLayout swipeLayout = view.findViewById(R.id.directory_swipe);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.directory_swipe_drawer));

        Person person = this.persons.get(position);
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

                mContext.startActivity(intent);
            }
        });

        holder.tvName.setText(person.getName());
        holder.tvAffiliation.setText(person.getAffiliation());

        if (person.email.length() == 0) {
            holder.tvEmail.setVisibility(View.GONE);
        } else {
            holder.tvEmail.setText(person.getEmail());
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


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        Set<String> starredContacts = sharedPref.getStringSet(mContext.getResources().getString(R.string.search_dir_star), new HashSet<String>());
        holder.star.setChecked(starredContacts.contains(currentPerson.name));
        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                Set<String> buffer = sharedPref.getStringSet(mContext.getResources().getString(R.string.search_dir_star), new HashSet<String>());
                Set<String> starredContacts = new HashSet<>(buffer);
                SharedPreferences.Editor editedPreferences = sharedPref.edit();
                ToggleButton star = (ToggleButton) v;
                boolean starred = star.isChecked();
                String currentName = currentPerson.name;
                if (starred) {
                    if (currentName != null) {
                        starredContacts.add(currentName);
                        editedPreferences.putString(currentName + mContext.getResources().getString(R.string.search_dir_star),
                                getDataString(currentPerson));
                    }
                } else {
                    starredContacts.remove(currentName);
                    if (currentName != null) {
                        editedPreferences.remove(currentName + mContext.getResources().getString(R.string.search_dir_star));
                    }
                }
                editedPreferences.putStringSet(mContext.getResources().getString(R.string.search_dir_star), starredContacts);
                editedPreferences.apply();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.persons.size();
    }

    private String getDataString(Person currentPerson){
        return (new Gson()).toJson(currentPerson, Person.class);
    }

}
