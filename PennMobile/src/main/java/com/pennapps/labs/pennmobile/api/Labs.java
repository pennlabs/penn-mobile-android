package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.HomeCell;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import rx.Observable;

public interface Labs {

    String pennLabsBaseUrl = "https://api.pennlabs.org";

    // home page
    @GET("/homepage")
    Observable<List<HomeCell>> getHomePage(
            @Header("X-Device-ID") String deviceID,
            @Header("X-Account-ID") String accountID,
            @Query("sessionid") String sessionID);

}
