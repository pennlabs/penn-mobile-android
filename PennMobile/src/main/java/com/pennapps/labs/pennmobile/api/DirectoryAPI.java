package com.pennapps.labs.pennmobile.api;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DirectoryAPI extends API{
    public DirectoryAPI() {
        super();
        BASE_URL = "http://api.pennlabs.org/directory/";
    }
    public JSONObject search(String name) {
        try {
            name = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        HttpGet httpGet = new HttpGet(BASE_URL + "search?name=" + name);
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
