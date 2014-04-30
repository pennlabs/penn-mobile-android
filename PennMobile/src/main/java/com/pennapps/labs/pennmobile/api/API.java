package com.pennapps.labs.pennmobile.api;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class API {
    protected String BASE_URL = "https://esb.isc-seo.upenn.edu/8091/open_data/";
    protected HttpClient httpClient;
    protected String ID;
    protected String PASSWORD;
    protected String urlPath;

    protected API() {
        httpClient = new DefaultHttpClient();
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
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


    protected StringBuilder inputStreamToString(InputStream is) {
        String line;
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {

        }

        return total;
    }
}
