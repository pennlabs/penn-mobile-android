package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.classes.AccessTokenResponse;
import com.pennapps.labs.pennmobile.classes.GSRReservation;
import com.pennapps.labs.pennmobile.classes.Venue;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import rx.Observable;

public interface GsrReservationsRequest {
    @GET("/gsr/reservations")
    Observable<List<GSRReservation>> getGsrReservations(
            @Header("Authorization") String bearerToken
    );
}