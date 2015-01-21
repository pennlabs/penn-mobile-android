package com.pennapps.labs.pennmobile.classes;

import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adel on 12/16/14.
 * Class for Dining Venues from Business Services API
 */
public class Venue {
    public int id;
    public String name;
    public String venueType;
    public List<Interval> hours = new ArrayList<>();
}
