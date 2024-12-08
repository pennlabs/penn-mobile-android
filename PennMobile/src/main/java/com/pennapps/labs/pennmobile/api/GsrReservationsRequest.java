package com.pennapps.labs.pennmobile.api;

import com.pennapps.labs.pennmobile.gsr.classes.GSRReservation;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

public interface GsrReservationsRequest {
    @GET("/gsr/reservations")
    Observable<List<GSRReservation>> getGsrReservations(
            @Header("Authorization") String bearerToken
    );
}