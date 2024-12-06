package com.pennapps.labs.pennmobile.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList
import java.util.Collections

/**
 * Created by Jackie on 10/26/2018.
 */

class MachineData {
    @SerializedName("0")
    @Expose
    private var m0: Double = 0.0

    @SerializedName("1")
    @Expose
    private var m1: Double = 0.0

    @SerializedName("2")
    @Expose
    private var m2: Double = 0.0

    @SerializedName("3")
    @Expose
    private var m3: Double = 0.0

    @SerializedName("4")
    @Expose
    private var m4: Double = 0.0

    @SerializedName("5")
    @Expose
    private var m5: Double = 0.0

    @SerializedName("6")
    @Expose
    private var m6: Double = 0.0

    @SerializedName("7")
    @Expose
    private var m7: Double = 0.0

    @SerializedName("8")
    @Expose
    private var m8: Double = 0.0

    @SerializedName("9")
    @Expose
    private var m9: Double = 0.0

    @SerializedName("10")
    @Expose
    private var m10: Double = 0.0

    @SerializedName("11")
    @Expose
    private var m11: Double = 0.0

    @SerializedName("12")
    @Expose
    private var m12: Double = 0.0

    @SerializedName("13")
    @Expose
    private var m13: Double = 0.0

    @SerializedName("14")
    @Expose
    private var m14: Double = 0.0

    @SerializedName("15")
    @Expose
    private var m15: Double = 0.0

    @SerializedName("16")
    @Expose
    private var m16: Double = 0.0

    @SerializedName("17")
    @Expose
    private var m17: Double = 0.0

    @SerializedName("18")
    @Expose
    private var m18: Double = 0.0

    @SerializedName("19")
    @Expose
    private var m19: Double = 0.0

    @SerializedName("20")
    @Expose
    private var m20: Double = 0.0

    @SerializedName("21")
    @Expose
    private var m21: Double = 0.0

    @SerializedName("22")
    @Expose
    private var m22: Double = 0.0

    @SerializedName("23")
    @Expose
    private var m23: Double = 0.0

    @SerializedName("24")
    @Expose
    private var m24: Double = 0.0

    @SerializedName("25")
    @Expose
    private var m25: Double = 0.0

    @SerializedName("26")
    @Expose
    private var m26: Double = 0.0

    // gets 24 hour data
    val data: MutableList<Double>
        get() {
            val data = ArrayList<Double>()
            data.add(m0)
            data.add(m1)
            data.add(m2)
            data.add(m3)
            data.add(m4)
            data.add(m5)
            data.add(m6)
            data.add(m7)
            data.add(m8)
            data.add(m9)
            data.add(m10)
            data.add(m11)
            data.add(m12)
            data.add(m13)
            data.add(m14)
            data.add(m15)
            data.add(m16)
            data.add(m17)
            data.add(m18)
            data.add(m19)
            data.add(m20)
            data.add(m21)
            data.add(m22)
            data.add(m23)
            return ArrayList(data)
        }

    // in case there is no data
    val adjustedData: List<Double>
        get() {
            var adjustedData: MutableList<Double> = ArrayList()
            val originalData = data
            val minIndex = originalData.indexOf(Collections.min(originalData))
            val minData = originalData[minIndex]
            val maxIndex = originalData.indexOf(Collections.max(originalData))
            val maxData = originalData[maxIndex]
            if (maxData - minData == 0.0) {
                adjustedData = originalData
            } else {
                for (data in originalData) {
                    adjustedData.add((maxData - data) / (maxData - minData))
                }
            }
            return adjustedData
        }

    // sets all data to 0
    fun setData() {
        m0 = 0.0
        m1 = 0.0
        m2 = 0.0
        m3 = 0.0
        m4 = 0.0
        m5 = 0.0
        m6 = 0.0
        m7 = 0.0
        m8 = 0.0
        m9 = 0.0
        m10 = 0.0
        m11 = 0.0
        m12 = 0.0
        m13 = 0.0
        m14 = 0.0
        m15 = 0.0
        m16 = 0.0
        m17 = 0.0
        m18 = 0.0
        m19 = 0.0
        m20 = 0.0
        m21 = 0.0
        m22 = 0.0
        m23 = 0.0
        m24 = 0.0
        m25 = 0.0
        m26 = 0.0
    }
}
