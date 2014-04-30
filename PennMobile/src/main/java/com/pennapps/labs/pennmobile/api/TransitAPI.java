package com.pennapps.labs.pennmobile.api;


public class TransitAPI extends API{
    private final String TRANSIT_ID       = "UPENN_OD_emrX_1000543";
    private final String TRANSIT_PASSWORD = "765chvthdd4osqdu9aaek6tm7c";

    public TransitAPI() {
        super();
        setUrlPath("transit/");
        ID = TRANSIT_ID;
        PASSWORD = TRANSIT_PASSWORD;
    }
}
