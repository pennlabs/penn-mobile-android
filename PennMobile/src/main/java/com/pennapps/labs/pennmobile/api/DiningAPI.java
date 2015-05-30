package com.pennapps.labs.pennmobile.api;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DiningAPI {
    protected OkHttpClient client;
    protected String BASE_URL = "http://api.pennlabs.org/dining/";

    public DiningAPI(OkHttpClient client) {
        this.client = client;
    }

    public JSONObject getAPIData(String urlPath) {
        Request request = new Request.Builder()
                .url(BASE_URL + urlPath)
                .header("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (JSONException | IOException ignored) {
            return null;
        }
    }

    public JSONObject getVenues() {
        return getAPIData("venues");
    }

    public JSONObject getDailyMenu(int hallID) {
        return getAPIData("daily_menu/" + hallID);
    }
}
