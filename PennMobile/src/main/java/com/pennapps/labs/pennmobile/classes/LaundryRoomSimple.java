package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jackie on 2017-10-21.
 */

public class LaundryRoomSimple {

    @SerializedName("hall_name")
    @Expose
    public String name;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("location")
    @Expose
    public String location;

}
