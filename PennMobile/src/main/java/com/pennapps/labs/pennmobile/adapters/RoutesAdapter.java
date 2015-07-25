package com.pennapps.labs.pennmobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pennapps.labs.pennmobile.R;
import com.pennapps.labs.pennmobile.TransitFragment;
import com.pennapps.labs.pennmobile.classes.BusRoute;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RoutesAdapter extends ArrayAdapter<BusRoute> {

    private LayoutInflater inflater;
    List<BusRoute> routes;
    Context context;
    Set<BusRoute> selectedRoutes;

    public RoutesAdapter(Context context, List<BusRoute> routes) {
        super(context, R.layout.route_list_item, routes);
        this.routes = routes;
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.selectedRoutes = TransitFragment.selectedRoutes;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final BusRoute busRoute = getItem(position);
        if (view == null) {
            view = inflater.inflate(R.layout.route_list_item, parent, false);
        }
        ((TextView) view.findViewById(R.id.routes_name)).setText(busRoute.route_name);
        final Button button = (Button) view.findViewById(R.id.routes_checkbox);
        updateCheckbox(busRoute, button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitFragment.toggleRouteSelection(busRoute);
                updateCheckbox(busRoute, v);
            }
        });
        button.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                button.setLayoutParams(new LinearLayout.LayoutParams(button.getMeasuredHeight(), button.getMeasuredHeight()));
            }
        });
        return view;
    }

    private void updateCheckbox(BusRoute busRoute, View v) {
        Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        RectF rectf = new RectF(15, 15, 60, 60);
        Paint paint = new Paint();
        if (selectedRoutes.contains(busRoute)) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(busRoute.getColor());
            canvas.drawRoundRect(rectf, 10, 10, paint);
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rectf, 10, 10, paint);
        BitmapDrawable drawable = new BitmapDrawable(v.getResources(), bitmap);
        v.setBackground(drawable);
    }
}