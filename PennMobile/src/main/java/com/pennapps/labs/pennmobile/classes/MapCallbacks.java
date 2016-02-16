package com.pennapps.labs.pennmobile.classes;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


public class MapCallbacks implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public LatLng latLng;
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    public boolean waiting, called, connected;
    public static LatLng DEFAULT_LATLNG = new LatLng(39.9529, -75.197098);

    public MapCallbacks() {
        createLocationRequest();
        called = false;
        connected = false;
        waiting = false;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            latLng = DEFAULT_LATLNG;
        } else {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        requestLocationUpdates();
        waiting = false;
    }

    public void requestLocationUpdates(){
        if (!called && connected) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest,
                    (com.google.android.gms.location.LocationListener) this);
        }
        called = true;
    }

    public void stopLocationUpdates() {
        if (connected && called) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
        if (latLng == null) {
            latLng = DEFAULT_LATLNG;
        }
        called = false;
    }

    @Override
    public void onConnectionSuspended(int cause){
        //handle cause later
        if (latLng == null) {
            latLng = DEFAULT_LATLNG;
        }
        waiting = false;
        connected = false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //handle connectionResult later
        if (latLng == null) {
            latLng = DEFAULT_LATLNG;
        }
        waiting = false;
    }

    public LatLng getLatLng(){
        if(latLng == null){
            return DEFAULT_LATLNG;
        }
        return latLng;
    }

    public GoogleApiClient getGoogleApiClient(){
        return mGoogleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient mGoogleApiClient){
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.AVAILABLE) {
            waiting = false;
            requestLocationUpdates();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (waiting) {
            requestLocationUpdates();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        stopLocationUpdates();
        waiting = true;
    }
}