package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FlingEvent {
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("end_time")
    @Expose
    public String endTime;
    @SerializedName("facebook")
    @Expose
    public String facebook;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("start_time")
    @Expose
    public String startTime;
    @SerializedName("website")
    @Expose
    public String website;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("description: ");
        if (description != null) {
            stringBuilder.append(description);
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(", email: ");
        if (email != null) {
            stringBuilder.append(email);
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(", end_time: ");
        if (endTime != null) {
            stringBuilder.append(endTime);
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(", facebook: ");
        if (facebook != null) {
            stringBuilder.append(facebook);
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(", imageUrl: ");
        if (imageUrl != null) {
            stringBuilder.append(imageUrl);
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(", name: ");
        if (name != null) {
            stringBuilder.append(name);
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(", startTime: ");
        if (startTime != null) {
            stringBuilder.append(startTime);
        } else {
            stringBuilder.append("null");
        }
        stringBuilder.append(", website: ");
        if (website != null) {
            stringBuilder.append(website);
        } else {
            stringBuilder.append("null");
        }
        return stringBuilder.toString();
    }
}
