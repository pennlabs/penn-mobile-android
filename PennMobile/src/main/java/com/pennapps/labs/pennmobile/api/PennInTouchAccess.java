package com.pennapps.labs.pennmobile.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;

public class PennInTouchAccess extends AsyncTask<Void, Void, Void> {

    String url = "https://pennintouch.apps.upenn.edu/pennInTouch/jsp/fast2.do";
    String title;
    Bitmap bitmap;

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //Connect to the website
            Document document = Jsoup.connect(url).get();

            Log.v("TESTING JAWN", document.title());

            //Get the logo source of the website
            Element img = document.select("img").first();
            // Locate the src attribute
            String imgSrc = img.absUrl("src");
            // Download image from URL
            InputStream input = new java.net.URL(imgSrc).openStream();
            // Decode Bitmap
            bitmap = BitmapFactory.decodeStream(input);

            //Get the title of the website
            title = document.title();
        } catch (IOException e){

        }
        return null;
    }
}
