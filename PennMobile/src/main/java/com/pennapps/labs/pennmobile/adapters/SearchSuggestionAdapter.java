package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;

import java.util.List;

/**
 * Created by Jason on 2/8/2016.
 */
public class SearchSuggestionAdapter extends ArrayAdapter<String> {

    private Context context;

    public SearchSuggestionAdapter(Context context, List<String> list) {
        super(context, R.layout.search_suggestion_item, list);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.search_suggestion_item, parent, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.search_item_text);
        textView.setText(getItem(position));
        return view;
    }
}
