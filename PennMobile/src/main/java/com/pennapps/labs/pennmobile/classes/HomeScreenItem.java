package com.pennapps.labs.pennmobile.classes;

/**
 * Created by Jackie on 2018-03-04.
 */

public class HomeScreenItem {
    private String name;
    private int viewType;

    public HomeScreenItem(String name, int viewType) {
        this.name = name;
        this.viewType = viewType;
    }

    public String getName() {
        return name;
    }

    public int getViewType() {
        return viewType;
    }

    @Override
    public String toString() {
        return name;
    }
}
