package com.pennapps.labs.pennmobile.api;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

public class RegistrarAPI extends API {
    private final String REGISTRAR_ID       = "UPENN_OD_empF_1000401";
    private final String REGISTRAR_PASSWORD = "3qle5rfgns5d466o5tq5qnqndo";

    public RegistrarAPI() {
        super();
        ID = REGISTRAR_ID;
        PASSWORD = REGISTRAR_PASSWORD;
        // BASE_URL = "http://58ddab82.ngrok.com/";
        urlPath = "course_section_search?course_id=";
    }

    public JSONObject getCourse(String courseId) {
        HttpGet httpGet = new HttpGet(BASE_URL + urlPath + courseId);
        httpGet.addHeader(new BasicHeader("Authorization-Bearer", ID));
        httpGet.addHeader(new BasicHeader("Authorization-Token", PASSWORD));
        httpGet.addHeader(new BasicHeader("Content-Type", "application/json; charset=utf-8"));

        try {
            HttpResponse response = httpClient.execute(httpGet);
            JSONTokener tokener = new JSONTokener(inputStreamToString
                    (response.getEntity().getContent()).toString());
            return new JSONObject(tokener);
        } catch (IOException e) {
            Log.v("vivlabs", "" + e);
            return null;
        } catch (JSONException e) {
            Log.v("vivlabs", "" + e);
            return null;
        }
    }
}
