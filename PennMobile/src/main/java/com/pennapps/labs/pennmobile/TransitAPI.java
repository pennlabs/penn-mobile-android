package com.pennapps.labs.pennmobile;


public class TransitAPI extends API{
    private final String TRANSIT_ID       = "UPENN_OD_emrX_1000543";
    private final String TRANSIT_PASSWORD = "765chvthdd4osqdu9aaek6tm7c";

    TransitAPI() {
        super();
        setUrlPath("transit/");
        ID = TRANSIT_ID;
        PASSWORD = TRANSIT_PASSWORD;
    }
}
