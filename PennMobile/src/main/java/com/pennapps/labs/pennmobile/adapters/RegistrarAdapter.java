package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;

public class RegistrarAdapter extends ResourceCursorAdapter {

    public RegistrarAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Log.v("vivlabs", Arrays.toString(cursor.getColumnNames()));

        TextView courseId = (TextView) view.findViewById(R.id.course_id_text);
        courseId.setText(cursor.getString(cursor.getColumnIndex("course_id")));

        TextView courseInstr = (TextView) view.findViewById(R.id.course_instr_text);
        courseInstr.setText(cursor.getString(cursor.getColumnIndex("instructor")));

        TextView courseTitle = (TextView) view.findViewById(R.id.course_title_text);
        courseTitle.setText(cursor.getString(cursor.getColumnIndex("course_title")));

        view.setTag(cursor.getString(cursor.getColumnIndex("course_id")));
    }
}