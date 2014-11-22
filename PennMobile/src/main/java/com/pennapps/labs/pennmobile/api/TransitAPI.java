package com.pennapps.labs.pennmobile.api;


import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

public class TransitAPI extends API{
    private final String TRANSIT_ID       = "UPENN_OD_emrX_1000543";
    private final String TRANSIT_PASSWORD = "765chvthdd4osqdu9aaek6tm7c";

    public TransitAPI() {
        super();
        setUrlPath("transit/");
        ID = TRANSIT_ID;
        PASSWORD = TRANSIT_PASSWORD;
    }

    public JSONObject getStops() {
        HttpGet httpGet = new HttpGet(BASE_URL + urlPath);
        httpGet.addHeader(new BasicHeader("Authorization-Bearer", ID));
        httpGet.addHeader(new BasicHeader("Authorization-Token", PASSWORD));
        httpGet.addHeader(new BasicHeader("Content-Type", "application/json; charset=utf-8"));

        try {
            HttpResponse response = httpClient.execute(httpGet);
            JSONTokener tokener = new JSONTokener(inputStreamToString
                    (response.getEntity().getContent()).toString());
            return new JSONObject(tokener);
        } catch (IOException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    public JSONObject getStop(int stopID) {
        HttpGet httpGet = new HttpGet(BASE_URL + urlPath + stopID + "/Configuration");
        httpGet.addHeader(new BasicHeader("Authorization-Bearer", ID));
        httpGet.addHeader(new BasicHeader("Authorization-Token", PASSWORD));
        httpGet.addHeader(new BasicHeader("Content-Type", "application/json; charset=utf-8"));

        try {
            HttpResponse response = httpClient.execute(httpGet);
            JSONTokener tokener = new JSONTokener(inputStreamToString
                    (response.getEntity().getContent()).toString());
            return new JSONObject(tokener);
        } catch (IOException e) {
            return null;
        } catch (JSONException e) {
            return null;
        }
    }
}
