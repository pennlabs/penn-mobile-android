package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MikeD on 2/10/2018.
 */

public class GSRRoom {

    @SerializedName("capacity")
    @Expose
    public Integer capacity;

    @SerializedName("gid")
    @Expose
    public Integer gid;


    @SerializedName("lid")
    @Expose
    public Integer lid;


    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("room_id")
    @Expose
    public Integer room_id;

    @SerializedName("thumbnail")
    @Expose
    public String thumbnail;


    @SerializedName("times")
    @Expose
    public GSRSlot[] slots;

    //getters


    public Integer getCapacity() {
        return capacity;
    }

    public Integer getGid() {
        return gid;
    }

    public Integer getLid() {
        return lid;
    }

    public String getName() {
        return name;
    }

    public Integer getRoom_id() {
        return room_id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public GSRSlot[] getSlots() {
        return slots;
    }
}
