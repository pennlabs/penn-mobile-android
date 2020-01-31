package com.pennapps.labs.pennmobile.classes;

import androidx.annotation.NonNull;

/**
 * Created by Jackie on 2018-03-04.
 */

public class HomeScreenItem implements Comparable<HomeScreenItem> {
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

    @Override
    public int compareTo(@NonNull HomeScreenItem homeScreenItem) {
        return name.compareTo(homeScreenItem.name);
    }
}
