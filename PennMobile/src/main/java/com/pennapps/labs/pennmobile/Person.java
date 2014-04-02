package com.pennapps.labs.pennmobile;


public class Person {

    private String affiliation;
    private String email;
    private String name;
    private String phone;
    private String organization;
    private String title_or_major;

    public static class Builder {
        // required
        private String name;
        private String affiliation;

        // optional
        private String phone = "";
        private String email = "";
        private String organization = "";
        private String title_or_major = "";

        public Builder(String name, String affiliation) {
            this.name = name;
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
        name           = builder.name;
        phone          = builder.phone;
        email          = builder.email;
        affiliation    = builder.affiliation;
        organization   = builder.organization;
        title_or_major = builder.title_or_major;
    }

    public String getName() {
        return name;
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
