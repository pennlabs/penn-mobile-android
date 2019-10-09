package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.SerializedName

class Degree {

    @SerializedName("school_name")
    var schoolName : String? = null

    @SerializedName("school_code")
    var schoolCode : String? = null

    @SerializedName("degree_name")
    var degreeName : String? = null

    @SerializedName("degree_code")
    var degreeCode : String? = null

    @SerializedName("expected_grad_term")
    var expectedGradTerm : String? = null

    @SerializedName("majors")
    var majors : Set<Major>? = null

    companion object Major {
        lateinit var name : String
        lateinit var code : String
    }

}

