package com.pennapps.labs.pennmobile.classes;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jason on 10/21/2015.
 */
public class LaundryHall {
    private String name;
    private Map<Integer, String> ids;

    public LaundryHall(String name, int id) {
        this.name = name;
        ids = new HashMap<>();
        ids.put(id, name);
    }

    public static List<LaundryHall> getLaundryHall(List<Laundry> laundries) {
        LinkedList<LaundryHall> halls = new LinkedList<>();
        for (Laundry l : laundries) {
            LaundryHall h = null;
            if (!halls.isEmpty()) {
                h = halls.getLast();
            }
            String name = l.name;
            if (l.name.contains("-")) {
                name = l.name.substring(0, l.name.indexOf('-'));
            }
            if (h != null && h.getName().equals(name)) {
                h.getIds().put(l.hall_no, l.name);
            } else {
                h = new LaundryHall(name, l.hall_no);
                halls.add(h);
            }
        }
        return halls;
    }

    public Map<Integer, String> getIds() {
        return ids;
    }

    public String getName() {
        return name;
    }
}
