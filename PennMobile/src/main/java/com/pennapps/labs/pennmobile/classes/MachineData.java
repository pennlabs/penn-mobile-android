package com.pennapps.labs.pennmobile.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jackie on 2017-12-26.
 */

public class MachineData {
    @SerializedName("0")
    @Expose
    private Double _0;
    @SerializedName("1")
    @Expose
    private Double _1;
    @SerializedName("2")
    @Expose
    private Double _2;
    @SerializedName("3")
    @Expose
    private Double _3;
    @SerializedName("4")
    @Expose
    private Double _4;
    @SerializedName("5")
    @Expose
    private Double _5;
    @SerializedName("6")
    @Expose
    private Double _6;
    @SerializedName("7")
    @Expose
    private Double _7;
    @SerializedName("8")
    @Expose
    private Double _8;
    @SerializedName("9")
    @Expose
    private Double _9;
    @SerializedName("10")
    @Expose
    private Double _10;
    @SerializedName("11")
    @Expose
    private Double _11;
    @SerializedName("12")
    @Expose
    private Double _12;
    @SerializedName("13")
    @Expose
    private Double _13;
    @SerializedName("14")
    @Expose
    private Double _14;
    @SerializedName("15")
    @Expose
    private Double _15;
    @SerializedName("16")
    @Expose
    private Double _16;
    @SerializedName("17")
    @Expose
    private Double _17;
    @SerializedName("18")
    @Expose
    private Double _18;
    @SerializedName("19")
    @Expose
    private Double _19;
    @SerializedName("20")
    @Expose
    private Double _20;
    @SerializedName("21")
    @Expose
    private Double _21;
    @SerializedName("22")
    @Expose
    private Double _22;
    @SerializedName("23")
    @Expose
    private Double _23;
    @SerializedName("24")
    @Expose
    private Double _24;
    @SerializedName("25")
    @Expose
    private Double _25;
    @SerializedName("26")
    @Expose
    private Double _26;

    // gets 24 hour data
    public List<Double> getData() {
        List data = new ArrayList();
        data.add(_0);
        data.add(_1);
        data.add(_2);
        data.add(_3);
        data.add(_4);
        data.add(_5);
        data.add(_6);
        data.add(_7);
        data.add(_8);
        data.add(_9);
        data.add(_10);
        data.add(_11);
        data.add(_12);
        data.add(_13);
        data.add(_14);
        data.add(_15);
        data.add(_16);
        data.add(_17);
        data.add(_18);
        data.add(_19);
        data.add(_20);
        data.add(_21);
        data.add(_22);
        data.add(_23);
        List copy = new ArrayList(data);
        return copy;
    }

    // sets all data to 0
    public void setData() {
        _0 = new Double(0);
        _1 = new Double(0);
        _2 = new Double(0);
        _3 = new Double(0);
        _4 = new Double(0);
        _5 = new Double(0);
        _6 = new Double(0);
        _7 = new Double(0);
        _8 = new Double(0);
        _9 = new Double(0);
        _10 = new Double(0);
        _11 = new Double(0);
        _12 = new Double(0);
        _13 = new Double(0);
        _14 = new Double(0);
        _15 = new Double(0);
        _16 = new Double(0);
        _17 = new Double(0);
        _18 = new Double(0);
        _19 = new Double(0);
        _20 = new Double(0);
        _21 = new Double(0);
        _22 = new Double(0);
        _23 = new Double(0);
        _24 = new Double(0);
        _25 = new Double(0);
        _26 = new Double(0);
    }

    public List<Double> getAdjustedData() {
        List<Double> adjustedData = new ArrayList<>();
        List<Double> originalData = getData();
        int minIndex = originalData.indexOf(Collections.min(originalData));
        double minData = originalData.get(minIndex).doubleValue();
        int maxIndex = originalData.indexOf(Collections.max(originalData));
        double maxData = originalData.get(maxIndex).doubleValue();

        // in case there is no data
        if (maxData - minData == 0) {
            adjustedData = originalData;
        } else {
            for (Double data : originalData) {
                adjustedData.add((maxData - data.doubleValue()) / (maxData - minData));
            }
        }
        return adjustedData;
    }
}
