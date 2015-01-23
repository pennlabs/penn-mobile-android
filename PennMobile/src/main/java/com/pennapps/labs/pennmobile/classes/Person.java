package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by Adel on 12/16/14.
 * Class for a Person from Directory
 */
public class Person {
    @SerializedName("person_id") public String id;
    @SerializedName("list_name") public String name;
    @SerializedName("list_affiliation") public String affiliation;
    @SerializedName("list_email") public String email;
    @SerializedName("list_organization") public String organization;
    @SerializedName("list_phone") public String phone;
    @SerializedName("list_phone_words") public String phone_words;
    @SerializedName("list_title_or_major") public String title_or_major;

    public Person(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.phone_words = "";
    }

    public Person(String name, String phone, String phone_words) {
        this.name = name;
        this.phone = phone;
        this.phone_words = phone_words;
    }

    public String getName() {
        int firstComma = name.indexOf(",");
        return WordUtils.capitalizeFully(name.substring(firstComma + 1).trim() + " " + name.substring(0, firstComma).trim());
    }
}
