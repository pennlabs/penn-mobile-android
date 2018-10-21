package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.classes.Person;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneSaveAdapter extends ArrayAdapter<Person> {

    private final LayoutInflater inflater;
    private final List<Person> selections;

    public PhoneSaveAdapter(@NonNull Context context, List<Person> contacts, List<Person> s) {
        super(context, R.layout.phone_save_list_item, contacts);
        inflater = LayoutInflater.from(context);
        selections = s;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        final Person currentPerson = getItem(pos);
        final PhoneSaveAdapter.ViewHolder holder;
        if (view != null) {
            holder = (PhoneSaveAdapter.ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.phone_save_list_item, parent, false);
            holder = new PhoneSaveAdapter.ViewHolder(view);
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
                if (holder.radio.isChecked()) {
                    holder.radio.setChecked(false);
                    for (Person p : selections) {
                        if (p.name.equals(holder.name.getText().toString())) {
                            selections.remove(p);
                            break;
                        }
                    }
                } else {
                    holder.radio.setChecked(true);
                    selections.add(new Person(holder.name.getText().toString(), holder.phone.getText().toString()));
                }
            }
        });

        return view;
    }

    public static class ViewHolder {
        @BindView(R.id.support_name) TextView name;
        @BindView(R.id.support_phone) TextView phone;
        @BindView(R.id.support_phone_icon) ImageView icon;
        @BindView(R.id.phone_save_radiobutton) RadioButton radio;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
