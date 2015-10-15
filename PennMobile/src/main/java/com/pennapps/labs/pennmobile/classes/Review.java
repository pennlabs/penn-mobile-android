package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.SerializedName;

/**
 * Class for average course review from Penn Course Review
 * Created by Adel on 10/15/15.
 */
public class Review {
    @SerializedName("rCourseQuality") public float courseQuality;
    @SerializedName("rInstructorQuality") public float instructorQuality;
    @SerializedName("rDifficulty") public float difficulty;

    public String courseQuality() {
        return String.format("%.1f", this.courseQuality);
    }

    public String instructorQuality() {
        return String.format("%.1f", this.instructorQuality);
    }

    public String difficulty() {
        return String.format("%.1f", this.difficulty);
    }
}
