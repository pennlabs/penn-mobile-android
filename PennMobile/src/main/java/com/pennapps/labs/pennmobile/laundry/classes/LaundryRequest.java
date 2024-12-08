package com.pennapps.labs.pennmobile.laundry.classes;

import java.util.List;

public class LaundryRequest {
    List<Integer> rooms;
    public LaundryRequest(List<Integer> laundryPreferences) {
        rooms = laundryPreferences;
    }
}
