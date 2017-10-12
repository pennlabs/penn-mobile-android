package com.pennapps.labs.pennmobile.classes;

import java.util.ArrayList;

/**
 * Created by MikeD on 10/9/2017.
 */

//class that keeps track of all the GSR rooms themselves
public class GSR {

    private String gsrName;
    //used to keep track availability of the given room
    private ArrayList<GSRSlot> gsrAvailableSlots = new ArrayList<GSRSlot>();


    public GSR (String constructorGsrName, String constructorTimeRange, String constructorDateTime,
                String constructorDayDate, String constructorDateNum, String constructorDuration, String constructorElementId) {

        gsrName = constructorGsrName;

        GSRSlot newGSRSlot = new GSRSlot(constructorTimeRange, constructorDateTime, constructorDayDate, constructorDateNum, constructorDuration, constructorElementId);

        gsrAvailableSlots.add(newGSRSlot);

    }

    public void addGSRSlot(String constructorTimeRange, String constructorDateTime,
                      String constructorDayDate, String constructorDateNum, String constructorDuration, String constructorElementId) {

        //created new GSR time slot object
        GSRSlot newGSRSlot = new GSRSlot(constructorTimeRange, constructorDateTime, constructorDayDate, constructorDateNum, constructorDuration, constructorElementId);

        gsrAvailableSlots.add(newGSRSlot);
    }

    public String getGsrName() {
        return gsrName;
    }

    public ArrayList<GSRSlot>  getAvailableGSRSlots () {
        return gsrAvailableSlots;
    }

}
