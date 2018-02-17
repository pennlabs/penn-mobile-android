package com.pennapps.labs.pennmobile.classes;

import java.util.ArrayList;

/**
 * Created by MikeD on 2/10/2018.
 */

public class GSRContainer {

    private String gsrName;
    //used to keep track availability of the given room
    private ArrayList<GSRContainerSlot> gsrAvailableSlots = new ArrayList<GSRContainerSlot>();


    public GSRContainer(String constructorGsrName, String constructorTimeRange, String constructorDateTime,
                        String constructorDayDate, String constructorDateNum, String constructorDuration, String constructorElementId) {

        gsrName = constructorGsrName;

        GSRContainerSlot newGSRSlot = new GSRContainerSlot(constructorTimeRange, constructorDateTime, constructorDayDate, constructorDateNum, constructorDuration, constructorElementId);

        gsrAvailableSlots.add(newGSRSlot);

    }

    public void addGSRSlot(String constructorTimeRange, String constructorDateTime,
                           String constructorDayDate, String constructorDateNum, String constructorDuration, String constructorElementId) {

        //created new GSR time slot object
        GSRContainerSlot newGSRSlot = new GSRContainerSlot(constructorTimeRange, constructorDateTime, constructorDayDate, constructorDateNum, constructorDuration, constructorElementId);

        gsrAvailableSlots.add(newGSRSlot);
    }

    public String getGsrName() {
        return gsrName;
    }

    public ArrayList<GSRContainerSlot> getAvailableGSRSlots() {
        return gsrAvailableSlots;
    }
}