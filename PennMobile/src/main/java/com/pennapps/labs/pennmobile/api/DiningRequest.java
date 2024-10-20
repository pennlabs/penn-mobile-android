package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

// This is for widget dining data network request)
public interface DiningRequest {
    @GET("/dining/venues")
    Observable<List<Venue>> venues();
}