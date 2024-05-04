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
    private var _0: Double = 0.0

    @SerializedName("1")
    @Expose
    private var _1: Double = 0.0

    @SerializedName("2")
    @Expose
    private var _2: Double = 0.0

    @SerializedName("3")
    @Expose
    private var _3: Double = 0.0

    @SerializedName("4")
    @Expose
    private var _4: Double = 0.0

    @SerializedName("5")
    @Expose
    private var _5: Double = 0.0

    @SerializedName("6")
    @Expose
    private var _6: Double = 0.0

    @SerializedName("7")
    @Expose
    private var _7: Double = 0.0

    @SerializedName("8")
    @Expose
    private var _8: Double = 0.0

    @SerializedName("9")
    @Expose
    private var _9: Double = 0.0

    @SerializedName("10")
    @Expose
    private var _10: Double = 0.0

    @SerializedName("11")
    @Expose
    private var _11: Double = 0.0

    @SerializedName("12")
    @Expose
    private var _12: Double = 0.0

    @SerializedName("13")
    @Expose
    private var _13: Double = 0.0

    @SerializedName("14")
    @Expose
    private var _14: Double = 0.0

    @SerializedName("15")
    @Expose
    private var _15: Double = 0.0

    @SerializedName("16")
    @Expose
    private var _16: Double = 0.0

    @SerializedName("17")
    @Expose
    private var _17: Double = 0.0

    @SerializedName("18")
    @Expose
    private var _18: Double = 0.0

    @SerializedName("19")
    @Expose
    private var _19: Double = 0.0

    @SerializedName("20")
    @Expose
    private var _20: Double = 0.0

    @SerializedName("21")
    @Expose
    private var _21: Double = 0.0

    @SerializedName("22")
    @Expose
    private var _22: Double = 0.0

    @SerializedName("23")
    @Expose
    private var _23: Double = 0.0

    @SerializedName("24")
    @Expose
    private var _24: Double = 0.0

    @SerializedName("25")
    @Expose
    private var _25: Double = 0.0

    @SerializedName("26")
    @Expose
    private var _26: Double = 0.0

    // gets 24 hour data
    val data: MutableList<Double>
        get() {
            val data = ArrayList<Double>()
            data.add(_0)
            data.add(_1)
            data.add(_2)
            data.add(_3)
            data.add(_4)
            data.add(_5)
            data.add(_6)
            data.add(_7)
            data.add(_8)
            data.add(_9)
            data.add(_10)
            data.add(_11)
            data.add(_12)
            data.add(_13)
            data.add(_14)
            data.add(_15)
            data.add(_16)
            data.add(_17)
            data.add(_18)
            data.add(_19)
            data.add(_20)
            data.add(_21)
            data.add(_22)
            data.add(_23)
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
        _0 = 0.0
        _1 = 0.0
        _2 = 0.0
        _3 = 0.0
        _4 = 0.0
        _5 = 0.0
        _6 = 0.0
        _7 = 0.0
        _8 = 0.0
        _9 = 0.0
        _10 = 0.0
        _11 = 0.0
        _12 = 0.0
        _13 = 0.0
        _14 = 0.0
        _15 = 0.0
        _16 = 0.0
        _17 = 0.0
        _18 = 0.0
        _19 = 0.0
        _20 = 0.0
        _21 = 0.0
        _22 = 0.0
        _23 = 0.0
        _24 = 0.0
        _25 = 0.0
        _26 = 0.0
    }
}
