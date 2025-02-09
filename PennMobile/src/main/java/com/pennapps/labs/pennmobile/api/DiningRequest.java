package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.dining.classes.Venue;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

// This is for widget dining data network request)
public interface DiningRequest {
    @GET("dining/venues")
    Observable<List<Venue>> venues();
}
