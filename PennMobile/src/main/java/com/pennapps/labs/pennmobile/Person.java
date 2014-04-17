package com.pennapps.labs.pennmobile;


import android.util.Log;

import org.apache.commons.lang3.text.WordUtils;

public class Person {

    private String affiliation;
    private String email;
    // private String name;
    private String first_name;
    private String last_name;
    private String phone;
    private String organization;
    private String title_or_major;

    public static class Builder {
        // required
        // private String name;
        private String first_name;
        private String last_name;
        private String affiliation;

        // optional
        private String phone = "";
        private String email = "";
        private String organization = "";
        private String title_or_major = "";

        public Builder(String name, String affiliation) {
            int firstComma = name.indexOf(",");
            if (name.contains("Dr")) Log.v("vivlabs", "name");
            this.first_name = WordUtils.capitalizeFully(name.substring(firstComma + 1).trim());
            this.last_name = WordUtils.capitalizeFully(name.substring(0, firstComma).trim());
            this.affiliation = affiliation;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Person build() {
            return new Person(this);
        }

    }

    private Person(Builder builder) {
        // name           = builder.name;
        first_name     = builder.first_name;
        last_name      = builder.last_name;
        phone          = builder.phone;
        email          = builder.email;
        affiliation    = builder.affiliation;
        organization   = builder.organization;
        title_or_major = builder.title_or_major;
    }

    public String getFirstName() {
        // return name;
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAffiliation() {
        return affiliation;
    }
}
