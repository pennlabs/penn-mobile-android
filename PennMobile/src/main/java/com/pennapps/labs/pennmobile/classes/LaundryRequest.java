package com.pennapps.labs.pennmobile.classes;

import java.util.List;

public class LaundryRequest {
    List<Integer> rooms;
    public LaundryRequest(List<Integer> laundryPreferences) {
        rooms = laundryPreferences;
    }
}
