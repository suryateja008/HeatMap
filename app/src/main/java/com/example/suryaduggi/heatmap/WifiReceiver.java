package com.example.suryaduggi.heatmap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.Parse;
import com.parse.ParseObject;


/**
 * Created by suryaduggi on 2/10/15.
 */
public class WifiReceiver extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks,
        LocationListener, GoogleApiClient.OnConnectionFailedListener {

    int count =0,prev=-1;
    String winame;

    private GoogleApiClient mGoogleApiClient=null;
    Context pContext;
    @Override

    public void onReceive(Context context, Intent intent) {

        pContext=context;
        int numberOfLevels=5;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int linkSpeed = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);


        //Parse.initialize(pContext, "leH3EigZZKFsc3rcM7EWGLS9H3NUHMu7HWBuYkRp", "9RSzw2F6Yb1Qg9aBjUaIPSBwQhHvWPpqV10cR0Ir");
        if(wifiInfo.getSSID().replace('"',' ').trim().equals("BroncoWiFi") && isConnected(context) ){

        Toast.makeText(context,linkSpeed+""+wifiInfo.getSSID().replace('"',' ').trim(),Toast.LENGTH_SHORT).show();

            mGoogleApiClient=null;
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
            mGoogleApiClient.connect();

        }


    }

    @Override
    public void onConnected(Bundle bundle) {

        LocationRequest mLocationRequest = new LocationRequest();


                  Parse.initialize(pContext, "leH3EigZZKFsc3rcM7EWGLS9H3NUHMu7HWBuYkRp", "9RSzw2F6Yb1Qg9aBjUaIPSBwQhHvWPpqV10cR0Ir");
                  mLocationRequest.setInterval(20000);
                  mLocationRequest.setFastestInterval(10000);
                  mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        int numberOfLevels=5;
        WifiManager wifiManager = (WifiManager) pContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
       int linkSpeed = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);


            if (wifiInfo.getSSID().replace('"', ' ').trim().equals("BroncoWiFi")) {
               // Log.d("TAGD", location.getLatitude() + "--" + count++ + "+++" + linkSpeed);
                ParseObject testObject = new ParseObject("BroncoData");
                testObject.put("latitude", location.getLatitude());
                testObject.put("longitude", location.getLongitude());
                testObject.put("strength", linkSpeed);
                testObject.saveInBackground();
            } else {
                Toast.makeText(pContext, "not", Toast.LENGTH_SHORT).show();
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo =
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }
}
