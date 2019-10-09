package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

data class User(val first: String?, val last: String?, val email: String?, val pennkey: String?, val degrees : Set<Degree>?, val courses : Set<Course>?) {

    @SerializedName("first_name")
    var mFirstName : String? = first

    @SerializedName("last_name")
    var mLastName : String? = last

    @SerializedName("email")
    var mEmail : String? = email

    @SerializedName("pennkey")
    var mPennKey : String? = pennkey

    @SerializedName("degrees")
    var mDegrees : Set<Degree>? = degrees

    @SerializedName("courses")
    var mCourses : Set<Course>? = courses

    fun parse(html: String) : User{
        return User(mFirstName, mLastName, mEmail, mPennKey, mDegrees, mCourses)
    }

}