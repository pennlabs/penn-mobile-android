package com.pennapps.labs.pennmobile.api;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RegistrarAPI extends API {
    public RegistrarAPI() {
        super();
        BASE_URL = "http://api.pennlabs.org/registrar/";
    }

    public JSONObject getCourse(String courseId) {
        urlPath = "search?q=";
        try {
            courseId = URLEncoder.encode(courseId, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        HttpGet httpGet = new HttpGet(BASE_URL + urlPath + courseId);
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
