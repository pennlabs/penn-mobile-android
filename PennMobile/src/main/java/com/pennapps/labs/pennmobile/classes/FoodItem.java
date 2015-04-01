package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Adel on 12/18/14.
 * Class for Food items in Dining menus
 */
public class FoodItem {
    @SerializedName("txtTitle") String title;
    @SerializedName("txtDescription") String description;
}
