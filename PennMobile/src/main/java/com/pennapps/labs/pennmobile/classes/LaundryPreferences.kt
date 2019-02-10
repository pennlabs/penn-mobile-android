package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Varun on 10/25/2018.
 */

class LaundryPreferences(@field:SerializedName("rooms")
                         @field:Expose
                         private val rooms: List<Int>)
