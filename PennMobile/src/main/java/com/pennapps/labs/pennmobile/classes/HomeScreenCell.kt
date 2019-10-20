package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Jackie on 2018-03-28. Updated by Marta on 2019-10-20.
 */

class HomeScreenCell {
    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("info")
    @Expose
    var info: HomeScreenInfo? = null
}
