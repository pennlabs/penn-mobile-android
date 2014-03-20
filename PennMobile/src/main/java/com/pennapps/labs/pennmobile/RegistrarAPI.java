package com.pennapps.labs.pennmobile;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrarAPI {
    private final String BASE_URL = "https://esb.isc-seo.upenn.edu/8091/open_data/";
    private final String ID       = "UPENN_OD_empF_1000401";
    private final String PASSWORD = "3qle5rfgns5d466o5tq5qnqndo";
    private HttpClient httpClient;

    protected RegistrarAPI() {
         httpClient = new DefaultHttpClient();
    }

    public JSONObject getCourse(String courseId) {
        HttpGet httpGet = new HttpGet(BASE_URL + "course_section_search?course_id=" + courseId);
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


    private StringBuilder inputStreamToString(InputStream is) {
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
