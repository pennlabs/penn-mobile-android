package com.pennapps.labs.pennmobile.api


class PennInTouchNetworkManager {

    fun getStudent() {

    }

    fun getDegrees(){

    }

    fun getCourses(){

    }

}

enum class URLS(val type : String) {
    BASE_URL("https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do"),
    DEGREE_URL("https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do?fastStart=mobileAdvisors"),
    COURSE_URL("https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do?fastStart=mobileSchedule")
}