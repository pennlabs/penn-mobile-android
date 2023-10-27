package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HomeCourse {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("start_time")
    @Expose
    var start_time: String? = null
    @SerializedName("end_time")
    @Expose
    var end_time: String? = null

/*    Example:
    "building": "TOWNE",
    "code": "240",
    "dept": "CIS",
    "end_date": "2019-12-09",
    "end_time": "6:00 PM",
    "instructors": [
    "Camillo Jose Taylor"
    ],
    "meeting_times": null,
    "name": "Intro To Comp Systems",
    "room": "100",
    "section": "001",
    "start_date": "2019-08-27",
    "start_time": "4:30 PM",
    "term": "2019C",
    "weekdays": "MW"
 */
}